package me.flyray.bsin.constants;

import me.flyray.bsin.utils.ReturnCode;

public enum ResponseCode implements ReturnCode {

    /***********************************通用错误码*****************************************/
    OK("0", "请求成功"),
    FAIL("500", "请求失败"),
    DATA_NOT_EXIST("100", "该数据不存在"),

    // 参数错误 400000
    PARAM_ERROR("1000", "参数错误"),


    // 数据库操作 100000
    DATA_BASE_UPDATE_FAILED("100001", "数据库更新失败"),

    // 用户 100100
    USER_NAME_ISNULL("100101", "用户名为空值"),
    USER_DELETE_EXCEPTION("100102", "用户删除异常"),
    USER_SAVE_EXCEPTION("100103", "用户插入异常"),
    USER_NOT_EXIST("100104", "不存在该用户"),
    USER_EXIST("100105", "该用户已存在"),
    USER_UPDATE_EXCEPTION("100106", "用户更新异常"),
    USERNAME_PASSWORD_ERROR("100107", "用户不存在或密码错误"),
    PASSWORD_ERROR("100108", "口令错误"),
    USER_POST_IS_RELATED("100109", "用户岗位存在关联关系"),
    USER_NOT_APP_ROLE("100111", "您没有该应用的角色"),
    DATA_HAS_EXIST("100116", "数据已存在！"),
    PHONE_IS_NOT_NULL("100117", "手机号不能为空"),
    USER_STATUS_ERROR("100118", "用户状态错误"),
    USERNAME_EXIST("100119", "该用户名已存在"),
    PHOEN_EMAIL_EXIST("100120", "手机号、邮箱已存在"),

    // 机构 100200
    ORG_NOT_EXIST("100201", "机构不存在"),
    ORG_UPDATE_EXCEPTION("100202", "机构更新异常"),
    ORG_HAVE_CHILDREN_ORG("100203", "机构下存在子集部门"),
    ORG_CODE_EXIST("100204", "机构编号已存在"),
    ORG_APP_IS_RELATED("100205", "机构应用存在关联"),
    ORG_POST_IS_RELATED("100206", "机构岗位存在关联"),

    // 岗位 100300
    POSITION_USER_IS_RELATED("100301", "岗位和用户存在关联"),
    POSITION_ROLE_IS_RELATED("100302", "岗位角色存在关联"),
    POST_NOT_EXIST("100303", "岗位不存在"),
    POST_CODE_EXIST("100304", "岗位编号已存在"),
    INVALID_FIELDS("100305", "请求参数非法"),

    // 角色 100400
    ROLE_CODE_EXISTS("100401", "编码已存在"),
    ROLE_NOT_ADD("100402", "您没有权限为该应用添加角色"),
    ROLE_NOT_DELETE("100403", "您没有权限删除该应用的角色"),
    ROLE_NOT_UPDATE("100404", "您没有权限编辑该应用的角色"),
    ROLE_NOT_AUTHORIZE_MENU("100405", "您没有权限为该应用的角色授予菜单权限"),

    // 菜单 100500
    MENU_CODE_EXISTS("100501", "编码已存在"),
    MENU_NOT_ADD("100502", "您没有权限为该应用添加菜单"),
    MENU_NOT_DELETE("100503", "您没有权限删除该应用的菜单"),
    MENU_NOT_UPDATE("100504", "您没有权限编辑该应用的菜单"),
    MENU_EXIST_SUBMENU("100505", "该菜单存在子菜单"),
    NOT_TOP_MENU("100506", "不能添加顶级菜单"),

    // 应用 100600
    APP_CODE_EXISTS("100601", "应用编码已存在"),
    APP_EXIST_USER("100602", "应用被其他机构使用中"),
    APP_NOT_DELETE("100603", "您没有权限删除该应用"),
    APP_NOT_UPDATE("100604", "您没有权限编辑该应用"),
    APP_NOT_EXISTS("1006005", "该应用不存在"),
    UPMS_NOT_ADD("1006006", "权限管理不能添加"),

