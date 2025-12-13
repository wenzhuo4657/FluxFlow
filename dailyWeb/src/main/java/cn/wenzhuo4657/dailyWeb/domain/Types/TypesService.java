package cn.wenzhuo4657.dailyWeb.domain.Types;

import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.DocsDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.repository.ITypesRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TypesService  implements   ITypesService{

    @Autowired
    private ITypesRepository typesRepository;

    @Override
    public List<TypeDto> getAllTypes(Long userId) {
        return typesRepository.getAllByUserId(userId);
    }

//    将所有long整数返回的数据都修改为string类型，以避免前端的精度损失
    @Override
    public List<DocsDto> getContentNameIdById(Long typeId, Long userId) {
        List<Docs> list = typesRepository.getDocsIdByTypeId(userId, typeId);
        return list.stream()
                .map(

                                contentName -> {
                                    DocsDto dto=new DocsDto();
                                    dto.setId(contentName.getDocsId());
                                    dto.setName(contentName.getName());
                                    return dto;
                                }

                )
                .collect(Collectors.toList());
    }
}
