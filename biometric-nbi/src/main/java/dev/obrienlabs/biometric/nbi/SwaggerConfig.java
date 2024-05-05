package dev.obrienlabs.biometric.nbi;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

// http://127.0.0.1:8080/nbi/swagger-resources
// http://127.0.0.1:8080/nbi/v2/api-docs
// http://127.0.0.1:8080/nbi/swagger-ui.html#/api-controller/processUsingGET

@Configuration
@EnableSwagger2 // without spring boot
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
				
	}

}
