package com.muying.weblog.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonSumaryVo {
    /**
     * 总结
     */
    private String sumaryContent;

    /**
     * AI总结
     */
    private String aiSumaryContent;
}
