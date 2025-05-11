package com.shiqi.demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiqi.demo.entity.User;
import com.shiqi.demo.mapper.UserMapper;
import com.shiqi.demo.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}