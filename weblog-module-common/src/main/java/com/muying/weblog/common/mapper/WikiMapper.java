package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muying.weblog.common.entity.Wiki;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface WikiMapper extends BaseMapper<Wiki> {

    /**
     * 分页查询
     * @param current
     * @param size
     * @param title
     * @param startDate
     * @param endDate
     * @return
     */
    default Page<Wiki> selectPageList(Long current, Long size, String title, LocalDate startDate, LocalDate endDate, Boolean isPublish){
        Page<Wiki> page = new Page<>(current, size);
        LambdaQueryWrapper<Wiki> queryWrapper = Wrappers.<Wiki>lambdaQuery()
                .like(StringUtils.isNotBlank(title), Wiki::getTitle, title) // like 模块查询
                .ge(Objects.nonNull(startDate), Wiki::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), Wiki::getCreateTime, endDate)  // 小于等于 endDate
                .eq(Objects.nonNull(isPublish), Wiki::getIsPublish, isPublish) // 发布状态
                .orderByDesc(Wiki::getWeight) // 按权重倒序
                .orderByDesc(Wiki::getCreateTime); // 按创建时间倒叙
        return this.selectPage(page, queryWrapper);
    }
    /**
     * 查询最大权重
     * @return
     */
    default Wiki selectMaxWeight() {
        return selectOne(Wrappers.<Wiki>lambdaQuery()
                .orderByDesc(Wiki::getWeight) // 按权重值降序排列
                .last("LIMIT 1")); // 仅查询出一条
    }

    /**
     * 查询已发布的知识库
     * @return
     */
    default List<Wiki> selectPublished() {
        return selectList(Wrappers.<Wiki>lambdaQuery()
                .eq(Wiki::getIsPublish, 1) // 查询已发布的， is_publish 值为 1
                .orderByDesc(Wiki::getWeight) // 按权重降序
                .orderByDesc(Wiki::getCreateTime) // 按发布时间降序
        );
    }
}
