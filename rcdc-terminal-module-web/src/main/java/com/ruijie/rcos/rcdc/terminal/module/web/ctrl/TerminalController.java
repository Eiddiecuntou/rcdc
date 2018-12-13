package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPageRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalPageRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
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
     * @param request 分页列表请求
     * @return 分页响应
     * @throws BusinessException 业务异常  
     */
    @RequestMapping("listTerminal")
    public DefaultWebResponse listTerminal(ListTerminalPageRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalPageRequest pageRequest = new CbbTerminalPageRequest();
        pageRequest.setCurrentPage(request.getCurrentPage());
        pageRequest.setPageSize(request.getPageSize());
        pageRequest.setTerminalSystemVersion(request.getTerminalSystemVersion());
        pageRequest.setTerminalType(request.getTerminalType());
        DefaultPageResponse<CbbTerminalBasicInfoDTO> resp = cbbTerminalAPI.listTerminal(pageRequest);
        return DefaultWebResponse.Builder.success(resp);
    }
    
//    public DefaultWebResponse getTerminalPage(PageWebRequest request) {
//        request.getLimit();
//        request.getPage();
//    }

}
