package com.order.dao;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.order.model.bo.Payment;
import com.order.model.bo.Refund;
import com.order.model.po.PaymentPo;
import com.order.model.po.RefundPo;
import com.order.repository.PaymentRepository;
import com.order.repository.RefundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PaymentDao {
    private static final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    RefundRepository refundRepository;

    /**
     * 插入支付单
     */
    public Mono<ReturnObject> insertPayment(Payment payment) {
        PaymentPo paymentPo = payment.getPaymentPo();
        return paymentRepository.save(paymentPo).map(res->{
            logger.info("insertPayment: insert Payment  = " + paymentPo.toString());
            payment.setId(paymentPo.getId());
            return new ReturnObject<>(payment);
        });
    }

    /**
     * 买家查询自己的支付信息
     */
    public Mono<ReturnObject> userQueryPaymentById(Long orderId) {
        return paymentRepository.findByOrderId(orderId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error(" userQueryPaymentById: 数据库不存在该支付单 orderId="+orderId);
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            List<PaymentPo> paymentPoS = resOptional.get();
            List<Payment> payments =new ArrayList<>(paymentPoS.size());
            for(PaymentPo paymentPo:paymentPoS){
                Payment payment = new Payment(paymentPo);
                payments.add(payment);
            }
            return Mono.just(new ReturnObject<>(payments));
        });
    }


    /**
     * 买家通过aftersaleId查询自己的支付信息
     */
    public Mono<ReturnObject<List>> queryPaymentByAftersaleIdForCus(Long aftersaleId) {
        return paymentRepository.findByAftersaleId(aftersaleId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error(" userQueryPaymentById: 数据库不存在该支付单 orderId=" + aftersaleId);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            List<PaymentPo> paymentPoS = resOptional.get();
            List<Payment> payments =new ArrayList<>(paymentPoS.size());
            for(PaymentPo paymentPo:paymentPoS){
                Payment payment = new Payment(paymentPo);
                payments.add(payment);
            }
            return Mono.just(new ReturnObject<>(payments));
        });
    }

    /**
     * 增加一个退款信息
     */
    public  Mono<ReturnObject<Refund>> insertRefunds(Refund refund) {
        RefundPo refundPo = refund.gotRefundPo();
        return paymentRepository.findById(refundPo.getPaymentId()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("paymentId不存在: "+refundPo.getPaymentId());
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            PaymentPo paymentPo = resOptional.get();
            refundPo.setOrderId(paymentPo.getOrderId());
            refundPo.setAftersaleId(paymentPo.getAftersaleId());
            return refundRepository.save(refundPo).map(res->{
                logger.debug("insertRefund: insert refund = " + refundPo.toString());
                return new ReturnObject<>(new Refund(refundPo));
            });
        });


    }

    /**
     * 根据OrderId获取退款信息
     */
    public Mono<ReturnObject<List<Refund>>> getOrdersRefundsByOrderId(Long id) {
        logger.debug("findRefundByOrderId: Id =" + id);
        return refundRepository.findByOrderId(id).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("getNewUser: 数据库不存在该Order的退款信息 orderId=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            List<RefundPo> refundPos = resOptional.get();
            List<Refund> refundBoList = new ArrayList<>(refundPos.size());
            for (RefundPo po: refundPos)
            {
                Refund refundBo = new Refund(po);
                refundBoList.add(refundBo);
            }

            return Mono.just(new ReturnObject<>(refundBoList));
        });
    }

    /**
     * 根据AftersaleId获取退款信息
     */
    public Mono<ReturnObject<List<Refund>>> getOrdersRefundsByAftersaleId(Long id) {
        logger.debug("findRefundByAftersaleId: Id =" + id);
        return refundRepository.findByAftersaleId(id).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                logger.error("getNewUser: 数据库不存在该Order的退款信息 orderId=" + id);
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            List<RefundPo> refundPos = resOptional.get();
            List<Refund> refundBoList = new ArrayList<>(refundPos.size());
            for (RefundPo po: refundPos)
            {
                Refund refundBo = new Refund(po);
                refundBoList.add(refundBo);
            }

            return Mono.just(new ReturnObject<>(refundBoList));
        });
    }

    /**
     *买家查询自己的退款信息 OrderId
     */
    public Mono<ReturnObject> findRefundsInfoByOrderId(Long orderId) {
        return refundRepository.findByOrderId(orderId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            List<RefundPo> refundPoList = resOptional.get();
            List<Refund> refundBoList = new ArrayList<>(refundPoList.size());
            for (RefundPo po: refundPoList)
            {
                Refund refundBo = new Refund(po);
                refundBoList.add(refundBo);
            }

            return Mono.just(new ReturnObject<>(refundBoList));
        });
    }


    /**
     *买家查询自己的退款信息 AftersaleId
     */
    public Mono<ReturnObject<List<Refund>>> findRefundsInfoByAfterSaleId(Long afterSaleId)
    {
        return refundRepository.findByAftersaleId(afterSaleId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional-> {
            if (!resOptional.isPresent()) {
                return Mono.just(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
            }
            List<RefundPo> refundPoList = resOptional.get();
            List<Refund> refundBoList = new ArrayList<>(refundPoList.size());
            for (RefundPo po: refundPoList)
            {
                Refund refundBo = new Refund(po);
                refundBoList.add(refundBo);
            }

            return Mono.just(new ReturnObject<>(refundBoList));
        });

    }

}