    APP_ID_NOT_EXISTS("1006006", "未找到对应appid!!"),

    // 租户 100700
    TENANT_CODE_EXISTS("100701", "租户编码已存在"),
    SUB_TENANT_NOT_AUTH("100702", "超级租户不能授权"),

    // 机构岗位 100800
    ID_NOT_ISNULL("100801", "Id不能为空"),

    // 分页 100900
    PAGE_NUM_ISNULL("100901", "分页参数为空"),


    NO_NOT_ISNULL("100801", "编号不能为空"),
    DISTRIBUTION_NOT_ISNULL("100802", "分配参数不能为空"),
    CHECK_PENDING("100803", "注册待审核中，请稍后..."),
    METADATA_ISNULL("100804", "metadata为空"),
    TENANT_ID_NOT_ISNULL("100806", "租户id不能为空"),
    TOTAL_SUPPLY_AND_METADATA_QUANTITY_MISMATCH("100805", "总供应量与metadata数量不匹配"),
    CCY_NOT_ISNULL("100807", "币种不能为空"),
    AMOUNT_NOT_ISNULL("100808", "金额不能为空"),
    TOKEN_ID_NOT_ISNULL("100808", "链上唯一标识不能为空"),
    NOT_YET_OPEN("100809", "功能暂未开放"),
    ACCOUNT_YET_OPEN("100810", "钱包尚未开通"),
    TASK_ISNULL("100811", "任务不存在"),
    NFT_ISNULL("100812", "你还没有属于自己的NFT哦~"),
    ACCOUNT_EXISTS("100813", "账户已存在"),
    QUANTITY_ERROR("100814", "数量错误"),
    THEME_NOT_EXISTS("100815", "主题不存在"),
    PASSWORD_EXISTS("100816", "口令不存在"),
    TOKEN_NOT_EXISTS("100817", "您还未发行属于自己的积分哦~"),
    TOKEN_CODE_EXISTS("100818", "code已存在哦~"),
    UNDER_STOCK("100819", "库存不足~"),
    POSITION_NOT_EXISTS("100821", "身份不存在~"),
    ADD_TENANT_FAIL("100822", "添加租户失败~"),
    APP_NOT_FEE_CONFIG("100823", "该应用未添加收费配置~"),
    CUSTOMER_ERROR("100825", "客户不存在！"),
    VOTE_ERROR("100826", "请勿重复投票！"),
    VOTEPROPOSAL_ERROR("100827", "您已完成该提案，请勿重复选择！"),
    PUBLISH_ERROR("100828", "请至少添加两条候选项！"),
    DAO_ID_IS_NULL("100829", "DaoId不能为空"),

    // 文件上传
    UPLOAD_PICTURE_NOT_EMPTY("100701", "上传图片不能为空"),
    TOKEN_ERROR("000001", "token失效"),
    SIGN_INVALIT("800000", "无效的签名"),
    SIGN_NOT_EMPTY("800001", "签名参数不能为空"),

    // 分页 100900
    KYC_NFT_OBTAINED("100832", "您已经领取"),
    NFT_OBTAINED("100832", "NFT已领取完"),

    METADATA_FORMAT_NOT_RIGHT("100833", "元数据格式不正确"),
    CUSTOMER_USERNAME_IS_EXISTS("100901", "用户名已经存在"),
    CUSTOMER_DAO_IS_EXISTS("100901", "您已经加入"),


