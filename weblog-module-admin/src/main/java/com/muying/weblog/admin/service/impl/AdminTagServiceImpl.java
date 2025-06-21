package com.muying.weblog.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muying.weblog.admin.model.vo.tag.*;
import com.muying.weblog.admin.service.AdminTagService;
import com.muying.weblog.common.entity.Tag;
import com.muying.weblog.common.enums.ResponseCodeEnum;
import com.muying.weblog.common.mapper.TagMapper;
import com.muying.weblog.common.model.SelectRspVO;
import com.muying.weblog.common.utils.PageResponse;
import com.muying.weblog.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminTagServiceImpl extends ServiceImpl<TagMapper,Tag> implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;

    /**
     * 添加分类
     *
     * @param addTagReqVO: 添加分类入参
     * @return
     */
    @Override
    public Response addTags(AddTagReqVO addTagReqVO) {
        List<Tag> tags = addTagReqVO.getTags().stream().map(tagName -> Tag.builder().name(tagName.trim())
                .build()).collect(Collectors.toList());
        boolean success = true;
        try {
            // 批量插入
             success = saveBatch(tags);
        } catch (Exception e) {
            log.warn("该标签已存在", e);
            return Response.fail();
        }
        return Response.success(success);
    }

    @Override
    public PageResponse findTagList(FindTagPageListReqVO findTagPageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();
        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();
        // 执行分页查询
        Page<Tag> tagDOPage = tagMapper.selectPage(current,size,name,startDate,endDate);
        List<Tag> tagDOS = tagDOPage.getRecords();

        // do 转 vo
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream().map(tagDO -> FindTagPageListRspVO.builder()
                    .id(Long.toString(tagDO.getId()))
                    .name(tagDO.getName())
                    .createTime(tagDO.getCreateTime())
                    .articlesTotal(tagDO.getArticlesTotal())
                    .build()).collect(Collectors.toList());
        }

        return PageResponse.success(tagDOPage, vos);


    }

    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        // 标签 ID
        Long tagId = Long.valueOf(deleteTagReqVO.getId());

        // 删除标签
        int count = tagMapper.deleteById(tagId);

        return count==1? Response.success():  Response.fail(ResponseCodeEnum.TAG_NOT_EXISTED);
    }

    @Override
    public Response searchTagList(SearchTagReqVO searchTagReqVO) {
        String key = searchTagReqVO.getKey();
        List<Tag> tags = tagMapper.selectListByKey(key);
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tags)) {
             vos = tags.stream()
                    .map(tag -> SelectRspVO.builder()
                            .label(tag.getName())
                            .value(Long.toString(tag.getId()))
                            .build())
                    .collect(Collectors.toList());
            return Response.success(vos);
        }
        return null;
    }

    /**
     * 查询标签 Select 列表数据
     *
     * @return
     */
    @Override
    public Response findTagSelectList() {
        // 查询所有标签
        List<Tag> tagS = tagMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagS)) {
            vos = tagS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(Long.toString(tagDO.getId()))
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }
}
