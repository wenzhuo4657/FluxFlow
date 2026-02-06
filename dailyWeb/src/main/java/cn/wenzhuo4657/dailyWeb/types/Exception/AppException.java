package cn.wenzhuo4657.dailyWeb.types.Exception;

/**
 * 用于前后端异常交流：tigger层对外抛出的异常只能是它
 */
public class AppException extends RuntimeException{
    private static final long serialVersionUID = 5317680961212299217L;

    /** 异常码 */
    private Integer code;

    /** 异常信息 */
    private String info;


    public AppException(ResponseCode responseCode){
        this.code = responseCode.getCode();
        this.info = responseCode.getInfo();
    }
    public AppException(ResponseCode responseCode, String info){
        this.code = responseCode.getCode();
        this.info = responseCode.getInfo() + info;
    }


    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "AppException{" +
                "code='" + code + '\'' +
                ", info='" + info + '\'' +
                '}';
    }

    @Override
    public String getMessage() {
        return toString();
    }


}
