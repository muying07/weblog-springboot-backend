package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muying.weblog.common.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

// 若使用@MapperScan，此处无需加@Mapper
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据用户名查询
     * @param tagName
     * @return
     */
    default Tag selectByName(String tagName) {
        // 构建查询条件
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tagName);

        // 执行查询
        return selectOne(wrapper);
    }

    /**
     * 分页查询
     * @param current
     * @param size
     * @param name
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<Tag> selectPage(Long current, Long size, String name, LocalDate startDate, LocalDate endDate) {
        // 分页对象(查询第几页、每页多少数据)
        Page<Tag> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(StringUtils.isNotBlank(name), Tag::getName, name.trim()) // like 模块查询
                .ge(Objects.nonNull(startDate), Tag::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), Tag::getCreateTime, endDate)  // 小于等于 endDate
                .orderByDesc(Tag::getCreateTime); // 按创建时间倒叙

        // 执行查询
        return selectPage(page, wrapper);
    }

    /**
     * 根据关键字模糊查询
     * @param key
     * @return
     */
    default List<Tag> selectListByKey(String key){
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Tag::getName, key);
        return selectList(wrapper);
    }
    /**
     * 根据标签 ID 批量查询
     * @param tagIds
     * @return
     */
    default List<Tag> selectByIds(List<Long> tagIds) {
        return selectList(Wrappers.<Tag>lambdaQuery()
                .in(Tag::getId, tagIds));
    }
}
