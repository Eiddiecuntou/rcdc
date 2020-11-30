package com.ruijie.rcos.rcdc.terminal.module.impl.spi.helper;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

    private static final int NO_AUTH_LIMIT = -1;

    @Autowired
    private TerminalLicenseService terminalLicenseService;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

    /**
     * 终端进行授权
     *
     * @param isNewConnection    是否新连接
     * @param isInUpgradeProcess 是否处于升级进程中
     * @param basicInfo          终端基本信息
     * @return TerminalAuthResult 授权结果
     */
    public TerminalAuthResult processTerminalAuth(boolean isNewConnection, boolean isInUpgradeProcess, CbbShineTerminalBasicInfo basicInfo) {
        Assert.notNull(basicInfo, "basicInfo can not be null");

        // 获取业务配置
        CbbTerminalBizConfigDTO bizConfigDTO = connectHandlerSPI.notifyTerminalSupport(basicInfo);

        String terminalId = basicInfo.getTerminalId();
        if (!basicInfoService.isNewTerminal(terminalId)) {
            LOGGER.info("终端[{}]{}不是新终端，需要更新终端信息", terminalId, basicInfo.getTerminalName());
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
        }

        LOGGER.info("新终端[{}]{}接入", terminalId, basicInfo.getTerminalName());
        if (isInUpgradeProcess) {
            LOGGER.info("终端处于升级过程中，暂不保存终端信息");
            // 终端需要升级，或者异常升级结果（不属于需要升级、不需要升级范畴，如：服务器准备中），不保存终端信息
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        CbbTerminalWorkModeEnums[] workModeArr = bizConfigDTO.getTerminalWorkModeArr();
        if (ArrayUtils.isEmpty(workModeArr)) {
            LOGGER.info("终端工作模式为空，跳过授权");
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
        }

        TerminalAuthResult finalResult = new TerminalAuthResult(true, TerminalAuthResultEnums.SUCCESS);
        for (CbbTerminalWorkModeEnums workMode : workModeArr) {
            if (workMode == CbbTerminalWorkModeEnums.IDV) {
                LOGGER.info("工作模式包含IDV，进行IDV授权");
                TerminalAuthResult authResult = processIdvTerminalLicense(basicInfo, isNewConnection);
                setAuthResult(finalResult, authResult);
            }

            if (workMode == CbbTerminalWorkModeEnums.VOI) {
                // TODO VOI授权暂未确定
                setAuthResult(finalResult, new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP));
            }
        }

        return finalResult;
    }

    private void setAuthResult(TerminalAuthResult finalResult, TerminalAuthResult authResult) {
        if (authResult.getAuthResult() == TerminalAuthResultEnums.FAIL) {
            LOGGER.info("授权失败");
            finalResult.setAuthResult(TerminalAuthResultEnums.FAIL);
        }

        if (!authResult.isNeedSaveTerminalInfo()) {
            LOGGER.info("需要更新终端信息");
            finalResult.setNeedSaveTerminalInfo(false);
        }
    }

    /**
     * idv终端授权处理。idv新终端接入并且idv授权个数有限制的情况下，如果终端没有处于不需要升级状态、或者处于不需要升级状态但授权不足，则不保存终端信息
     *
     * @param basicInfo       shine上报的终端基本信息
     * @param isNewConnection 是否是新连接
     * @return TerminalAuthResult  needSaveTerminalInfo -需要保存终端信息；false -不需要保存终端信息
     */
    private TerminalAuthResult processIdvTerminalLicense(CbbShineTerminalBasicInfo basicInfo, boolean isNewConnection) {
        String terminalId = basicInfo.getTerminalId();

        int licenseNum = terminalLicenseService.getIDVTerminalLicenseNum();
        if (licenseNum == NO_AUTH_LIMIT) {
            LOGGER.info("当前不限制IDV终端授权");
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
        }

        // 不需要升级场景下，如果授权失败无须保存idv终端信息；如果授权成功，在授权时已经保存了idv终端信息，无须再次保存
        if (terminalLicenseService.authIDV(terminalId, isNewConnection, basicInfo)) {
            LOGGER.info("idv终端[{}]{}授权成功", terminalId, basicInfo.getTerminalName());
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SUCCESS);
        }

        LOGGER.info("授权数不足，不保存idv终端[{}]{}信息", terminalId, basicInfo.getTerminalName());
        return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
    }

}
