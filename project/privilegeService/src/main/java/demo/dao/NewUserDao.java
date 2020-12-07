//package demo.dao;
//
//import cn.edu.xmu.ooad.util.ResponseCode;
//import cn.edu.xmu.ooad.util.ReturnObject;
//import demo.model.po.NewUserPo;
//import demo.repository.NewUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//
///**
// * @author chei1
// */
//@Repository
//public class NewUserDao {
//    @Autowired
//    NewUserRepository newUserRepository;
//
//    public Mono<NewUserPo> findNewUserById(Long id) {
//        return newUserRepository.findById(id);
//    }
//
//    public Mono<Void> physicallyDeleteUser(Long id) {
//
//        return newUserRepository.deleteById(id);
//
//    }
//}
