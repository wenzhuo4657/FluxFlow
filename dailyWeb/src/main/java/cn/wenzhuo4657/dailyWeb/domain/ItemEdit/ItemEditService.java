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
    public boolean updateTaskStatus(Long taskId, String status) {
        try {
            DocsItem task = mdRepository.selectDocsItem(taskId);
            if (task == null) {
                throw new AppException(ResponseCode.RESOURCE_NOT_FOUND);
            }

            // 解析现有字段
            Map<String, String> fieldMap = DocsItemFiled.toMap(task.getItemField());

            // 检查任务是否已被冻结（如果父任务已完成，子任务不能修改）
            String currentStatus = fieldMap.get(DocsItemFiled.ItemFiled.task_status.getFiled());
            if ("3".equals(currentStatus)) {
                log.warn("任务已销毁，不能修改状态: taskId={}", taskId);
                throw new AppException(ResponseCode.INVALID_STATUS);
            }

            // 验证状态值
            if (!status.equals("1") && !status.equals("2") && !status.equals("3")) {
                log.warn("状态值无效: taskId={}, status={}", taskId, status);
                throw new AppException(ResponseCode.INVALID_STATUS);
            }

            // 更新状态
            fieldMap.put(DocsItemFiled.ItemFiled.task_status.getFiled(), status);
            String newField = DocsItemFiled.toFiled(fieldMap);
            mdRepository.updateField(taskId, newField);

            // 如果任务状态为完成，将所有子任务设置为销毁状态
            if ("1".equals(status)) {
                freezeChildrenTasks(taskId);
            }

            return true;
        } catch (Exception e) {
            log.error("更新任务状态失败", e);
            return false;
        }
    }

    @Override
    public boolean updateTaskScore(Long taskId, String score) {
        try {
            DocsItem task = mdRepository.selectDocsItem(taskId);
            if (task == null) {
                throw new AppException(ResponseCode.RESOURCE_NOT_FOUND);
            }

            // 解析现有字段
            Map<String, String> fieldMap = DocsItemFiled.toMap(task.getItemField());

            // 验证评分范围（1-5或1-10）
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

            // 更新评分
            fieldMap.put(DocsItemFiled.ItemFiled.score.getFiled(), score);
            String newField = DocsItemFiled.toFiled(fieldMap);
            mdRepository.updateField(taskId, newField);
            return true;
        } catch (Exception e) {
            log.error("更新任务评分失败", e);
            return false;
        }
    }

    @Override
    public boolean finishTask(Long taskId) {
        try {
            return updateTaskStatus(taskId, "1");
        } catch (Exception e) {
            log.error("完成任务失败", e);
            return false;
        }
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

}
