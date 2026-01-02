package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy;

import java.io.File;
import java.io.InputStream;

public class NotifierMessage {


    private String title;

    private String content;

    private String file1; // 附件（可选,外部url）

    private File file2;//附件（可选，上传文件）




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



    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }


    public File getFile2() {
        return file2;
    }

    public void setFile2(File file2) {
        this.file2 = file2;
    }
}
