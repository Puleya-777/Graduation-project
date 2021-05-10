package com.order.service;

import com.example.model.VoObject;
import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.order.dao.PaymentDao;
import com.order.model.bo.Payment;
import com.order.model.bo.Refund;
import com.order.feign.AftersaleFeign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    private AftersaleFeign aftersaleFeign;


//    @DubboReference
//    private IOrderService iOrderService;
//
//    @DubboReference
//    private IAftersaleService iAftersaleService;

    private Logger logger = LoggerFactory.getLogger(PaymentService.class);

    /**
     * 买家为订单创建支付单
     */
    public Mono<ReturnObject<VoObject>> createPayment(Payment payment) {
        LocalDateTime localDateTime = LocalDateTime.now();

        payment.setBeginTime(localDateTime);
        payment.setEndTime(localDateTime.plusHours(24));
        //支付成功
        payment.setState((byte)0);
        payment.setPayTime(localDateTime);
        payment.setPaySn(Common.genSeqNum());
        payment.setGmtModified(localDateTime);

        return paymentDao.insertPayment(payment).map(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return returnObject;
            }else {
                return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
            }
        });
    }

    /**
     * 买家查询自己的支付信息
     */
    public Mono<ReturnObject> userQueryPayment(Long orderId) {
//        ReturnObject<OrderInnerDTO> returnObject = iOrderService.findUserIdbyOrderId(orderId);
//        if(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
//            return Mono.just(returnObject);
        return paymentDao.userQueryPaymentById(orderId);
    }

    /**
     * 管理员查询订单的支付信息
     */
    public Mono<ReturnObject> queryPayment(Long shopId, Long orderId) {
        return paymentDao.userQueryPaymentById(orderId);
        //如果该商店不拥有这个order则查不到
//        ReturnObject<ResponseCode> returnObject = iOrderService.judgeOrderBelongToShop(shopId, orderId);
//        if(returnObject.getCode().equals(ResponseCode.OK)){
//            return paymentDao.userQueryPaymentById(orderId);
//        }
//        else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_OUTSCOPE)){
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
//        }
//        else if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)){
//            logger.error(" queryPaymentById: 数据库不存在该支付单 orderId="+orderId);
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        }
//        return Mono.just(new ReturnObject<>());
    }

    /**
     * 买家为售后单创建支付单
     */
    @Transactional
    public Mono<ReturnObject<VoObject>> createPaymentByAftersaleId(Payment payment,Long aftersaleId) {
        //TODO：从order模块拿到orderItemId，再拿到orderId
//        Long retOrderItemId = iAftersaleService.findOrderItemIdbyAftersaleId(aftersaleId).getData();
//        if (retOrderItemId == null)
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"找不到aftersaleId对应的OrderItemId"));
//
//        ReturnObject<Long> returnObt=iOrderService.getOrderIdByOrderItemId(retOrderItemId);
//        Long retorderId=returnObt.getData();

        LocalDateTime localDateTime = LocalDateTime.now();

        payment.setAftersaleId(aftersaleId);
