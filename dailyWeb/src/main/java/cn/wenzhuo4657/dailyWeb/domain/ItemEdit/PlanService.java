package cn.wenzhuo4657.dailyWeb.domain.ItemEdit;


import cn.wenzhuo4657.dailyWeb.domain.ItemEdit.model.dto.UpdateCheckListDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 规划任务集相关服务接口
 */
public interface PlanService {

    /**
     * 设置任务父级
     * @param taskId 任务ID
     * @param parentId 父任务ID（如果为null则取消父级）
     * @return 是否成功
     */
    boolean setParentTask(Long taskId, Long parentId);

    /**
     * 更新任务属性（状态、评分等）
     * @param taskId 任务ID
     * @param taskStatus 状态（1-完成，2-待做，3-销毁），可为null
     * @param score 评分（1-10），可为null
     * @return 是否成功
     */
    boolean updateTask(Long taskId, String taskStatus, String score);

    /**
     * 完成任务（会将所有子任务设置为销毁状态）
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean finishTask(Long taskId);


    /**
     * 关联基本日报
     *
     * 关联逻辑：
     * 在规划类型当中添加一个字段关联基本文档的id，并前端按钮处设置跳转按钮
     */
    boolean  connectionBase(Long taskId, Long userID);
}
