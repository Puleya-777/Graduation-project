package com.order.model;

public enum Status {
    // ref: cn.edu.xmu.ooad.util.ResponseCode
    OK(0, "成功"),
    INTERNAL_SERVER_ERR(500, "服务器内部错误"),
    AUTH_INVALID_JWT(501, "JWT不合法"),
    AUTH_JWT_EXPIRED(502, "JWT过期"),
    FIELD_NOTVALID(503, "字段不合法"),
    RESOURCE_ID_NOTEXIST(504, "操作的资源id不存在"),
    RESOURCE_ID_OUTSCOPE(505, "操作的资源id不是自己的对象"),
    FILE_NO_WRITE_PERMISSION(506, "目录文件夹没有写入的权限"),
    RESOURCE_FALSIFY(507, "信息签名不正确"),
    IMG_FORMAT_ERROR(508, "图片格式不正确"),
    IMG_SIZE_EXCEED(509, "图片大小超限"),
    Log_Bigger(610,"开始时间大于结束时间"),
    SKU_NOTENOUGH(900, "商品规格库存不够"),
    SKUSN_SAME(901, "商品规格重复"),
    SKUPRICE_CONFLICT(902, "商品浮动价格时间冲突"),
    USER_NOTBUY(903, "用户没有购买此商品"),
    COUPON_ACTIVITY_STATE_DENIED(904, "优惠活动状态禁止"),
    COUPON_STATENOTALLOW(905, "优惠卷状态禁止"),
    PRESALE_STATENOTALLOW(906, "预售活动状态禁止"),
    GROUPON_STATENOTALLOW(907, "团购活动状态禁止"),
    USER_HAS_SHOP(908, "用户已经有店铺"),
    COUPON_NOTBEGIN(909, "未到优惠卷领取时间"),
    COUPON_FINISH(910, "优惠卷领罄"),
    COUPON_END(911, "优惠卷活动终止"),
    Log_END_NULL(612,"结束时间不能为空"),

    LOGIN_REQUIRED(704, "未登录"), // !!! actually for gateway

    // ShopService
    SHOP_ALREADY_AUDITED(912, "店铺已完成审核"),
    SHOP_STATE_DENIED(980, "店铺状态禁止"),
    SKU_DISABLED(914, "商品已禁用"),
    FLASH_SALE_STATE_DENIED(923, "店铺状态禁止"),

    // GoodsService
    SKU_NOT_HAVE_SPU(916, "商品sku无spu集合"),
    DATABASE_OPERATION_ERROR(917, "数据库操作错误"),
    SPU_NOTOPERABLE(926,"失效的SPU"),
    SHOP_CREATE_GATEWAY_DENIED(918, "权限网关拒绝注册店铺"),

    //CommentService
    COMMENT_CONFIRMED(919, "评论已审核"),
    STATE_NOCHANGE(920,"状态未改变"),
    COMMENT_CREATED(941, "已评论"),


    GOODS_STATE_DENIED(922, "商品状态禁止"),
    COUPON_ACTIVITY_ITEM_DUPLICATED(934, "优惠活动商品重复添加"),
    COUPON_NO_NEED(924, "活动无需使用优惠券"),
    COUPON_ALREADY_CLAIMED(925, "用户已领取过此优惠券"),

    BRAND_EXISTED(990,"品牌名重复"),
    GOODSCATEGORY_EXISTED(991,"分类名重复"),

    PARENT_CATEGORY_NOT_EXIST(928,"父分类不存在"),
    ADDED_BRAND(930,"已加入品牌"),
    ADDED_CATEGORY(931,"已加入分类"),
    NOT_ADDED_BRAND(932,"未加入品牌"),
    NOT_ADDED_CATEGORY(933,"未加入分类"),
    TIMESEG_CONFLICT(604,"时段冲突");

    private final Integer errno;
    private final String errmsg;

    Status(Integer errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public Integer getErrno() {
        return errno;
    }

    public String getErrmsg() {
        return errmsg;
    }
}
