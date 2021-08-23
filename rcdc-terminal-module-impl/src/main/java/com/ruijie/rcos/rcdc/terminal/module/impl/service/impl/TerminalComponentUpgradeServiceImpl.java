package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalOsTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalOsArchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
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
        Assert.notNull(terminalEntity.getTerminalOsType(), "osType can not be null");

        CbbCpuArchType cpuArchType = terminalEntity.getCpuArch() == null ? CbbCpuArchType.X86_64 : terminalEntity.getCpuArch();
        CbbTerminalOsTypeEnums osType = CbbTerminalOsTypeEnums.valueOf(terminalEntity.getTerminalOsType().toUpperCase());
        TerminalOsArchType osArchType = TerminalOsArchType.convert(osType, cpuArchType);
        LOGGER.info("终端[{}]获取组件升级处理对象, 操作系统架构为：{}", terminalEntity.getTerminalId(), osArchType.name());
        TerminalComponentUpgradeHandler handler;
        try {
            handler = handlerFactory.getHandler(osArchType);
        } catch (BusinessException e) {
            LOGGER.error("接入系统类型为[{}]的终端[{}]，组件升级不支持", osType.name(), terminalEntity.getTerminalId());
            return buildUnSupportResult();
        }

        GetVersionDTO versionRequest = new GetVersionDTO();
        versionRequest.setTerminalId(terminalEntity.getTerminalId());
        versionRequest.setRainUpgradeVersion(terminalEntity.getRainUpgradeVersion());
        versionRequest.setOsInnerVersion(terminalEntity.getOsInnerVersion());
        versionRequest.setValidateMd5(validateMd5);
        versionRequest.setCpuArch(terminalEntity.getCpuArch());
        return handler.getVersion(versionRequest);
    }

    private TerminalVersionResultDTO buildUnSupportResult() {
        return new TerminalVersionResultDTO(CbbTerminalComponentUpgradeResultEnums.NOT_SUPPORT.getResult(), null);
    }

}
