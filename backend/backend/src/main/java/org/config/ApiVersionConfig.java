package org.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {
    private static final String API_V1_PREFIX = "/api/v1";

    public static String versionedPath(String path) {
        return String.join(
                "",
                new String[]{API_V1_PREFIX, path}
        );
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
                API_V1_PREFIX,
                handlerType -> handlerType.isAnnotationPresent(RestController.class) ||
                        handlerType.isAnnotationPresent(Controller.class)
        );
    }
}
