package demo.Controller;

import demo.Service.NewUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import util.ResponseCode;
import util.ReturnObject;

/**
 * @author chei1
 */
@RestController
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class PrivilegeController {
    @Autowired
    NewUserService newUserService;

    //@Audit // 需要认证
    @PutMapping("shops/{did}/adminusers/{id}/approve")
    public Object approveUser(@PathVariable Long id, @PathVariable Long did, BindingResult bindingResult, @RequestBody Boolean approve, @Depart Long shopid) {
        //logger.debug("approveUser: did = "+ did+" userid: id = "+ id+" opinion: "+approve);
        ReturnObject returnObject=null;
        if(did==0|| did.equals(shopid))
        {
            returnObject=newUserService.approveUser(approve,id);
        }
        else
        {
            //logger.error("approveUser: 无权限查看此部门的用户 did=" + did);
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        return returnObject;
    }
}
