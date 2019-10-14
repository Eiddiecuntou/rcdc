package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.GetVersionRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalComponentUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.TerminalComponentUpgradeHandlerFactory;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 终端组件升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
@Service
public class TerminalComponentUpgradeServiceImpl implements TerminalComponentUpgradeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalComponentUpgradeServiceImpl.class);

    @Autowired
    private TerminalComponentUpgradeHandlerFactory handlerFactory;

    @Override
    public TerminalVersionResultDTO getVersion(TerminalEntity terminalEntity, @Nullable String validateMd5) throws BusinessException {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        Assert.notNull(terminalEntity.getPlatform(), "platform can not be null");

        TerminalTypeEnums terminalType = TerminalTypeEnums.convert(terminalEntity.getPlatform().name(), terminalEntity.getTerminalOsType());
        LOGGER.info("获取组件升级处理对象");
        TerminalComponentUpgradeHandler handler = handlerFactory.getHandler(terminalType);

        GetVersionRequest versionRequest = new GetVersionRequest();
        versionRequest.setRainUpgradeVersion(terminalEntity.getRainUpgradeVersion());
        versionRequest.setRainOsVersion(terminalEntity.getRainOsVersion());
        versionRequest.setValidateMd5(validateMd5);
        return handler.getVersion(versionRequest);
    }

}
