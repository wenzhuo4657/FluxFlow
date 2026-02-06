package cn.wenzhuo4657.dailyWeb.domain.notifier.model.vo;

public enum NotifierType {
    TG_BOT("Telegram Bot", 0),
    GEMAIL("Gmail", 1);

    private String name;
    private Integer code;


    NotifierType(String name, Integer code) {
        this.name = name;
        this.code = code;
    }


    @Override
    public String toString() {
        return "NotifierType{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
