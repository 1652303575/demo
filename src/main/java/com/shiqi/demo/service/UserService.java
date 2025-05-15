package com.shiqi.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiqi.demo.entity.User;
import java.util.List;

public interface UserService extends IService<User> {
    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(Long id);
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean removeById(Long id);
    /**
     * 新增用户
     *
     * @param user 用户对象
     * @return 是否新增成功
     */
    boolean save(User user);
    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否批量删除成功
     */

    boolean removeByIds(List<Long> ids);
    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    List<User> list();

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean updateById(User user);

    /**
     * 根据名称查询用户
     *
     * @param name 用户名称
     * @return 用户信息
     */
    User getByName(String name);

    /**
     * 更新用户并清除相关缓存
     *
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean updateUserAndClearCache(User user);

    /**
     * 更新用户名称
     *
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean updateName(User user);

    /**
     * 批量保存用户
     *
     * @param users 用户列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<User> users);
}