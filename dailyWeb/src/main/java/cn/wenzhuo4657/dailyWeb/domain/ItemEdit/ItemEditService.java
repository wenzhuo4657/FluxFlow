package cn.wenzhuo4657.dailyWeb.domain.ItemEdit;


import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.*;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemFiled;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.vo.DocsItemType;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.repository.IItemEditRepository;
import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.strategy.TypeStrategy;
import cn.wenzhuo4657.dailyWeb.domain.Types.ITypesService;
import cn.wenzhuo4657.dailyWeb.domain.Types.repository.ITypesRepository;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.Docs;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsItem;
import cn.wenzhuo4657.dailyWeb.infrastructure.database.entity.DocsType;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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


    @Autowired
    private ITypesRepository typesRepository;



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
    public List<ItemDto> getItem(Long docsId) {
        try {
            List<DocsItem> docsItems = mdRepository.getDocsItems(docsId);
            Long type=mdRepository.queryTypeByDocsId(docsId);
            return  strategy.apply(type, docsItems);
        }catch (ClassNotFoundException e){
            throw  new AppException(ResponseCode.MISSING_CREDENTIALS);
        }
    }

    @Override
    public boolean deleteItem(long index) {
        mdRepository.deleteItem(index);
        return true;
    }

    // ==================== PlanService 实现 ====================

    @Override
    public boolean setParentTask(Long taskId, Long parentId) {
        try {
            DocsItem task = mdRepository.selectDocsItem(taskId);
            if (task == null) {
                throw new AppException(ResponseCode.RESOURCE_NOT_FOUND);
            }

            // 解析现有字段
            Map<String, String> fieldMap = DocsItemFiled.toMap(task.getItemField());

            // 设置或移除父任务ID
            if (parentId == null) {
                fieldMap.remove(DocsItemFiled.ItemFiled.parent_id.getFiled());
            } else {
                // 验证父任务是否存在
                DocsItem parentTask = mdRepository.selectDocsItem(parentId);
                if (parentTask == null) {
                    throw new AppException(ResponseCode.RESOURCE_NOT_FOUND);
                }
                fieldMap.put(DocsItemFiled.ItemFiled.parent_id.getFiled(), String.valueOf(parentId));
            }

            // 更新字段
            String newField = DocsItemFiled.toFiled(fieldMap);
            mdRepository.updateField(taskId, newField);
            return true;
        } catch (Exception e) {
            log.error("设置任务父级失败", e);
            return false;
        }
    }

    @Override
    public boolean updateTask(Long taskId, String taskStatus, String score) {
        try {
            DocsItem task = mdRepository.selectDocsItem(taskId);
            if (task == null) {
                throw new AppException(ResponseCode.RESOURCE_NOT_FOUND);
            }

            // 解析现有字段
            Map<String, String> existingFieldMap = DocsItemFiled.toMap(task.getItemField());

            // 检查任务是否已被冻结（如果父任务已完成，子任务不能修改）
            String currentStatus = existingFieldMap.get(DocsItemFiled.ItemFiled.task_status.getFiled());
            if ("3".equals(currentStatus)) {
                log.warn("任务已销毁，不能修改: taskId={}", taskId);
                throw new AppException(ResponseCode.INVALID_STATUS);
            }

            // 处理taskStatus字段
            if (taskStatus != null) {
                // 验证状态值
                if (!taskStatus.equals("1") && !taskStatus.equals("2") && !taskStatus.equals("3")) {
                    log.warn("状态值无效: taskId={}, taskStatus={}", taskId, taskStatus);
                    throw new AppException(ResponseCode.INVALID_STATUS);
                }
                existingFieldMap.put(DocsItemFiled.ItemFiled.task_status.getFiled(), taskStatus);
            }

            // 处理score字段
            if (score != null) {
                // 验证评分范围（1-10）
                try {
                    int scoreValue = Integer.parseInt(score);
                    if (scoreValue < 1 || scoreValue > 10) {
                        log.warn("评分范围无效: taskId={}, score={}", taskId, score);
                        throw new AppException(ResponseCode.INVALID_PARAM);
                    }
                } catch (NumberFormatException e) {
                    log.warn("评分格式错误: taskId={}, score={}", taskId, score);
                    throw new AppException(ResponseCode.INVALID_PARAM);
                }
                existingFieldMap.put(DocsItemFiled.ItemFiled.score.getFiled(), score);
            }

            // 更新字段
            String newField = DocsItemFiled.toFiled(existingFieldMap);
            mdRepository.updateField(taskId, newField);

            // 如果任务状态为完成，将所有子任务设置为销毁状态
            String newStatus = existingFieldMap.get(DocsItemFiled.ItemFiled.task_status.getFiled());
            if ("1".equals(newStatus)) {
                freezeChildrenTasks(taskId);
            }

            return true;
        } catch (Exception e) {
            log.error("更新任务失败", e);
            return false;
        }
    }

    @Override
    public boolean finishTask(Long taskId) {
        return updateTask(taskId, "1", null);
    }

    /**
     * 冻结子任务（将所有子任务设置为销毁状态）
     * @param parentId 父任务ID
     */
    private void freezeChildrenTasks(Long parentId) {
        List<DocsItem> children = mdRepository.getChildrenByParentId(parentId);
        for (DocsItem child : children) {
            try {
                Map<String, String> fieldMap = DocsItemFiled.toMap(child.getItemField());
                fieldMap.put(DocsItemFiled.ItemFiled.task_status.getFiled(), "3"); // 销毁状态
                String newField = DocsItemFiled.toFiled(fieldMap);
                mdRepository.updateField(child.getIndex(), newField);

                // 递归冻结子任务的子任务
                freezeChildrenTasks(child.getIndex());
            } catch (Exception e) {
                log.error("冻结子任务失败: {}", child.getIndex(), e);
            }
        }
    }
    private final ConcurrentHashMap<Long, Object> lockMap = new ConcurrentHashMap<>();

    @Override
    public boolean connectionBase(Long taskId,Long userId) {

        Object lock = lockMap.computeIfAbsent(taskId, k -> new Object());

        synchronized (lock){
            DocsItem task = mdRepository.selectDocsItem(taskId);
            if (task == null){
                throw new AppException(ResponseCode.MISSING_CREDENTIALS);
            }
            Map<String, String> fieldMap;
            try {
                fieldMap = DocsItemFiled.toMap(task.getItemField());
                boolean b = fieldMap.containsKey(DocsItemFiled.ItemFiled.connection.getFiled());
                if (b){
                    return true;
                }

            }catch (ClassNotFoundException e){
                throw new AppException(ResponseCode.programmingError,e.getMessage());
            }

//        1,创建新的基本文档，
//            todo 基本文档和plan文档的关联有问题， 无法从基本文档找到plan文档，原因是docs类型没有属性，只有ite当中才有属性
            Long newDocsId = typesRepository.addDocs(Long.valueOf(DocsItemType.ItemType.dailyBase.getCode()), userId, fieldMap.get(DocsItemFiled.ItemFiled.title.getFiled()));

            if (newDocsId == null) {
                throw new AppException(ResponseCode.programmingError);
            }

//        2,关联
            try {
                Map<String, String> map = DocsItemFiled.toMap(task.getItemField());
                map.put(DocsItemFiled.ItemFiled.connection.getFiled(), String.valueOf(newDocsId));
                String filed = DocsItemFiled.toFiled(map);
                task.setItemField(filed);
                mdRepository.updateField(taskId, filed);
            } catch (ClassNotFoundException e) {
                throw new AppException(ResponseCode.programmingError,e.getMessage());
            }
            return true;
        }



    }

    @Override
    public PreViewDto queryUserByToday(Long userId) {
        // 1. 获取今日日期字符串
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        log.debug("查询用户今日日报, userId={}, today={}", userId, today);

        // 2. 获取并处理基本日报 (dailyBase, code=0)
        List<Docs> baseDocs = typesRepository.getDocsIdByTypeId(userId, DocsItemType.ItemType.dailyBase.getCode());
        List<Long> baseDocIds = baseDocs.stream().map(Docs::getDocsId).toList();
        List<DocsItem> baseItems = mdRepository.getDocsItemsByDocsIds(baseDocIds);
        List<TodayItemDto> baseDtos = filterTodayBaseItems(baseItems, today);

        // 3. 获取并处理计划任务 (Plan_I, code=1)
        List<Docs> planDocs = typesRepository.getDocsIdByTypeId(userId, DocsItemType.ItemType.Plan_I.getCode());
        List<Long> planDocIds = planDocs.stream().map(Docs::getDocsId).toList();
        List<DocsItem> planItems = mdRepository.getDocsItemsByDocsIds(planDocIds);
        List<ItemDto> planDtos = filterValidPlanItems(planItems, today);

        log.debug("用户今日日报查询完成, baseItemCount={}, planItemCount={}", baseDtos.size(), planDtos.size());
        return new PreViewDto(baseDtos, planDtos);
    }

    /**
     * 过滤当日的基本日报项
     * @param items 原始文档项列表
     * @param today 今日日期 (yyyy-MM-dd)
     * @return 当日的日报项DTO列表
     */
    private List<TodayItemDto> filterTodayBaseItems(List<DocsItem> items, String today) {
        return items.stream()
                .filter(item -> {
                    try {
                        Map<String, String> fieldMap = DocsItemFiled.toMap(item.getItemField());
                        String data = fieldMap.get(DocsItemFiled.ItemFiled.data.getFiled());
                        return today.equals(data);
                    } catch (ClassNotFoundException e) {
                        log.warn("解析文档项字段失败, index={}", item.getIndex(), e);
                        return false;
                    }
                })
                .map(item -> {

                        TodayItemDto todayItemDto = new TodayItemDto();
                        todayItemDto.setDocsId(String.valueOf(item.getDocsId()));
                        todayItemDto.setIndex(item.getIndex());
                        todayItemDto.setContent(item.getItemContent());
                        Docs docs=mdRepository.getDocs(item.getDocsId());
                        todayItemDto.setName(docs.getName());
                        return todayItemDto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 过滤有效期内的计划任务
     * @param items 原始文档项列表
     * @param today 今日日期 (yyyy-MM-dd)
     * @return 有效期内的计划任务DTO列表
     */
    private List<ItemDto> filterValidPlanItems(List<DocsItem> items, String today) {
        LocalDateTime todayDate = LocalDateTime.parse(today + "T00:00:00");

        return items.stream()
                .filter(item -> {
                    try {
                        Map<String, String> fieldMap = DocsItemFiled.toMap(item.getItemField());

                        // 过滤状态：只保留待做状态 (task_status=2)
                        String taskStatus = fieldMap.get(DocsItemFiled.ItemFiled.task_status.getFiled());
                        if (!"2".equals(taskStatus)) {
                            return false;
                        }

                        // 过滤有效期
                        String dataStart = fieldMap.get(DocsItemFiled.ItemFiled.data_start.getFiled());
                        String dataEnd = fieldMap.get(DocsItemFiled.ItemFiled.data_end.getFiled());

                        // data_start 不能为空
                        if (dataStart == null || "null".equalsIgnoreCase(dataStart)) {
                            return false;
                        }

                        LocalDateTime startDate = LocalDateTime.parse(dataStart + "T00:00:00");

                        // 今日 >= 开始日期
                        if (todayDate.isBefore(startDate)) {
                            return false;
                        }

                        // 今日 <= 结束日期 (如果设置了结束日期)
                        if (dataEnd != null && !"null".equalsIgnoreCase(dataEnd)) {
                            LocalDateTime endDate = LocalDateTime.parse(dataEnd + "T00:00:00");
                            return !todayDate.isAfter(endDate);
                        }

                        // 没有设置结束日期，表示永久有效
                        return true;

                    } catch (ClassNotFoundException | java.time.DateTimeException e) {
                        log.warn("解析计划任务字段失败, index={}", item.getIndex(), e);
                        return false;
                    }
                })
                .map(item -> {
                    try {
                        return strategy.apply(DocsItemType.ItemType.Plan_I.getCode(), List.of(item)).get(0);
                    } catch (ClassNotFoundException e) {
                        log.error("转换计划任务项失败", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ItemDto::getIndex))
                .toList();
    }

    @Override
    public boolean tailAdd(Long docsId, String content) throws ClassNotFoundException {
        content="\n"+content;
        List<DocsItem> docsItems = mdRepository.getDocsItems(docsId);
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<DocsItem> collect = docsItems.stream()
                .filter(item -> {
                    try {
                        Map<String, String> fieldMap = DocsItemFiled.toMap(item.getItemField());
                        String data = fieldMap.get(DocsItemFiled.ItemFiled.data.getFiled());
                        return today.equals(data);
                    } catch (ClassNotFoundException e) {
                        log.warn("解析文档项字段失败, index={}", item.getIndex(), e);
                        return false;
                    }
                }).collect(Collectors.toList());
        if (collect.size()==1){
            DocsItem item = collect.get(0);
            mdRepository.updateItem(
                    item.getIndex(), item.getItemContent()+content
            );
        }else {
//            创建一个新文档
            String filed = strategy.toFiled(DocsItemType.ItemType.dailyBase.getCode());
            DocsItem item=new DocsItem();
            item.setDocsId(docsId);
            item.setItemField(filed);
            item.setItemContent(content);
            item.setIndex(SnowflakeUtils.getSnowflakeId());
            return mdRepository.addItem(item);
        }


        return true;
    }
}
