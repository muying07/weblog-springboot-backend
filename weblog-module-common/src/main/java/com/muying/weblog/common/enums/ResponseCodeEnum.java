package com.muying.weblog.common.enums;

import com.muying.weblog.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: 刘涛
 * @date: 2025-04-08 10:33
 * @description: 响应异常码
 **/
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),
    UNAUTHORIZED("20002", "无访问权限，请先登录！"),
    PARAM_NOT_VALID("10001", "参数错误"),
    PARAM_IS_BLANK("10002", "参数为空"),
    PARAM_TYPE_ERROR("10003", "参数类型错误"),
    PARAM_NOT_COMPLETE("10004", "参数缺失"),
    BUSINESS_ERROR("10005", "业务逻辑错误"),
    REQUEST_FORBIDDEN("10006", "禁止访问"),
    SIGNATURE_NOT_MATCH("10007", "请求数据签名验证不通过"),
    NOT_LOGIN("10008", "未登录"),
    NO_PERMISSION("10009", "无权限"),
    SERVER_BUSY("10010", "服务器正忙，请稍后再试"),
    OPERATION_FAIL("10011", "操作失败"),
    OPERATION_TIMEOUT("10012", "操作超时"),
    OPERATION_TOO_FREQUENT("10013", "操作太频繁"),
    OPERATION_NOT_ALLOWED("10014", "操作不允许"),
    OPERATION_NOT_SUPPORTED("10015", "操作不支持"),
    OPERATION_NOT_FOUND("10016", "操作未找到"),
    OPERATION_NOT_PERMITTED("10017", "操作未授权"),
    OPERATION_NOT_ALLOWED_IP("10018", "操作不允许的IP"),
    OPERATION_NOT_ALLOWED_DEVICE("10019", "操作不允许的设备"),
    OPERATION_NOT_ALLOWED_BROWSER("10020", "操作不允许的浏览器"),
    OPERATION_NOT_ALLOWED_OS("10021", "操作不允许的操作系统"),
    OPERATION_NOT_ALLOWED_NETWORK("10022", "操作不允许的网络"),
    OPERATION_NOT_ALLOWED_LOCATION("10023", "操作不允许的位置"),
    OPERATION_NOT_ALLOWED_TIME("10024", "操作不允许的时间"),

    LOGIN_FAIL("20000", "登录失败"),
    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),
    USER_NOT_EXIST("20002", "用户不存在"),
    USER_NOT_LOGIN("20003", "用户未登录"),
    USER_ACCOUNT_FORBIDDEN("20004", "账号已被禁用"),
    USER_NOT_PERMISSION("20005", "无权限"),
    TOKEN_EXPIRED("20006", "token已过期"),
    TOKEN_ERROR("20007", "token错误"),
    TOKEN_MISS("20008", "token缺失"),
    TOKEN_BE_REPLACED("20009", "token不可用"),

    // ----------- 业务异常状态码 -----------
    PRODUCT_NOT_FOUND("20000", "该产品不存在（测试使用）"),
    USERNAME_NOT_FOUND("20003", "该用户不存在"),
    FORBIDDEN("20004", "演示账号仅支持查询操作！"),
    CATEGORY_NAME_IS_EXISTED("20005", "该分类已存在，请勿重复添加！"),
    TAG_NAME_IS_EXISTED("20006", "该标签已存在，请勿重复添加！"),
    TAG_NOT_EXISTED("20007", "该标签不存在！"),
    FILE_UPLOAD_FAILED("20008", "文件上传失败！"),
    CATEGORY_NOT_EXISTED("20009", "提交的分类不存在！"),
    ARTICLE_NOT_FOUND("20010", "该文章不存在！"),
    CATEGORY_CAN_NOT_DELETE("20011", "该分类下包含文章，请先删除对应文章，才能删除！"),
    TAG_CAN_NOT_DELETE("20012", "该标签下包含文章，请先删除对应文章，才能删除！"),
    WIKI_NOT_FOUND("20013", "该知识库不存在"),

    NOT_QQ_NUMBER("20014", "QQ 号格式不正确"),
    COMMENT_CONTAIN_SENSITIVE_WORD("20015", "评论内容中包含敏感词，请重新编辑后再提交"),
    COMMENT_WAIT_EXAMINE("20016", "评论已提交, 等待博主审核通过"),
    COMMENT_NOT_FOUND("20017", "该评论不存在"),
    COMMENT_STATUS_NOT_WAIT_EXAMINE("20018", "该评论未处于待审核状态"),
    ;


    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

}
