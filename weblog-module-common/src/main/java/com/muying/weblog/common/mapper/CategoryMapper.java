package com.muying.weblog.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.muying.weblog.common.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Objects;

// 若使用@MapperScan，此处无需加@Mapper
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据用户名查询
     * @param categoryName
     * @return
     */
    default Category selectByName(String categoryName) {
        // 构建查询条件
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, categoryName);

        // 执行查询
        return selectOne(wrapper);
    }

    default Page<Category> selectPageList(Long current, Long size, String name, LocalDate startDate, LocalDate endDate) {
        // 分页对象(查询第几页、每页多少数据)
        Page<Category> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(StringUtils.isNotBlank(name), Category::getName, name.trim()) // like 模块查询
                .ge(Objects.nonNull(startDate), Category::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), Category::getCreateTime, endDate)  // 小于等于 endDate
                .orderByDesc(Category::getCreateTime); // 按创建时间倒叙

        // 执行查询
        return selectPage(page, wrapper);
    }
}
