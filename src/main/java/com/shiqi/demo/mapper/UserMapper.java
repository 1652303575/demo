package com.shiqi.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqi.demo.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}