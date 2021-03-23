package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

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

    private static final int NO_AUTH_LIMIT = -1;

    @Autowired
    private TerminalLicenseIDVServiceImpl terminalLicenseIDVServiceImpl;

    @Autowired
    private TerminalLicenseVoiServiceImpl terminalLicenseVoiServiceImpl;

    @Autowired
    private TerminalLicenseVoiUpgradeServiceImpl terminalLicenseVoiUpgradeServiceImpl;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

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

        // 获取业务配置
        CbbTerminalBizConfigDTO bizConfigDTO = connectHandlerSPI.notifyTerminalSupport(basicInfo);

        String terminalId = basicInfo.getTerminalId();
        if (basicInfoService.isAuthed(terminalId)) {
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

        TerminalAuthResult finalResult = new TerminalAuthResult(true, TerminalAuthResultEnums.SUCCESS);
        if (basicInfo.getPlatform() == CbbTerminalPlatformEnums.IDV) {
            LOGGER.info("平台类型为IDV，进行IDV授权");
            TerminalAuthResult authResult = processIdvTerminalLicense(basicInfo, isNewConnection);
            return authResult;
        }

        if (basicInfo.getPlatform() == CbbTerminalPlatformEnums.VOI) {
            LOGGER.info("平台类型为VOI，进行VOI授权");
            TerminalAuthResult authResult = processVoiTerminalLicense(basicInfo, isNewConnection);
            return authResult;
        }

        return finalResult;
    }

    /**
     * idv终端授权处理。idv新终端接入并且idv授权个数有限制的情况下，如果终端没有处于不需要升级状态、或者处于不需要升级状态但授权不足，则不保存终端信息
     *
     * @param basicInfo shine上报的终端基本信息
     * @param isNewConnection 是否是新连接
     * @return TerminalAuthResult needSaveTerminalInfo -需要保存终端信息；false -不需要保存终端信息
     */
    private TerminalAuthResult processIdvTerminalLicense(CbbShineTerminalBasicInfo basicInfo, boolean isNewConnection) {
        String terminalId = basicInfo.getTerminalId();

        int licenseNum = terminalLicenseIDVServiceImpl.getTerminalLicenseNum();
        if (licenseNum == NO_AUTH_LIMIT) {
            LOGGER.info("当前不限制IDV终端授权");
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
        }

        // 不需要升级场景下，如果授权失败无须保存idv终端信息；如果授权成功，在授权时已经保存了idv终端信息，无须再次保存
        if (terminalLicenseIDVServiceImpl.auth(terminalId, isNewConnection, basicInfo)) {
            LOGGER.info("idv终端[{}]{}授权成功", terminalId, basicInfo.getTerminalName());
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SUCCESS);
        }

        if (terminalLicenseVoiServiceImpl.authByIdv(terminalId, isNewConnection, basicInfo)) {
            LOGGER.info("idv终端[{}]{}使用VOI升级授权成功", terminalId, basicInfo.getTerminalName());
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SUCCESS);
        }

        LOGGER.info("授权数不足，保存idv终端[{}]{}信息为未授权状态", terminalId, basicInfo.getTerminalName());
        return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
    }


    /**
     * voi终端授权处理。voi新终端接入并且voi授权个数有限制的情况下，如果终端没有处于不需要升级状态、或者处于不需要升级状态但授权不足，则不保存终端信息
     *
     * @param basicInfo shine上报的终端基本信息
     * @param isNewConnection 是否是新连接
     * @return TerminalAuthResult needSaveTerminalInfo -需要保存终端信息；false -不需要保存终端信息
     */
    private TerminalAuthResult processVoiTerminalLicense(CbbShineTerminalBasicInfo basicInfo, boolean isNewConnection) {
        String terminalId = basicInfo.getTerminalId();

        int licenseNum = terminalLicenseVoiServiceImpl.getTerminalLicenseNum();
        if (licenseNum == NO_AUTH_LIMIT) {
            LOGGER.info("当前不限制VOI终端授权");
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
        }

        // 不需要升级场景下，如果授权失败无须保存idv终端信息；如果授权成功，在授权时已经保存了idv终端信息，无须再次保存
        if (terminalLicenseVoiServiceImpl.auth(terminalId, isNewConnection, basicInfo)) {
            LOGGER.info("voi终端[{}]{}授权成功", terminalId, basicInfo.getTerminalName());
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SUCCESS);
        }

        LOGGER.info("授权数不足，保存voi终端[{}]{}信息为未授权状态", terminalId, basicInfo.getTerminalName());
        return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
    }

    /**
     * 处理终端授权扣除逻辑
     *
     * @param terminalId 终端id
     * @param terminalPlatform 平台类型
     * @param authed 是否授权
     */
    public void processDecreaseTerminalLicense(String terminalId, CbbTerminalPlatformEnums terminalPlatform
            , Boolean authed) {
        Assert.notNull(terminalId, "terminalId can not be null");
        Assert.notNull(terminalPlatform, "terminalPlatform can not be null");
        Assert.notNull(authed, "authed can not be null");

        if (terminalPlatform == CbbTerminalPlatformEnums.IDV && Objects.equals(authed, Boolean.TRUE)) {
            LOGGER.info("删除已授权IDV终端[{}]，IDV终端授权数量-1", terminalId);
            processDecreaseIdvTerminalLicense();
        }

        if (terminalPlatform == CbbTerminalPlatformEnums.VOI && Objects.equals(authed, Boolean.TRUE)) {
            LOGGER.info("删除已授权VOI终端[{}]，VOI终端授权数量-1",  terminalId);
            processDecreaseVoiTerminalLicense();
        }
    }

    private void processDecreaseIdvTerminalLicense() {
        // 如果存在VOI升级授权，则先扣除VOI升级授权
        Integer voiUpgradeUsed = terminalLicenseVoiUpgradeServiceImpl.getUsedNum();
        LOGGER.info("VOI升级授权已用数为：[{}]", voiUpgradeUsed);
        if (voiUpgradeUsed > 0) {
            LOGGER.info("存在voi升级授权，则先扣除voi升级授权");
            terminalLicenseVoiServiceImpl.decreaseCacheLicenseUsedNumByIdv();
        } else {
            terminalLicenseIDVServiceImpl.decreaseCacheLicenseUsedNum();
        }
    }

    private void processDecreaseVoiTerminalLicense() {
        terminalLicenseVoiServiceImpl.decreaseCacheLicenseUsedNum();
    }

}
