package winw.game.config;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class WebLogRecoder implements RequestBodyAdvice, HandlerInterceptor, ResponseBodyAdvice<Object> {

	private static final NamedThreadLocal<Long> requestTimeThreadLocal = new NamedThreadLocal<Long>(
			"trackStartTimeThreadLocal");
	private static final NamedThreadLocal<String> requestBodyThreadLocal = new NamedThreadLocal<String>(
			"requestBodyThreadLocal");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		requestTimeThreadLocal.set(System.currentTimeMillis());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
//		String query = request.getQueryString();
//		String body = requestBodyThreadLocal.get();
//		log.info("{} {} {} {} {} {}ms", request.getRemoteAddr(), request.getMethod(), request.getRequestURI(),
//				query == null ? "-" : query, body == null ? "-" : body,
//				(System.currentTimeMillis() - requestTimeThreadLocal.get()));
//		requestTimeThreadLocal.remove();
//		requestBodyThreadLocal.remove();
	}

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return methodParameter.getParameterAnnotation(RequestBody.class) != null;// 记录所有 @RequestBody
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter,
			Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
		return new MappingJacksonInputMessage(httpInputMessage.getBody(), httpInputMessage.getHeaders());
	}

	@Override
	public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type,
			Class<? extends HttpMessageConverter<?>> aClass) {
		requestBodyThreadLocal.set(JSON.toJSONString(o));
		return o;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return null;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		if (log.isDebugEnabled()) {
			log.debug("{}", JSON.toJSONString(body));
		}
		return body;
	}
}
