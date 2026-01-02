package cn.wenzhuo4657.noifiterBot.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache")
public  class CacheConfiguration {

    /**
     * 是否启用第三方缓存，false表示使用本地缓存
     */
    private boolean enabled = true;

    /**
     * 当前使用的缓存类型 (redis 或 valkey 或 local)
     */
    private String activeType = "local";

    /**
     * 缓存键前缀
     */
    private String keyPrefix = "app:cache:";



    /**
     * Redis 配置
     */
    private Redis redis = new Redis();

    /**
     * Valkey 配置
     */
    private Valkey valkey = new Valkey();

    /**
     * Local 配置
     */
    private Local local = new Local();

    // Getter and Setter methods
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }



    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public Valkey getValkey() {
        return valkey;
    }

    public void setValkey(Valkey valkey) {
        this.valkey = valkey;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    /**
     * Local 配置类
     */
    public  static  class  Local{

    }


    /**
     * Redis 配置类
     */
    public static class Redis {
        private String host = "localhost";
        private int port = 6379;
        private String password = "";
        private int database = 0;
        private int connectionPoolSize = 64;
        private int connectionMinimumIdleSize = 10;
        private int connectTimeout = 10000;
        private int timeout = 3000;
        private int retryAttempts = 3;
        private int retryInterval = 1500;

        // Getter and Setter methods for Redis
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public int getConnectionPoolSize() {
            return connectionPoolSize;
        }

        public void setConnectionPoolSize(int connectionPoolSize) {
            this.connectionPoolSize = connectionPoolSize;
        }

        public int getConnectionMinimumIdleSize() {
            return connectionMinimumIdleSize;
        }

        public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
            this.connectionMinimumIdleSize = connectionMinimumIdleSize;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryAttempts() {
            return retryAttempts;
        }

        public void setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        public void setRetryInterval(int retryInterval) {
            this.retryInterval = retryInterval;
        }
    }

    /**
     * Valkey 配置类
     */
    public static class Valkey {
        private String host = "localhost";
        private int port = 6379;
        private String password = "";
        private int database = 0;
        private String clientType = "lettuce";
        private boolean shareNativeConnection = true;
        private boolean enableValkeySpecificFeatures = false;

        // Getter and Setter methods for Valkey
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public String getClientType() {
            return clientType;
        }

        public void setClientType(String clientType) {
            this.clientType = clientType;
        }

        public boolean isShareNativeConnection() {
            return shareNativeConnection;
        }

        public void setShareNativeConnection(boolean shareNativeConnection) {
            this.shareNativeConnection = shareNativeConnection;
        }

        public boolean isEnableValkeySpecificFeatures() {
            return enableValkeySpecificFeatures;
        }

        public void setEnableValkeySpecificFeatures(boolean enableValkeySpecificFeatures) {
            this.enableValkeySpecificFeatures = enableValkeySpecificFeatures;
        }
    }
}
