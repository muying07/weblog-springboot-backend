package com.muying.weblog.admin.controller;

import com.muying.weblog.admin.model.vo.wiki.*;
import com.muying.weblog.admin.service.AdminWikiService;
import com.muying.weblog.common.aspect.ApiOperationLog;
import com.muying.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/wiki")
@Api(tags = "Admin 知识库模块")
public class AdminWikiController {

    @PostMapping("/list")
    @ApiOperation(value = "查询知识库分页数据")
    @ApiOperationLog(description = "查询知识库分页数据")
    public Response findWikiPageList(@RequestBody @Validated FindWikiPageListReqVO findWikiPageListReqVO) {
        return wikiService.findWikiPageList(findWikiPageListReqVO);
    }

    @PostMapping("/catalog/list")
    @ApiOperation(value = "查询知识库目录数据")
    @ApiOperationLog(description = "查询知识库目录数据")
    public Response findWikiCatalogList(@RequestBody @Validated FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        return wikiService.findWikiCatalogList(findWikiCatalogListReqVO);
    }

    @PostMapping("/catalog/update")
    @ApiOperation(value = "更新知识库目录")
    @ApiOperationLog(description = "更新知识库目录")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWikiCatalogs(@RequestBody @Validated UpdateWikiCatalogReqVO updateWikiCatalogsReqVO) {
        return wikiService.updateWikiCatalogs(updateWikiCatalogsReqVO);
    }

    @Autowired
    private AdminWikiService wikiService;

    @PostMapping("/add")
    @ApiOperation(value = "新增知识库")
    @ApiOperationLog(description = "新增知识库")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response addWiki(@RequestBody @Validated AddWikiReqVO addWikiReqVO) {
        return wikiService.addWiki(addWikiReqVO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新知识库")
    @ApiOperationLog(description = "更新知识库")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWiki(@RequestBody @Validated UpdateWikiReqVO updateWikiReqVO) {
        return wikiService.updateWiki(updateWikiReqVO);
    }

    @PostMapping("/isTop/update")
    @ApiOperation(value = "更新知识库置顶状态")
    @ApiOperationLog(description = "更新知识库置顶状态")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWikiIsTop(@RequestBody @Validated UpdateWikiIsTopReqVO updateWikiIsTopReqVO) {
        return wikiService.updateWikiIsTop(updateWikiIsTopReqVO);
    }

    @PostMapping("/isPublish/update")
    @ApiOperation(value = "更新知识库发布状态")
    @ApiOperationLog(description = "更新知识库发布状态")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response updateWikiIsPublish(@RequestBody @Validated UpdateWikiIsPublishReqVO updateWikiIsPublishReqVO) {
        return wikiService.updateWikiIsPublish(updateWikiIsPublishReqVO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "知识库删除")
    @ApiOperationLog(description = "知识库删除")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Response deleteWiki(@RequestBody @Validated DeleteWikiReqVO deleteWikiReqVO) {
        return wikiService.deleteWiki(deleteWikiReqVO);
    }
}
