package com.heb.hebcodingchallenge.config;

import com.heb.hebcodingchallenge.utils.ImageUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            String imagesPath = Paths.get(ImageUtils.getApplicationLocationPath().toString(),"images/").toString().concat(System.getProperty("file.separator"));
            registry.addResourceHandler("/imgs/**").addResourceLocations("file:"+imagesPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}