    /***********************************支付类 100900 开头*****************************************/
    PAYMENT_NOT_EXISTS("100900", "支付信息不存在"),
    PAYMENT_STATUS_ERROR("100901", "支付状态错误"),
    PAYMENT_AMOUNT_ERROR("100902", "支付金额错误"),
    PAYMENT_METHOD_ERROR("100903", "支付方式错误"),
    PAYMENT_ORDER_ERROR("100904", "支付订单错误"),
    PAYMENT_ORDER_NOT_EXISTS("100905", "支付订单不存在"),
    PAYMENT_ORDER_STATUS_ERROR("100906", "支付订单状态错误"),
    PAYMENT_WECHAT_PARSE_CALLBACK_ERROR("100907", "微信支付解析回调请求"),
    PAY_CHANNEL_CONFIG_NOT_EXIST("100908", "支付渠道配置不存在"),

    /***********************************区块链服务类 200000 开头*****************************************/
    WALLET_CREATE_FAIL("200000", "创建链钱包失败~"),
    NOT_SUPPORTED_ChAIN_TYPE("200001", "不支持的链类型！"),
    NOT_SUPPORTED_ChAIN_ENV("200002", "不支持的链网络类型！"),
    NOT_FOUND_CONTRACT("200003", "未找到合约！"),
    NOT_FOUND_CONTRACT_BYTECODE("200004", "未找到合约字节码！"),
    SODILITY_TYPE_CONCERT_ERROR("200005", "sodility类型转换错误！"),
    DEPLOY_CONTRACT_ERROR("200005", "部署合约失败！"),
    NOT_ENOUGH_GAS_FEE("200010", "gas费不足！"),
    HASH_NON_EXISTENT("200011", "交易Hash不存在！"),
    TRANSACTION_NOT_CONFIRMED("200012", "交易未确认！"),
    DESIRED_MINT_FAILED("200013", "铸造积分不满足期望值"),
    TRANSACTION_ON_PAUSE("200014", "交易处于冻结状态"),
    NOT_FOUND_NFT_METADATA_IMAGE("200015", "未找到元数据中的图片！"),
    NOT_FOUND_NFT_METADATA_TEMPLATE("200016", "未找到元数据模板！"),
    NOT_FOUND_NFT_METADATA("200017", "未找到元数据！"),
    ILLEGAL_TOKEN_ID("200018", "tokenId需要大于0！"),

    ILLEGAL_ASSETS_PROTOCOL("200019", "不支持的资产协议！"),

    DIGITAL_ASSETS_ITEM_NOT_EXISTS("200020", "数字资产未注册到应用中！"),


    DIGITAL_ASSETS_COLLECTION_NOT_EXISTS("200021", "数字资产模板未注册到应用中！"),

    INSUFFFICIENT_INVENTORY("200022", "库存不足！"),
    GENERATE_MATADATA_FILE_FAILED("200023", "生成metadata文件失败！"),
    TRANSACTION_CONFIRMED_TIMEOUT("200024", "链上交易确认超时！"),

    /***********************************客户类 300000 开头*****************************************/
    CUSTOMER_NO_NOT_ISNULL("300000", "客户编号不能为空！"),
    CUSTOMER_WALLET_ISNULL("300001", "客户钱包未配置！"),
    SMS_VERIFY_CODE_NO_USE("300002", "验证码已失效！"),
    CUSTOMER_WALLET_PRIVATEKEY_ERROR("300003", "客户钱包私钥错误！"),
    CUSTOMER_APP_WALLET_ISNULL("300004", "客户应用钱包未配置！"),
    WEB3_LOGIN_FAIL("300005", "钱包登录失败！"),
    CUSTOMER_IS_NOT_NULL("300006", "至少选择一位会员！"),
    MERCHANT_WALLET_PRIVATEKEY_ERROR("300007", "商户钱包私钥错误！"),
    CUSTOMER_WALLET_ADDRESS_ERROR("300008", "商户钱包地址错误！"),

    MERCHANT_NO_IS_NULL("300009", "商户号不能为空！"),
    MERCHANT_NOT_EXISTS("300010", "商户账号不存在！"),

