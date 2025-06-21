package com.muying.weblog.admin.service;

import com.muying.weblog.admin.model.vo.tag.AddTagReqVO;
import com.muying.weblog.admin.model.vo.tag.DeleteTagReqVO;
import com.muying.weblog.admin.model.vo.tag.FindTagPageListReqVO;
import com.muying.weblog.admin.model.vo.tag.SearchTagReqVO;
import com.muying.weblog.common.utils.PageResponse;
import com.muying.weblog.common.utils.Response;

public interface AdminTagService {
    /**
     * 添加标签
     *
     * @param addTagReqVO
     * @return
     */
    Response addTags(AddTagReqVO addTagReqVO);
    /**
     * 标签分页数据查询
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO);

    /**
     * 删除标签
     * @param deleteTagReqVO
     * @return
     */
    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    /**
     * 标签列表模糊查询
     * @param searchTagReqVO
     * @return
     */
    Response searchTagList(SearchTagReqVO searchTagReqVO);
    /**
     * 查询标签 Select 列表数据
     * @return
     */
    Response findTagSelectList();
}