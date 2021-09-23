package winw.game.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApiResult<T> {

    @ApiModelProperty(value = "数据对象")
    private T data;

    @ApiModelProperty(value = "代码：0，成功；-1，参数非法；-2，系统异常；-3，服务调用失败；")
    private int code;

    @ApiModelProperty(value = "错误信息")
    private String message;

    @ApiModelProperty(value = "时间戳，毫秒")
    private Long time;

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public ApiResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<T>(0, "成功", data);
    }

    /**
     * 只返回message，data 为null
     * 
     * @param message
     * @return
     */
    public static ApiResult<String> success(String message) {
        return new ApiResult<String>(0, message, null);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<T>(0, message, data);
    }

    /**
     * 只返回message，data 为null
     * 
     * @param <T>
     * @param message
     * @return
     */
    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<T>(-1, message, null);
    }

    public static <T> ApiResult<T> error(String message, T data) {
        return new ApiResult<T>(-1, message, data);
    }

    public static ApiResult<String> exception() {
        return new ApiResult<String>(-2, "系统异常，请稍后重试！");
    }

}
