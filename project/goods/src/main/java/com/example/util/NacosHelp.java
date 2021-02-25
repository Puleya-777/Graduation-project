package com.example.util;

import com.example.model.bo.Freight;
import com.example.model.bo.User;
import org.springframework.stereotype.Component;

@Component
public class NacosHelp {

    public User findUserById(Long userId){
        return new User();
    }

    public Freight findFreightById(Long freight){
        return new Freight();
    }

}
