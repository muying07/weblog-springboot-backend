package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muying.weblog.common.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
    /**
     * 根据用户名查询
     * @param username
     * @return
     */
    default List<UserRoleEntity> selectByUsername(String username) {
        LambdaQueryWrapper<UserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoleEntity::getUsername, username);

        return selectList(wrapper);
    }
}
