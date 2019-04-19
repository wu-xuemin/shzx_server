package com.eservice.api.web;
import com.eservice.api.core.Result;
import com.eservice.api.core.ResultGenerator;

import com.eservice.api.model.user.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
* Class Description: xxx
* @author Wilson Hu
* @date 2018/12/17.
*/
@RestController
@RequestMapping("/CAS")
public class CASController {

    @PostMapping("/demo")
    public Result demo(String ticket) {
        User user = new User();
        user.setAccount("浦晓瑛");
        user.setValid(1);
        user.setId(17);
        user.setName("浦晓瑛");
        user.setPhone("13918430375");
        user.setRoleId(4);
        user.setPassword(ticket);
        return ResultGenerator.genSuccessResult(user);
    }

}
