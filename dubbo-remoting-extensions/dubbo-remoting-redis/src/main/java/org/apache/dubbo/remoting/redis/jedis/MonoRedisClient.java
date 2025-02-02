/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.redis.jedis;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.redis.RedisClient;
import org.apache.dubbo.remoting.redis.support.AbstractRedisClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.Set;

import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_TIMEOUT;
import static org.apache.dubbo.common.constants.CommonConstants.TIMEOUT_KEY;

public class MonoRedisClient extends AbstractRedisClient implements RedisClient {
    private static final Logger logger = LoggerFactory.getLogger(MonoRedisClient.class);

    private static final String START_CURSOR = "0";

    private JedisPool jedisPool;

    public MonoRedisClient(URL url) {
        super(url);
        jedisPool = new JedisPool(getConfig(), url.getHost(), url.getPort(),
                url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), url.getPassword());
    }

    @Override
    public Long hset(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.hset(key, field, value);
        jedis.close();
        return result;
    }

    @Override
    public Long publish(String channel, String message) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.publish(channel, message);
        jedis.close();
        return result;
    }

    @Override
    public boolean isConnected() {
        Jedis jedis = jedisPool.getResource();
        boolean connected = jedis.isConnected();
        jedis.close();
        return connected;
    }

    @Override
    public void destroy() {
        jedisPool.close();
    }

    @Override
    public Long hdel(String key, String... fields) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.hdel(key, fields);
        jedis.close();
        return result;
    }

    @Override
    public Set<String> scan(String pattern) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = super.scan(jedis, pattern);
        jedis.close();
        return result;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> result = jedis.hgetAll(key);
        jedis.close();
        return result;
    }

    @Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
        Jedis jedis = jedisPool.getResource();
        jedis.psubscribe(jedisPubSub, patterns);
        jedis.close();
    }

    @Override
    public void disconnect() {
        jedisPool.close();
    }

    @Override
    public void close() {
        jedisPool.close();
    }


}
