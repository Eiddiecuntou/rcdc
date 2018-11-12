package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void shutdown(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        operatorService.shutdown(terminalId);
    }

    @Override
    public void restart(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        operatorService.restart(terminalId);
    }

    @Override
    public void changePassword(String terminalId, String password) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        Assert.hasLength(password, "password不能为空");
        operatorService.changePassword(terminalId, password);
    }

    @Override
    public void gatherLog(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        operatorService.gatherLog(terminalId);
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        operatorService.detect(terminalId);
    }

    @Override
    public void detect(String[] terminalIdArr) throws BusinessException {
        Assert.notNull(terminalIdArr, "terminalIdArr不能为null");
        Assert.state(0 == terminalIdArr.length, "terminalIdArr不能为空");
        for (String terminalId : terminalIdArr) {
            detect(terminalId);
        }
    }

    @Override
    public String getTerminalLogName(String terminalId) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId不能为空");
        GatherLogCache cache = gatherLogCacheManager.getCache(terminalId);
        if (cache == null) {
            LOGGER.warn("收集日志缓存中不存在日志文件");
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
        if (cache.getState() == GatherLogStateEnums.FAILURE) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_GATHER_LOG_NOT_EXIST);
        }
        return cache.getLogFileName();
    }
}
