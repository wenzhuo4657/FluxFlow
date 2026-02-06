package cn.wenzhuo4657.dailyWeb.domain.agent.mcp.dto;

import java.io.Serializable;
import java.util.Objects;

public class TodayDataReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    public TodayDataReq() {
    }

    public TodayDataReq(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TodayDataReq that = (TodayDataReq) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

    @Override
    public String toString() {
        return "TodayDataReq{" +
                "userId=" + userId +
                '}';
    }
}
