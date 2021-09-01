package com.cookbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket cookBookApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.cookbook.controllers")).build()
                .apiInfo(restApiInfo());
    }

    private ApiInfo restApiInfo() {


        ApiInfo apiInfo = new ApiInfo("CookBook - Recipe Management Application API", "API Documentation for the Recipe Management Application",
                "v1.0", "TERMS OF USE", new Contact("Akahrsh M", "sample.domain.com/akahrsh", "akahrsh.m@tcs.com"), "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
        return apiInfo;
    }



}
