package demo.dao;

import com.example.model.VoObject;
import com.example.util.ResponseCode;
import com.example.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import demo.repository.PrivilegeRepository;
import demo.model.bo.Privilege;
import demo.model.po.PrivilegePo;
import demo.model.vo.PrivilegeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PrivilegeDao {

    @Autowired
    PrivilegeRepository privilegeRepository;

    public Mono<ReturnObject> changePriv(Long id, PrivilegeVo vo) {

        return privilegeRepository.findById(id).map(Privilege::new).flatMap(privilege -> {
            if(!privilege.getCacuSignature().equals(privilege.getSignature())){
                return Mono.just(new ReturnObject(ResponseCode.RESOURCE_FALSIFY, "该权限可能被篡改，请联系管理员处理"));
            }else{
                return privilegeRepository.findByUrlAndRequestType(vo.getUrl(), vo.getRequestType())
                .map(privilege2->{
                    if(privilege2==null){
                        return new ReturnObject(ResponseCode.URL_SAME, "URL和RequestType不得与已有的数据重复");
                    }else{
                        PrivilegePo newPo=privilege.createUpdatePo(vo);
                        privilegeRepository.save(newPo);
                        return new ReturnObject();
                    }
                });
            }
        });
    }

    public Mono<ReturnObject<PageInfo<VoObject>>> findAllPrivs(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Flux<PrivilegePo> privilegePoFlux=privilegeRepository.findAll();
        Mono<List<VoObject>> ret=privilegePoFlux.map(Privilege::new).filter(privilege->privilege.authetic()).collect(Collectors.toList());
        Mono<List<PrivilegePo>> privilegePos=privilegePoFlux.collect(Collectors.toList());

        return Mono.zip(privilegePos,ret).map(tuple->{
            PageInfo<PrivilegePo> privPoPage = PageInfo.of(tuple.getT1());
            PageInfo<VoObject> privPage = new PageInfo<>(tuple.getT2());
            privPage.setPages(privPoPage.getPages());
            privPage.setPageNum(privPoPage.getPageNum());
            privPage.setPageSize(privPoPage.getPageSize());
            privPage.setTotal(privPoPage.getTotal());
            return new ReturnObject<>(privPage);
        });
    }
}
