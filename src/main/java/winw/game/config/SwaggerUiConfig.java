package winw.game.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

@EnableOpenApi
@Configuration
public class SwaggerUiConfig {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.OAS_30).enable(true)
				.securitySchemes(
						Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("JWT").build()))
				.securityContexts(Collections.singletonList(SecurityContext.builder()
						.securityReferences(Collections.singletonList(
								SecurityReference.builder().scopes(new AuthorizationScope[0]).reference("JWT")
										.build()))
						.operationSelector(o -> o.requestMappingPattern().matches("/.*")).build()))// 声明作用域
				.apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.lsg.video.controller")) // package
				.paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("LSG CONTENT-SECURITY API").version("1.0").description("API SPEC")
				.contact(new springfox.documentation.service.Contact("Yao ShengJie", null, "winwjava@sina.com"))
				.build();
	}

	@Bean
	UiConfiguration uiConfig() {
		return UiConfigurationBuilder.builder().deepLinking(true).displayOperationId(false).defaultModelsExpandDepth(1)
				.defaultModelExpandDepth(1).defaultModelRendering(ModelRendering.EXAMPLE).displayRequestDuration(false)
				.docExpansion(DocExpansion.NONE).filter(false).maxDisplayedTags(null)
				.operationsSorter(OperationsSorter.ALPHA).showExtensions(false).tagsSorter(TagsSorter.ALPHA)
				.validatorUrl(null).build();
	}

//    private List<SecurityScheme> securitySchemes() {
//        ApiKey apiKey = new ApiKey("Authorization", "token", In.HEADER.toValue());
//        return Collections.singletonList(apiKey);
//    }

}
