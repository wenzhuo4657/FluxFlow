package cn.wenzhuo4657.noifiterBot.app.domain.notifier.service.strategy;

public class NotifierResult {

    private boolean isSuccess;//通知是否成功


    public NotifierResult() {
    }

    public NotifierResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public   static  NotifierResult ok(){
        return new NotifierResult(true);
    }


    public static  NotifierResult fail(){
        return new NotifierResult(false);
    }

    @Override
    public String toString() {
        return "通知结果：NotifierResult{" +
                "isSuccess=" + isSuccess +
                '}';
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
