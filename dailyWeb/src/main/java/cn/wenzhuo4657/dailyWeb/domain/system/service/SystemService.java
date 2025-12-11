package cn.wenzhuo4657.dailyWeb.domain.system.service;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;

public interface SystemService {

/**
 * 重置数据库
 * @param tempFile 临时数据库文件
 */
    boolean reset(File tempFile);
    /**
     * 导出数据库文件至指定路径
     * @param tempBackup 临时数据库路径
     */
    boolean export(Path tempBackup) throws SQLException;
}
