package com.order.controller;


import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ResponseUtil;
import com.example.util.ReturnObject;
import com.order.model.bo.Payment;
import com.order.model.bo.Refund;
import com.order.model.vo.*;
import com.order.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class PaymentController {
    private  static  final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;


//    @DubboReference
//    private IOrderService iOrderService;

    /**
     * 获得支付单的所有状态
     */
    @GetMapping("/payments/states")
    public Mono getAllPaymentsStates()
    {
        Payment.State[] states = Payment.State.class.getEnumConstants();
        List<PaymentStateVo> stateVos=new ArrayList<PaymentStateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new PaymentStateVo(states[i]));
        }
        return Mono.just(ResponseUtil.ok(new ReturnObject<List>(stateVos).getData()));
    }


    /**
     * 获得支付渠道，目前只返回002 模拟支付渠道
     */
    @GetMapping("/payments/patterns")
    public Mono userQueryPayment()
    {
        List<PayPatternVo> payPatternVos=new ArrayList<PayPatternVo>();
//        payPatternVos.add(new PayPatternVo("001","返点支付"));
        payPatternVos.add(new PayPatternVo("002","模拟支付渠道"));
        return Mono.just(ResponseUtil.ok(new ReturnObject<List>(payPatternVos).getData()));
    }

    /**
     * 买家为订单创建支付单
     */
    @PostMapping("/orders/{id}/payments")
    public Mono createPayment(@RequestBody PaymentVo vo,
                                @PathVariable("id") Long orderId){
        Payment payment = vo.createPayment();
        payment.setOrderId(orderId);
        payment.setGmtCreate(LocalDateTime.now());

//        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return paymentService.createPayment(payment).map(Common::decorateReturnObject);
    }

    /**
     * 买家查询自己的支付信息
     */
    @GetMapping("/orders/{id}/payments")
    public Mono userQueryPayment(@PathVariable("id") Long orderId){
        return paymentService.userQueryPayment(orderId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getListRetObject(returnObject);
            }
            else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST) {
//                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return Common.decorateReturnObject(returnObject);
            }else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 管理员查询订单的支付信息
     */
    @GetMapping("/shops/{shopId}/orders/{id}/payments")
    public Mono queryPayment(@PathVariable("shopId") Long shopId,
                               @PathVariable("id") Long orderId){
        return paymentService.queryPayment(shopId, orderId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                System.out.println(returnObject.getData().toString());
                return Common.getListRetObject(returnObject);
            }
            else if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE){
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("操作的资源id不是自己的对象")));

            }
            else {
                return Common.decorateReturnObject(returnObject);
            }
        });

    }

    /**
     * 买家为售后单创建支付单
     */
    @PostMapping("/aftersales/{id}/payments")
    public Mono createPaymentByAftersaleId( @RequestBody PaymentVo vo,
                                             @PathVariable("id") Long aftersaleId){
        //需要通过aftersaleId从其他模块的aftersale表中获取orderid等信息
        Payment payment = vo.createPayment();
        payment.setGmtCreate(LocalDateTime.now());

        return paymentService.createPaymentByAftersaleId(payment,aftersaleId).map(retObject->{
//            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            if (retObject.getCode() == ResponseCode.OK) {
                return Common.getRetObject(retObject);
            } else {
                return Common.decorateReturnObject(retObject);
            }
        });
    }

    /**
     * 买家查询自己的支付信息
     */
    @GetMapping("/aftersales/{id}/payments")
    public Mono customerQueryPaymentByAftersaleId(@PathVariable("id") Long aftersaleId){
        return paymentService.customerQueryPaymentByAftersaleId(aftersaleId).map(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
//                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return Common.decorateReturnObject(returnObject);
            }
            else if (returnObject.getCode().equals(ResponseCode.OK)) {
                return Common.getListRetObject(returnObject);
            }
            else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 管理员查询售后单的支付信息
     */
    @GetMapping("/shops/{shopId}/aftersales/{id}/payments")
    public Mono getPaymentByAftersaleId(@PathVariable("shopId") Long shopId,@PathVariable("id") Long aftersaleId)  {
        return paymentService.getPaymentByAftersaleId(shopId,aftersaleId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.getListRetObject(returnObject);
            } else {
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 管理员创建退款信息
     */
    @PostMapping("/shops/{shopId}/payments/{id}/refunds")
    public Mono insertRole(@RequestBody amountVo amount,
                             @PathVariable("id") Long id, @PathVariable("shopId") Long shopId) {
        Refund refund=new Refund();
        refund.setPaymentId(id);
        refund.setAmount(amount.getAmount());
        refund.setGmtCreate(LocalDateTime.now());

        return paymentService.insertRefunds(refund,shopId).map(retObject->{
//            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            if (retObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(retObject);
            } else {
                return Common.decorateReturnObject(retObject);
            }
        });
    }

    /**
     * 管理员查询订单退款信息
     */
    @GetMapping("/shops/{shopId}/orders/{id}/refunds")
    public Mono getOrdersRefundsByOrderId(@PathVariable("shopId") Long shopId,
                                          @PathVariable("id") Long id){
        return paymentService.getOrdersRefundsByOrderId(id,shopId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            } else {
                if (returnObject.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
//                    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
                }
                return Common.decorateReturnObject(returnObject);
            }
        });

    }

    /**
     * 通过AfterSaleId查询订单的退款信息
     */
    @GetMapping("/shops/{shopId}/aftersales/{id}/refunds")
    public Mono getOrdersRefundsByAftersaleId(@PathVariable("id") Long id,
                                                @PathVariable("shopId") Long shopId){
        return paymentService.getOrdersRefundsByAftersaleId(id, shopId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            } else {
                if (returnObject.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
//                    httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                    return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
                }
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     *买家查询自己的退款信息
     */
    @GetMapping("/orders/{id}/refunds")
    public Mono queryUserRefundsByOrderId(@PathVariable("id") Long orderId) {
        return paymentService.userQueryRefundsByOrderId(orderId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            } else {
                if (returnObject.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
                    return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
                }
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

    /**
     * 买家查询自己的退款信息
     */
    @GetMapping("/aftersales/{id}/refunds")
    public Mono queryUserRefundsByAftersaleId(@PathVariable("id") Long aftersaleId) {
        return paymentService.userQueryRefundsByAftersaleId(aftersaleId).map(returnObject->{
            if (returnObject.getCode() == ResponseCode.OK) {
                return Common.decorateReturnObject(returnObject);
            } else {
                if (returnObject.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE) {
                    return Common.decorateReturnObject(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,ResponseCode.RESOURCE_ID_OUTSCOPE.getMessage()));
                }
                return Common.decorateReturnObject(returnObject);
            }
        });
    }

}
