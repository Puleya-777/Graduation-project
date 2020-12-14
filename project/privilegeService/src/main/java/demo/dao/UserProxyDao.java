package demo.dao;

import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import demo.Repository.UserProxyRepository;
import demo.model.po.UserProxyPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @author chei1
 */
@Slf4j
@Repository
public class UserProxyDao {

    @Autowired
    UserProxyRepository userProxyRepository;


    public Mono removeUserProxy(Long id, Long aid) {
        return userProxyRepository.findById(id).map(it->{
            if(aid.compareTo(it.getUserAId())==0){
                try {
                    userProxyRepository.deleteById(id);
                    return Mono.just(new ReturnObject());
                }catch (DataAccessException e) {
                    // 数据库错误
                    log.error("数据库错误：" + e.getMessage());
                    return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                            String.format("发生了严重的数据库错误：%s", e.getMessage())));
                } catch (Exception e) {
                    // 属未知错误
                    log.error("严重错误：" + e.getMessage());
                    return Mono.just(new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                            String.format("发生了严重的未知错误：%s", e.getMessage())));
                }
            }else{
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
            }
        });
    }

    public ReturnObject listProxies(Long aId, Long bId,Long did) {

        //r2dbc与jpa不兼容
        return new ReturnObject<>();
    }

    public Mono removeAllProxies(Long id,Long did) {
        return userProxyRepository.findById(id).map(it->{
            if(!Objects.equals(did, 0L) &&!did.equals(it.getDepartId())){
                return new ReturnObject(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT);
            }else{
                try {
                    userProxyRepository.deleteById(id);
                    return new ReturnObject();
                }
                catch (DataAccessException e) {
                    // 数据库错误
                    log.error("数据库错误：" + e.getMessage());
                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                            String.format("发生了严重的数据库错误：%s", e.getMessage()));
                } catch (Exception e) {
                    // 属未知错误
                    log.error("严重错误：" + e.getMessage());
                    return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                            String.format("发生了严重的未知错误：%s", e.getMessage()));
                }
            }
        });

    }


}
