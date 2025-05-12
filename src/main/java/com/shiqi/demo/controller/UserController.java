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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 用户管理接口
 */
@Tag(name = "用户管理接口", description = "用户的增删改查接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息1
     */
    @Operation(summary = "根据ID获取用户信息", description = "通过用户ID获取用户的详细信息")
    @GetMapping("/{id}")
    public Result<User> getById(@Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @Operation(summary = "获取所有用户列表", description = "获取所有用户的列表信息")
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
    @Operation(summary = "新增用户", description = "新增一个用户")
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
    @Operation(summary = "根据ID删除用户", description = "通过用户ID删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}