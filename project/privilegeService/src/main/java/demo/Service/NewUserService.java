package demo.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chei1
 */
@Service
public class NewUserService {
    @Transactional
    public ReturnObject approveUser(boolean approve, Long id) {
        ReturnObject returnObject = null;
        if (approve == true ) {
            NewUserPo newUserPo = newUserDao.findNewUserById(id);
            returnObject = userDao.addUser(newUserPo);
            newUserDao.physicallyDeleteUser(id);
        }
        else if (approve == false ) {
            returnObject=newUserDao.physicallyDeleteUser(id);
        }
        return returnObject;
    }
}
