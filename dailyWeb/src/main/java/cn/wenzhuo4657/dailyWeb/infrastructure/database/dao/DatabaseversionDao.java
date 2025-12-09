package cn.wenzhuo4657.dailyWeb.infrastructure.database.dao;

import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Databaseversion;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (Databaseversion)表数据库访问层
 *
 * @author makejava
 * @since 2025-12-09 10:22:29
 */
public interface DatabaseversionDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Databaseversion queryById(Integer id);

  
    /**
     * 统计总行数
     *
     * @param databaseversion 查询条件
     * @return 总行数
     */
    long count(Databaseversion databaseversion);

    /**
     * 新增数据
     *
     * @param databaseversion 实例对象
     * @return 影响行数
     */
    int insert(Databaseversion databaseversion);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Databaseversion> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Databaseversion> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Databaseversion> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Databaseversion> entities);

    /**
     * 修改数据
     *
     * @param databaseversion 实例对象
     * @return 影响行数
     */
    int update(Databaseversion databaseversion);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 获取数据库中最新的数据库版本号（查询原则： id最大）
     * @return
     */
    String getVersion();
}

