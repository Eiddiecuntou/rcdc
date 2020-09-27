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

        if (versionResult.getResult() == CbbTerminalComponentUpgradeResultEnums.NOT.getResult() &&
            systemUpgradeCheckResult.getSystemUpgradeCode() == CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE.getResult()) {
            LOGGER.info("终端[{}]{}无须升级", request.getTerminalId(), basicInfo.getTerminalName());
            if (terminalType == CbbTerminalTypeEnums.IDV_LINUX && basicInfoService.isNewTerminal(terminalId) &&
                !terminalLicenseService.auth(terminalId)) {
                LOGGER.info("终端[{}]{}是idv，是新接入的终端，并且授权失败。升级结果设值为：NO_AUTH", request.getTerminalId(),
                    basicInfo.getTerminalName());
                versionResult.setResult(CbbTerminalComponentUpgradeResultEnums.NO_AUTH.getResult());
            } else {
                LOGGER.info("终端[{}]{}不是idv终端，或者是接入过的终端，或者授权成功。保存终端信息", request.getTerminalId(), basicInfo.getTerminalName());
                basicInfoService.saveBasicInfo(terminalId, request.getNewConnection(), basicInfo);
            }
        }

        // 构建组件升级和系统升级检测结果对象
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
