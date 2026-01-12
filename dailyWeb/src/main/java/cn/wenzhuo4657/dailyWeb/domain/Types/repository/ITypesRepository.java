package cn.wenzhuo4657.dailyWeb.domain.Types.repository;

import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;

import java.util.List;

public interface ITypesRepository {

    List<TypeDto> getAll();


    List<TypeDto> getAllByUserId(Long userId);

    List<Docs> getDocsIdByTypeId(Long userId, Long typeId);

    boolean addDocs(Long typeId, Long userId, String docsName);

    boolean deleteDocs(Long docsId, Long userId);
}
