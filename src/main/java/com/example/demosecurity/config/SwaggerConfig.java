package com.example.demosecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.any())
                .build().pathMapping("/")
                .apiInfo(metaData());
    }
    private ApiInfo metaData(){

        Contact contact = new Contact("pooya-fils", "https://github.com/pooyafils","" +
                "mygithub");

        return new ApiInfo(
                "swagger features",
                "    example of swaager features with spring-boot",
                "1.0",
                "Terms of Service",
                contact,
                "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }
    //this configuration is also valid
    /*@Bean
public Docket api(){
    return new Docket(DocumentationType.SWAGGER_2)
            .select().paths(PathSelectors.ant("/**"))
            .apis(RequestHandlerSelectors.basePackage("com.example.swaggerhelloword"))
            .build().apiInfo(metaData());

}
*/
    @Configuration
    public class CorsConfiguration implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS");
        }
    }
}
