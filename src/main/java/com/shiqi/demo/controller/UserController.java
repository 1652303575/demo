package com.shiqi.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiqi.demo.entity.User;
import com.shiqi.demo.service.UserService;
import com.shiqi.demo.vo.Result;

/**
 * 用户管理接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @GetMapping
    public Result<List<User>> list() {
        return Result.success(userService.list());
    }

    /**
     * 新增用户
     *
     * @param user 用户对象
     * @return 是否成功
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody User user) {
        return Result.success(userService.save(user));
    }

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}