package cn.wenzhuo4657.dailyWeb.types.Exception;

/**
 * 响应码枚举类，用于定义异常的代码和信息。
 * 1xx - 信息响应
 * 2xx - 成功响应
 * 3xx - 重定向
 * 4xx - 客户端错误
 * 5xx - 服务器错误
 */
public enum ResponseCode {
    SUCCESS(200, "操作成功"),
    FAILED(400, "操作失败"),
    RESOURCE_NOT_FOUND(404, "资源不存在"),
    MISSING_CREDENTIALS(400,    "客户端请求参数错误、格式不正确、必填字段缺失等" ),
    NOT_PERMISSIONS(403,"文档存在，但用户权限不足"),
    programmingError(500,"未知错误" ),
    NOT_LOGIN(401,"未登录、token无效，请重新登录" )//401不可重复，前端用于判断是否需要跳转登录页面
    ,
    DATABASE_VERSION_ERROR(501,"传入数据库版本过高" );

    private String info;
    private Integer code;


    ResponseCode(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResponseCode{" +
                "info='" + info + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
