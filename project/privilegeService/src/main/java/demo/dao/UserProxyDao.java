package demo.dao;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import demo.repository.UserProxyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author chei1
 */
@Slf4j
@Repository
public class UserProxyDao {

    private final UserProxyRepository userProxyRepository;

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

    public ReturnObject listProxies(Long aId, Long bId,Long did) {

        //r2dbc与jpa不兼容
        return new ReturnObject<>();
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


}
