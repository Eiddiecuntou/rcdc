package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.EditAdminPwdWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdWebRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
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

    /**
     * 修改终端管理员密码
     * 
     * @param request 请求参数
     * @param optLogRecorder 日志记录对象
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "changePassword")
    public DefaultWebResponse changePassword(EditAdminPwdWebRequest request, ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        Assert.notNull(request, "request不能为null");
        Assert.notNull(optLogRecorder, "optLogRecorder不能为null");

        String pwd = request.getPwd();
        CbbChangePasswordRequest changePwdRequest = new CbbChangePasswordRequest();
        changePwdRequest.setPassword(pwd);
        try {
            terminalOperatorAPI.changePassword(changePwdRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CHANGE_PWD_SUCCESS_LOG, pwd);
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CHANGE_PWD_FAIL_LOG, pwd, e.getI18nMessage());
            throw e;
        }
        return DefaultWebResponse.Builder.success();
    }

}
