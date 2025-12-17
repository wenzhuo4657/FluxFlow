package cn.wenzhuo4657.dailyWeb.domain.system.service.impl;

import cn.wenzhuo4657.dailyWeb.domain.system.model.DataBaseVersionVo;
import cn.wenzhuo4657.dailyWeb.domain.system.service.SystemService;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DatabaseversionDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.repository.SystemRepository;
import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SystemServiceImpl implements SystemService {
    private static final Logger log = LoggerFactory.getLogger(SystemServiceImpl.class);

    @Autowired
    private DataSource dataSource;
    @Autowired
    private SystemRepository systemRepository;




    ReentrantLock alock=new ReentrantLock(true);

    ReentrantLock block=new ReentrantLock(true);

    @Override
    public boolean reset(File tempFile) {
        String mainDatabaseVersion  = systemRepository.getDatabaseVersion();



        try {
//            1，加锁，数据库操作必须是串行进行，且注意，该锁为可重入锁
            alock.tryLock(60, TimeUnit.SECONDS);
            // 2，执行核心流程
//            （1）获取当前数据库版本，导入数据库版本，（假定前者大于等于后者，如果后者大，则拒绝合并）
//            （2）根据版本获取选择流程，若相同，直接覆盖合并，结束递归，若不相同，则进行临时数据版本升级操作
//            （3） 版本升级结束，返回流程2
            try (Connection conn =dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                conn.setAutoCommit(false); // 开启事务

                // 1，挂载数据库，获取数据库版本
                String tempDatabaseName = "tempDb"; // 假设临时数据库名称为 tempDb
                String attachSql = "ATTACH DATABASE '" + tempFile.getAbsolutePath() + "' AS " + tempDatabaseName + ";";
                stmt.execute(attachSql);

                String versionsql="SELECT version FROM "+tempDatabaseName+".databaseversion order by id desc limit 1;";
                ResultSet rs=stmt.executeQuery(versionsql);
                String tempDatabaseVersion = rs.getString("version");


//                2，导入数据库版本校验，如果不一致，则进行版本兼容操作

                //                    2.1，判断是否可以进行版本兼容操作
                DataBaseVersionVo main = DataBaseVersionVo.getEnumByVersion(mainDatabaseVersion);
                DataBaseVersionVo temp = DataBaseVersionVo.getEnumByVersion(tempDatabaseVersion);
                if (main.getCode()<temp.getCode()){
                    throw  new AppException(ResponseCode.DATABASE_VERSION_ERROR);
                }else if (main.getCode()>temp.getCode()){
//                    执行版本升级操作
//                    todo 等待实现
                    throw  new AppException(ResponseCode.programmingError);
                }else {


//                    清除原表数据
                    String deleteSql=main.getDeleteSql();
                    ByteArrayResource deleteResource = new ByteArrayResource(deleteSql.getBytes(StandardCharsets.UTF_8));
                    ScriptUtils.executeSqlScript(conn, deleteResource);

//                    填充新表数据
                    String insertSql=main.getInsertSql();
                    ByteArrayResource insertResource = new ByteArrayResource(insertSql.getBytes(StandardCharsets.UTF_8));
                    ScriptUtils.executeSqlScript(conn, insertResource);
                }
                conn.commit(); // 提交事务
                conn.close();
                stmt.close();

                // 重新创建Statement以避免锁定问题
                try (Statement detachStmt = dataSource.getConnection().createStatement()) {
                    detachStmt.execute("DETACH DATABASE " + tempDatabaseName + ";");

                }
            } catch (Exception e) {
                throw new RuntimeException("数据库导入失败", e);
            }finally {

                boolean delete = tempFile.delete();
                System.out.println("删除临时文件：" + delete);
            }


        }catch (InterruptedException e) {
            log.error("排队超时",e);
        }finally {
            alock.unlock();
        }



//     todo 库表相同，但程序启动时的数据初始化不同，例如data.sql 、用户初始化


        return  true;


    }


    @Override
    public boolean export(Path tempBackup) throws SQLException {
        try {
            block.tryLock(10, TimeUnit.SECONDS);
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(true);

            String string = tempBackup.toAbsolutePath().toString();

            connection.createStatement().execute("VACUUM INTO '" + string + "';");

        }catch (InterruptedException e){
            log.error("排队超时",e);
            return  false;
        }finally {
            block.unlock();
        }

        return  true;

    }
}
