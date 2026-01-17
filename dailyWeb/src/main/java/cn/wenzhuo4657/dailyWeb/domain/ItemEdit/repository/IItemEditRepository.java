package cn.wenzhuo4657.dailyWeb.domain.ItemEdit.repository;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.UpdateItemDto;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;

import java.util.List;


public interface IItemEditRepository {


    List<DocsItem> getDocsItems(Long docs_Id);


    boolean addItem(DocsItem docs_Id);


    DocsItem selectDocsItem(Long id);

    void updateField(Long id, String field);


    boolean isPermissions(Long docsId, Long userId);

    boolean updateItem(Long index, String content);

    void deleteItem(Long index);

    /**
     * 根据父任务ID查询所有子任务
     * @param parentId 父任务的index
     * @return 子任务列表
     */
    List<DocsItem> getChildrenByParentId(Long parentId);

    /**
     * 查询文档下所有顶层任务（没有父任务的任务）
     * @param docsId 文档ID
     * @return 顶层任务列表
     */
    List<DocsItem> getTopLevelTasks(Long docsId);
}
