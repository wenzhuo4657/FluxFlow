package cn.wenzhuo4657.dailyWeb.domain.ItemEdit;


import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.*;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemFiled;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemType;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.repository.IItemEditRepository;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.TypeStrategy;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;
import cn.wenzhuo4657.dailyWeb.types.Exception.AppException;
import cn.wenzhuo4657.dailyWeb.types.Exception.ResponseCode;
import cn.wenzhuo4657.dailyWeb.types.utils.SnowflakeUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 默认文档行为实现
 */
@Service
public  class ItemEditService implements baseService,PlanService {
    Logger log = org.slf4j.LoggerFactory.getLogger(ItemEditService.class);

    @Autowired
    protected IItemEditRepository mdRepository;



    @Autowired
    private TypeStrategy strategy;



    @Override
    public boolean insertItem(InsertItemDto dto,Long userId) {

        try {

            if (!mdRepository.isPermissions(dto.getDocsId(),userId)){
                throw  new AppException(ResponseCode.NOT_PERMISSIONS);
            }
            String filed = strategy.toFiled(dto.getType());
            DocsItem item=new DocsItem();
            item.setDocsId(dto.getDocsId());
            item.setItemField(filed);
            item.setItemContent("");
            item.setIndex(SnowflakeUtils.getSnowflakeId());
            return mdRepository.addItem(item);

        } catch (ClassNotFoundException e) {
            throw new AppException(ResponseCode.MISSING_CREDENTIALS);
        }


    }

    @Override
    public boolean insertItem_II(InsertItemDto dto, Long userId, Map<String, String> fieldMap) {


        try {
            String filed = strategy.toFiled(dto.getType(),fieldMap);
            DocsItem item=new DocsItem();
            item.setDocsId(dto.getDocsId());
            item.setItemField(filed);
            item.setItemContent("");
            item.setIndex(SnowflakeUtils.getSnowflakeId());
            return mdRepository.addItem(item);
        }catch (ClassNotFoundException e){
            return  false;
        }

    }

    @Override
    public boolean updateItem(UpdateItemDto dto) {
//        todo 部分类型可以用，部分类型不能用，
            return mdRepository.updateItem(dto.getIndex(),dto.getContent());
    }

    @Override
    public List<ItemDto> getItem(QueryItemDto dto) {

        try {
            List<DocsItem> docsItems = mdRepository.getDocsItems(dto.getDocsId());
            return  strategy.apply(dto.getType(), docsItems);
        }catch (ClassNotFoundException e){
            throw  new AppException(ResponseCode.MISSING_CREDENTIALS);
        }


    }

    @Override
    public boolean deleteItem(long index) {
        mdRepository.deleteItem(index);
        return true;
    }


    @Override
    public boolean CheckList(UpdateCheckListDto params) {
        DocsItem docsItem = mdRepository.selectDocsItem(params.getIndex());
        if (docsItem==null){
            log.error("未找到该条目");
            return false;

        }
        try {
            Map<String, String> map = DocsItemFiled.toMap(docsItem.getItemField());
            map.put(DocsItemFiled.ItemFiled.title.getFiled(), params.getTitle());
            String filed = DocsItemFiled.toFiled(map);
            mdRepository.updateField(docsItem.getIndex(),filed );
        }catch (ClassNotFoundException e){
            log.error("不支持的属性");
            return false;
        }
        return true;
    }

    @Override
    public boolean CheckListFinish(Long id) {
        DocsItem docsItem = mdRepository.selectDocsItem(id);
        if (docsItem==null){
            log.error("未找到该条目");
            return false;
        }
        try {
            Map<String, String> map = DocsItemFiled.toMap(docsItem.getItemField());
            map.put(DocsItemFiled.ItemFiled.status.getFiled(), "true");
            String filed = DocsItemFiled.toFiled(map);
            mdRepository.updateField(docsItem.getDocsId(),filed);
        }catch (ClassNotFoundException e){
            log.error("不支持的属性");
            return false;
        }
        return true;
    }


    @Override
    public boolean CheckListToday(Long id) {
        DocsItem docsItem = mdRepository.selectDocsItem(id);
        if (docsItem==null){
            log.error("未找到该条目");
            return false;
        }
        try {
            Map<String, String> map = DocsItemFiled.toMap(docsItem.getItemField());

            if (map.containsKey(DocsItemFiled.ItemFiled.time_point.getFiled())){
//                如果存在时间点属性，我们认为这个条目是PlanI
                map.put(DocsItemFiled.ItemFiled.status.getFiled(), "true");
                String filed = DocsItemFiled.toFiled(map);
                mdRepository.updateField(docsItem.getDocsId(),filed);
            }else {
//                如果不存在，则是Plan_II,在content添加一个数据     [当前时间戳]
                String itemContent = docsItem.getItemContent();
                //以\n分割item内容，填入一个set保证不重复
                Set<String> set = new HashSet<>();
                String[] itemContentArray = itemContent.split("\n");
                for (String s : itemContentArray) {
                    if (!s.isEmpty()){
                        set.add(s);
                    }
                }
                //添加时间戳，时间精度为日级
                String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); //获取当前日期
                set.add(time);
                //将set转换为字符串，以\n分割
                String content = String.join("\n", set);
                docsItem.setItemContent(content);
                mdRepository.updateItem(docsItem.getDocsId(),docsItem.getItemContent());

            }


        }catch (ClassNotFoundException e){
            log.error("不支持的属性");
            return false;
        }
        return true;

    }
}
