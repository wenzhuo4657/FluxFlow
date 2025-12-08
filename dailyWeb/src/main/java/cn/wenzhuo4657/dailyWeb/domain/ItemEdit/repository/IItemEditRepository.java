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
}
