package com.ruijie.rcos.rcdc.terminal.module.impl.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbChangePasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBatDetectRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNameResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

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

    @Autowired
    private TerminalOperatorService operatorService;

    @Autowired
    private GatherLogCacheManager gatherLogCacheManager;

    @Override
    public DefaultResponse shutdown(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request,"CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.shutdown(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse restart(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request,"CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.restart(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse changePassword(CbbChangePasswordRequest request) throws BusinessException {
        Assert.notNull(request, "CbbChangePasswordRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        Assert.hasText(request.getPassword(), "password不能为空");
        operatorService.changePassword(request.getTerminalId(), request.getPassword());
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse gatherLog(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request,"CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.gatherLog(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse detect(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request,"CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        operatorService.detect(terminalId);
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse detect(CbbTerminalBatDetectRequest request) throws BusinessException {
        Assert.notNull(request, "CbbTerminalBatDetectRequest不能为null");
        Assert.state(request.getTerminalIdArr().length > 0, "terminalIdArr不能为空");
        for (String terminalId : request.getTerminalIdArr()) {
            CbbTerminalIdRequest terminalIdRequest = new CbbTerminalIdRequest();
            terminalIdRequest.setTerminalId(terminalId);
            detect(terminalIdRequest);
        }
        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbTerminalNameResponse getTerminalLogName(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request,"CbbTerminalIdRequest不能为空");
        Assert.hasText(request.getTerminalId(), "terminalId不能为空");
        String terminalId = request.getTerminalId();
        GatherLogCache cache = gatherLogCacheManager.getCache(terminalId);
        if (cache == null) {
            LOGGER.warn("收集日志缓存中不存在日志文件");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
        if (cache.getState() == GatherLogStateEnums.FAILURE) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
        CbbTerminalNameResponse response = new CbbTerminalNameResponse();
        response.setTerminalName(cache.getLogFileName());
        return response;
    }
}
