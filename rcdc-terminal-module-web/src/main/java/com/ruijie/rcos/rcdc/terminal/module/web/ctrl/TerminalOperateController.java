package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.CloseTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.RestartTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.EditAdminPwdWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdArrWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdWebRequest;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItem;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/3
 *
 * @author Jarman
 */
@Controller
@RequestMapping("/cbb/terminal")
@EnableCustomValidate(enable = false)
public class TerminalOperateController {

    @Autowired
    private CbbTerminalOperatorAPI terminalOperatorAPI;

    @Autowired
    private CbbTerminalBasicInfoAPI basicInfoAPI;

    /**
     * 关闭终端
     *
     * @param request 终端id请求参数对象
     * @return 返回成功或失败
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "shutdown")
    public DefaultWebResponse shutdownTerminal(TerminalIdArrWebRequest request, ProgrammaticOptLogRecorder optLogRecorder,
                                               BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "TerminalIdArrWebRequest不能为null");
        Assert.notNull(optLogRecorder, "optLogRecorder不能为null");
        Assert.notNull(builder, "builder不能为null");
        
        String[] terminalIdArr = request.getIdArr();
        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
        final Iterator<DefaultBatchTaskItem> iterator =
                Stream.of(idArr).map(id -> DefaultBatchTaskItem.builder().itemId(id).itemName("关闭终端").build()).iterator();
        CloseTerminalBatchTaskHandler handler = new CloseTerminalBatchTaskHandler(this.terminalOperatorAPI, idMap, iterator, optLogRecorder);

        builder.setTaskName(BusinessKey.RCDC_TERMINAL_ClOSE_TASK_NAME, new String[]{}).setTaskDesc(BusinessKey.RCDC_TERMINAL_ClOSE_TASK_DESC, new String[]{})
                .registerHandler(handler).start();
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 批量重启终端
     *
     * @param request 终端id请求参数对象
     * @return 返回成功或失败
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "restart")
    public DefaultWebResponse restartTerminal(TerminalIdArrWebRequest request, ProgrammaticOptLogRecorder optLogRecorder,
            BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "TerminalIdWebRequest不能为null");
        Assert.notNull(optLogRecorder, "optLogRecorder不能为null");
        Assert.notNull(builder, "builder不能为null");
        
        String[] terminalIdArr = request.getIdArr();
        Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
        UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
        final Iterator<DefaultBatchTaskItem> iterator =
                Stream.of(idArr).map(id -> DefaultBatchTaskItem.builder().itemId(id).itemName("重启终端").build()).iterator();
        RestartTerminalBatchTaskHandler handler = new RestartTerminalBatchTaskHandler(this.terminalOperatorAPI, idMap, iterator, optLogRecorder);

        builder.setTaskName(BusinessKey.RCDC_TERMINAL_RESTART_TASK_NAME, new String[]{}).setTaskDesc(BusinessKey.RCDC_TERMINAL_RESTART_TASK_DESC, new String[]{})
                .registerHandler(handler).start();
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 修改终端管理员密码
     *
     * @param request        请求参数
     * @param optLogRecorder 日志记录对象
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "changePassword")
    public DefaultWebResponse changePassword(EditAdminPwdWebRequest request, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        Assert.notNull(request, "request不能为null");
        Assert.notNull(optLogRecorder, "optLogRecorder不能为null");

        String pwd = request.getPwd();
        CbbChangePasswordRequest changePwdRequest = new CbbChangePasswordRequest();
        changePwdRequest.setPassword(pwd);
        try {
            terminalOperatorAPI.changePassword(changePwdRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CHANGE_PWD_SUCCESS_LOG, pwd);
        } catch (BusinessException e) {
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CLOSE_FAIL_LOG, pwd, e.getI18nMessage());
            throw e;
        }
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 收集日志
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "collectLog")
    public DefaultWebResponse collectLog(TerminalIdWebRequest request) throws BusinessException {
        Assert.notNull(request, "request不能为null");

        CbbTerminalIdRequest idRequest = new CbbTerminalIdRequest();
        idRequest.setTerminalId(request.getTerminalId());
        terminalOperatorAPI.collectLog(idRequest);

        return DefaultWebResponse.Builder.success();
    }
    
    /**
     * 获取终端收集日志状态
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "getCollectLogStatus")
    public DefaultWebResponse getCollectLogStatus(TerminalIdWebRequest request) throws BusinessException {
        Assert.notNull(request, "request不能为null");

        CbbTerminalIdRequest idRequest = new CbbTerminalIdRequest();
        idRequest.setTerminalId(request.getTerminalId());
        //TODO 前端交互未确定
        CbbTerminalCollectLogStatusResponse response = terminalOperatorAPI.getCollectLog(idRequest);

        return DefaultWebResponse.Builder.success(response);
    }
    
}
