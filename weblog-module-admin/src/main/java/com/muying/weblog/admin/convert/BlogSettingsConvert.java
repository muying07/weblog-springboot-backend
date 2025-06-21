package com.muying.weblog.admin.convert;

import com.muying.weblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import com.muying.weblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import com.muying.weblog.common.entity.BlogSettings;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlogSettingsConvert {
    /**
     * 初始化 convert 实例
     */
    BlogSettingsConvert INSTANCE = Mappers.getMapper(BlogSettingsConvert.class);

    /**
     * 将 VO 转化为 DO
     * @param bean
     * @return
     */
    BlogSettings convertVO2DO(UpdateBlogSettingsReqVO bean);

    /**
     * 将 DO 转化为 VO
     * @param bean
     * @return
     */
    FindBlogSettingsRspVO convertDO2VO(BlogSettings bean);
}
