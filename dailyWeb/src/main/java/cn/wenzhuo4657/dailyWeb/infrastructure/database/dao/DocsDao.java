package cn.wenzhuo4657.dailyWeb.infrastructure.database.dao;

import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (Docs)表数据库访问层
 *
 * @author makejava
 * @since 2025-11-10 13:41:14
 */
public interface DocsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Docs queryById(Long id);

  
    /**
     * 统计总行数
     *
     * @param docs 查询条件
     * @return 总行数
     */
    long count(Docs docs);

    /**
     * 新增数据
     *
     * @param docs 实例对象
     * @return 影响行数
     */
    int insert(Docs docs);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Docs> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Docs> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Docs> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Docs> entities);

    /**
     * 修改数据
     *
     * @param docs 实例对象
     * @return 影响行数
     */
    int update(Docs docs);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);


    boolean isPermissions(@Param("docsId") Long docsId, @Param("userId") Long userId);

    boolean isExist(@Param("docsId") Long docsId);

    int deleteByDocsID(Long docsId);

    List<Docs> queryByUserIdAndtypeId(@Param("userId") Long userId, @Param("typeId") Long typeId);

    Docs queryByDocsId(@Param("docsId") Long docsId);

    Long queryTypeByDocsId(@Param("docsId")Long docsId);
}