    GRADE_NOT_EXISTS("300011", "等级不存在！"),
    EQUITY_NOT_EXISTS("300012", "权益不存在！"),
    SYS_AGENT_NOT_EXISTS("300013", "代理商账号不存在！"),
    CONDITION_NOT_EXISTS("300014", "条件不存在！"),
    MEMBER_NOT_EXISTS("300015", "会员账号不存在！"),
    CUSTOMER_NO_IS_NULL("300016", "客户号不能为空！"),
    BIZ_ROLE_TYPE_ERROR("300017", "业务角色类型错误！"),

    INVITE_CODE_ERROR("300018", "邀请码错误！"),
    INVITE_RELATION_NOT_EXISTS("300019", "邀请关系不存在！"),


    /***********************************分销类 310000 开头*****************************************/
    DIS_MODEL_NOT_EXISTS("310001", "分销模型不存在~"),

    /***********************************账户类 400000 开头*****************************************/
    API_CONSUMING_FAIL("400000", "账户余额不足或是计费异常！"),
    ACCOUNT_BALANCE_ANNORMAL("400001", "账户余额异常！"),
    API_LIMITING("400002", "请勿频繁调用!"),
    CUSTOMER_ACCOUNT_IS_NULL("400003", "客户账户不存在!"),


    HAS_CHANGE("400300", "已兑换"),
    ACCOUNT_NOT_EXISTS("400201", "账户不存在或已被冻结"),
    ACCOUNT_BALANCE_INSUFFICIENT("400204", "账户余额不足"),
    ACCOUNT_TYPE_FALSE("400205", "账户类型错误"),
    AMOUNT_MUST_GREATER_THAN_ZERO("400206", "金额必须大于0"),
    POINT_RULE_FINISH("400207", "积分规则已完成"),
    TYPE_NOT_EXISTS("400208", "类型不存在"),
    STATUS_NOT_EXISTS("400209", "状态不存在"),
    CODE_EXISTS("400210", "名称对应的编号存在"),
    FEE_NOT_CONFIG("400211", "交易手续费未配置"),


    /***********************************数字资产类 500000 开头*****************************************/
    NFT_INVERTORY_NOT_ENOUGH("500000", "NFT已领取完，下次再来！"),
    TOKEN_ID_MINTED("500001", "TOKEN ID 已经被铸造！"),
    NFT_IS_NOT_ENOUPH("500002", "NFT未集齐，无法合成！"),
    INSUFFUCIENT_ASSETS_BALANCE("500003", "数字资产余额不足！"),

    /***********************************数字资产类 600000 dao相关 *****************************************/

    /***********************************数字资产类 650000 任务相关 *****************************************/
    NON_CLAIM_CONDITION("649000", "不具备条件!"),
    TASK_NON_EXISTENT("650000", "任务ID不存在!"),
    TASK_NON_PUBLISH_OR_CLAIMED("650001", "任务未发布或已经领取！"),
    TASK_HAS_PUBLISHED("650002", "任务已经发布状态！"),
    TASK_NON_SUBMITED("650003", "任务未提交或已经完成！"),
    TASK_NON_CLAIMED("650004", "任务未领取！"),
    TASK_TIME_NO("650005", "当前时间非任务有效时间！"),
    TASK_NON_CLAIM_CONDITION("650010", "不具备领取条件！"),
    TASK_EQUITY_NON_CONFIG("650011", "请配置任务权益！"),
    TASK_NON_CLAIM_CONDITION_GRADE("650012", "不具备领取条件-会员等级不够！"),
    TASK_NON_CLAIM_CONDITION_PLEDGE("650013", "不具备领取条件-质押积分不够！"),
    TASK_NON_CLAIM_CONDITION_CLAIMED("650014", "不具备领取条件-您已领取该任务！"),
    TASK_NON_CLAIM_CONDITION_LIMIT_PATICIPANTS("650015", "参与人数超过上限！"),

