package cn.wenzhuo4657.dailyWeb.types.Exception;

/**
 * 响应码枚举类，用于定义异常的代码和信息。
 * 1xx - 信息响应
 * 2xx - 成功响应
 * 3xx - 重定向
 * 4xx - 客户端错误
 * 5xx - 服务器错误
 */
public enum ResponseCode {


    SUCCESS(200, "操作成功"),

    FAILED(400, "操作失败"),
    MISSING_CREDENTIALS(40101, "客户端请求参数错误、格式不正确、必填字段缺失等"),

    RESOURCE_NOT_FOUND(404, "资源不存在"),

    NOT_PERMISSIONS(403,"文档存在，但用户权限不足"),

    NOT_LOGIN(401,"未登录、token无效，请重新登录"),

    ACCESS_TOKEN_INVALID(410, "ACCESS_TOKEN无效或已过期"),

    programmingError(500,"未知错误"),

    UnsupportedType(501,"不支持的类型"),

    DATABASE_VERSION_ERROR(510,"传入数据库版本过高"),

    INVALID_STATUS(405,"状态无效或操作不允许"),

    INVALID_PARAM(406,"参数无效");


    private String info;
    private Integer code;


    ResponseCode(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResponseCode{" +
                "info='" + info + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
