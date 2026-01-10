package cn.wenzhuo4657.noifiterBot.app.tigger.http.dto;

/**
 * 发送Gmail邮件请求DTO
 */
public class SendGmailRequest {

    /**
     * 通信器索引
     */
    private Long communicatorIndex;

    /**
     * 邮件标题
     */
    private String title;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 附件URL（可选）
     */
    private String fileUrl;

    public SendGmailRequest() {}

    public SendGmailRequest(Long communicatorIndex, String title, String content, String fileUrl) {
        this.communicatorIndex = communicatorIndex;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
    }

    public Long getCommunicatorIndex() {
        return communicatorIndex;
    }

    public void setCommunicatorIndex(Long communicatorIndex) {
        this.communicatorIndex = communicatorIndex;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return "SendGmailRequest{" +
                "communicatorIndex=" + communicatorIndex +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }
}
