package com.muying.weblog.admin.model.vo.article;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "更新文章置顶请求 VO")
public class UpdateArticleIsTopReqVO {
    /**
     * 文章 ID
     */
    @NotNull(message = "文章 ID 不能为空")
    private String id;

    /**
     * 文章置顶状态
     */
    @NotNull(message = "文章置顶状态不能为空")
    private Boolean isTop;
}
