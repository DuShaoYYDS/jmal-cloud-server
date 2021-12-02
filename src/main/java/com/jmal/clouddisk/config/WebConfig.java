package com.jmal.clouddisk.config;

import com.jmal.clouddisk.interceptor.AuthInterceptor;
import com.jmal.clouddisk.interceptor.FileInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * WebConfig
 *
 * @author jmal
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    FileProperties fileProperties;

    @Autowired
    AuthInterceptor authInterceptor;

    @Autowired
    FileInterceptor fileInterceptor;

    // /**
    //  * 注册拦截器
    //  */
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
	// 	registry.addInterceptor(authInterceptor).addPathPatterns("/**").
	// 			excludePathPatterns("/login/**", "/public/**", "/articles/**", "/error/**", "/file/**" , "/files/**","/swagger-ui/**");
    //     registry.addInterceptor(fileInterceptor).addPathPatterns("/file/**").addPathPatterns("/files/**");
    // }
    //
    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //     registry.addResourceHandler("/file/**")
    //             .addResourceLocations("file:" + fileProperties.getRootDir() + File.separator)
    //             .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
    //     .resourceChain(true);
    //     log.info("静态资源目录:{}", fileProperties.getRootDir() + File.separator);
    // }
}
