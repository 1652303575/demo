package com.shiqi.demo.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.shiqi.demo.entity.User;
import com.shiqi.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class RedisUtilsTest {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TEST_KEY = "test";
    private static final String TEST_VALUE = "123456测试";
    @BeforeEach
    void setUp() {
        // 设置日志级别
        System.setProperty("logging.level.com.shiqi.demo", "DEBUG");
    }
    @Test
    void testSetAndGet() {
        // 测试设置和获取值
        redisUtils.set(TEST_KEY, TEST_VALUE);
        Object value = redisUtils.get(TEST_KEY);
        System.out.println("value:"+value);
        assertEquals(TEST_VALUE, value);
    }

    @Test
    void testSetWithExpire() {
        // 测试设置带过期时间的值
        redisUtils.set(TEST_KEY, TEST_VALUE, 1, TimeUnit.MINUTES);
        Object value = redisUtils.get(TEST_KEY);
        assertEquals(TEST_VALUE, value);
        
        // 验证过期时间
        Long expire = redisUtils.getExpire(TEST_KEY, TimeUnit.SECONDS);
        assertTrue(expire > 0 && expire <= 60);
    }

    @Test
    void testDelete() {
        // 测试删除操作
        redisUtils.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisUtils.hasKey(TEST_KEY));
        
        redisUtils.delete(TEST_KEY);
        assertFalse(redisUtils.hasKey(TEST_KEY));
    }

    @Test
    void testHasKey() {
        // 测试key是否存在
        redisUtils.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisUtils.hasKey(TEST_KEY));
        
        redisUtils.delete(TEST_KEY);
        assertFalse(redisUtils.hasKey(TEST_KEY));
    }

    @Test
    void testExpire() {
        // 测试设置过期时间
        redisUtils.set(TEST_KEY, TEST_VALUE);
        assertTrue(redisUtils.expire(TEST_KEY, 1, TimeUnit.MINUTES));
        
        Long expire = redisUtils.getExpire(TEST_KEY, TimeUnit.SECONDS);
        assertTrue(expire > 0 && expire <= 60);
    }

    @Test
    void testIncrementAndDecrement() {
        // 测试递增操作
        String counterKey = "test:counter";
        redisUtils.set(counterKey, 0);
        
        Long incremented = redisUtils.increment(counterKey, 5);
        assertEquals(5L, incremented);
        
        // 测试递减操作
        Long decremented = redisUtils.decrement(counterKey, 2);
        assertEquals(3L, decremented);
    }

    @Test
    void testComplexObject() {
        // 测试复杂对象的存储和获取
        TestUser user = new TestUser("张三", 25);
        String userKey = "test:user";
        
        redisUtils.set(userKey, user);
        TestUser retrievedUser = (TestUser) redisUtils.get(userKey);
        
        assertNotNull(retrievedUser);
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getAge(), retrievedUser.getAge());
    }

    @Test
    void testCache() {
        // 清理测试数据
        String cacheKey = "user::1";
        redisTemplate.delete(cacheKey);
        
        // 第一次查询（数据库）
        User user1 = userService.getById(1L);
        
        // 第二次查询（缓存）
        User user2 = userService.getById(1L);
        
        // 验证缓存
        Object cachedUser = redisTemplate.opsForValue().get(cacheKey);
        
        // 测试删除
        userService.removeById(1L);
        
        // 测试新增
        User newUser = new User();
        newUser.setName("测试用户");
        userService.save(newUser);
    }

    @Test
    void testCacheWithMultipleUsers() {
        // 清理测试数据
        redisTemplate.delete("user::1");
        redisTemplate.delete("user::2");
        
        // 查询多个用户
        User user1 = userService.getById(1L);
        User user2 = userService.getById(2L);
        
        // 再次查询
        User cachedUser1 = userService.getById(1L);
        
        // 删除一个用户
        userService.removeById(1L);
        
        // 验证缓存
        Object deletedCache = redisTemplate.opsForValue().get("user::1");
        Object existingCache = redisTemplate.opsForValue().get("user::2");
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        // 清理测试数据
        String cacheKey = "user::1";
        redisTemplate.delete(cacheKey);
        
        // 查询用户
        User user = userService.getById(1L);
        
        // 验证缓存
        Object cachedUser = redisTemplate.opsForValue().get(cacheKey);
        
        // 等待过期
        Thread.sleep(2000);
        
        // 再次查询
        User user2 = userService.getById(1L);
    }

    // 测试用的内部类
    private static class TestUser {
        private String name;
        private int age;

        public TestUser() {
        }

        public TestUser(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
} 