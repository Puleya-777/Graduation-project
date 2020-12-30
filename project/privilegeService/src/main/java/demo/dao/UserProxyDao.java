package demo.dao;

import com.example.util.Common;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.example.util.encript.SHA256;
import demo.model.bo.UserProxy;
import demo.model.po.UserProxyPo;
import demo.repository.UserProxyRepository;
import demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chei1
 */
@Slf4j
@Repository
public class UserProxyDao {

    private final UserProxyRepository userProxyRepository;

    @Autowired
    UserRepository userRepository;

    public UserProxyDao(UserProxyRepository userProxyRepository) {
        this.userProxyRepository = userProxyRepository;
    }


    public Mono removeUserProxy(Long id, Long aid) {
        return userProxyRepository.findById(id).map(it->{
            if(it!=null&&aid.compareTo(it.getUserAId())==0){
                /**
                 * 此处delete需要做异常处理
                 */
                return userProxyRepository.deleteUserProxyPoById(id).map(o->{
                    if(o==1){
                        return Mono.just(new ReturnObject());
                    }
                    return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
                });
                //return Mono.just(new ReturnObject());
//                try {
//                    userProxyRepository.deleteUserProxyPoByUserAIdAndUserBId(aid, it.getUserBId());
//                    log.info("删除成功");
//                    return Mono.just(new ReturnObject());
//                }catch (DataAccessException e) {
//                    // 数据库错误
//                    log.error("数据库错误：" + e.getMessage());
//                    return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                            String.format("发生了严重的数据库错误：%s", e.getMessage())));
//                } catch (Exception e) {
//                    // 属未知错误
//                    log.error("严重错误：" + e.getMessage());
//                    return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                            String.format("发生了严重的未知错误：%s", e.getMessage())));
//                }
            }else{
                log.info("无权限");
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
        }).defaultIfEmpty(Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST)));
    }

    public Mono listProxies(Long aId, Long bId,Long did) {
        if(aId==null&&bId==null){
            return userProxyRepository.findAllByDepartId(did).collectList();
        }else{
            if(aId==null){
                return userProxyRepository.findAllByUserBId(bId).collectList();
            }else if(bId==null){
                return userProxyRepository.findAllByUserAId(aId).collectList();
            }else{
                return userProxyRepository.findAllByUserAIdAndUserBId(aId,bId).collectList();
            }
        }
    }

    public Mono removeAllProxies(Long id,Long did) {
        /**
         * 此处delete需要异常处理
         */
        return userProxyRepository.findById(id).map(it->{
            if(!Objects.equals(did, 0L) &&!did.equals(it.getDepartId())){
                return new ReturnObject(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT);
            }else{
                return userProxyRepository.deleteUserProxyPoById(id).map(o->{
                    if(o==1){
                        return Mono.just(new ReturnObject());
                    }
                    return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
                });
//                try {
//                    userProxyRepository.deleteById(id);
//                    return new ReturnObject();
//                }
//                catch (DataAccessException e) {
//                    // 数据库错误
//                    log.error("数据库错误：" + e.getMessage());
//                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                            String.format("发生了严重的数据库错误：%s", e.getMessage()));
//                } catch (Exception e) {
//                    // 属未知错误
//                    log.error("严重错误：" + e.getMessage());
//                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                            String.format("发生了严重的未知错误：%s", e.getMessage()));
//                }
            }
        }).defaultIfEmpty(Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST)));
    }

    public Mono<ReturnObject> usersProxy(Long aid, Long id, UserProxy bo, Long departid) {
        if(Objects.equals(aid,id)){
            return Mono.just(new ReturnObject(ResponseCode.USERPROXY_SELF));
        }
        if(!isBiggerBegin(bo)){
            return Mono.just(new ReturnObject(ResponseCode.USERPROXY_BIGGER));
        }
        return Mono.zip(isExistProxy(aid, id, bo),userRepository.findById(id)).flatMap(tuple->{
            if (tuple.getT1()) {
                return Mono.just(new ReturnObject(ResponseCode.USERPROXY_CONFLICT));
            }
            if(tuple.getT2().getDepartId()!=departid){
                return Mono.just(new ReturnObject(ResponseCode.USERPROXY_DEPART_CONFLICT));
            }
            UserProxyPo userProxyPo = new UserProxyPo();
            userProxyPo.setUserAId(aid);
            userProxyPo.setUserBId(id);
            userProxyPo.setDepartId(departid);
            userProxyPo.setValid((byte) 0);
            userProxyPo.setBeginDate(bo.getBegin_time());
            userProxyPo.setEndDate(bo.getEnd_time());
            userProxyPo.setGmtCreate(LocalDateTime.now());
            StringBuilder signature = Common.concatString("-", userProxyPo.getUserAId().toString(), userProxyPo.getUserBId().toString(),userProxyPo.getBeginDate().toString(),userProxyPo.getEndDate().toString(),userProxyPo.getValid().toString());
            userProxyPo.setSignature(SHA256.getSHA256(signature.toString()));
//            try {
            return userProxyRepository.save(userProxyPo).map(res->new ReturnObject());
//            }
//            catch (DataAccessException e) {
//                // 数据库错误
//                logger.error("数据库错误：" + e.getMessage());
//                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
//            } catch (Exception e) {
//                // 属未知错误
//                logger.error("严重错误：" + e.getMessage());
//                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                        String.format("发生了严重的未知错误：%s", e.getMessage()));
//            }
        });


    }

    public boolean isBiggerBegin(UserProxy bo){
        LocalDateTime nowBeginDate = bo.getBegin_time();
        LocalDateTime nowEndDate = bo.getEnd_time();
        return nowEndDate.isAfter(nowBeginDate);
    }

    public Mono<Boolean> isExistProxy(Long aId, Long bId, UserProxy bo) {
        return userProxyRepository.findAllByUserAIdAndUserBId(aId,bId).collect(Collectors.toList()).flatMap(res->Mono.just(Optional.of(res))).defaultIfEmpty(Optional.empty()).flatMap(resOptional->{
            if (!resOptional.isPresent()){
                return Mono.just(false);
            }else {
                List<UserProxyPo> results = resOptional.get();
                LocalDateTime nowBeginDate = bo.getBegin_time();
                LocalDateTime nowEndDate = bo.getEnd_time();
                for (UserProxyPo po: results){
                    LocalDateTime beginDate = po.getBeginDate();
                    LocalDateTime endDate = po.getEndDate();

                    //判断开始时间和失效时间是不是不在同一个区间里面
                    if(nowBeginDate.equals(beginDate) || nowBeginDate.equals(endDate) || (nowBeginDate.isAfter(beginDate) && nowBeginDate.isBefore(endDate)) ){
                        return Mono.just(true);
                    }
                    if(nowEndDate.equals(beginDate) || nowEndDate.equals(endDate) || (nowEndDate.isAfter(beginDate) && nowEndDate.isBefore(endDate)) ){
                        return Mono.just(true);
                    }
                }
                return Mono.just(false);
            }
        });

    }

    public Mono<ReturnObject> aUsersProxy(Long aid, Long bid, UserProxy bo,Long departid) {
        if(Objects.equals(aid,bid)){
            return Mono.just(new ReturnObject(ResponseCode.USERPROXY_SELF));
        }
        if(!isBiggerBegin(bo)){
            return Mono.just(new ReturnObject(ResponseCode.USERPROXY_BIGGER));
        }
        return Mono.zip(isExistProxy(aid, bid, bo),userRepository.findById(aid),userRepository.findById(bid)).flatMap(tuple->{
            if (tuple.getT1()) {
                return Mono.just(new ReturnObject(ResponseCode.USERPROXY_CONFLICT));
            }
            if(tuple.getT2().getDepartId()!=tuple.getT3().getDepartId()){
                return Mono.just(new ReturnObject(ResponseCode.USERPROXY_DEPART_CONFLICT));
            }
            if(!Objects.equals(departid, 0L) && departid != tuple.getT2().getDepartId()){
                return Mono.just(new ReturnObject(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT));
            }
            UserProxyPo userProxyPo = new UserProxyPo();
            userProxyPo.setUserAId(aid);
            userProxyPo.setUserBId(bid);
            userProxyPo.setDepartId(tuple.getT2().getDepartId());
            userProxyPo.setValid((byte) 0);
            userProxyPo.setBeginDate(bo.getBegin_time());
            userProxyPo.setEndDate(bo.getEnd_time());
            userProxyPo.setGmtCreate(LocalDateTime.now());
            StringBuilder signature = Common.concatString("-", userProxyPo.getUserAId().toString(), userProxyPo.getUserBId().toString(),userProxyPo.getBeginDate().toString(),userProxyPo.getEndDate().toString(),userProxyPo.getValid().toString());
            userProxyPo.setSignature(SHA256.getSHA256(signature.toString()));
//            try {
            return userProxyRepository.save(userProxyPo).map(res->new ReturnObject());
//            }
//            catch (DataAccessException e) {
//                // 数据库错误
//                logger.error("数据库错误：" + e.getMessage());
//                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
//            } catch (Exception e) {
//                // 属未知错误
//                logger.error("严重错误：" + e.getMessage());
//                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
//                        String.format("发生了严重的未知错误：%s", e.getMessage()));
//            }

        });
    }

}
