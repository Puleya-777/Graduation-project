package demo.Service;

import cn.edu.xmu.ooad.util.ReturnObject;
import demo.dao.NewUserDao;
import demo.dao.UserDao;
import demo.model.po.NewUserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * @author chei1
 */
@Service
public class NewUserService {

    @Autowired
    NewUserDao newUserDao;
    @Autowired
    UserDao userDao;


    //@Transactional

    public Mono<ReturnObject> approveUser(boolean approve, Long id) {
        if (approve) {

            return Mono.just(new ReturnObject(newUserDao.findNewUserById(id).map(it->{
                newUserDao.physicallyDeleteUser(id);
                return userDao.addUser(it);
            })));

//            NewUserPo newUserPo = newUserDao.findNewUserById(id);
//            returnObject = userDao.addUser(newUserPo);
//            newUserDao.physicallyDeleteUser(id);

        }
        else {
            return Mono.just(new ReturnObject(newUserDao.physicallyDeleteUser(id)));
        }
    }
}
