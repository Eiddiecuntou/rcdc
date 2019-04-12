package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.Size;
import com.ruijie.rcos.sk.webmvc.api.request.WebRequest;

/**
 * 
 * Description: 修改终端管理员密码请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月15日
 * 
 * @author nt
 */
public class EditAdminPwdWebRequest implements WebRequest {

    @NotBlank
    @Size(min = 3, max = 16)
    private String pwd;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
