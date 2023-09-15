package org.github.jaylondev.swift.boot.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jaylon 2023/8/20 21:49
 */
public class RedisTestConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        Configurations config = Configurations.getInstance();
        RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration();
        List<RedisNode> redisNodes = new ArrayList<>();
        String[] nodeUrlArray = config.getRedisNodes().split(",");
        RedisNode redisNode = null;
        for (String nodeUrl : nodeUrlArray) {
            String[] hostAndPort = nodeUrl.split(":");
            redisNode = new RedisNode(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
            redisNodes.add(redisNode);
        }
        redisClusterConfig.setClusterNodes(redisNodes);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfig);
        if (!StringUtils.isEmpty(config.getRedisPassWord())) {
            jedisConnectionFactory.setPassword(config.getRedisPassWord());
        }
        return jedisConnectionFactory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}
