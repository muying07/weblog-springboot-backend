package com.muying.weblog.web.controller;

import com.muying.weblog.common.aspect.ApiOperationLog;
import com.muying.weblog.common.utils.Response;
import com.muying.weblog.web.model.vo.article.FindArticleDetailReqVO;
import com.muying.weblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import com.muying.weblog.web.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
@Api(tags = "文章")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/list")
    @ApiOperation(value = "获取首页文章分页数据")
    @ApiOperationLog(description = "获取首页文章分页数据")
    public Response findArticlePageList(@RequestBody FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        return articleService.findArticlePageList(findIndexArticlePageListReqVO);
    }




    @PostMapping("/hotlist")
    @ApiOperation(value = "获取首页推荐文章列表")
    @ApiOperationLog(description = "获取首页推荐文章列表")
    public Response findArticleHotList() {
        return articleService.findArticleHotList();
    }



    @PostMapping("/detail")
    @ApiOperation(value = "获取文章详情")
    @ApiOperationLog(description = "获取文章详情")
    public Response findArticleDetail(@RequestBody FindArticleDetailReqVO findArticleDetailReqVO) {
        return articleService.findArticleDetail(findArticleDetailReqVO);
    }

    @PostMapping("/loginlist")
    @ApiOperation(value = "获取登录页推荐文章列表")
    @ApiOperationLog(description = "获取登录页推荐文章列表")
    public Response findArticleLoginList() {
        return articleService.findArticleLoginList();
    }

}

