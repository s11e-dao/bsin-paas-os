package e

type MsgInfo struct {
	MsgCode int    // 错误码
	Msg     string // 错误描述
}

var MsgFlags = map[int]string{
	SUCCESS:                        "ok",
	ERROR:                          "fail",
	INVALID_PARAMS:                 "请求参数错误",
	INVALID_JSON_PARAMS:            "请求参数非json",
	ERROR_EXIST_TAG:                "已存在该标签名称",
	ERROR_EXIST_TAG_FAIL:           "获取已存在标签失败",
	ERROR_NOT_EXIST_TAG:            "该标签不存在",
	ERROR_GET_TAGS_FAIL:            "获取所有标签失败",
	ERROR_COUNT_TAG_FAIL:           "统计标签失败",
	ERROR_ADD_TAG_FAIL:             "新增标签失败",
	ERROR_EDIT_TAG_FAIL:            "修改标签失败",
	ERROR_DELETE_TAG_FAIL:          "删除标签失败",
	ERROR_EXPORT_TAG_FAIL:          "导出标签失败",
	ERROR_IMPORT_TAG_FAIL:          "导入标签失败",
	ERROR_NOT_EXIST_ARTICLE:        "该文章不存在",
	ERROR_ADD_ARTICLE_FAIL:         "新增文章失败",
	ERROR_DELETE_ARTICLE_FAIL:      "删除文章失败",
	ERROR_CHECK_EXIST_ARTICLE_FAIL: "检查文章是否存在失败",
	ERROR_EDIT_ARTICLE_FAIL:        "修改文章失败",
	ERROR_COUNT_ARTICLE_FAIL:       "统计文章失败",
	ERROR_GET_ARTICLES_FAIL:        "获取多个文章失败",
	ERROR_GET_ARTICLE_FAIL:         "获取单个文章失败",
	ERROR_GEN_ARTICLE_POSTER_FAIL:  "生成文章海报失败",

	ERROR_NOT_EXIST_COMMENT:        "该评论不存在",
	ERROR_ADD_COMMENT_FAIL:         "新增评论失败",
	ERROR_DELETE_COMMENT_FAIL:      "删除评论失败",
	ERROR_CHECK_EXIST_COMMENT_FAIL: "检查评论是否存在失败",
	ERROR_EDIT_COMMENT_FAIL:        "修改评论失败",
	ERROR_COUNT_COMMENT_FAIL:       "统计评论失败",
	ERROR_GET_COMMENTS_FAIL:        "获取多个评论失败",
	ERROR_GET_COMMENT_FAIL:         "获取单个评论失败",
	ERROR_GEN_COMMENT_POSTER_FAIL:  "生成评论海报失败",

	ERROR_NOT_EXIST_MESSAGE:        "该留言不存在",
	ERROR_ADD_MESSAGE_FAIL:         "新增留言失败",
	ERROR_DELETE_MESSAGE_FAIL:      "删除留言失败",
	ERROR_CHECK_EXIST_MESSAGE_FAIL: "检查留言是否存在失败",
	ERROR_EDIT_MESSAGE_FAIL:        "修改留言失败",
	ERROR_COUNT_MESSAGE_FAIL:       "统计留言失败",
	ERROR_GET_MESSAGES_FAIL:        "获取多个留言失败",
	ERROR_GET_MESSAGE_FAIL:         "获取单个留言失败",
	ERROR_GEN_MESSAGE_POSTER_FAIL:  "生成留言海报失败",

	ERROR_NOT_EXIST_USER:        "该用户不存在",
	ERROR_ADD_USER_FAIL:         "新增用户失败",
	ERROR_DELETE_USER_FAIL:      "删除用户失败",
	ERROR_CHECK_EXIST_USER_FAIL: "检查用户是否存在失败",
	ERROR_EDIT_USER_FAIL:        "修改用户失败",
	ERROR_COUNT_USER_FAIL:       "统计用户失败",
	ERROR_GET_USERS_FAIL:        "获取多个用户失败",
	ERROR_GET_USER_FAIL:         "获取单个用户失败",
	ERROR_GEN_USER_POSTER_FAIL:  "生成用户海报失败",

	ERROR_EXIST_TOPIC_FAIL:       "该话题已存在",
	ERROR_NOT_EXIST_TOPIC:        "该话题不存在",
	ERROR_DELETE_TOPIC_FAIL:      "删除话题失败",
	ERROR_ADD_TOPIC_FAIL:         "新增话题失败",
	ERROR_CHECK_EXIST_TOPIC_FAIL: "检查话题是否存在失败",
	ERROR_EDIT_TOPIC_FAIL:        "修改话题失败",
	ERROR_COUNT_TOPIC_FAIL:       "统计话题失败",
	ERROR_GET_TOPICS_FAIL:        "获取多个话题失败",
	ERROR_GET_TOPIC_FAIL:         "获取单个话题失败",
	ERROR_GEN_TOPIC_POSTER_FAIL:  "生成话题海报失败",
	ERROR_EXIST_TOPIC:            "已存在该话题名称",
	ERROR_EXPORT_TOPIC_FAIL:      "导出话题失败",
	ERROR_IMPORT_TOPIC_FAIL:      "导入话题失败",

	ERROR_IMPORT_FOOD_FAIL:      "导入食物失败",
	ERROR_EXPORT_FOOD_FAIL:      "导出食物失败",
	ERROR_EXIST_FOOD:            "已存在该食物名称",
	ERROR_EXIST_FOOD_FAIL:       "该食物已存在",
	ERROR_NOT_EXIST_FOOD:        "该食物不存在",
	ERROR_DELETE_FOOD_FAIL:      "删除食物失败",
	ERROR_ADD_FOOD_FAIL:         "新增食物失败",
	ERROR_CHECK_EXIST_FOOD_FAIL: "检查食物是否存在失败",
	ERROR_EDIT_FOOD_FAIL:        "修改食物失败",
	ERROR_COUNT_FOOD_FAIL:       "统计食物失败",
	ERROR_GET_FOODS_FAIL:        "获取多个食物失败",
	ERROR_GET_FOOD_FAIL:         "获取单个食物失败",
	ERROR_GEN_FOOD_POSTER_FAIL:  "生成食物海报失败",

	ERROR_NOT_EXIST_CATEGORY:        "该分类不存在",
	ERROR_ADD_CATEGORY_FAIL:         "新增分类失败",
	ERROR_DELETE_CATEGORY_FAIL:      "删除分类失败",
	ERROR_CHECK_EXIST_CATEGORY_FAIL: "检查分类是否存在失败",
	ERROR_EDIT_CATEGORY_FAIL:        "修改分类失败",
	ERROR_COUNT_CATEGORY_FAIL:       "统计分类失败",
	ERROR_GET_CATEGORYS_FAIL:        "获取多个分类失败",
	ERROR_GET_CATEGORY_FAIL:         "获取单个分类失败",
	ERROR_GEN_CATEGORY_POSTER_FAIL:  "生成分类海报失败",
	ERROR_ALREADY_EXIST_CATEGORY:    "改分类已经存在",

	ERROR_CAPTCHA_QUERY_PARAM_ERROR:  "验证码请求参数有误",
	ERROR_CAPTCHA_RESOURCE_NOT_FOUNT: "验证码访问资源不存在",
	ERROR_CAPTCHA_SYSTEM_ERROE:       "验证码服务内部错误",
	ERROR_CAPTCHA_ERROR:              "验证码验证码错误",

	ERROR_AUTH_CHECK_TOKEN_FAIL:     "Token鉴权失败",
	ERROR_AUTH_CHECK_TOKEN_TIMEOUT:  "Token已超时",
	ERROR_AUTH_TOKEN:                "Token生成失败",
	ERROR_AUTH:                      "Token错误",
	ERROR_UPLOAD_SAVE_IMAGE_FAIL:    "保存图片失败",
	ERROR_UPLOAD_CHECK_IMAGE_FAIL:   "检查图片失败",
	ERROR_UPLOAD_CHECK_IMAGE_FORMAT: "校验图片错误，图片格式或大小有问题",
}

func GetMsg(code int) string {
	msg, ok := MsgFlags[code]
	if ok {
		return msg
	}

	return MsgFlags[ERROR]
}

func NewMsg(code int) MsgInfo {
	v, ok := MsgFlags[code]
	if ok {
		return MsgInfo{MsgCode: code, Msg: v}
	} else {
		return MsgInfo{MsgCode: code, Msg: "Unknown error"}
	}
}

func NewDefineMsg(code int, msg string) MsgInfo {
	return MsgInfo{MsgCode: code, Msg: msg}

}

func Valid(err MsgInfo) bool {
	return err.MsgCode == 200
}
