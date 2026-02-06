package cn.wenzhuo4657.dailyWeb.domain.Types;

import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.DocsDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;

import java.util.List;

public interface ITypesService {

    /**
     * 获取所有文档类型
     *
     */
    List<TypeDto> getAllTypes(Long userId);

    /**
     * 根据类型id获取相应用户的文档id
     */
    List<DocsDto> getContentNameIdById(Long typeId, Long userId);


    boolean addDocs(Long typeId, Long userId,String docsName);

    boolean deleteDocs(Long DocsId, Long userId);
}
