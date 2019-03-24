package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalLogNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.CloseTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.RestartTerminalBatchTaskHandler;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.batchtask.TerminalIdMappingUtils;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.EditAdminPwdWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdArrWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalIdWebRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request.TerminalLogDownLoadWebRequest;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.batch.BatchTaskSubmitResult;
import com.ruijie.rcos.sk.base.batch.DefaultBatchTaskItem;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
import com.ruijie.rcos.sk.webmvc.api.response.DownloadWebResponse;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalOperateController.class);

    @Autowired
    private CbbTerminalOperatorAPI terminalOperatorAPI;

    /**
     * 关闭终端
     *
     * @param request 终端id请求参数对象
     * @param optLogRecorder 日志记录对象
     * @param builder 批量任务创建对象
     * @return 返回成功或失败
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "shutdown")
    public DefaultWebResponse shutdownTerminal(TerminalIdArrWebRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "TerminalIdArrWebRequest不能为null");
        Assert.notNull(optLogRecorder, "optLogRecorder不能为null");
        Assert.notNull(builder, "builder不能为null");

        String[] terminalIdArr = request.getIdArr();
        if (terminalIdArr.length == 1) {
            return shutdownSingleTerminal(terminalIdArr[0], optLogRecorder);
        } else {
            Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
            UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
            final Iterator<DefaultBatchTaskItem> iterator =
                    Stream.of(idArr)
                            .map(id -> DefaultBatchTaskItem.builder().itemId(id)
                                    .itemName(BusinessKey.RCDC_TERMINAL_CLOSE_ITEM_NAME, new String[] {}).build())
                            .iterator();
            CloseTerminalBatchTaskHandler handler =
                    new CloseTerminalBatchTaskHandler(this.terminalOperatorAPI, idMap, iterator, optLogRecorder);
            BatchTaskSubmitResult result =
                    builder.setTaskName(BusinessKey.RCDC_TERMINAL_CLOSE_TASK_NAME, new String[] {})
                            .setTaskDesc(BusinessKey.RCDC_TERMINAL_CLOSE_TASK_DESC, new String[] {})
                            .registerHandler(handler).start();
            return DefaultWebResponse.Builder.success(result);
        }
    }

    private DefaultWebResponse shutdownSingleTerminal(String terminalId, ProgrammaticOptLogRecorder optLogRecorder) {

        try {
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.shutdown(request);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CLOSE_SUCCESS_LOG, terminalId);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_TERMINAL_CLOSE_SEND_SUCCESS, new String[] {});
        } catch (Exception e) {
            LOGGER.error("关闭终端失败：" + terminalId, e);
            if (e instanceof BusinessException) {
                BusinessException ex = (BusinessException) e;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CLOSE_FAIL_LOG, terminalId, ex.getI18nMessage());
                return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_TERMINAL_CLOSE_SEND_FAIL, new String[] {});
            } else {
                throw new IllegalStateException("发送关闭终端命令异常，终端为[" + terminalId + "]", e);
            }
        }
    }

    /**
     * 批量重启终端
     *
     * @param request 终端id请求参数对象
     * @param optLogRecorder 日志记录对象
     * @param builder 批量任务创建对象
     * @return 返回成功或失败
     * @throws BusinessException 业务异常
     */
    @RequestMapping(value = "restart")
    public DefaultWebResponse restartTerminal(TerminalIdArrWebRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "TerminalIdWebRequest不能为null");
        Assert.notNull(optLogRecorder, "optLogRecorder不能为null");
        Assert.notNull(builder, "builder不能为null");

        String[] terminalIdArr = request.getIdArr();
        if (terminalIdArr.length == 1) {
            return restartSingleTerminal(terminalIdArr[0], optLogRecorder);
        } else {
            Map<UUID, String> idMap = TerminalIdMappingUtils.mapping(terminalIdArr);
            UUID[] idArr = TerminalIdMappingUtils.extractUUID(idMap);
            final Iterator<DefaultBatchTaskItem> iterator =
                    Stream.of(idArr)
                            .map(id -> DefaultBatchTaskItem.builder().itemId(id)
                                    .itemName(BusinessKey.RCDC_TERMINAL_RESTART_ITEM_NAME, new String[] {}).build())
                            .iterator();
            RestartTerminalBatchTaskHandler handler =
                    new RestartTerminalBatchTaskHandler(this.terminalOperatorAPI, idMap, iterator, optLogRecorder);
            BatchTaskSubmitResult result =
                    builder.setTaskName(BusinessKey.RCDC_TERMINAL_RESTART_TASK_NAME, new String[] {})
                            .setTaskDesc(BusinessKey.RCDC_TERMINAL_RESTART_TASK_DESC, new String[] {})
                            .registerHandler(handler).start();
            return DefaultWebResponse.Builder.success(result);
        }

    }

    private DefaultWebResponse restartSingleTerminal(String terminalId, ProgrammaticOptLogRecorder optLogRecorder) {
        try {
            CbbTerminalIdRequest request = new CbbTerminalIdRequest();
            request.setTerminalId(terminalId);
            terminalOperatorAPI.restart(request);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_RESTART_SUCCESS_LOG, terminalId);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_TERMINAL_RESTART_SEND_SUCCESS, new String[] {});
        } catch (Exception e) {
            LOGGER.error("重启终端失败：" + terminalId, e);
            if (e instanceof BusinessException) {
                BusinessException ex = (BusinessException) e;
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_RESTART_FAIL_LOG, terminalId, ex.getI18nMessage());
                return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_TERMINAL_RESTART_SEND_FAIL, new String[] {});
            } else {
                throw new IllegalStateException("重启终端异常，terminalId为[" + terminalId + "]", e);
            }
        }
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
    public DefaultWebResponse changePassword(EditAdminPwdWebRequest request, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        Assert.notNull(request, "request不能为null");
        Assert.notNull(optLogRecorder, "optLogRecorder不能为null");

        String pwd = request.getPwd();
        CbbChangePasswordRequest changePwdRequest = new CbbChangePasswordRequest();
        changePwdRequest.setPassword(pwd);
        try {
            terminalOperatorAPI.changePassword(changePwdRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CHANGE_PWD_SUCCESS_LOG);
            return DefaultWebResponse.Builder.success(BusinessKey.RCDC_TERMINAL_MODULE_OPERATE_SUCCESS,
                    new String[] {});
        } catch (BusinessException e) {
            LOGGER.error("编辑终端管理员密码失败", e);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CHANGE_PWD_FAIL_LOG, e.getI18nMessage());
            return DefaultWebResponse.Builder.fail(BusinessKey.RCDC_TERMINAL_MODULE_OPERATE_FAIL, new String[] {});
        }
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
    @RequestMapping(value = "getCollectLog")
    public DefaultWebResponse getCollectLog(TerminalIdWebRequest request) throws BusinessException {
        Assert.notNull(request, "request不能为null");

        CbbTerminalCollectLogStatusResponse response = getCollectLogById(request);
        return DefaultWebResponse.Builder.success(response);
    }

    /**
     * 下载终端收集日志状态
     * 
     * @param request 请求参数
     * @return 请求结果
     * @throws BusinessException 业务异常
     * @throws IOException io异常
     */
    @RequestMapping(value = "downloadLog")
    public DownloadWebResponse downloadLog(TerminalLogDownLoadWebRequest request)
            throws BusinessException, IOException {
        Assert.notNull(request, "request不能为null");

        CbbTerminalLogNameRequest logRequest = new CbbTerminalLogNameRequest();
        CbbTerminalLogFileInfoResponse response = terminalOperatorAPI.getTerminalLogFileInfo(logRequest);

        InputStream inputStream = new FileInputStream(new File(response.getLogFilePath()));
        DownloadWebResponse.Builder builder = new DownloadWebResponse.Builder();

        return builder.setInputStream(inputStream, (long) inputStream.available())
                .setName(response.getLogFileName(), response.getSuffix()).build();
    }

    private CbbTerminalCollectLogStatusResponse getCollectLogById(TerminalIdWebRequest request)
            throws BusinessException {
        CbbTerminalIdRequest idRequest = new CbbTerminalIdRequest();
        idRequest.setTerminalId(request.getTerminalId());
        CbbTerminalCollectLogStatusResponse response = terminalOperatorAPI.getCollectLog(idRequest);
        return response;
    }

}
