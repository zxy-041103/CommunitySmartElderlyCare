package com.community.elderly.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.server-path:}")
    private String redisServerPath;

    private Process redisProcess;

    @PostConstruct
    public void startRedisServer() {
        // 如果配置了Redis服务器路径，尝试自动启动
        if (redisServerPath != null && !redisServerPath.isEmpty()) {
            File redisServer = new File(redisServerPath);
            if (redisServer.exists()) {
                // 先检查Redis是否已经在运行
                if (!isRedisRunning()) {
                    try {
                        log.info("正在启动Redis服务器...");
                        ProcessBuilder pb = new ProcessBuilder(redisServerPath);
                        pb.inheritIO();
                        redisProcess = pb.start();
                        
                        // 等待Redis启动
                        int attempts = 0;
                        while (!isRedisRunning() && attempts < 10) {
                            Thread.sleep(500);
                            attempts++;
                        }
                        
                        if (isRedisRunning()) {
                            log.info("Redis服务器启动成功！");
                        } else {
                            log.warn("Redis服务器可能未成功启动，请手动检查");
                        }
                    } catch (Exception e) {
                        log.error("启动Redis服务器失败: {}", e.getMessage());
                    }
                } else {
                    log.info("Redis服务器已经在运行");
                }
            } else {
                log.warn("配置的Redis服务器路径不存在: {}", redisServerPath);
            }
        }
    }

    @PreDestroy
    public void stopRedisServer() {
        if (redisProcess != null && redisProcess.isAlive()) {
            log.info("正在关闭Redis服务器...");
            redisProcess.destroy();
        }
    }

    private boolean isRedisRunning() {
        try (Socket socket = new Socket(redisHost, redisPort)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        try {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(redisHost);
            config.setPort(redisPort);
            
            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
            connectionFactory.afterPropertiesSet();
            
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(connectionFactory);

            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        } catch (Exception e) {
            System.err.println("Redis连接失败，将禁用Redis缓存功能: " + e.getMessage());
            return null;
        }
    }
}
