package dev.starryeye.monolithic_3.common.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private static final String LOCK_PREFIX = "lock:";
    private static final Duration DEFAULT_TTL = Duration.ofMillis(3_000); // 3초

    private final StringRedisTemplate redisTemplate;

    public Boolean tryLock(String domainPrefix, String key) {
        return redisTemplate.opsForValue()
                .setIfAbsent(generateKey(domainPrefix, key), "lock", DEFAULT_TTL);
    }

    public void unlock(String domainPrefix, String key) {
        redisTemplate.delete(generateKey(domainPrefix, key));
    }

    private String generateKey(String domainPrefix, String key) {
        return LOCK_PREFIX + domainPrefix + key;
    }
}
