package cn.wenzhuo4657.dailyWeb.domain.system.service.impl;

import cn.wenzhuo4657.dailyWeb.domain.system.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SystemServiceImpl implements SystemService {
    private static final Logger log = LoggerFactory.getLogger(SystemServiceImpl.class);

    @Autowired
    private DataSource dataSource;


    ReentrantLock alock=new ReentrantLock(true);

    ReentrantLock block=new ReentrantLock(true);

    @Override
    public void reset(File tempFile) {

        try {
            alock.tryLock(60, TimeUnit.SECONDS);
            //  不能原子替换，进程锁定了当前db文件 :ATTACH DATABASE后合并数据，
            try (Connection backendConn =dataSource.getConnection();
                 Statement stmt = backendConn.createStatement()) {

                backendConn.setAutoCommit(false); // 开启事务

                // 使用 Statement 执行 ATTACH DATABASE
                String attachSql = "ATTACH DATABASE '" + tempFile.getAbsolutePath() + "' AS tempDb;";
                stmt.execute(attachSql);

                // **合并 表数据**
//            TODO sql脚本，以后数据库一定会变，所以之后要注意对不同版本的数据库进行兼容，对于库表版本进行标记，
//                情况1： 数据库版本版本一致，清除原表数据、添加新数据
//                情况2： 数据库版本不一致（导入数据库为低版本，当前数据库为高版本，也就是说导入数据存在缺少字段，或者表结构变更），清除原表数据，将旧表数据库动态变化为新表
//                情况3： 数据库版本不一致（导入为高版本，当前为低版本，不允许该操作！  原因1： 复杂度增加， 原因2： 表结构的语义可能不同，）
//                实现，
//                1， sql定义， 情况1、2都需要两个sql，一个是删除原表sql，另一个是数据导入sql，
//                2，  库表版本定义，该表不可变更，
//                流程定义
//                1，检查数据版本，
//                2，情况1好处理，直接执行两个sql导入，情况2需要将低版本升级，
//                3，假如情况2，例如，导入1,1 ，当前1，4
//                4,1,1升级为1.2，得到新的数据库文件，重新回到步骤1
//                api了解，核心在于是否能够创建临时数据库，并且，这些sql需要知道如何创建不同版本的数据库


//            1，清除原表数据
                String deleteSql = """
                    DELETE FROM main.docs_item;
                    DELETE FROM main.docs;
                    DELETE FROM main.docs_type;
                    DELETE FROM main.user;
                    DELETE FROM main.user_auth;
                    DELETE FROM main.notifier;
                    DELETE FROM main.notifier_type;
            """;
                stmt.executeUpdate(deleteSql);
//            2，添加新库数据
                String insertSql = """

                   INSERT INTO main.docs_type (id, name, des, type_id)
                   SELECT id, name, des, type_id FROM tempDb.docs_type;

                   INSERT INTO main.docs (id, name, type_id, create_time, update_time, docs_id, user_id)
                   SELECT id, name, type_id, create_time, update_time, docs_id, user_id FROM tempDb.docs;

                   INSERT INTO main.docs_item (id, "index", docs_id, item_content, item_Field)
                   SELECT id, "index", docs_id, item_content, item_Field FROM tempDb.docs_item;

                   INSERT INTO main."user" (id, user_id, oauth_userId, oauth_provider, created_at, name, avatar_url)
                   SELECT id, user_id, oauth_userId, oauth_provider, created_at, name, avatar_url FROM tempDb."user";

                   INSERT INTO main.user_auth (id, user_id, docs_type_id)
                   SELECT id, user_id, docs_type_id FROM tempDb.user_auth;

                   INSERT INTO main.notifier (id, user_id, notifier_type_id, name, config_json)
                   SELECT id, user_id, notifier_type_id, name, config_json FROM tempDb.notifier;

                   INSERT INTO main.notifier_type (id, type_id, name, des)
                   SELECT id, type_id, name, des FROM tempDb.notifier_type;
                   """;
                stmt.executeUpdate(insertSql);

                backendConn.commit(); // 提交事务

                stmt.execute("DETACH DATABASE tempDb;");
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


    }


    private  File reset(File tempFile, String insertSql,String deleteSql) throws IOException {

    }

    @Override
    public void export(Path tempBackup) throws SQLException {
        try {
            block.tryLock(10, TimeUnit.SECONDS);
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(true);

            String string = tempBackup.toAbsolutePath().toString();

            connection.createStatement().execute("VACUUM INTO '" + string + "';");

        }catch (InterruptedException e){
            log.error("排队超时",e);
        }finally {
            block.unlock();
        }

    }
}
