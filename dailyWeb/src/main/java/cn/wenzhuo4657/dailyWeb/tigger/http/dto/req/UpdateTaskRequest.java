package cn.wenzhuo4657.dailyWeb.tigger.http.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 更新任务请求
 */
public class UpdateTaskRequest {

    @NotNull
    @Min(value = 0)
    private Long taskId;

    /**
     * 任务状态：1-完成，2-待做，3-销毁
     */
    private String taskStatus;

    /**
     * 任务评分：1-10
     */
    private String score;

    public UpdateTaskRequest() {
    }

    public UpdateTaskRequest(Long taskId, String taskStatus, String score) {
        this.taskId = taskId;
        this.taskStatus = taskStatus;
        this.score = score;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "UpdateTaskRequest{" +
                "taskId=" + taskId +
                ", taskStatus='" + taskStatus + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
