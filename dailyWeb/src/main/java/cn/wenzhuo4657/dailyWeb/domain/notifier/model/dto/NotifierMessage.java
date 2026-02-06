package cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto;

import cn.wenzhuo4657.dailyWeb.domain.notifier.model.vo.NotifierType;

import java.util.Objects;

public class NotifierMessage {
    private Long userId;
    private Integer notifierType;
    private NotifierBody notifierBody;

    public NotifierMessage(Long userId, Integer notifierType, NotifierBody notifierBody) {
        this.userId = userId;
        this.notifierType = notifierType;
        this.notifierBody = notifierBody;
    }

    public NotifierMessage() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotifierMessage that = (NotifierMessage) o;
        return Objects.equals(userId, that.userId) && notifierType == that.notifierType && Objects.equals(notifierBody, that.notifierBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, notifierType, notifierBody);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getNotifierType() {
        return notifierType;
    }

    public void setNotifierType(Integer notifierType) {
        this.notifierType = notifierType;
    }

    public NotifierBody getNotifierBody() {
        return notifierBody;
    }

    public void setNotifierBody(NotifierBody notifierBody) {
        this.notifierBody = notifierBody;
    }
}
