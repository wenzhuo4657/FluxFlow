package cn.wenzhuo4657.dailyWeb.infrastructure.database.repository;

import cn.wenzhuo4657.dailyWeb.domain.system.repository.ISystemRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.dao.DatabaseversionDao;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SystemRepository implements ISystemRepository {

    @Autowired
    private DatabaseversionDao databaseversionDao;


    @Override
    public String getDatabaseVersion() {
        return databaseversionDao.getVersion();
    }
}
