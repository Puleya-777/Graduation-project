package com.order.dao;

import com.example.model.VoObject;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.order.model.bo.OrderItems;
import com.order.model.bo.Orders;
import com.order.model.po.OrderItemPo;
import com.order.model.po.OrdersPo;
import com.order.repository.OrderItemRepository;
import com.order.repository.OrdersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderDao {
    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrdersRepository ordersRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    /**
     * 查询订单
     */
    public Mono<ReturnObject<PageInfo<VoObject>>> getOrdersByUserId(Long userId, Integer pageNum, Integer pageSize,
                                                                    String orderSn, Integer state,
                                                                    String beginTime, String endTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (userId == null)
        {
            logger.info("userId is " + userId);
            return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
        }
        if (beginTime != null && !beginTime.equals(""))
        {
            if (beginTime.contains("T"))
                beginTime = beginTime.replace("T"," ");
            LocalDateTime.parse(beginTime, df);
        }
        if (endTime != null && !endTime.equals(""))
        {
            if (endTime.contains("T"))
                endTime = endTime.replace("T"," ");
            LocalDateTime.parse(endTime, df);
        }
        if (beginTime != null && endTime != null && !beginTime.equals("") && !endTime.equals(""))
        {
            if (LocalDateTime.parse(beginTime, df).isAfter(LocalDateTime.parse(endTime, df)))
            {
                logger.info("开始时间大于结束时间");
                return Mono.just(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
            }
        }
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        final String beginTimeTemp = beginTime;
        final String endTimeTemp = endTime;

        Mono<PageInfo<OrdersPo>> ordersPos = Mono.empty();
        if(orderSn!=null&&!orderSn.equals("")&&state!=null) {
            ordersPos = ordersRepository.findByCustomerIdAndBeDeletedAndOrderSnAndState(userId, (byte) 0, orderSn, state).collect(Collectors.toList()).map(ordersPolist -> {
                if (beginTimeTemp != null && endTimeTemp != null && !beginTimeTemp.equals("") && !endTimeTemp.equals("")) {
                    List<OrdersPo> temp = new ArrayList<>();
                    //在持续时间范围内
                    for (OrdersPo ordersPo : ordersPolist) {
                        if (ordersPo.getGmtCreate().isAfter(LocalDateTime.parse(beginTimeTemp, df)) && ordersPo.getGmtCreate().isBefore(LocalDateTime.parse(endTimeTemp, df))) {
                            temp.add(ordersPo);
                        }
                    }
                    return temp;
                }
                return ordersPolist;
            }).map(PageInfo::new);
        }else {
            ordersPos = ordersRepository.findByCustomerIdAndBeDeleted(userId,(byte)0).collect(Collectors.toList()).map(ordersPolist -> {
                if (beginTimeTemp != null && endTimeTemp != null && !beginTimeTemp.equals("") && !endTimeTemp.equals("")) {
                    List<OrdersPo> temp = new ArrayList<>();
                    //在持续时间范围内
                    for (OrdersPo ordersPo : ordersPolist) {
                        if (ordersPo.getGmtCreate().isAfter(LocalDateTime.parse(beginTimeTemp, df)) && ordersPo.getGmtCreate().isBefore(LocalDateTime.parse(endTimeTemp, df))) {
                            temp.add(ordersPo);
                        }
                    }
                    return temp;
                }
                return ordersPolist;
            }).map(PageInfo::new);
        }
        Mono<List<VoObject>>  orders = ordersPos.map(pageInfo->pageInfo.getList().stream().map(Orders::new).collect(Collectors.toList()));
        return Mono.zip(ordersPos,orders).map(tuple->{
            PageInfo<VoObject> returnObject = new PageInfo<>(tuple.getT2());
            returnObject.setPages(tuple.getT1().getPages());
            returnObject.setPageNum(tuple.getT1().getPageNum());
            returnObject.setPageSize(tuple.getT1().getPageSize());
            returnObject.setTotal(tuple.getT1().getTotal());
            return new ReturnObject<>(returnObject);
        });
    }



    /**
     *创建订单
     */
    public Mono<ReturnObject<Orders>> createOrders(Orders orders, List<OrderItems> orderItemsList)
    {

        OrdersPo ordersPo = orders.gotOrdersPo();
        return ordersRepository.save(ordersPo).map(res->{
            logger.debug("insertOrder: insert order = " + ordersPo.toString());
            Long insertOrderId = ordersPo.getId();
            orders.setId(insertOrderId);
            for (OrderItems bo: orderItemsList)
            {
                bo.setOrderId(insertOrderId);
                OrderItemPo orderItemPo = bo.gotOrderItemPo();
                orderItemRepository.save(orderItemPo);
                bo.setId(orderItemPo.getId());
            }
            orders.setOrderItemsList(orderItemsList);
            return new ReturnObject<>(orders);
        });
    }

    /**
     * 查询订单 OrderId
     */
    public Mono<ReturnObject<Orders>> findOrderById(Long id) {
        return ordersRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("getOrder: 订单数据库不存在该订单 orderid=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            OrdersPo ordersPo = resOptional.get();
            Orders orders=new Orders(ordersPo);
            return Mono.just(new ReturnObject<>(orders));
        });
    }

    /**
     * 查询订单明细 OrderId
     */
    public Mono<ReturnObject<List<OrderItemPo>>> findOrderItemById(Long orderId) {
        logger.debug("findOrderItemByOrderId: Id =" + orderId);
        return ordersRepository.findById(orderId).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("getOrder: 数据库不存在该订单 orderid=" + orderId);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            OrdersPo ordersPo = resOptional.get();
            if (ordersPo.getBeDeleted()==null||ordersPo.getBeDeleted().equals((byte)1))
            {
                logger.error("getOrder: 数据库不存在该订单 orderid=" + orderId);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            return orderItemRepository.findByOrderId(orderId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional2-> {
                if (!resOptional2.isPresent()) {
                    logger.error("getOrder: 订单明细数据库不存在该订单 orderid=" + orderId);
                    return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
                }
                List<OrderItemPo> orderItemPos = resOptional2.get();
                return Mono.just(new ReturnObject<>(orderItemPos));
            });
        });

    }

    /**
     * 修改订单
     */
    public Mono<ReturnObject<Orders>> updateOrder(Orders orders) {
        OrdersPo ordersPo = orders.gotOrdersPo();
        System.out.println("dao: "+ ordersPo.getState());
        return ordersRepository.save(ordersPo).map(res->{
            System.out.println("dao:返回结果："+ res.getState());
            logger.debug("updateRole: update orders = " + ordersPo.toString());
            return new ReturnObject<>();
        });

    }

    /**
     *获得OrdersPo
     */
    public Mono<ReturnObject<OrdersPo>> getOrdersPo(Long orderId)
    {
        return ordersRepository.findById(orderId).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("getOrder: 订单数据库不存在该订单 orderid=" + orderId);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            OrdersPo ordersPo = resOptional.get();
            return Mono.just(new ReturnObject<>(ordersPo));
        });
    }

    /**
     * 买家标记确认收货
     */
    public Mono<ReturnObject<ResponseCode>> userConfirm(Orders orders)
    {
        OrdersPo ordersPo = orders.gotOrdersPo();
        return ordersRepository.save(ordersPo).map(res->new ReturnObject<>(ResponseCode.OK));
    }

    /**
     * 买家将团购订单转为普通订单
     */
    public Mono<Integer> transOrder(Long orderId) {
        logger.debug("transOrderByOrderId: Id =" + orderId);
        return ordersRepository.findById(orderId).flatMap(ordersPo->{
            Integer type=0;
            ordersPo.setOrderType(type);
            ordersPo.setSubstate(Orders.State.HAS_PAID.getCode());
            ordersPo.setGmtModified(LocalDateTime.now());
            return ordersRepository.save(ordersPo).map(res->1);
        });
    }

    /**
     * 店家查询商户所有订单 (概要)
     */
    public Mono<ReturnObject<PageInfo<VoObject>>> getShopAllOrders(Long shopId, Long customerId, String orderSn, String beginTime, String endTime, Integer page, Integer pageSize)
    {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (shopId == null)
        {
            logger.info("userId is " + shopId);
            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
        }
        if (beginTime != null)
        {
            if (beginTime.contains("T"))
                beginTime = beginTime.replace("T"," ");
            LocalDateTime.parse(beginTime, df);
        }
        if (endTime != null)
        {
            if (endTime.contains("T"))
                endTime = endTime.replace("T"," ");
            LocalDateTime.parse(endTime, df);
        }
        if (beginTime != null && endTime != null)
        {
            if (LocalDateTime.parse(beginTime, df).isAfter(LocalDateTime.parse(endTime, df)))
            {
                logger.info("开始时间大于结束时间");
                return Mono.just(new ReturnObject<>(ResponseCode.FIELD_NOTVALID));
            }
        }

        //分页查询
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        final String beginTimeTemp = beginTime;
        final String endTimeTemp = endTime;
        Mono<PageInfo<OrdersPo>> ordersPos=Mono.empty();
        if(customerId!=null&&orderSn!=null&&!orderSn.equals(""))
        {
//            ordersPos = ordersRepository.findByShopIdAndBeDeletedAndCustomerIdAndOrderSn(shopId,(byte)0,customerId,orderSn).collect(Collectors.toList()).map(ordersPolist->{
//                List<OrdersPo> temp = new ArrayList<>();
//                //活动在持续时间范围内
//                for(OrdersPo ordersPo:ordersPolist){
//                    if(ordersPo.getGmtCreate().isAfter(LocalDateTime.parse(beginTimeTemp, df))&&ordersPo.getGmtCreate().isBefore(LocalDateTime.parse(endTimeTemp, df)))
//                    {
//                        temp.add(ordersPo);
//                    }
//                }
//                return temp;
//            }).map(PageInfo::new);
            ordersPos = ordersRepository.findByShopIdAndBeDeletedAndCustomerIdAndOrderSn(shopId,(byte)0,customerId,orderSn).collect(Collectors.toList()).map(PageInfo::new);
        }else {
//            ordersPos = ordersRepository.findByShopIdAndBeDeleted(shopId,(byte)0).collect(Collectors.toList()).map(ordersPolist->{
//                List<OrdersPo> temp = new ArrayList<>();
//                //活动在持续时间范围内
//                for(OrdersPo ordersPo:ordersPolist){
//                    System.out.println("123"+ordersPo.getGmtCreate());
//                    if(ordersPo.getGmtCreate().isAfter(LocalDateTime.parse(beginTimeTemp, df))&&ordersPo.getGmtCreate().isBefore(LocalDateTime.parse(endTimeTemp, df)))
//                    {
//                        temp.add(ordersPo);
//                    }
//                }
//                return temp;
//            }).map(PageInfo::new);
            ordersPos = ordersRepository.findByShopIdAndBeDeleted(shopId,(byte)0).collect(Collectors.toList()).map(PageInfo::new);
        }
        Mono<List<VoObject>>  orders = ordersPos.map(pageInfo->pageInfo.getList().stream().map(Orders::new).collect(Collectors.toList()));
        return Mono.zip(ordersPos,orders).map(tuple->{
            PageInfo<VoObject> returnObject = new PageInfo<>(tuple.getT2());
            returnObject.setPages(tuple.getT1().getPages());
            returnObject.setPageNum(tuple.getT1().getPageNum());
            returnObject.setPageSize(tuple.getT1().getPageSize());
            returnObject.setTotal(tuple.getT1().getTotal());
            return new ReturnObject<>(returnObject);
        });
    }


    /**
     * 店家修改订单 (留言)
     */
    public Mono<ReturnObject> shopUpdateOrder(Orders orders) {
        return isOrderBelongToShop(orders.getShopId(),orders.getId()).flatMap(returnObject->{
            if(returnObject.getCode()!=ResponseCode.OK){
                return Mono.just(returnObject);
            }
            return ordersRepository.findById(orders.getId()).flatMap(po->{
                po.setMessage(orders.getMessage());
                po.setGmtModified(LocalDateTime.now());
                return ordersRepository.save(po).map(res->new ReturnObject<>());
            });
        });
    }

    //判断order是否属于shop
    public Mono<ReturnObject> isOrderBelongToShop(Long shopId, Long orderId){
        return ordersRepository.findById(orderId).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            OrdersPo po = resOptional.get();
            if(!(po.getShopId().equals(shopId))){
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            if(po.getShopId().toString().equals("null")){
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
            return Mono.just(new ReturnObject(po));
        });
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     */
    public Mono<ReturnObject<Orders>> getOrderById(Long shopId, Long id)
    {
        return ordersRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("getOrderById: shujvkubucunzai order_id=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            OrdersPo ordersPo = resOptional.get();
            if (ordersPo.getBeDeleted()==null||ordersPo.getBeDeleted().equals((byte)1))
            {
                logger.error("getOrder: 数据库不存在该订单 orderid=" + ordersPo.getId());
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            if(!ordersPo.getShopId().equals(shopId))
            {
                logger.error("getOrderById: dianpuidbupipei order_id=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("店铺id不匹配：" + shopId)));
            }
            Orders orders = new Orders(ordersPo);
            return Mono.just(new ReturnObject<>(orders));
        });
    }

    /**
     * 管理员取消本店铺订单
     */
    public Mono<ReturnObject<Orders>> cancelOrderById(Long shopId, Long id)
    {
        return ordersRepository.findById(id).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("不存在对应的订单id" )));
            }
            OrdersPo ordersPo = resOptional.get();
            if(!ordersPo.getShopId().equals(shopId))
            {
                logger.debug("cancelOrderById: update Order fail " + ordersPo.toString() );
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("该订单不属于该店铺" )));
            }
            else if(ordersPo.getSubstate() != null && ordersPo.getSubstate().equals(Orders.State.HAS_DELIVERRED.getCode()))
            {
                return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
            }
            else if(ordersPo.getState() != null &&( ordersPo.getState().equals(Orders.State.HAS_FINISHED.getCode()) ||ordersPo.getState().equals(Orders.State.CANCEL.getCode())))
            {
                return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
            }
            else
            {
                Integer type = Orders.State.CANCEL.getCode();
                ordersPo.setState(type);
                ordersPo.setSubstate(null);
                ordersPo.setGmtModified(LocalDateTime.now());
                return ordersRepository.save(ordersPo).map(res->new ReturnObject<>());
            }
        });
    }

    /**
     * 店家对订单标记发货
     */
    public Mono<ReturnObject> shopDeliverOrder(Orders orders) {
        return isOrderBelongToShop(orders.getShopId(),orders.getId()).flatMap(returnObject->{
            if(returnObject.getCode()!=ResponseCode.OK){
                return Mono.just(returnObject);
            }
            OrdersPo ordersPo=(OrdersPo) returnObject.getData();
            if(ordersPo.getBeDeleted().equals((byte)1)){
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            if(ordersPo.getState()==null){
                return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
            }
            //订单没有处于已支付状态，则不允许改变
            if(!ordersPo.getState().equals(21)){
                //修改失败
                logger.error("shopDeliverOrder:Error Order State : " + ordersPo.toString());
                return Mono.just(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW));
            }
            //改为发货中状态
            ordersPo.setState(24);
            //设置运输sn
            ordersPo.setShipmentSn(orders.getShipmentSn());
            ordersPo.setGmtModified(LocalDateTime.now());
            return ordersRepository.save(ordersPo).map(res->new ReturnObject<>());
        });
    }


}
