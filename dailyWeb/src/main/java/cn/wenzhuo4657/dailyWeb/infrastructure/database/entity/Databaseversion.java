package cn.wenzhuo4657.dailyWeb.infrastructure.database.entity;

import java.io.Serializable;

/**
 * (Databaseversion)实体类
 *
 * @author makejava
 * @since 2025-12-09 10:22:29
 */
public class Databaseversion implements Serializable {
    private static final long serialVersionUID = 519002660922310482L;

    private Integer id;

    private String version;

    private String date;

    private String log;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

}

