package com.order.service;

import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import com.order.dao.OrderDao;
import com.order.model.bo.OrderItems;
import com.order.model.bo.Orders;
import com.order.model.po.OrderItemPo;
import com.order.model.po.OrdersPo;
import com.order.model.po.ShopPo;
import com.order.model.po.UserPo;
import com.order.model.vo.CustomerRetVo;
import com.order.model.vo.OrderCreateRetOVo;
import com.order.model.vo.OrdersVo;
import com.order.model.vo.ShopRetVo;
import com.order.feign.GoodsFeign;
import com.order.feign.UserFeign;
import com.order.repository.OrdersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private OrdersRepository ordersRepository;

//    @DubboReference
//    private IGoodsService goodsServiceI;
//
//    @DubboReference
//    private IAftersaleService aftersaleServiceI;
//
//    @DubboReference
//    private IFreightService freightServiceI;
//
//    @DubboReference
//    private IAddressService addressServiceI;
//
//    @DubboReference
//    private IActivityService activityServiceI;
//
//    @DubboReference
//    private ICustomerService customerServiceI;
//
//    @DubboReference
//    private ICartService cartServiceI;
//
//    @DubboReference
//    private IShareService shareServiceI;

    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    /**
     *查询订单
     */
    public Mono<ReturnObject<PageInfo<VoObject>>> selectOrders(Long userId, Integer pageNum, Integer pageSize,
                                                               String orderSn, Integer state,
                                                               String beginTimeStr, String endTimeStr)
    {
            return orderDao.getOrdersByUserId(userId, pageNum, pageSize, orderSn, state, beginTimeStr, endTimeStr);
    }

    /**
     * 创建订单
     */
    @Transactional
    public Mono<Long> createOrders(Long userId, OrdersVo ordersVo)
    {
//        return Mono.just((long)1);
        Orders ordersBo = ordersVo.createOrdersBo();
        ordersBo.setOrderType(0);
        ordersBo.setFreightPrice((long)17);
        ordersBo.setOriginPrice((long)67);
        ordersBo.setDiscountPrice((long)67);
        ordersBo.setOrderSn(Common.genSeqNum());

        Orders.State state = Orders.State.CREATE_ORDER;
        ordersBo.setState(state.getCode());

        ordersBo.setGmtCreated(LocalDateTime.now());

        ordersBo.setCustomerId(userId);

        OrdersPo ordersPo = ordersBo.gotOrdersPo();
        ordersPo.setBeDeleted((byte)0);
        return ordersRepository.save(ordersPo).map(res->res.getId());

//        Orders ordersBo = ordersVo.createOrdersBo();
//        List<OrderItemsCreateVo> orderItemsVo = ordersVo.getOrderItems();
//        Long activityId = ordersBo.getCouponId();
//
//        Long regionId = ordersBo.getRegionId();
//
//        // 判断couponId、presaleId、grouponId是否有效
//        // 设置订单类型
//        Integer orderType = 0;
//        if (ordersBo.getPresaleId() != null && ordersBo.getPresaleId() != 0)
//        {
////            if (!activityServiceI.judgePresaleIdValid(ordersBo.getPresaleId()).getCode().equals(ResponseCode.OK))
////            {
////                logger.info("the presale is not valid: " + ordersBo.getPresaleId());
////                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
////            }
//            orderType = 2;
//            activityId = ordersBo.getPresaleId();
//            ordersBo.setOrderType(orderType);
//        }
//        else if (ordersBo.getGrouponId() != null && ordersBo.getGrouponId() != 0)
//        {
////            if (!activityServiceI.judgeGrouponIdValid(ordersBo.getGrouponId()).getCode().equals(ResponseCode.OK))
////            {
////                logger.info("the groupon is not valid: " + ordersBo.getGrouponId());
////                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
////            }
//            orderType = 1;
//            activityId = ordersBo.getGrouponId();
//            ordersBo.setOrderType(orderType);
//        }
//        if (ordersBo.getCouponId() != null && ordersBo.getCouponId() != 0)
//        {
////            if (!activityServiceI.judgeCouponIdValid(ordersBo.getCouponId()).getCode().equals(ResponseCode.OK))
////            {
////                logger.info("the couponId is not valid: " + ordersBo.getCouponId());
////                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
////            }
//            orderType = 3;
//            activityId = ordersBo.getCouponId();
//            ordersBo.setOrderType(0);
//        }
//
//
//// 通过orderItemsVo找到全的
//        List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
//        List<Integer> countList = new ArrayList<Integer>();
//        List<Long> skuIdList = new ArrayList<Long>();
//        List<Long> couponActIdList = new ArrayList<Long>();
//
//        Long origin_price = 0L;
//
//        for (OrderItemsCreateVo vo: orderItemsVo){
//            if (!activityServiceI.judgeCouponActivityIdValid(ordersBo.getCouponActivityId()).getCode().equals(ResponseCode.OK))
//            {
//                logger.info("the couponActivity is not valid: " + ordersBo.getCouponActivityId());
//                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//            }
//            // 判断返回值 同时扣库存 存在库存不够的情况
//            ReturnObject<GoodsDetailDTO> retGoodsDetailDTO = goodsServiceI.getGoodsBySkuId(vo.getGoodsSkuId(), orderType, activityId, -vo.getQuantity());
//            if (!retGoodsDetailDTO.getCode().equals(ResponseCode.OK))
//            {
//                logger.info("The inventory is not enough, the skuId is " + vo.getSkuId());
//                int index = orderItemsVo.indexOf(vo);
//                for (int i = 0; i < index;i ++)
//                {
//                    ReturnObject<GoodsDetailDTO> addInventoryRet = goodsServiceI.getGoodsBySkuId(orderItemsVo.get(i).getGoodsSkuId(), orderType, activityId, orderItemsVo.get(i).getQuantity());
//                    if(!addInventoryRet.getCode().equals(ResponseCode.OK))
//                        logger.info("回加库存失败！skuId is " + orderItemsVo.get(i).getSkuId());
//                }
//                return Mono.just(new ReturnObject<>(ResponseCode.SKU_NOTENOUGH));
//            }
//            GoodsDetailDTO goodsDetailDTO = retGoodsDetailDTO.getData();
////            GoodsDetailDTO goodsDetailDTO = new GoodsDetailDTO("caixin", 123L, 10);
//            OrderItems orderItems = new OrderItems(vo);
//            orderItems.setName(goodsDetailDTO.getName());
//            orderItems.setPrice(goodsDetailDTO.getPrice());
//            origin_price += goodsDetailDTO.getPrice()*vo.getQuantity();
//            orderItemsList.add(orderItems);
//
//            countList.add(vo.getQuantity());
//            skuIdList.add(vo.getSkuId());
//        }
//        // 算运费
//        Long freight_Price = freightServiceI.calcuFreightPrice(countList, skuIdList, regionId).getData();
//        ordersBo.setFreightPrice(freight_Price);
//
//        // 初始价格
//        ordersBo.setOriginPrice(origin_price);
//
//        // 算discountPrice
//        Long discountPrice = 0L;
//        List<String> couponRule=goodsServiceI.getActivityRule(ordersBo.getCouponId(),couponActIdList).getData();
//        for(int i=0;i<orderItemsList.size();i++) {//设置优惠金额
//            OrderItems orderItems=orderItemsList.get(i);//订单明细
//            Gson gson=new Gson();
//            DiscountStrategy discountStrategy=gson.fromJson(couponRule.get(i),DiscountStrategy.class);
//            if(discountStrategy.getType().equals(0)){
//                if((orderItems.getPrice()*orderItems.getQuantity())>=discountStrategy.getDiscountoff())
//                {
//                    orderItemsList.get(i).setDiscount(discountStrategy.getDiscountoff());
//                }
//            }
//            else if(discountStrategy.getType().equals(1))
//            {
//                if((orderItems.getPrice()*orderItems.getQuantity())>=discountStrategy.getDiscountoff())
//                {
//                    orderItemsList.get(i).setDiscount((long)(orderItems.getPrice()*(100-discountStrategy.getDiscountoff())/100));
//                }
//            }
//            else if(discountStrategy.getType().equals(2)){
//                if(orderItems.getQuantity()>=discountStrategy.getDiscountoff())
//                {
//                    orderItemsList.get(i).setDiscount(discountStrategy.getDiscountoff());
//                }
//            }
//            else if(discountStrategy.getType().equals(3)){
//                if(orderItems.getQuantity()>=discountStrategy.getDiscountoff())
//                {
//                    orderItemsList.get(i).setDiscount((long)(orderItems.getPrice()*(100-discountStrategy.getDiscountoff())/100));
//                }
//            }
//            discountPrice+=orderItemsList.get(i).getDiscount();
//        }
//        ordersBo.setDiscountPrice(discountPrice);
//
//        // 算orderSn
//        ordersBo.setOrderSn(Common.genSeqNum());
//
//
//        // 设置状态码
//        Orders.State state = Orders.State.CREATE_ORDER;
//        ordersBo.setState(state.getCode());
//
//        ordersBo.setGmtCreated(LocalDateTime.now());
//
//        if (!cartServiceI.deleteGoodsInCart(userId, skuIdList).getData().equals(ResponseCode.OK))
//        {
//            for (OrderItemsCreateVo vo: orderItemsVo) {
//                ReturnObject<GoodsDetailDTO> addInventoryRet = goodsServiceI.getGoodsBySkuId(vo.getGoodsSkuId(), orderType, activityId, -vo.getQuantity());
//                if(!addInventoryRet.getCode().equals(ResponseCode.OK))
//                    logger.info("回加库存失败！skuId is " + vo.getSkuId());
//            }
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        }
//        ordersBo.setCustomerId(userId);
//        return orderDao.createOrders(ordersBo, orderItemsList).map(orders->{
//            if (!orders.getCode().equals(ResponseCode.OK))
//                return new ReturnObject<>(orders.getCode());
//            CustomerRetVo customerRetVo = new CustomerRetVo();
//            ShopRetVo shopRetVo = new ShopRetVo();
//            OrderCreateRetVo orderCreateRetVo = new OrderCreateRetVo(orders.getData(),customerRetVo,shopRetVo);
//            return new ReturnObject<>(orderCreateRetVo);
//        });
    }

    /**
     * 查询订单 OrderId
     */
    public Mono<ReturnObject<OrderCreateRetOVo>> getOrdersByOrderId(Long id) {
        return orderDao.findOrderById(id).flatMap(ordersRet->{
            if (!ordersRet.getCode().equals(ResponseCode.OK))
            {
                logger.info("the order not exists: " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            final Orders orders = ordersRet.getData();
            return orderDao.findOrderItemById(id).flatMap(orderItemList->{
                if (!orderItemList.getCode().equals(ResponseCode.OK))
                    return Mono.just(new ReturnObject<>(orderItemList.getCode()));
                List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
                orders.getSubstate();
                Long userId=orders.getCustomerId();
                return Mono.zip(userFeign.getById(userId),goodsFeign.getById(orders.getShopId())).map(tuple->{
                    UserPo userPo = tuple.getT1();
                    ShopPo shopPo = tuple.getT2();

                    CustomerRetVo customerRetVo = new CustomerRetVo();
                    customerRetVo.setId(userId);
                    customerRetVo.setName(userPo.getRealName());
                    customerRetVo.setUserName(userPo.getUserName());

                    ShopRetVo shopRetVo = new ShopRetVo();
                    shopRetVo.setId(shopPo.getId());
//                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    LocalDateTime gmtCreate = LocalDateTime.parse(shopPo.getGmtCreate(), df);
//                    LocalDateTime gmtModified = LocalDateTime.parse(shopPo.getGmtModified(), df);
                    shopRetVo.setGmtCreate(shopPo.getGmtCreate());
                    shopRetVo.setGmtModified(shopPo.getGmtModified());
                    shopRetVo.setName(shopPo.getName());
                    shopRetVo.setState(shopPo.getState());

                    for (OrderItemPo po: orderItemList.getData())
                    {
                        OrderItems orderItems = new OrderItems(po);
                        orderItemsList.add(orderItems);
                    }
                    orders.setOrderItemsList(orderItemsList);
                    if(orders != null) {
                        OrderCreateRetOVo orderCreateRetVo = new OrderCreateRetOVo(orders, customerRetVo, shopRetVo);
                        System.out.println(orderCreateRetVo.toString());
                        return new ReturnObject<>(orderCreateRetVo);
                    } else {
                        logger.debug("findOrdersById: Not Found");
                        return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }
                });
            });
        });
    }

    /**
     * 买家修改名下订单
     */
    public Mono<ReturnObject<VoObject>> updateOrders(Orders orders) {
        return orderDao.getOrdersPo(orders.getId()).flatMap(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            OrdersPo o = returnObject.getData();
            if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
            {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            if(o.getBeDeleted().equals((byte)1)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            //若订单已发货、完成和取消，则无法修改
            if(o.getState().equals(Orders.State.HAS_FINISHED.getCode())||o.getState().equals(Orders.State.CANCEL.getCode())|| Objects.equals(o.getSubstate(), Orders.State.HAS_DELIVERRED.getCode()) )
            {
                return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
            }
            Orders tempOrders = new Orders();
            tempOrders = tempOrders.updateOrders(o);
            tempOrders = tempOrders.changeOrders(orders);
            return orderDao.updateOrder(tempOrders).map(retObj->{
                if (retObj.getCode().equals(ResponseCode.OK)) {
                    return new ReturnObject<>(retObj.getData());
                } else {
                    return new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
                }
            });
        });
    }

    /**
     * 买家取消，逻辑删除本人名下订单
     */
    public Mono<ReturnObject<Orders>> cancelOrders(Orders orders) {
        return orderDao.getOrdersPo(orders.getId()).flatMap(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            OrdersPo o = returnObject.getData();
            if(returnObject.getData().getBeDeleted().equals((byte)1)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            System.out.println(o.getState());
            //若订单已发货，则无法修改
            if(o.getSubstate()!=null){
                if(returnObject.getData().getSubstate().equals(Orders.State.HAS_DELIVERRED.getCode()))
                {
                    System.out.println("订单已发货，无法修改");
                    return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
                }
            }
            if(o.getState().equals(Orders.State.TO_BE_PAID.getCode())||o.getState().equals(Orders.State.TO_BE_SIGNED_IN.getCode())){
                System.out.println("第一个if");
                Orders tempOrders =  new Orders();
                tempOrders = tempOrders.updateOrders(o);
                System.out.println("修改前service： "+ tempOrders.getState());
                tempOrders.setGmtModified(orders.getGmtModified());
                tempOrders.setState(4);
                tempOrders.setSubstate(null);
                System.out.println("修改后service： "+ tempOrders.getState());
                return orderDao.updateOrder(tempOrders);
            }
            if(o.getState().equals(Orders.State.CANCEL.getCode())||o.getState().equals(Orders.State.HAS_FINISHED.getCode())){
                System.out.println("第二个if");
                Orders tempOrders =  new Orders();
                tempOrders = tempOrders.updateOrders(o);
                tempOrders.setGmtModified(orders.getGmtModified());
                tempOrders.setBeDeleted((byte)1);
                return orderDao.updateOrder(tempOrders);
            }
            return Mono.just(new ReturnObject<>());
        });
    }

    /**
     * 买家标记确认收货
     */
    @Transactional
    public Mono<ReturnObject<ResponseCode>> userConfirmState(Long orderId)
    {
        return orderDao.getOrdersPo(orderId).flatMap(ordersPoReturnObject->{
            if (!ordersPoReturnObject.getCode().equals(ResponseCode.OK))
            {
                logger.info("not found order, order Id is: " + orderId);
                return Mono.just(new ReturnObject<>(ordersPoReturnObject.getCode()));
            }
            Orders orders = new Orders(ordersPoReturnObject.getData());
            System.out.println("service： "+orders.getState());
            if (orders.getBeDeleted().equals((byte)1))
            {
                logger.info("order is logical deleted");
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }if (orders.getState().equals(Orders.State.TO_BE_SIGNED_IN.getCode())
                    &&( orders.getSubstate()==null || orders.getSubstate().equals(Orders.State.HAS_DELIVERRED.getCode())))
            {
                orders.setState(Orders.State.HAS_FINISHED.getCode());
                orders.setGmtModified(LocalDateTime.now());
                return orderDao.userConfirm(orders);
            }
            logger.info("不能收货!");
            return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
        });
    }

    /**
     * 买家将团购订单转为普通订单
     */
    @Transactional
    public Mono<ReturnObject<VoObject>> transOrder(Long id) {
        return orderDao.findOrderById(id).flatMap(ordersRet->{
            if (ordersRet.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
            {
                logger.info("the order not exists: " + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            Orders orders = ordersRet.getData();
            if(orders==null||orders.getOrderType()==null||orders.getBeDeleted()==(byte)1){//订单不存在
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            else if(orders.getOrderType()==1&&orders.getBeDeleted()!=(byte)1) {
                System.out.println("这里");
                if(orders.getSubstate()==22||orders.getSubstate()==23||orders.getState()==2) {
                    return orderDao.transOrder(id).map(ret->{
                        if(ret == 1) {
                            Integer type=0;
                            orders.setOrderType(type);
                            orders.setGmtModified(LocalDateTime.now());
                            return new ReturnObject<>(orders);
                        }else{
                            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                        }
                    });
                }else {
                    return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
                }
            }else if(orders.getOrderType()!= 1){
                System.out.println("该订单不是团购订单，无法进行转换");
//                logger.debug("该订单不是团购订单，无法进行转换");
                return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
            }
            return Mono.empty();
        });
    }

    /**
     * 店家查询商户所有订单 (概要)
     */
    public Mono<ReturnObject<PageInfo<VoObject>>> getShopAllOrders(Long shopId, Long customerId, String orderSn, String beginTime, String endTime, Integer page, Integer pageSize)
    {
        return orderDao.getShopAllOrders(shopId,customerId, orderSn, beginTime, endTime, page, pageSize);
    }

    /**
     * 店家修改订单 (留言)
     */
    public Mono<ReturnObject<Object>> shopUpdateOrder(Orders orders) {
        return orderDao.shopUpdateOrder(orders).map(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return returnObject;
            }else {
                return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
            }
        });
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     */
    public Mono<ReturnObject> getOrderById(Long shopId, Long id)
    {
        return orderDao.getOrderById(shopId,id).flatMap(ordersReturnObject->{
            if(!ordersReturnObject.getCode().equals(ResponseCode.OK))
            {
                return Mono.just(new ReturnObject<>(ordersReturnObject.getCode()));
            }
            Orders orders = ordersReturnObject.getData();
            return orderDao.findOrderItemById(id).flatMap(orderItemPos->{
                if (!orderItemPos.getCode().equals(ResponseCode.OK))
                    return Mono.just(new ReturnObject<>(orderItemPos.getCode()));
                List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
                for(OrderItemPo po : orderItemPos.getData())
                {
                    OrderItems orderItems = new OrderItems(po);
                    orderItemsList.add(orderItems);
                }

                return Mono.zip(userFeign.getById(orders.getCustomerId()),GoodsFeign.getById(shopId)).map(tuple->{
                    UserPo userPo = tuple.getT1();
                    ShopPo shopPo = tuple.getT2();

                    CustomerRetVo customerRetVo = new CustomerRetVo();
                    customerRetVo.setId(orders.getCustomerId());
                    customerRetVo.setName(userPo.getRealName());
                    customerRetVo.setUserName(userPo.getUserName());

                    ShopRetVo shopRetVo = new ShopRetVo();
                    shopRetVo.setId(shopId);
                    shopRetVo.setName(shopPo.getName());
                    shopRetVo.setState(shopPo.getState());
//                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    LocalDateTime gmtCreate = LocalDateTime.parse(shopPo.getGmtCreate(), df);
//                    LocalDateTime gmtModified = LocalDateTime.parse(shopPo.getGmtModified(), df);
                    shopRetVo.setGmtCreate(shopPo.getGmtCreate());
                    shopRetVo.setGmtModified(shopPo.getGmtModified());

                    orders.setOrderItemsList(orderItemsList);
                    if(orders != null) {
                        return new ReturnObject(new OrderCreateRetOVo(orders, customerRetVo, shopRetVo));
                    } else {
                        logger.debug("findOrdersById: Not Found");
                        return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                    }
                });
            });
        });
    }

    /**
     * 管理员取消本店铺订单
     */
    public Mono<ReturnObject<VoObject>> cancelOrderById(Long shopId,Long id)
    {
        return orderDao.cancelOrderById(shopId, id).map(returnObject->{
            if(returnObject.getCode() == ResponseCode.OK)
                return new ReturnObject<>(returnObject.getData());
            else
                return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
        });
    }

    /**
     * 店家对订单标记发货
     */
    public Mono<ReturnObject> shopDeliverOrder(Orders orders) {
        return orderDao.shopDeliverOrder(orders).map(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return returnObject;
            }else {
                return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
            }
        });
    }

}