    /***********************************数字资产类 651000 活动相关 *****************************************/
    ACTIVITY_NON_EXISTENT("651000", "活动ID不存在!"),
    ACTIVITY_NON_PUBLISH_OR_CLAIMED("651001", "活动未发布或已经结束！"),
    ACTIVITY_HAS_PUBLISHED("650002", "活动已经发布状态！"),
    ACTIVITY_TIME_NO("615005", "当前时间非活动有效时间！"),
    ACTIVITY_NON_CLAIM_CONDITION_CLAIMED("650014", "不具备领取条件-您已领报名任务！"),

    /***********************************数字资产类 700000 ipfs相关*****************************************/
    TOKEN_ID_METADATA_IMAGE_HAS_EXISTS("700000", "tokenId对应的资源已经存在！"),
    TOKEN_ID_METADATA_IMAGE_NOT_EXISTS("700001", "tokenId对应的资源不存在！"),
    CONTRACT_NOT_SETTING_SPONSOR("700002", "合约暂未设置赞助费用，请设置合约赞助费用！"),
    IPFS_DIR_IS_EXISTS("700003", "ipfs文件目录已经存在！"),
    IPFS_MK_DIR_ERROR("700004", "ipfs创建租户根目录失败！"),
    BC_POINTS_EXISTS("700005", "您已经创建商户联合曲线积分！"),

    /***********************************工作流引擎 800000相关*****************************************/
    SUSPEND_PROCESS_INSTANCE_FAIL("800000", "流程实例挂起失败！"),
    ACTIVATE_PROCESS_INSTANCE_FAIL("800001", "流程实例激活失败！"),
    PROCESS_DEFINITION_DEPLOY_FAIL("800002", "部署流程定义！"),
    PROCESS_INSTANCE_START_FAIL("800003", "启动流程实例失败！"),
    NULL_PARAMETER("800004", "流程实例激活失败！"),
    SUSPEND_FAILURE("800005", "流程挂起失败！"),
    TASK_OPERATION_NOT_ALLOWED_IN_SUSPENDED_INSTANCE("800006", "流程实例激活失败！"),
    TASK_OPERATION_NOT_ALLOWED_IN_SUSPENDED_TASK("800007", "流程实例激活失败！"),
    ACTIVATE_FAILURE("800008", "流程实例激活失败！"),
    PROCESS_INSTANCE_NOT_EXISTS("800009", "流程实例删除失败！"),

    /***********************************工作流admin 900000相关*****************************************/
    MODEL_TYPE_ADD_FAIL("900001", "模型类型添加失败"),
    MODEL_TYPE_UPDATE_FAIL("900002", "模型类型更新失败"),
    MODEL_NOT_EXIST("900003", "模型不存在"),
    MODEL_KEY_ALREADY_EXIST("900004", "模型key已存在"),

    /*********************************** saas子应用 110000相关*****************************************/
    APP_INFO_ERROR("110001", "获取应用信息失败！"),


    /*********************************** oms 120000*****************************************/
    CATEGORY_EXIST("120001", "该分类已存在"),
    CATEGORY_HAD_USED("120002", "分类已绑定商品"),
    CATEGORY_HAD_CHILD("120003", "分类下存在子集"),
    ORDER_TYPE_NOT_TRUE("120004", "订单类型不正确"),
    GOODS_NOT_EXIST("120005", "商品不存在"),
    GOODS_STATUS_NOT_SALE("120006", "商品已下架"),
    GOODS_SPEC_NOT_EXIST("120007", "商品规格不存在"),
    GOODS_UNDER_STOCK("120008", "库存不足"),
    GOODS_SPEC_ID_ISNULL("120009", "规格ID为空"),
    TYPE_IS_EMPTY("120010", "类型不能为空！"),
    ORDER_NOT_EXIST("120011", "订单不存在！"),

    ;

    private String code;
    private String message;


    private ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String getReturnCode() {
        return code;
    }

    @Override
    public String getReturnMessage() {
        return message;
    }

}
