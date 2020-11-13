package com.ruijie.rcos.rcdc.terminal.module.impl.spi;


import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.SystemUpgradeCheckResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalUpgradeResult;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Description: 终端检查升级，同时需要保存终端基本信息
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@DispatcherImplemetion(ShineAction.CHECK_UPGRADE)
public class CheckUpgradeHandlerSPIImpl implements CbbDispatcherHandlerSPI {

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalComponentUpgradeService componentUpgradeService;

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Autowired
    private TerminalLicenseService terminalLicenseService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckUpgradeHandlerSPIImpl.class);

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");

        LOGGER.info("组件升级处理请求开始处理。。。");
        // 保存终端基本信息
        String terminalId = request.getTerminalId();
        CbbShineTerminalBasicInfo basicInfo = convertJsondata(request);
        TerminalEntity terminalEntity = basicInfoService.convertBasicInfo2TerminalEntity(terminalId, request.getNewConnection(),
            basicInfo);

        // 检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(terminalEntity.getPlatform().name(), terminalEntity.getTerminalOsType());

        TerminalVersionResultDTO versionResult = componentUpgradeService.getVersion(terminalEntity, basicInfo.getValidateMd5());

        SystemUpgradeCheckResult systemUpgradeCheckResult = getSystemUpgradeCheckResult(terminalEntity, terminalType);

        boolean isNeedSaveTerminalBasicInfo = processIdvTerminalLicense(basicInfo, request.getNewConnection(),
            versionResult, systemUpgradeCheckResult);

        if (isNeedSaveTerminalBasicInfo) {
            basicInfoService.saveBasicInfo(terminalId, request.getNewConnection(), basicInfo);
        }
        responseToShine(request, versionResult, systemUpgradeCheckResult);
    }

    /**
     * idv终端授权处理。idv新终端接入并且idv授权个数有限制的情况下，如果终端没有处于不需要升级状态、或者处于不需要升级状态但授权不足，则不保存终端信息
     * @param basicInfo shine上报的终端基本信息
     * @param isNewConnection 是否是新连接
     * @param versionResult 终端组件升级检查结果
     * @param systemUpgradeCheckResult 终端系统升级检查结果
     * @return true -需要保存终端信息；false -不需要保存终端信息
     */
    private boolean processIdvTerminalLicense(CbbShineTerminalBasicInfo basicInfo, boolean isNewConnection,
        TerminalVersionResultDTO versionResult, SystemUpgradeCheckResult systemUpgradeCheckResult) {
        CbbTerminalTypeEnums terminalType = CbbTerminalTypeEnums.convert(basicInfo.getPlatform().name(),
            basicInfo.getTerminalOsType());
        String terminalId = basicInfo.getTerminalId();

        if (!isNeedAuthTerminal(terminalType)) {
            LOGGER.info("终端[{}]{}不是IDV终端，或者IDV终端授权数为-1，此时不限制终端授权并需要保存终端信息", terminalId, basicInfo.getTerminalName());
            return true;
        }

        if (!basicInfoService.isNewTerminal(terminalId)) {
            LOGGER.info("终端[{}]{}不是新终端，需要更新终端信息", terminalId, basicInfo.getTerminalName());
            return true;
        }

        LOGGER.info("新终端[{}]{}接入", terminalId, basicInfo.getTerminalName());
        if (isNeedUpgradeOrAbnormalUpgradeResult(versionResult, systemUpgradeCheckResult)) {
            LOGGER.info("终端升级检查结果为：{}、{}，暂不保存终端信息", versionResult.getResult(), systemUpgradeCheckResult.getSystemUpgradeCode());
            // 终端需要升级，或者异常升级结果（不属于需要升级、不需要升级范畴，如：服务器准备中），不保存终端信息
            return false;
        }

        // 不需要升级场景下，如果授权失败无须保存idv终端信息；如果授权成功，在授权时已经保存了idv终端信息，无须再次保存
        LOGGER.info("终端[{}]{}无须升级", terminalId, basicInfo.getTerminalName());
        if (terminalLicenseService.authIDV(terminalId, isNewConnection, basicInfo)) {
            LOGGER.info("idv终端[{}]{}授权成功", terminalId, basicInfo.getTerminalName());
            return false;
        }

        LOGGER.info("授权数不足，不保存idv终端[{}]{}信息", terminalId, basicInfo.getTerminalName());
        versionResult.setResult(CbbTerminalComponentUpgradeResultEnums.NO_AUTH.getResult());
        return false;
    }

    private void responseToShine(CbbDispatcherRequest request, TerminalVersionResultDTO versionResult,
        SystemUpgradeCheckResult systemUpgradeCheckResult) {
        TerminalUpgradeResult terminalUpgradeResult = buildTerminalUpgradeResult(versionResult, systemUpgradeCheckResult);
        try {
            CbbResponseShineMessage cbbShineMessageRequest = MessageUtils.buildResponseMessage(request, terminalUpgradeResult);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("response check upgrade : {}", JSON.toJSONString(cbbShineMessageRequest));
            }
            messageHandlerAPI.response(cbbShineMessageRequest);
        } catch (Exception e) {
            LOGGER.error("升级检查消息应答失败", e);
        }
    }

    private boolean isNeedUpgradeOrAbnormalUpgradeResult(TerminalVersionResultDTO versionResult,
        SystemUpgradeCheckResult systemUpgradeCheckResult) {
        return versionResult.getResult() != CbbTerminalComponentUpgradeResultEnums.NOT.getResult() ||
            systemUpgradeCheckResult.getSystemUpgradeCode() != CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE.getResult();
    }

    /**
     * 判断终端是否有可能需要授权
     * @param terminalType 终端类型
     * @return true idv终端，并且当前限制IDV终端授权个数（终端可能需要被授权）；false 非idv终端，或者当前不限制IDV终端授权个数
     */
    private boolean isNeedAuthTerminal(CbbTerminalTypeEnums terminalType) {
        int licenseNum = terminalLicenseService.getIDVTerminalLicenseNum();
        if (licenseNum == -1) {
            LOGGER.info("当前不限制IDV终端授权");
            return false;
        }
        return terminalType == CbbTerminalTypeEnums.IDV_LINUX;
    }

    private TerminalUpgradeResult buildTerminalUpgradeResult(TerminalVersionResultDTO versionResult,
            SystemUpgradeCheckResult systemUpgradeCheckResult) {
        TerminalUpgradeResult upgradeResult = new TerminalUpgradeResult();
        upgradeResult.setResult(versionResult.getResult());
        upgradeResult.setUpdatelist(versionResult.getUpdatelist());
        upgradeResult.setSystemUpgradeCode(systemUpgradeCheckResult.getSystemUpgradeCode());
        upgradeResult.setSystemUpgradeInfo(systemUpgradeCheckResult.getContent());
        return upgradeResult;
    }

    private SystemUpgradeCheckResult getSystemUpgradeCheckResult(TerminalEntity terminalEntity, CbbTerminalTypeEnums terminalType) {
        SystemUpgradeCheckResult systemUpgradeCheckResult;
        try {
            TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(terminalType);
            systemUpgradeCheckResult = handler.checkSystemUpgrade(terminalType, terminalEntity);
        } catch (BusinessException e) {
            // 这里有不支持系统升级的终端接入，如软终端，为避免大量的日志级别改为debug
            LOGGER.debug("获取终端系统升级处理对象异常，不支持升级", e);
            systemUpgradeCheckResult = new SystemUpgradeCheckResult();
            systemUpgradeCheckResult.setSystemUpgradeCode(CheckSystemUpgradeResultEnums.UNSUPPORT.getResult());
            systemUpgradeCheckResult.setContent(null);
        }

        return systemUpgradeCheckResult;
    }

    private CbbShineTerminalBasicInfo convertJsondata(CbbDispatcherRequest request) {
        String jsonData = String.valueOf(request.getData());
        CbbShineTerminalBasicInfo basicInfo = JSON.parseObject(jsonData, CbbShineTerminalBasicInfo.class);
        return basicInfo;
    }

}
