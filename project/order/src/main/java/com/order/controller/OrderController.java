package com.order.controller;

import com.example.annotation.LoginUser;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ResponseUtil;
import com.example.util.ReturnObject;
import com.order.model.bo.Orders;
import com.order.model.vo.*;
import com.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class OrderController {

    @Autowired
    private OrderService orderService;


    private  static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * 获得订单的所有状态
     */
    @GetMapping("/orders/states")
    public Mono getOrderState()
    {
        Orders.State[] states=Orders.State.class.getEnumConstants();
        List<OrderStateVo> orderStateVos =new ArrayList<OrderStateVo>();
        for(int i=0;i<states.length;i++){
            orderStateVos.add(new OrderStateVo(states[i]));
        }
        return Mono.just(ResponseUtil.ok(new ReturnObject<List>(orderStateVos).getData()));
    }

    /**
     *查询订单
     */
    @GetMapping("/orders")
    public Mono selectAllOrders(
            @LoginUser @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        return orderService.selectOrders(userId, page, pageSize, orderSn, state, beginTime, endTime).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.FIELD_NOTVALID)
            {
//                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID,ResponseCode.FIELD_NOTVALID.getMessage()));

            }
            else if (returnObject.getCode() == ResponseCode.OK)
                return Common.getPageRetObject(returnObject);
            else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 创建订单
     */
    @PostMapping("/orders")
    public Mono createOrders(@LoginUser @RequestParam(required = false) Long userId,
                             @RequestBody OrdersVo ordersVo) {
        if (ordersVo.getCouponId() != null && ordersVo.getGrouponId() != null)
            return Mono.just(Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID)));
        if (ordersVo.getPresaleId() != null && ordersVo.getGrouponId() != null)
            return Mono.just(Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID)));
        if (ordersVo.getPresaleId() != null && ordersVo.getCouponId() != null)
            return Mono.just(Common.decorateReturnObject(new ReturnObject<>(ResponseCode.FIELD_NOTVALID)));
        return orderService.createOrders(userId,ordersVo).map(res->ResponseUtil.ok(res));
    }

    /**
     * 查询订单 OrderId
     */
    @GetMapping("/orders/{id}")
    public Mono getOrdersByOrderId(@PathVariable("id") Long id,
                                    @LoginUser Long userId){
        System.out.println("userId: "+userId);
        return orderService.getOrdersByOrderId(id,userId).map(returnObject->{
            if (returnObject.getCode().equals(ResponseCode.OK)) {
                return Common.decorateReturnObject(returnObject);
            } else {
                if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE))
                {
                    return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
                }
                else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
                {
                    return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
                }
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 买家修改名下订单
     */
    @PutMapping("/orders/{id}")
    public Mono updateOrder(@PathVariable("id") Long id, @RequestBody OrderSimpleVo vo) {
        Orders orders=vo.createOrders();
        orders.setId(id);
        orders.setGmtModified(LocalDateTime.now());
        return orderService.updateOrders(orders).map(retObject->{
            if(retObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST){
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
            }
            return Common.decorateReturnObject(retObject);
        });
    }

    /**
     * 买家取消，逻辑删除名下订单
     */
    @DeleteMapping("/orders/{id}")
    public Mono logicDeleteOrder( @PathVariable("id") Long id) {
        Orders orders=new Orders();
        orders.setId(id);
        orders.setGmtModified(LocalDateTime.now());
        return orderService.cancelOrders(orders).map(Common::decorateReturnObject);
    }

    /**
     * 买家标记确认收货
     */
    @PutMapping("/orders/{id}/confirm")
    public Mono updateOrderStateToConfirm( @PathVariable("id") Long id) {
        return orderService.userConfirmState(id).map(returnObject->{
            if (returnObject.getCode().equals(ResponseCode.OK))
            {
                return Common.decorateReturnObject(returnObject);
            }
            else if (returnObject.getCode().equals(ResponseCode.ORDER_STATENOTALLOW))
            {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW,ResponseCode.ORDER_STATENOTALLOW.getMessage()));
            }
            else if (returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE))
            {
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
            }
            return Common.decorateReturnObject(returnObject);
        });
    }

    /**
     * 买家将团购订单转为普通订单
     */
    @PostMapping("/orders/{id}/groupon-normal")
    public Mono transOrder(@PathVariable("id") Long id) {
        return orderService.transOrder(id).map(retObject->{
//            if(retObject.getCode()==ResponseCode.OK)
//                httpServletResponse.setStatus(HttpStatus.CREATED.value());
//            else if(retObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
//                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
//            else if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
//            else if(retObject.getCode()==ResponseCode.ORDER_STATENOTALLOW)
//                httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.decorateReturnObject(retObject);
        });
    }

    /**
     * 店家查询商户所有订单 (概要)
     */
    @GetMapping("/shops/{shopId}/orders")
    public Mono getShopAllOrders(@PathVariable("shopId") Long shopId,
//                                          @Depart @ApiIgnore Long departId,
                                          @RequestParam(required = false) Long customerId,
                                          @RequestParam(required = false) String orderSn,
                                          @RequestParam(required = false) String beginTime,
                                          @RequestParam(required = false) String endTime,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        return orderService.getShopAllOrders(shopId,customerId, orderSn, beginTime, endTime, page, pageSize).map(Common::getPageRetObject);
    }

    /**
     * 店家修改订单 (留言)
     */
    @PutMapping("/shops/{shopId}/orders/{id}")
    public Mono shopUpdateOrder(@PathVariable("id") Long orderId,
                                  @RequestBody MessageVo vo,
                                  @PathVariable("shopId") Long shopId){
        Orders orders=vo.createOrder();
        orders.setId(orderId);
        orders.setShopId(shopId);
        return orderService.shopUpdateOrder(orders).map(retObject->{
            if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
            }
            return Common.decorateReturnObject(retObject);
        });
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     */
    @GetMapping("/shops/{shopId}/orders/{id}")
    public Mono getOrderById(@PathVariable("shopId") Long shopId,
                               @PathVariable("id") Long id){
        return orderService.getOrderById(shopId, id).map(returnObject->{
//            if (returnObject.getCode() == ResponseCode.OK) {
//                return Common.decorateReturnObject(returnObject);
//            }
//            else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
//            {
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
//                return Common.decorateReturnObject(returnObject);
//            }
//            else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
//            {
//                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
//                return Common.decorateReturnObject(returnObject);
//            }
//            else {
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
//                return Common.decorateReturnObject(returnObject);
//            }
            return Common.decorateReturnObject(returnObject);
        });
    }

    /**
     * 管理员取消本店铺订单
     */
    @DeleteMapping("/shops/{shopId}/orders/{id}")
    public Mono cancelOrderById(@PathVariable("shopId") Long shopId,
                                  @PathVariable("id") Long id) {
        return orderService.cancelOrderById(shopId, id).map(returnObject->{
//            if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE))
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
//            if(returnObject.getCode().equals(ResponseCode.ORDER_STATENOTALLOW))
//                httpServletResponse.setStatus(HttpStatus.OK.value());
            return Common.decorateReturnObject(returnObject);
        });
    }

    /**
     * 店家对订单标记发货
     */
    @PutMapping("/shops/{shopId}/orders/{id}/deliver")
    public Mono shopDeliverOrder(@PathVariable("id") Long orderId,
                                   @RequestBody FreightSnVo vo,
                                   @PathVariable("shopId") Long shopId){
        Orders orders=vo.createOrder();
        orders.setId(orderId);
        orders.setShopId(shopId);
        orders.setGmtModified(LocalDateTime.now());
        return orderService.shopDeliverOrder(orders).map(retObject->{
            if(retObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
//                return Common.getNullRetObj(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()), httpServletResponse);
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
            }
            return Common.decorateReturnObject(retObject);
        });
    }

    //测试nacos




}
