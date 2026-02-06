package cn.wenzhuo4657.dailyWeb.domain.notifier.model.dto;

import java.util.Objects;

public class NotifierBody {

    private String title;
    private String content;

    public NotifierBody(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public NotifierBody() {
    }

    @Override
    public String toString() {
        return "NotifierBody{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotifierBody that = (NotifierBody) o;
        return Objects.equals(title, that.title) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
