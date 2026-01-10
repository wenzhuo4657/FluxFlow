package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.tgBot;

import cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy.NotifierConfig;

public class TgBotConfig implements NotifierConfig {

    private String botToken;
    
    // 代理配置
    private String proxyHost;
    private Integer proxyPort;
    private String proxyType = "HTTP"; // HTTP, SOCKS
    
    // 超时配置（秒）
    private Integer connectTimeout = 60;
    private Integer readTimeout = 60;
    private Integer writeTimeout = 60;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Integer writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}
