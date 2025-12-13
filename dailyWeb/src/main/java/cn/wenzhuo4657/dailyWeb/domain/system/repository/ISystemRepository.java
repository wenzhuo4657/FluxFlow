package cn.wenzhuo4657.dailyWeb.domain.system.repository;

import org.springframework.scheduling.annotation.Async;

public interface ISystemRepository {

    /**
     * 获取数据库版本
     * @return 数据库版本号
     */

    String getDatabaseVersion();
}
