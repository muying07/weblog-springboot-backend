package com.muying.weblog.admin.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "标签列表模糊查询 VO")
public class SearchTagReqVO {
    /**
     * 标签查询关键词
     */
    @NotBlank(message = "标签查询关键词 不能为空")
    private String key;
}
