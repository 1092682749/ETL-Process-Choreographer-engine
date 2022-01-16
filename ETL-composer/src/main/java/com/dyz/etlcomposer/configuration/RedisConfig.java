package com.dyz.etlcomposer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {
    /**
     * redis.standalone.database=0
     * redis.stndalone.host-name=10.100.42.151
     * redis.standalone.password=oas
     * redis.standalone.port=9999
     * redisStandaloneConfiguration.setPort(port);
     *         redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
     *         redisStandaloneConfiguration.setDatabase(database);
     *         redisStandaloneConfiguration.setHostName(hostName);
     * @return
     */

    private int database;
    private int port;
    private String hostName;
    private String password;

    @Bean("redisStandaloneConfiguration")
    @ConfigurationProperties(prefix = "redis.standalone")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        return redisStandaloneConfiguration;
    }


//    @Bean
//    public Jedis jedis() {
//        Jedis jedis = new Jedis();
//
//        return
//    }


    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
