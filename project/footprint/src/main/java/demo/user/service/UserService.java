package demo.user.service;

import com.example.util.*;
import com.example.util.encript.AES;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import demo.advertise.model.po.AdvertisePo;
import demo.user.model.po.UserPo;
import demo.user.model.vo.*;
import demo.user.repository.UserRepository;
import demo.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author chei1
 */
@Service
@Slf4j
public class UserService {
    @Resource
    UserRepository userRepository;
    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;
    @Value("${userService.login.jwtExpire}")
    private Integer jwtExpireTime;
    @Value("${userService.login.multiply}")
    private Boolean canMultiplyLogin;
    /**
     * 分布式锁的过期时间（秒）
     */
    @Value("${userService.lockerExpireTime}")
    private long lockerExpireTime;

    public Mono registered(RegisteredVo vo){
        return Mono.just(vo).flatMap(aa-> Mono.zip(userRepository.countAllByEmail(vo.getEmail()),
                userRepository.countAllByMobile(vo.getMobile()),
                userRepository.countAllByUserName(vo.getUserName())).flatMap(tuple->{
                    if(tuple.getT1()!=0){
                        return Mono.just(new ReturnObject<>(ResponseCode.EMAIL_REGISTERED));
                    }
                    if(tuple.getT2()!=0){
                        return Mono.just(new ReturnObject<>(ResponseCode.MOBILE_REGISTERED));
                    }
                    if(tuple.getT3()!=0){
                        return Mono.just(new ReturnObject<>(ResponseCode.USER_NAME_REGISTERED));
                    }
                    UserPo po=new UserPo();
                    po.registered(vo);
                    return userRepository.save(po).map(userPo -> {
                        po.setPassword("******");
                        return new ReturnObject<>(po);
                    });
        }));
    }

