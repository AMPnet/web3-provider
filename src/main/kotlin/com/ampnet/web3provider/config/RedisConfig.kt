package com.ampnet.web3provider.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableConfigurationProperties
class RedisConfig {

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val connectionFactory = JedisConnectionFactory(RedisStandaloneConfiguration())
        connectionFactory.afterPropertiesSet()
        return connectionFactory
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.setConnectionFactory(jedisConnectionFactory())
        template.keySerializer = StringRedisSerializer()
        template.afterPropertiesSet()
        return template
    }
}