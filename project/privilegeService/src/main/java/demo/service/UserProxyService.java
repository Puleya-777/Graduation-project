package demo.service;

import com.example.util.ReturnObject;
import demo.dao.UserProxyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author chei1
 */
@Service
public class UserProxyService {

    @Autowired
    UserProxyDao userProxyDao;

    public Mono removeUserProxy(Long id, Long userId) {
        return userProxyDao.removeUserProxy(id, userId);
    }

    public Mono listProxies(Long aId, Long bId,Long did) {
        return userProxyDao.listProxies(aId, bId,did).flatMap(it-> Mono.just(new ReturnObject(it)));
    }

    public Mono removeAllProxies(Long id,Long did) {
        return userProxyDao.removeAllProxies(id,did);
    }

}
