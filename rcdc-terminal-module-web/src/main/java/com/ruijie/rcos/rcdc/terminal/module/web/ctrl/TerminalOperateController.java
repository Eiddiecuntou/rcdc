package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdWebRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/3
 *
 * @author Jarman
 */
@Controller
@RequestMapping("/terminal")
@EnableCustomValidate(enable = false)
public class TerminalOperateController {

    @Autowired
    private CbbTerminalOperatorAPI terminalOperatorAPI;

    /**
     * 关闭终端
     *
     * @param request 终端id请求参数对象
     * @return 返回成功或失败
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "shutdown")
    public DefaultWebResponse shutdownTerminal(TerminalIdWebRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalIdWebRequest不能为null");
        CbbTerminalIdRequest apiRequest = new CbbTerminalIdRequest();
        apiRequest.setTerminalId(request.getTerminalId());
        terminalOperatorAPI.shutdown(apiRequest);
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 重启终端
     *
     * @param request 终端id请求参数对象
     * @return 返回成功或失败
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "restart")
    public DefaultWebResponse restartTerminal(TerminalIdWebRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalIdWebRequest不能为null");
        CbbTerminalIdRequest apiRequest = new CbbTerminalIdRequest();
        apiRequest.setTerminalId(request.getTerminalId());
        terminalOperatorAPI.restart(apiRequest);
        return DefaultWebResponse.Builder.success();
    }

}
