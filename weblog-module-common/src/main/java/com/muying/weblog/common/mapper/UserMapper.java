package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muying.weblog.common.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * @author: 刘涛
 * @date: 2025-04-08 17:06
 * @description: TODO
 **/
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    default UserEntity findByUsername(String username) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        return selectOne(wrapper);
    }

    default int updatePasswordByUsername(String username, String password) {
        LambdaUpdateWrapper<UserEntity> wrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段
        wrapper.set(UserEntity::getPassword, password);
        wrapper.set(UserEntity::getUpdateTime, LocalDateTime.now());
        // 更新条件
        wrapper.eq(UserEntity::getUsername, username);

        return update(null, wrapper);
    }

}