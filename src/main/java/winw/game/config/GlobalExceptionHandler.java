package winw.game.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import lombok.extern.slf4j.Slf4j;
import winw.game.base.ApiResult;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
	@Autowired
	MultipartProperties multipartProperties;

	@ResponseBody
	@ExceptionHandler(value = MultipartException.class)
	public ApiResult<String> multipartExceptionHandler(HttpServletRequest r, MultipartException e) {
		log.error(e.getMessage(), e);
		ApiResult<String> result = ApiResult.exception();
		String message = "";
		if (e instanceof org.springframework.web.multipart.MaxUploadSizeExceededException) {
			message = "文件大小不得超过" + (multipartProperties.getMaxFileSize().toBytes() / 1024 / 1024) + "M";
		} else {
			message = "文件上传失败！";
		}
		result.setMessage(message);
		return result;
	}

	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public ApiResult<String> otherExceptionHandler(HttpServletRequest r, Exception e) {
		log.error(e.getMessage(), e);
		ApiResult<String> result = ApiResult.exception();
		if (e instanceof MethodArgumentNotValidException) {// 验证处理
			BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
			FieldError error = bindingResult.getFieldError();
			String field = error.getField();
			String code = error.getDefaultMessage();
			String message = String.format("%s:%s", field, code);
			result.setMessage(message);
		} else {
			result.setMessage(e.getMessage());
		}
		return result;
	}

}
