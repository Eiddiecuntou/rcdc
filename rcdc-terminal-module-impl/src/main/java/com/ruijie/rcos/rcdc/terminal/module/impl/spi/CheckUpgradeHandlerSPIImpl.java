package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.codec.adapter.def.api.CbbTranspondMessageHandlerAPI;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbDispatcherRequest;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.codec.adapter.def.spi.CbbDispatcherHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBizConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalComponentUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.MessageUtils;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineAction;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalComponentUpgradeService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.SystemUpgradeCheckResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradeHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.spi.response.TerminalUpgradeResult;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

import java.util.concurrent.ExecutorService;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckUpgradeHandlerSPIImpl.class);

    @Autowired
    private CbbTranspondMessageHandlerAPI messageHandlerAPI;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalComponentUpgradeService componentUpgradeService;

    @Autowired
    private TerminalSystemUpgradeHandlerFactory handlerFactory;

    @Autowired
    private CbbTerminalConnectHandlerSPI connectHandlerSPI;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TerminalAuthHelper terminalAuthHelper;

    @Autowired
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    private static final ExecutorService CHECK_UPGRADE_THREAD_POOL =
            ThreadExecutors.newBuilder("checkUpgradeThreadPool").maxThreadNum(80).queueSize(1).build();

    private static final ExecutorService TERMINAL_EVENT_NOTICE_THREAD_POOL =
            ThreadExecutors.newBuilder("terminalEventNoticeThreadPool").maxThreadNum(80).queueSize(1).build();

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest不能为空");
        LOGGER.info("终端[{}]组件升级处理请求", request.getTerminalId());

        CHECK_UPGRADE_THREAD_POOL.execute(() -> {
            LOGGER.info("开始处理终端[{}]组件升级", request.getTerminalId());
            doDispatch(request);
        });
    }

    private void doDispatch(CbbDispatcherRequest request) {
        CbbShineTerminalBasicInfo basicInfo = convertJsondata(request);

        // 通知上层组件终端接入，判断是否允许接入
        boolean allowConnect = connectHandlerSPI.isAllowConnect(basicInfo);
        if (!allowConnect) {
            LOGGER.info("终端[{}]不允许接入", basicInfo.getTerminalId());
            Session session = sessionManager.getSessionByAlias(basicInfo.getTerminalId());
            session.close();
            return;
        }

        // 获取业务配置，将上层设置的终端类型存入数据库
        CbbTerminalBizConfigDTO terminalBizConfigDTO = connectHandlerSPI.notifyTerminalSupport(basicInfo);
        LOGGER.info("terminalId:{},terminalBizConfigDTO: {}", request.getTerminalId(), JSON.toJSONString(terminalBizConfigDTO));
        Assert.notEmpty(terminalBizConfigDTO.getTerminalWorkModeArr(), "TerminalWorkModeArr can not empty");
        Assert.notNull(terminalBizConfigDTO.getTerminalPlatform(), "TerminalPlatform can not null");

        if (terminalBizConfigDTO.getTerminalPlatform() == CbbTerminalPlatformEnums.PC) {
            LOGGER.info("终端[{}]，其平台类型为PC，无需升级处理", basicInfo.getTerminalId());
            return;
        }
        basicInfo.setPlatform(terminalBizConfigDTO.getTerminalPlatform());
        basicInfo.setAuthMode(terminalBizConfigDTO.getAuthMode());
        basicInfo.setTerminalWorkSupportModeArr(terminalBizConfigDTO.getTerminalWorkModeArr());

        if (terminalBizConfigDTO.getAuthMode() == CbbTerminalPlatformEnums.IDV
                || terminalBizConfigDTO.getAuthMode() == CbbTerminalPlatformEnums.VOI) {
            LOGGER.info("平台类型为[{}],进行升级包处理（包含授权）", terminalBizConfigDTO.getTerminalPlatform().name());
            handleIdvProcess(request, basicInfo, terminalBizConfigDTO);
        } else {
            LOGGER.info("平台类型为[{}],进行升级处理", terminalBizConfigDTO.getTerminalPlatform().name());
            handleVdiProcess(request, basicInfo, terminalBizConfigDTO);
        }

        TERMINAL_EVENT_NOTICE_THREAD_POOL.execute(() -> {
            LOGGER.debug("开始通知其他组件终端为在线状态[{}]", request.getTerminalId());
            doNotice(basicInfo);
        });
    }

    private void handleIdvProcess(CbbDispatcherRequest request, CbbShineTerminalBasicInfo basicInfo, CbbTerminalBizConfigDTO terminalBizConfigDTO) {
        // 保存终端基本信息
        TerminalEntity terminalEntity =
                basicInfoService.convertBasicInfo2TerminalEntity(request.getTerminalId(), request.getNewConnection(), basicInfo);
        // 检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        TerminalVersionResultDTO versionResult = componentUpgradeService.getVersion(terminalEntity, basicInfo.getValidateMd5());
        SystemUpgradeCheckResult systemUpgradeCheckResult = getSystemUpgradeCheckResult(terminalEntity);

        boolean isInUpgradeProcess = isNeedUpgradeOrAbnormalUpgradeResult(versionResult, systemUpgradeCheckResult);
        TerminalAuthResult authResult = terminalAuthHelper.processTerminalAuth(isInUpgradeProcess, basicInfo);
        basicInfoService.saveBasicInfo(terminalEntity, basicInfo, authResult.isAuthed());

        if (authResult.getAuthResult() == TerminalAuthResultEnums.FAIL) {
            LOGGER.info("终端[{}]授权失败", basicInfo.getTerminalId());
            versionResult.setResult(CbbTerminalComponentUpgradeResultEnums.NO_AUTH.getResult());
        }

        responseToShine(request, terminalBizConfigDTO, versionResult, systemUpgradeCheckResult);
    }

    private void handleVdiProcess(CbbDispatcherRequest request, CbbShineTerminalBasicInfo basicInfo, CbbTerminalBizConfigDTO terminalBizConfigDTO) {

        // 保存终端基本信息
        TerminalEntity terminalEntity =
                basicInfoService.convertBasicInfo2TerminalEntity(request.getTerminalId(), request.getNewConnection(), basicInfo);
        basicInfoService.saveBasicInfo(terminalEntity, basicInfo, Boolean.TRUE);

        // 检查终端升级包版本与RCDC中的升级包版本号，判断是否升级
        TerminalVersionResultDTO versionResult = componentUpgradeService.getVersion(terminalEntity, basicInfo.getValidateMd5());
        SystemUpgradeCheckResult systemUpgradeCheckResult = getSystemUpgradeCheckResult(terminalEntity);

        responseToShine(request, terminalBizConfigDTO, versionResult, systemUpgradeCheckResult);
    }

    private void responseToShine(CbbDispatcherRequest request, CbbTerminalBizConfigDTO terminalBizConfigDTO, TerminalVersionResultDTO versionResult,
            SystemUpgradeCheckResult systemUpgradeCheckResult) {
        TerminalUpgradeResult terminalUpgradeResult = buildTerminalUpgradeResult(terminalBizConfigDTO, versionResult, systemUpgradeCheckResult);
        try {
            CbbResponseShineMessage cbbShineMessageRequest = MessageUtils.buildResponseMessage(request, terminalUpgradeResult);

            LOGGER.debug("终端[{}]升级处理结束 : {}", request.getTerminalId(), JSON.toJSONString(versionResult));
            messageHandlerAPI.response(cbbShineMessageRequest);
        } catch (Exception e) {
            LOGGER.error("升级检查消息应答失败", e);
        }
    }

    private boolean isNeedUpgradeOrAbnormalUpgradeResult(TerminalVersionResultDTO versionResult, SystemUpgradeCheckResult systemUpgradeCheckResult) {
        return versionResult.getResult() != CbbTerminalComponentUpgradeResultEnums.NOT.getResult()
                || systemUpgradeCheckResult.getSystemUpgradeCode() != CheckSystemUpgradeResultEnums.NOT_NEED_UPGRADE.getResult();
    }

    private TerminalUpgradeResult buildTerminalUpgradeResult(CbbTerminalBizConfigDTO terminalBizConfig, TerminalVersionResultDTO versionResult,
            SystemUpgradeCheckResult systemUpgradeCheckResult) {
        TerminalUpgradeResult upgradeResult = new TerminalUpgradeResult();
        upgradeResult.setResult(versionResult.getResult());
        upgradeResult.setUpdatelist(versionResult.getUpdatelist());
        upgradeResult.setSystemUpgradeCode(systemUpgradeCheckResult.getSystemUpgradeCode());
        upgradeResult.setSystemUpgradeInfo(systemUpgradeCheckResult.getContent());
        upgradeResult.setTerminalWorkModeArr(terminalBizConfig.getTerminalWorkModeArr());
        upgradeResult.setPackageObtainMode(systemUpgradeCheckResult.getPackageObtainMode());
        return upgradeResult;
    }

    private SystemUpgradeCheckResult getSystemUpgradeCheckResult(TerminalEntity terminalEntity) {

        CbbTerminalTypeEnums terminalType = basicInfoService.obtainTerminalType(terminalEntity);

        SystemUpgradeCheckResult systemUpgradeCheckResult;
        try {
            TerminalTypeArchType terminalArchType = TerminalTypeArchType.convert(terminalType, terminalEntity.getCpuArch());
            TerminalSystemUpgradeHandler handler = handlerFactory.getHandler(terminalArchType);
            systemUpgradeCheckResult = handler.checkSystemUpgrade(terminalType, terminalEntity);
        } catch (Exception e) {
            // 这里有不支持系统升级的终端接入，如软终端，为避免大量的日志级别改为debug
            LOGGER.error("获取终端系统升级处理对象异常，不支持升级", e);
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

    private void doNotice(CbbShineTerminalBasicInfo basicInfo) {
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(CbbNoticeEventEnums.ONLINE);
        noticeRequest.setTerminalBasicInfo(basicInfo);
        terminalEventNoticeSPI.notify(noticeRequest);
    }
}
