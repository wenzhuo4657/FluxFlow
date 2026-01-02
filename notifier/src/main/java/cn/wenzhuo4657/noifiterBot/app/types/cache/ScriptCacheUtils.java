package cn.wenzhuo4657.noifiterBot.app.types.cache;

import cn.wenzhuo4657.noifiterBot.app.infrastructure.cache.GlobalCache;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptCacheUtils {




    public static enum  Script{
        qps("qps_MAX","scripts/qps_MAX.lua",ScriptType.lua);

        Script(String fileName, String classfilePath, ScriptType scriptType) {
            this.fileName = fileName;
            this.classfilePath = classfilePath;
            this.scriptType = scriptType;
        }

        String fileName;
        String classfilePath;
        ScriptType scriptType;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getClassfilePath() {
            return classfilePath;
        }

        public void setClassfilePath(String classfilePath) {
            this.classfilePath = classfilePath;
        }

        public ScriptType getScriptType() {
            return scriptType;
        }

        public void setScriptType(ScriptType scriptType) {
            this.scriptType = scriptType;
        }
    }


    public static enum ScriptType{
        lua(CacheKey.lua_key);

        ScriptType(String key) {
            this.key = key;
        }

        String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }





    /**
     * 加载脚本，返回sha1索引
     * @param script    脚本的全局描述
     * @param globalCache      全局缓存
     * @return  sha1索引
     * @throws IOException
     *
     *            //       获取脚本加载的哈希（全局统一加载，需要分布式缓存）
     *            1，脚本会一直存在，无需担心sha1失效
     *            2，可能会存在多个脚本，如何管理缓存键？数据结构特征是？
     *            前缀key: <文件名称,  sha1>      map结构
     *                  前缀key基本等同于目录的别名： 文件名称则由文件系统的特性保证唯一
     *
     */
    public  static  String loadScript(Script script, GlobalCache globalCache) throws IOException {


        // 这里应该检查缓存中是否已经存在脚本的sha1，如果存在则直接返回
        if (globalCache.hasKey(script.scriptType.key)){
            Map<String,String> map = globalCache.get(script.scriptType.key, Map.class);
               if (map.containsKey(script.fileName)){
                   return map.get(script.fileName);
               }
        }

            Map<String,String> map=new HashMap<>();
            ClassPathResource resource = new ClassPathResource(script.classfilePath);
            String qpsScript = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String qpsScriptSha1 = globalCache.loadLuaScript(qpsScript);
            map.put(script.fileName,qpsScriptSha1);
            globalCache.set(script.scriptType.key, map);
            return  qpsScriptSha1;




    }
}
