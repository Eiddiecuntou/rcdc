package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalWhiteListHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseAuthService;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseCommonService;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Description: TerminalAuthHelper
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/20
 *
 * @author nting
 */
@Service
public class TerminalAuthHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalAuthHelper.class);

    @Autowired
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

    @Autowired
    private CbbTerminalWhiteListHandlerSPI whiteListHandlerSPI;

    @Autowired
    private TerminalLicenseAuthService terminalLicenseAuthService;

    @Autowired
    private TerminalLicenseCommonService terminalLicenseCommonService;

    @Autowired
    private TerminalAuthorizationWhitelistService terminalAuthorizationWhitelistService;

    /**
     * 终端进行授权
     *
     * @param isNewConnection 是否新连接
     * @param isInUpgradeProcess 是否处于升级进程中
     * @param basicInfo 终端基本信息
     * @return TerminalAuthResult 授权结果
     */
    public TerminalAuthResult processTerminalAuth(boolean isNewConnection, boolean isInUpgradeProcess, CbbShineTerminalBasicInfo basicInfo) {
        Assert.notNull(basicInfo, "basicInfo can not be null");

        // TCI OCS授权优先
        if (terminalAuthorizationWhitelistService.checkWhiteList(basicInfo)) {
            LOGGER.info("终端[{}]在白名单中，无需认证", basicInfo.getTerminalId());
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        if (whiteListHandlerSPI.checkWhiteList(basicInfo)) {
            LOGGER.info("终端[{}]在白名单中，无需认证", basicInfo.getTerminalId());
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        // 获取业务配置
        CbbTerminalBizConfigDTO bizConfigDTO = connectHandlerSPI.notifyTerminalSupport(basicInfo);

        String terminalId = basicInfo.getTerminalId();

        if (terminalLicenseCommonService.isTerminalAuthed(terminalId)) {
            LOGGER.info("终端[{}]{}已授权，需要更新终端信息", terminalId, basicInfo.getTerminalName());
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
        }

        LOGGER.info("未授权终端[{}]{}接入", terminalId, basicInfo.getTerminalName());
        if (isInUpgradeProcess) {
            LOGGER.info("终端处于升级过程中");
            // 终端需要升级，或者异常升级结果（不属于需要升级、不需要升级范畴，如：服务器准备中），不保存终端信息
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        CbbTerminalWorkModeEnums[] workModeArr = bizConfigDTO.getTerminalWorkModeArr();
        if (ArrayUtils.isEmpty(workModeArr)) {
            LOGGER.error("终端工作模式为空，跳过授权");
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        try {
            return terminalLicenseAuthService.auth(isNewConnection, basicInfo);
        } catch (BusinessException e) {
            LOGGER.error("终端[" + basicInfo.getTerminalId() + "]授权异常", e);
            return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
        }

    }

    /**
     * 处理终端授权扣除逻辑
     *
     * @param terminalId 终端id
     * @param authMode 平台类型
     * @param authed 是否授权
     * @throws BusinessException 业务异常
     */
    public void processDecreaseTerminalLicense(String terminalId, CbbTerminalPlatformEnums authMode, Boolean authed) throws BusinessException {
        Assert.notNull(terminalId, "terminalId can not be null");
        Assert.notNull(authMode, "authMode can not be null");
        Assert.notNull(authed, "authed can not be null");

        if (Objects.equals(authed, Boolean.TRUE)) {
            LOGGER.info("删除已授权终端[{}]，终端授权数量-1", terminalId);
            terminalLicenseAuthService.recycle(terminalId, authMode);
            return;
        }

        LOGGER.info("删除未授权终端[{}]，无需处理", terminalId);

    }


}