//        payment.setOrderId(retorderId);
        payment.setOrderId((long)1);
        payment.setBeginTime(LocalDateTime.now());
        payment.setEndTime(localDateTime.plusHours(24));

        //支付成功
        payment.setState((byte)0);
        payment.setPayTime(localDateTime);

        return paymentDao.insertPayment(payment).map(returnObject->{
            if(returnObject.getCode().equals(ResponseCode.OK)){
                return returnObject;
            }else {
                return new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
            }
        });
    }

    /**
     * 买家查询自己的支付信息
     */
    public Mono<ReturnObject<List>> customerQueryPaymentByAftersaleId(Long aftersaleId) {
//        ReturnObject<Long> returnObject = iAftersaleService.findUserIdbyAftersaleId(aftersaleId);
//        if(returnObject.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST))
//        {
//            return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
//        }
        return paymentDao.queryPaymentByAftersaleIdForCus(aftersaleId);
    }

    /**
     * 管理员查询售后单的支付信息
     */
    public Mono<ReturnObject<List>> getPaymentByAftersaleId(Long shopId, Long aftersaleId)  {
        return paymentDao.queryPaymentByAftersaleIdForCus(aftersaleId).map(res->{
            List<Payment> retPayments = res.getData();
            if(res.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("不存在对应的aftersaleId的记录" ));
            }
            List<Payment> payments =new ArrayList<>(retPayments.size());
            Long retShopId=null;
            for(Payment payment:retPayments){
                //TODO: 从order模块根据orderId获得shopId
//                retShopId=iOrderService.findShopIdbyOrderId(payment.getOrderId()).getData().getShopId();
                retShopId=(long)1;
                if(retShopId.equals(shopId))
                    payments.add(payment);
            }
            if(payments.isEmpty())
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("找不到符合条件的支付记录" ));
            return new ReturnObject<>(payments);
        });
    }

    /**
     * 新增退款信息
     */
    @Transactional
    public Mono<ReturnObject<VoObject>> insertRefunds(Refund refund, Long shopId){
//        ReturnObject<OrderInnerDTO> orderInnerDTO = iOrderService.findShopIdbyOrderId(refund.getOrderId());
//        Long retShopId = orderInnerDTO.getData().getShopId();
//        Long retShopId = (long)1;
//        if(retShopId.equals(shopId)) {
            return paymentDao.insertRefunds(refund).map(retObj->{
                if (retObj.getCode().equals(ResponseCode.OK)) {
                    return new ReturnObject<>(retObj.getData());
                } else {
                    return new ReturnObject<VoObject>(retObj.getCode(), retObj.getErrmsg());
                }
            });
//        }
//        return Mono.just(null);
    }

    /**
     * 管理员查询订单退款信息
     */
    public Mono<ReturnObject<List<Refund>>> getOrdersRefundsByOrderId(Long orderId, Long shopId){
//        ReturnObject<OrderInnerDTO> orderInnerDTO = iOrderService.findShopIdbyOrderId(orderId);
//        if (!orderInnerDTO.getCode().equals(ResponseCode.OK))
//        {
//            logger.info("not found orderInnerDTO, the orderId is " + orderId);
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        }
//        Long retShopId = orderInnerDTO.getData().getShopId();
//        if (retShopId == null)
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        if (!retShopId.equals(shopId))
//        {
//            logger.info("shopId not fitted");
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE));
//        }
        return paymentDao.getOrdersRefundsByOrderId(orderId).map(retObj->{
            if (retObj.getCode().equals(ResponseCode.OK)) {
                return retObj;
            } else {
                return new ReturnObject(retObj.getCode(), retObj.getErrmsg());
            }
        });
    }

    /**
     * 通过AfterSaleId查询订单的退款信息
     */
    @Transactional
    public Mono<ReturnObject<List<Refund>>> getOrdersRefundsByAftersaleId(Long aftersaleId, Long shopId){
//        ReturnObject<Long> ret = iAftersaleService.findShopIdbyAftersaleId(aftersaleId);
//        if (!ret.getCode().equals(ResponseCode.OK))
//        {
//            logger.info("the return object is error!");
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        }
//        Long retShopId = ret.getData();
//        if (retShopId == null)
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        if (!retShopId.equals(shopId))
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
        return paymentDao.getOrdersRefundsByAftersaleId(aftersaleId).map(retObj->{
            if (retObj.getCode().equals(ResponseCode.OK)) {
                return retObj;
            } else {
                return new ReturnObject(retObj.getCode(), retObj.getErrmsg());
            }
        });
    }

    /**
     *买家查询自己的退款信息 OrderId
     */
    @Transactional
    public Mono<ReturnObject> userQueryRefundsByOrderId(Long orderId) {
//        ReturnObject<OrderInnerDTO> orderInnerDTO = iOrderService.findUserIdbyOrderId(orderId);
//        if (!orderInnerDTO.getCode().equals(ResponseCode.OK))
//        {
//            logger.info("not found orderInnerDTO, the orderId is " + orderId);
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        }
//        Long retUserId = orderInnerDTO.getData().getCustomerId();
//        if (retUserId == null)
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
        return paymentDao.findRefundsInfoByOrderId(orderId).map(returnObject->{
            if (returnObject.getCode().equals(ResponseCode.OK)) {
                return returnObject;
            }else{
                return new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
            }
        });


    }

    /**
     *买家查询自己的退款信息 AftersaleId
     */
    @Transactional
    public Mono<ReturnObject<List<Refund>>> userQueryRefundsByAftersaleId(Long aftersaleId)
    {
//        ReturnObject<Long> ret = iAftersaleService.findUserIdbyAftersaleId(aftersaleId);
//        if (!ret.getCode().equals(ResponseCode.OK))
//        {
//            logger.info("retUserId is " + ret.getData());
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
//        }
//        Long retUserId = ret.getData();
//        if (retUserId == null)
//            return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
        return paymentDao.findRefundsInfoByAfterSaleId(aftersaleId).map(returnObject->{
            if (returnObject.getCode().equals(ResponseCode.OK)) {
                return returnObject;
            }else{
                return new ReturnObject<>(returnObject.getCode(), returnObject.getErrmsg());
            }
        });

    }



}
