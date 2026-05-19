package com.example.music.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * WebMvc 资源映射配置。
 * 用于把项目根目录下的本地文件夹映射为浏览器可直接访问的静态资源路径。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 本地静态资源根目录。
     * 这里默认指向 项目根目录/music-player-files。
     */
    @Value("${file.storage.root-path}")
    private String rootPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path musicPath = Paths.get(rootPath, "music").toAbsolutePath().normalize();
        Path coverPath = Paths.get(rootPath, "cover").toAbsolutePath().normalize();

        createDirectoryIfMissing(musicPath);
        createDirectoryIfMissing(coverPath);

        // 访问 /music/** 时，实际读取本地 music 目录中的文件。
        registry.addResourceHandler("/music/**")
                .addResourceLocations("file:" + toResourceLocation(musicPath));

        // 访问 /cover/** 时，实际读取本地 cover 目录中的文件。
        registry.addResourceHandler("/cover/**")
                .addResourceLocations("file:" + toResourceLocation(coverPath));
    }

    private void createDirectoryIfMissing(Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);
        } catch (Exception exception) {
            throw new RuntimeException("无法创建本地静态资源目录: " + directoryPath, exception);
        }
    }

    /**
     * ResourceHandler 需要目录路径以 / 结尾，避免拼接文件名时出错。
     */
    private String toResourceLocation(Path directoryPath) {
        return directoryPath.toString().replace("\\", "/") + "/";
    }
}
