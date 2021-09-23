package winw.game.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Security配置，使用JWT的Token验证。
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

	@Bean
	public RemoteIpFilter remoteIpFilter() {
		return new RemoteIpFilter();// 用于获取真实IP
	}

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/archive/**").addResourceLocations("file:");
//    }

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new WebLogRecoder()).addPathPatterns("/**");
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// 禁用 CSRF
				.csrf().disable()

				// 授权异常
				.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint())
				.accessDeniedHandler(new JwtAccessDeniedHandler())

				// 防止iframe 造成跨域
				.and().headers().frameOptions().disable()

				// 不创建会话
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and().authorizeRequests()

				// 放行静态资源
				.antMatchers(HttpMethod.GET, "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/webSocket/**")
				.permitAll()

				// 放行swagger
				.antMatchers("/swagger-ui.html").permitAll().antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/webjars/**").permitAll().antMatchers("/*/api-docs").permitAll()

				// 放行 H2数据库
				.antMatchers("/h2/**").permitAll()
				// 放行监控
				.antMatchers("/actuator/**").permitAll()

				// 放行文件访问
				.antMatchers("/archive/**").permitAll()
				// FIXME 暂时放行文件上传，以便测试
				.antMatchers("/file/**").permitAll()
				// 放行OPTIONS请求
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

				.antMatchers("/**").permitAll()// 允许匿名及登录用户访问
				// 所有请求都需要认证
				.anyRequest().authenticated();

		// 禁用缓存
		httpSecurity.headers().cacheControl();

		// 添加JWT filter
		httpSecurity.apply(new TokenConfigurer());

	}

	public class TokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

		@Override
		public void configure(HttpSecurity http) {
//            http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		}
	}

	public class JwtAccessDeniedHandler implements AccessDeniedHandler {
		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
				AccessDeniedException accessDeniedException) throws IOException {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
		}
	}

	public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					authException == null ? "Unauthorized" : authException.getMessage());
		}
	}

}
