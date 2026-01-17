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
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 状态（1-完成，2-待做，3-销毁）
     * @return 是否成功
     */
    boolean updateTaskStatus(Long taskId, String status);

    /**
     * 更新任务评分
     * @param taskId 任务ID
     * @param score 评分
     * @return 是否成功
     */
    boolean updateTaskScore(Long taskId, String score);

    /**
     * 完成任务（会将所有子任务设置为销毁状态）
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean finishTask(Long taskId);
}
