package demo.dao;

import demo.PriviledgeServiceApplication;
import demo.model.po.NewUserPo;
import demo.repository.NewUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PriviledgeServiceApplication.class)
//@Transactional
class NewUserDaoTest {
    @Autowired
    NewUserRepository newUserRepository;

    @Test
    public void addTest(){
        newUserRepository.save(NewUserPo.builder().mobile("1234567890").email("123@qq.com").userName("chhhh")
                .password("root").build()).log();
    }

}