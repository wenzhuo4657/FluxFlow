package cn.wenzhuo4657.dailyWeb.infrastructure.database.dao;

import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (DocsItem)表数据库访问层
 *
 * @author makejava
 * @since 2025-11-10 13:41:14
 */
public interface DocsItemDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DocsItem queryById(Long id);

  
    /**
     * 统计总行数
     *
     * @param docsItem 查询条件
     * @return 总行数
     */
    long count(DocsItem docsItem);

    /**
     * 新增数据
     *
     * @param docsItem 实例对象
     * @return 影响行数
     */
    int insert(DocsItem docsItem);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<DocsItem> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<DocsItem> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<DocsItem> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<DocsItem> entities);

    /**
     * 修改数据
     *
     * @param docsItem 实例对象
     * @return 影响行数
     */
    int update(DocsItem docsItem);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    DocsItem selectDocsItem(@Param("id") Long id);

    List<DocsItem> queryByDocsId(Long docsId);

    DocsItem queryByIndex(@Param("index") Long id);

    void delete(@Param("index")Long index);
}