    public Mono getUser(Long customerId){
        return userRepository.findById(customerId).map(po->{
            po.setPassword("******");
            return new ReturnObject(po);
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
    public Mono ModifiedUser(Long customerId, ModifiedUserVo vo){
        return userRepository.findById(customerId).flatMap(po->{
            po.modified(vo);
            return userRepository.save(po).map(it-> new ReturnObject<>());
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }
    public Mono ResetPwd(ResetPasswordVo vo){
        return userRepository.findByUserName(vo.getUserName()).map(po->{
            if(po.getState()!=1){
                return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
            }
            if(!po.getEmail().equals(vo.getEmail())){
                return new ReturnObject<>(ResponseCode.EMAIL_WRONG);
            }
            //随机生成验证码
            String captcha = RandomCaptcha.getRandomString(6);
            while(redisTemplate.hasKey(captcha)) {
                captcha = RandomCaptcha.getRandomString(6);
            }
            String id = po.getId().toString();
            String key = "cp_" + captcha;
            //key:验证码,value:id存入redis
            redisTemplate.opsForValue().set(key,id);
            //五分钟后过期
            redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);
            boolean flag= EmailUtil.sendEmail(vo.getEmail(),captcha);
            if(flag){
                return new ReturnObject<>();
            }else {
                return new ReturnObject<>(ResponseCode.EMAIL_WRONG);
            }
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"无效邮箱"));
    }
    public Mono modifiedPwd(ModifiedPwdVo vo){
        return Mono.just(redisTemplate.hasKey("cp_" + vo.getCaptcha())).flatMap(flag -> {
            if (!flag) {
                return Mono.just(new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT));
            } else {
                String id = redisTemplate.opsForValue().get("cp_" + vo.getCaptcha()).toString();
                return userRepository.findById(Long.parseLong(id)).flatMap(po -> {
                    if (UserPo.getMD5Str(vo.getNewPassword()).equals(po.getPassword())) {
                        return Mono.just(new ReturnObject<>(ResponseCode.PASSWORD_SAME));
                    }
                    po.setPassword(UserPo.getMD5Str(vo.getNewPassword()));
                    po.setGmtModified(LocalDateTime.now());
                    String key = "cp_" + vo.getCaptcha();
                    redisTemplate.delete(key);
                    return userRepository.save(po).flatMap(it -> Mono.just(new ReturnObject<>(ResponseCode.OK)));
                });

            }
        });
    }
    public Mono getAllUser(String userName,String mobile,String email,Integer pageNum, Integer pageSize){
        return Mono.just("puleya").flatMap(it->{
            if(userName!=null){
                return userRepository.findByUserName(userName).map(po->{
                    ReturnUserVo vo=new ReturnUserVo();
                    vo.setId(po.getId());
                    vo.setName(po.getRealName());
                    vo.setUserName(po.getUserName());
                    ArrayList<ReturnUserVo> list=new ArrayList<>();
                    list.add(vo);
                    return list;
                });
            }
            if(mobile!=null){
                return userRepository.findByMobile(mobile).map(po->{
                    ReturnUserVo vo=new ReturnUserVo();
                    vo.setId(po.getId());
                    vo.setName(po.getRealName());
                    vo.setUserName(po.getUserName());
                    ArrayList<ReturnUserVo> list=new ArrayList<>();
                    list.add(vo);
                    return list;
                });
            }
            if(email!=null){
                return userRepository.findByEmail(email).map(po->{
                    ReturnUserVo vo=new ReturnUserVo();
                    vo.setId(po.getId());
                    vo.setName(po.getRealName());
                    vo.setUserName(po.getUserName());
                    ArrayList<ReturnUserVo> list=new ArrayList<>();
                    list.add(vo);
                    return list;
                });
            }
            return userRepository.findAll().map(po->{
                ReturnUserVo vo=new ReturnUserVo();
                vo.setId(po.getId());
                vo.setName(po.getRealName());
                vo.setUserName(po.getUserName());
                return vo;
            }).collectList();
        }).map(it->{
            Page page = new Page(pageNum, pageSize);
            int total = it.size();
            page.setTotal(total);
            int startIndex = Math.min((pageNum - 1) * pageSize,total);
            int endIndex = Math.min(startIndex + pageSize,total);
            page.addAll(it.subList(startIndex,endIndex));
            PageInfo<AdvertisePo> retPage=new PageInfo(page);
            return new ReturnObject(retPage);
        });
    }
    public Mono banUser(Long id){
        return userRepository.findById(id).flatMap(po->{
            po.setState(2);
            po.setGmtModified(LocalDateTime.now());
            return userRepository.save(po).map(it-> new ReturnObject());
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }

    public Mono releaseUser(Long id){
        return userRepository.findById(id).flatMap(po->{
            po.setState(1);
            po.setGmtModified(LocalDateTime.now());
            return userRepository.save(po).map(it-> new ReturnObject());
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST));
    }







    public Mono login(String userName, String password)
    {
        return userRepository.findByUserName(userName).map(user->{
            if(user.getState()!=1){
                return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            }
            final String thepassword = UserPo.getMD5Str(password);
            if(user == null || !thepassword.equals(user.getPassword())){
                return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            }
            if (user.getState() != 1){
                return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
            }
            String key = "up_" + user.getId();
            if(redisTemplate.hasKey(key) && !canMultiplyLogin){
                log.debug("login: multiply  login key ="+key);
                // 用户重复登录处理
                Set<Serializable> set = redisTemplate.opsForSet().members(key);
                redisTemplate.delete(key);

                /* 将旧JWT加入需要踢出的集合 */
                String jwt = null;
                for (Serializable str : set) {
                    /* 找出JWT */
                    if((str.toString()).length() > 8){
                        jwt =  str.toString();
                        break;
                    }
                }
                log.debug("login: oldJwt" + jwt);
                this.banJwt(jwt);
            }
            //创建新的token
            JwtHelper jwtHelper = new JwtHelper();
            String jwt = jwtHelper.createToken(user.getId(),777L, jwtExpireTime);
            return new ReturnObject<>(jwt);
        }).defaultIfEmpty(new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT));
    }

    /**
     * 禁止持有特定令牌的用户登录
     * @param jwt JWT令牌
     */
    private void banJwt(String jwt){
        String[] banSetName = {"BanJwt_0", "BanJwt_1"};
        long bannIndex = 0;
        if (!redisTemplate.hasKey("banIndex")){
            redisTemplate.opsForValue().set("banIndex", Long.valueOf(0));
        } else {
            log.debug("banJwt: banIndex = " +redisTemplate.opsForValue().get("banIndex"));
            bannIndex = Long.parseLong(redisTemplate.opsForValue().get("banIndex").toString());
        }
        log.debug("banJwt: banIndex = " + bannIndex);
        String currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
        log.debug("banJwt: currentSetName = " + currentSetName);
        if(!redisTemplate.hasKey(currentSetName)) {
            // 新建
            log.debug("banJwt: create ban set" + currentSetName);
            redisTemplate.opsForSet().add(currentSetName, jwt);
            redisTemplate.expire(currentSetName,jwtExpireTime * 2, TimeUnit.SECONDS);
        }else{
            //准备向其中添加元素
            if(redisTemplate.getExpire(currentSetName, TimeUnit.SECONDS) > jwtExpireTime) {
                // 有效期还长，直接加入
                log.debug("banJwt: add to exist ban set" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
            } else {
                // 有效期不够JWT的过期时间，准备用第二集合，让第一个集合自然过期
                // 分步式加锁
                log.debug("banJwt: switch to next ban set" + currentSetName);
                long newBanIndex = bannIndex;
                while (newBanIndex == bannIndex &&
                        !redisTemplate.opsForValue().setIfAbsent("banIndexLocker","nouse", lockerExpireTime, TimeUnit.SECONDS)){

                    newBanIndex = (Long) redisTemplate.opsForValue().get("banIndex");
                }
                if (newBanIndex == bannIndex) {
                    //切换ban set
                    bannIndex = redisTemplate.opsForValue().increment("banIndex");
                }else{
                    //已经被其他线程改变
                    bannIndex = newBanIndex;
                }

                currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
                //启用之前，不管有没有，先删除一下，应该是没有，保险起见
                redisTemplate.delete(currentSetName);
                log.debug("banJwt: next ban set =" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
                redisTemplate.expire(currentSetName,jwtExpireTime * 2,TimeUnit.SECONDS);
                // 解锁
                redisTemplate.delete("banIndexLocker");
            }
        }
    }

    public Mono logout(Long userId){
        redisTemplate.delete("up_" + userId);
        return Mono.just(new ReturnObject<>());
    }

}
