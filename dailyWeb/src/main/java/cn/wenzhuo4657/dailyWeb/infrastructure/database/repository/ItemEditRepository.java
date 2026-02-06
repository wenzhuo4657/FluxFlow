package cn.wenzhuo4657.dailyWeb.infrastructure.database.repository;

import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.UpdateItemDto;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.repository.IItemEditRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsItemDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;
import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemEditRepository implements IItemEditRepository {


    @Autowired
    private DocsDao docsDao;

    @Autowired
    private DocsItemDao docsItemDao;

    @Override
    public List<DocsItem> getDocsItems(Long docs_Id) {
        boolean exist = docsDao.isExist(docs_Id);

        if (!exist){
            throw  new AppException(ResponseCode.RESOURCE_NOT_FOUND);
        }
        return docsItemDao.queryByDocsId(docs_Id);
    }

    @Override
    public boolean updateItem(Long index, String content) {
        if (StringUtils.isEmpty(content)){
            content="";
        }

        DocsItem item=new DocsItem();
        item.setIndex(index);
        item.setItemContent(content);
        docsItemDao.update(item);
        return true;
    }

    @Override
    public boolean isPermissions(Long docsId, Long userId) {
        boolean permissions = docsDao.isPermissions(docsId, userId);
        if (permissions){
            return true;
        }else {
            return false;
        }



    }

    @Override
    public Long queryTypeByDocsId(Long docsId) {
        return docsDao.queryTypeByDocsId(docsId);
    }

    @Override
    public boolean addItem(DocsItem docs) {
        docsItemDao.insert(docs);
        return true;
    }

    @Override
    public DocsItem selectDocsItem(Long id) {
        return  docsItemDao.queryByIndex(id);
    }

    @Override
    public void updateField(Long id, String field) {
        DocsItem item=new DocsItem();
        item.setIndex(id);
        item.setItemField(field);
        docsItemDao.update(item);
    }

    @Override
    public void deleteItem(Long index) {
        docsItemDao.delete(index);
    }

    @Override
    public List<DocsItem> getChildrenByParentId(Long parentId) {
        return docsItemDao.queryByParentId(parentId);
    }

    @Override
    public List<DocsItem> getTopLevelTasks(Long docsId) {
        List<DocsItem> allTasks = docsItemDao.queryByDocsId(docsId);
        return allTasks.stream()
                .filter(item -> {
                    String field = item.getItemField();
                    return field == null || !field.contains("parent_id:");
                })
                .toList();
    }

    @Override
    public List<DocsItem> getDocsItemsByDocsIds(List<Long> docsIds) {
        if (docsIds == null || docsIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return docsItemDao.queryByDocsIds(docsIds);
    }

    @Override
    public Docs getDocs(Long docsId) {
        return docsDao.queryByDocsId(docsId);
    }
}
