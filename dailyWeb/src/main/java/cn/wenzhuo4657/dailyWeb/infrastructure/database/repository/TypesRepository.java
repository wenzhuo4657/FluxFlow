package cn.wenzhuo4657.dailyWeb.infrastructure.database.repository;

import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.repository.ITypesRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsTypeDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.UserAuthDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Repository
public class TypesRepository implements ITypesRepository {

    private final static Logger log= org.slf4j.LoggerFactory.getLogger(TypesRepository.class);


    @Autowired
    private DocsTypeDao docsTypeDao;
    @Autowired
    private UserAuthDao userAuthDao;

    @Override
    public List<TypeDto> getAll(){
        List<DocsType> all = docsTypeDao.getAll();
        List<TypeDto> typeDtos = new ArrayList<>();
        for (DocsType docsType : all){
            TypeDto typeDto = new TypeDto();
            typeDto.setId(docsType.getTypeId());
            typeDto.setName(docsType.getName());
            typeDtos.add(typeDto);
        }
        return typeDtos;
    }

    @Override
    public List<TypeDto> getAllByUserId(Long userId) {
//        todo 这里需要调整为动态路由，根据用户身份来做权限控制
        List<DocsType> all = docsTypeDao.getAll();
        List<TypeDto> typeDtos = new ArrayList<>();
        for (DocsType docsType : all){
            TypeDto typeDto = new TypeDto();
            typeDto.setId(docsType.getTypeId());
            typeDto.setName(docsType.getName());
            typeDtos.add(typeDto);
        }
        return typeDtos;
    }

    @Override
    public List<Docs> getDocsIdByTypeId(Long userId, Long typeId) {
        List<Docs> list = userAuthDao.queryByUserIdAndtypeId(userId, typeId);
        return list;
    }
}
