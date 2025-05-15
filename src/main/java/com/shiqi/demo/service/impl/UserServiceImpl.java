package com.shiqi.demo.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiqi.demo.entity.User;
import com.shiqi.demo.mapper.UserMapper;
import com.shiqi.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * @Cacheable 用于查询操作
     * @CachePut 用于更新操作
     * @CacheEvict 用于删除操作
     * @Caching 用于组合多个缓存操作
     * @CacheConfig 用于类级别的缓存配置
     */
    
    @Override
    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    public User getById(Long id) {
        log.info("从数据库查询用户: {}", id);
        return super.getById(id);
    }

    @Override
    @Cacheable(value = "users", key = "'list'")
    public List<User> list() {
        log.info("从数据库查询用户列表");
        return super.list();
    }

    @Override
    @CachePut(value = "user", key = "#user.id")
    public boolean updateById(User user) {
        log.info("更新用户: {}", user);
        return super.updateById(user);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public boolean removeById(Long id) {
        log.info("删除用户: {}", id);
        return super.removeById(id);
    }

    @Override
    @CacheEvict(value = {"user", "users"}, allEntries = true)
    public boolean save(User user) {
        log.info("新增用户: {}", user);
        return super.save(user);
    }

    @Override
    @CacheEvict(value = {"user", "users"}, allEntries = true)
    public boolean removeByIds(List<Long> ids) {
        log.info("批量删除用户: {}", ids);
        return super.removeByIds(ids);
    }

    @Override
    @Cacheable(value = "user", key = "#name", unless = "#result == null")
    public User getByName(String name) {
        log.info("根据名称查询用户: {}", name);
        return lambdaQuery().eq(User::getName, name).one();
    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "user", key = "#user.id"),
            @CacheEvict(value = "users", key = "'list'")
        }
    )
    public boolean updateUserAndClearCache(User user) {
        log.info("更新用户并清除缓存: {}", user);
        return updateById(user);
    }

    @Override
    @CachePut(value = "user", key = "#user.id", condition = "#user.name != null")
    public boolean updateName(User user) {
        log.info("更新用户名称: {}", user);
        return lambdaUpdate()
                .eq(User::getId, user.getId())
                .set(User::getName, user.getName())
                .update();
    }

    @Override
    @CacheEvict(value = {"user", "users"}, allEntries = true)
    public boolean saveBatch(List<User> users) {
        log.info("批量保存用户: {}", users);
        return super.saveBatch(users);
    }
}