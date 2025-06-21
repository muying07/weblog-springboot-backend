package com.muying.weblog.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_article")
public class Article {

    /**
     * 主键
     **/
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     **/
    private String title;

    /**
     * 封面
     **/
    private String cover;

    /**
     * 文章摘要内容
     **/
    private String summary;

    private String aiDescribe;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;

    /**
     * 阅读数
     **/
    private Long readNum;

    /**
     * 文章权重，用于是否置顶（0: 未置顶；>0: 参与置顶，权重值越高越靠前）
     **/
    private Integer weight;

    /**
     * 文章类型 - 1：普通文章，2：收录于知识库
     **/
    private Integer type;
}
