package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.offlinelogin.OfflineLoginSettingRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalCollectLogStatusResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalLogFileInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.offlinelogin.OfflineLoginSettingResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Description: 终端操作实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/5
 *
 * @author Jarman
 */
public class CbbTerminalOperatorAPIImpl implements CbbTerminalOperatorAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalOperatorAPIImpl.class);

    private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,16})$";

    @Autowired
    private TerminalOperatorService operatorService;

    @Autowired
    private CollectLogCacheManager collectLogCacheManager;

    @Override
    public void shutdown(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.shutdown(terminalId);
    }

    @Override
    public void restart(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.restart(terminalId);
    }

    @Override
    public void changePassword(CbbChangePasswordRequest request) throws BusinessException {
        Assert.notNull(request, "CbbChangePasswordRequest不能为空");

        checkPwdIsLegal(request.getPassword());
        operatorService.changePassword(request.getPassword());
    }

    private void checkPwdIsLegal(String password) throws BusinessException {
        if (Pattern.matches(REGEX_PASSWORD, password)) {
            return;
        }
        throw new BusinessException(BusinessKey.RCDC_TERMINAL_ADMIN_PWD_ILLEGAL);
    }

    @Override
    public void collectLog(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        String terminalId = request.getTerminalId();
        operatorService.collectLog(terminalId);
    }

    @Override
    public void singleDetect(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");

        String terminalId = request.getTerminalId();
        operatorService.detect(terminalId);
    }

    @Override
    public CbbTerminalCollectLogStatusResponse getCollectLog(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CollectLogCache cache = getCollectLogCache(request.getTerminalId());
        CbbTerminalCollectLogStatusResponse response = new CbbTerminalCollectLogStatusResponse();
        response.setLogName(cache.getLogFileName());
        response.setState(cache.getState());
        return response;
    }

    @Override
    public CbbTerminalLogFileInfoResponse getTerminalLogFileInfo(String logFileName) throws BusinessException {
        Assert.notNull(logFileName, "logFileName can not be null");

        String logFilePath = Constants.STORE_TERMINAL_LOG_PATH + logFileName;
        checkFileExist(logFilePath);
        String logFileNameWithoutExtension = Files.getNameWithoutExtension(logFileName);
        String suffix = getFileSuffix(logFileName);
        CbbTerminalLogFileInfoResponse response = new CbbTerminalLogFileInfoResponse();
        response.setLogFilePath(logFilePath);
        response.setLogFileName(logFileNameWithoutExtension);
        response.setSuffix(suffix);

        return response;
    }

    private void checkFileExist(String logFilePath) throws BusinessException {
        File logFile = new File(logFilePath);
        if (logFile.isFile()) {
            return;
        }
        throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_NOT_EXIST);
    }

    private String getFileSuffix(String fileName) {
        String suffix = "";
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf > -1) {
            suffix = fileName.substring(lastIndexOf + 1);
        }
        return suffix;
    }

    /**
     * 获取日志缓存
     * 
     * @param terminalId 终端id
     * @return 日志收集缓存
     * @throws BusinessException 业务异常
     */
    private CollectLogCache getCollectLogCache(String terminalId) throws BusinessException {
        CollectLogCache cache = collectLogCacheManager.getCache(terminalId);
        if (cache == null) {
            // 日志不存在，构造失败状态信息
            cache = new CollectLogCache(CbbCollectLogStateEnums.FAULT);
        }
        return cache;
    }

    @Override
    public void relieveFault(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.relieveFault(terminalId);
    }

    @Override
    public void clearIdvTerminalDataDisk(CbbTerminalIdRequest idRequest) throws BusinessException {
        Assert.notNull(idRequest,"idRequest can not be null");

        String terminalId = idRequest.getTerminalId();
        operatorService.diskClear(terminalId);
    }

    /**
     * IDV终端离线登录设置
     *
     * @param request 请求参数
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @Override
    public void idvOfflineLoginSetting(OfflineLoginSettingRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        operatorService.offlineLoginSetting(request.getOfflineAutoLocked());
    }

    /**
     * IDV终端离线登录设置
     *
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @Override
    public OfflineLoginSettingResponse queryOfflineLoginSetting() throws BusinessException {
        String offlineLoginSetting = operatorService.queryOfflineLoginSetting();
        return new OfflineLoginSettingResponse(offlineLoginSetting);
    }

}
