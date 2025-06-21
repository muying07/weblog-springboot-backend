package com.muying.weblog.web.model.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindIndexArticleHotListRspVO {
    private String id;
    private String cover;
    private String title;
    private LocalDateTime createTime;
    private String summary;
}