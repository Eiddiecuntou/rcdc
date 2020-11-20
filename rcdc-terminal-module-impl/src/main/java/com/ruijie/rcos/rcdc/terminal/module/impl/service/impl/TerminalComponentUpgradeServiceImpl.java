package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.GetVersionDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.TerminalComponentUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.componentupgrade.TerminalComponentUpgradeHandlerFactory;
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
    public TerminalVersionResultDTO getVersion(TerminalEntity terminalEntity, @Nullable String validateMd5) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        Assert.notNull(terminalEntity.getPlatform(), "platform can not be null");

        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(terminalEntity.getPlatform().name(), terminalEntity.getTerminalOsType());
        LOGGER.info("获取组件升级处理对象");
        TerminalComponentUpgradeHandler handler = null;
        try {
            handler = handlerFactory.getHandler(terminalType);
        } catch (BusinessException e) {
            LOGGER.error("接入类型为[{}]的终端[{}]，组件升级不支持", terminalType.name(), terminalEntity.getTerminalId());
            return buildUnSupportResult();
        }

        GetVersionDTO versionRequest = new GetVersionDTO();
        versionRequest.setTerminalId(terminalEntity.getTerminalId());
        versionRequest.setRainUpgradeVersion(terminalEntity.getRainUpgradeVersion());
        versionRequest.setOsInnerVersion(terminalEntity.getOsInnerVersion());
        versionRequest.setValidateMd5(validateMd5);
        return handler.getVersion(versionRequest);
    }

    private TerminalVersionResultDTO buildUnSupportResult() {
        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), null);
    }

}
