package com.muying.weblog.web.controller;

import com.muying.weblog.common.aspect.ApiOperationLog;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.wiki.FindWikiArticlePreNextReqVO;
import com.muying.weblog.web.model.vo.wiki.FindWikiCatalogListReqVO;
import com.muying.weblog.web.service.WikiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 知识库
 **/
@RestController
@RequestMapping("/wiki")
@Api(tags = "知识库")
public class WikiController {

    @Autowired
    private WikiService wikiService;

    @PostMapping("/list")
    @ApiOperation(value = "获取知识库数据")
    @ApiOperationLog(description = "获取知识库数据")
    public Response findWikiList() {
        return wikiService.findWikiList();
    }
    @PostMapping("/catalog/list")
    @ApiOperation(value = "获取知识库目录数据")
    @ApiOperationLog(description = "获取知识库目录数据")
    public Response findWikiCatalogList(@RequestBody @Validated FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        return wikiService.findWikiCatalogList(findWikiCatalogListReqVO);
    }

    @PostMapping("/article/preNext")
    @ApiOperation(value = "获取知识库文章上下页")
    @ApiOperationLog(description = "获取知识库文章上下页")
    public Response findArticlePreNext(@RequestBody FindWikiArticlePreNextReqVO findWikiArticlePreNextReqVO) {
        return wikiService.findArticlePreNext(findWikiArticlePreNextReqVO);
    }

}
