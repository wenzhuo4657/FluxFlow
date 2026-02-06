package cn.wenzhuo4657.dailyWeb.infrastructure.database.repository;

import cn.wenzhuo4657.dailyWeb.domain.Types.model.dto.TypeDto;
import cn.wenzhuo4657.dailyWeb.domain.Types.repository.ITypesRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsItemDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DocsTypeDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.UserAuthDao;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsType;
import cn.wenzhuo4657.dailyWeb.types.utils.SnowflakeUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Repository
public class TypesRepository implements ITypesRepository {

    private final static Logger log= org.slf4j.LoggerFactory.getLogger(TypesRepository.class);

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private DocsTypeDao docsTypeDao;
    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private DocsDao docsDao;

    @Autowired
    private DocsItemDao docsItemDao;

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
//        todo  暂时不让userAuth表生效
        List<Docs> list = docsDao.queryByUserIdAndtypeId(userId, typeId);
//        List<Docs> list = userAuthDao.queryByUserIdAndtypeId(userId, typeId);
        return list;
    }

    @Override
    public Long addDocs(Long typeId, Long userId, String docsName) {
        try {
            Docs docs = new Docs();
            docs.setName(docsName);
            docs.setTypeId(typeId);
            docs.setUserId(userId);
            docs.setDocsId(SnowflakeUtils.getSnowflakeId());
            docs.setCreateTime(simpleDateFormat.format(new Date()));
            docs.setUpdateTime(simpleDateFormat.format(new Date()));
            int insert = docsDao.insert(docs);
            if (insert != 1) {
                log.warn("添加文档发生超预期内容");
                return null;
            }
            return docs.getDocsId();
        }catch (Exception e){
            log.warn("添加文档发生异常",e);
            return null;
        }

    }

    @Override
    public boolean deleteDocs(Long docsId, Long userId) {
        try {
            if (docsDao.isPermissions(docsId, userId)) {
                int delete = docsDao.deleteByDocsID(docsId);
                if (delete != 1) {
                    log.warn("删除文档发生超预期内容");
                    return false;
                }
                return true;
            }
            log.warn("用户没有权限删除文档");
            return true;
        }catch (Exception e){
            log.warn("删除文档发生异常",e);
            return false;
        }
    }

    @Override
    public boolean deleteAllItemByDocsId(Long docsId) {
        return docsItemDao.deleteByDocsId(docsId);

    }
}
