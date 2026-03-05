import axios, { AxiosInstance, AxiosRequestHeaders, InternalAxiosRequestConfig } from 'axios';
import { LocalStorage } from '@/constants/storage';
import router from '@/router/index';
import { useAuthStore } from '@/storage/auth';
import { refeshToken } from '@/services/request';

let service: AxiosInstance | null = null;


// 为了方便，也可以导出一个获取实例的函数
export function getHttp(): AxiosInstance {
    if (!service) {
        return initHttp();
    }
    return service;
}


// Token 刷新状态管理
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: any) => void;
  reject: (reason?: any) => void;
}> = [];

/**
 * 处理等待刷新 token 的队列
 */
const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};






// 导出一个函数，用于初始化 axios 实例
export function initHttp(): AxiosInstance {
  if (service) {
    return service;
  }

  service = axios.create({
    baseURL: import.meta.env.VITE_API_BASE || '',
  });

  // 请求拦截器
  service.interceptors.request.use(
    (config) => {
    
      // 1, 填充accessToken
      const accessToken = useAuthStore().accessToken;

      if (accessToken) {
        const authHeader = `Bearer ${accessToken}`;
        (config.headers as AxiosRequestHeaders)['ACCESS-TOKEN'] = authHeader;
      }
      return config;
    }
  );

  // 响应拦截器
  service.interceptors.response.use(
    (response) => {
      // 处理http请求 code=2xx, 也就是说业务系统处理的流量，无异常的响应
      const responseData = response.data;
      const auth = useAuthStore();


      // 1, 判断响应结构是否合规
      if (
          responseData &&
          typeof responseData === 'object' &&
          'code' in responseData &&
          'message' in responseData
         ){
                const code = responseData.code;
                const message = responseData.message || '请求失败';


                  //2 ， 特殊错误码处理

                 // 2.1 401处理： refesh无效，回退至登录页面
                if (code === 401) {
                 // Token 无效，清除登录状态并跳转到登录页

                  auth.logout();
                  localStorage.removeItem(LocalStorage.TOKEN);
                  // 使用Vue Router进行路由跳转
                  router.push('/login')
                  console.log('Token无效，清除登录状态');
                  return Promise.reject(new Error('未登录'));
                }

                  // 2.2 410处理： access无效，尝试刷新
                if(code==410){
                    // 2.2.1  打上标记，避免避免重复刷新
                    const originalRequest = response.config as InternalAxiosRequestConfig & { _retry?: boolean };
                    
                    if (originalRequest._retry) {
                        return Promise.reject(new Error('Token refresh failed'));
                    }
                    originalRequest._retry = true;
                  
                    //  2.2.2如果正在刷新accesstoken,则加入队列
                    if (isRefreshing) 
                      {
                              return new Promise
                                ((resolve, reject) => 
                                {
                                    failedQueue.push({ resolve, reject });
                                 })
                               .then((token) => 
                                {
                                          if (originalRequest.headers) 
                                          {
                                             (originalRequest.headers as AxiosRequestHeaders)['ACCESS_TOKEN'] = `Bearer ${token}`;
                                          }
                                   return service!(originalRequest);
                                 })  
                               .catch((err) => 
                                {
                                     return Promise.reject(err);
                                 });
                       }

            
                     isRefreshing = true;

                    //  2.2.3 刷新token，并重放请求
                    return refeshToken()
                      .then((newAccessToken) => {
                        // 更新 store 中的 accessToken
                        auth.setAccessToken(newAccessToken);
                      
                        // 处理队列中的请求
                        processQueue(null, newAccessToken);
                      
                       // 更新请求头并重放当前请求
                        if (originalRequest.headers) {
                             (originalRequest.headers as AxiosRequestHeaders)['ACCESS-TOKEN'] = `Bearer ${newAccessToken}`;
                        }
                        return service!(originalRequest);
                      })
                      .catch((err) => {
                        // 刷新失败，处理队列中的请求
                        processQueue(err, null);
                        auth.logout();
                               localStorage.removeItem(LocalStorage.TOKEN);
                        router.push('/login');
                        return Promise.reject(err);
                      })
                      .finally(() => {
                        isRefreshing = false;
                      });
          
           
        }
      
      }

      return response;
    }
    
  );

  return service;
}
