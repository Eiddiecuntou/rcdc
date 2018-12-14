package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPageRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.webmvc.api.annotation.NoAuthUrl;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;

/**
 * 
 * Description: 终端
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月21日
 * 
 * @author nt
 */
@Controller
@RequestMapping("/terminal")
@EnableCustomValidate(enable = false)
public class TerminalController {


    /**
     * 终端基本信息接口
     */
    @Autowired
    private CbbTerminalAPI cbbTerminalAPI;

    /**
     * 
     * 终端分页列表
     * 
     * @param request 分页列表请求参数
     * @return 分页结果
     * @throws BusinessException 业务异常  
     */
    @RequestMapping(value = "list")
    @NoAuthUrl
    public DefaultWebResponse listTerminal(PageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalPageRequest pageRequest = new CbbTerminalPageRequest();
        pageRequest.setPage(request.getPage());
        pageRequest.setLimit(request.getLimit());
        DefaultPageResponse<CbbTerminalBasicInfoDTO> resp = cbbTerminalAPI.listTerminal(pageRequest);
        return DefaultWebResponse.Builder.success(resp);
    }
    
    /**
     * 
     * 终端分页列表
     * 
     * @param request 分页列表请求参数
     * @return 分页结果
     * @throws BusinessException 业务异常  
     */
    @RequestMapping(value = "test")
    @NoAuthUrl
    public DefaultWebResponse test(PageWebRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        return DefaultWebResponse.Builder.success();
    }
    
}
