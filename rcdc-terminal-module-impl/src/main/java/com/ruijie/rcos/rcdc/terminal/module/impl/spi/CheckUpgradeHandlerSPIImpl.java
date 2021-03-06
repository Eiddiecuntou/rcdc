package com.ruijie.rcos.rcdc.terminal.module.impl.spi;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
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
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalConnectHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CheckSystemUpgradeResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
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
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherImplemetion;

/**
 * Description:???????????????????????????????????????????????????????????
 * Copyright:??Copyright??(c)??2018
 * Company:??Ruijie??Co.,??Ltd.
 * Create??Time:??2018/10/24
 *
 * @author??Jarman
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

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    private static final ExecutorService CHECK_UPGRADE_THREAD_POOL =
            ThreadExecutors.newBuilder("checkUpgradeThreadPool").maxThreadNum(80).queueSize(1000).build();

    private static final ExecutorService TERMINAL_EVENT_NOTICE_THREAD_POOL =
            ThreadExecutors.newBuilder("terminalEventNoticeThreadPool").maxThreadNum(80).queueSize(1000).build();

    private static final ExecutorService LEARN_TERMINAL_MODEL_THREAD_POOL =
            ThreadExecutors.newBuilder("learnTerminalModelThreadPool").maxThreadNum(80).queueSize(1000).build();

    private static final Long DEFAULT_TIME = 15000L;

    @Override
    public void dispatch(CbbDispatcherRequest request) {
        Assert.notNull(request, "CbbDispatcherRequest????????????");
        LOGGER.info("??????[{}]????????????????????????", request.getTerminalId());

        CbbShineTerminalBasicInfo basicInfo = convertJsondata(request);
        basicInfo.setReceiveDate(new Date());
        CHECK_UPGRADE_THREAD_POOL.execute(() -> {
            LOGGER.info("??????????????????[{}]????????????", request.getTerminalId());
            doDispatch(request, basicInfo);
        });
    }

    private void doDispatch(CbbDispatcherRequest request,CbbShineTerminalBasicInfo basicInfo) {

        long receiveTime = new Date().getTime() - basicInfo.getReceiveDate().getTime();
        if (receiveTime >= DEFAULT_TIME) {
            LOGGER.warn("??????[{}]??????????????????", request.getTerminalId());
            return;
        }

        // ?????????????????????????????????????????????????????????
        boolean allowConnect = connectHandlerSPI.isAllowConnect(basicInfo);
        if (!allowConnect) {
            LOGGER.info("??????[{}]???????????????", basicInfo.getTerminalId());
            Session session = sessionManager.getSessionByAlias(basicInfo.getTerminalId());
            session.close();
            return;
        }

        // ??????????????????????????????????????????????????????????????????
        CbbTerminalBizConfigDTO terminalBizConfigDTO = connectHandlerSPI.notifyTerminalSupport(basicInfo);
        LOGGER.info("terminalId:{},terminalBizConfigDTO: {}", request.getTerminalId(), JSON.toJSONString(terminalBizConfigDTO));
        Assert.notEmpty(terminalBizConfigDTO.getTerminalWorkModeArr(), "TerminalWorkModeArr can not empty");
        Assert.notNull(terminalBizConfigDTO.getTerminalPlatform(), "TerminalPlatform can not null");

        if (terminalBizConfigDTO.getTerminalPlatform() == CbbTerminalPlatformEnums.PC) {
            LOGGER.info("??????[{}]?????????????????????PC?????????????????????", basicInfo.getTerminalId());
            return;
        }
        basicInfo.setPlatform(terminalBizConfigDTO.getTerminalPlatform());
        basicInfo.setAuthMode(terminalBizConfigDTO.getAuthMode());
        basicInfo.setTerminalWorkSupportModeArr(terminalBizConfigDTO.getTerminalWorkModeArr());

        if (terminalBizConfigDTO.getAuthMode() == CbbTerminalPlatformEnums.IDV
                || terminalBizConfigDTO.getAuthMode() == CbbTerminalPlatformEnums.VOI) {
            LOGGER.info("??????[{}]???????????????[{}],???????????????????????????????????????", basicInfo.getTerminalId(), terminalBizConfigDTO.getTerminalPlatform().name());
            handleIdvProcess(request, basicInfo, terminalBizConfigDTO);
        } else {
            LOGGER.info("??????[{}]???????????????[{}],??????????????????", basicInfo.getTerminalId(), terminalBizConfigDTO.getTerminalPlatform().name());
            handleVdiProcess(request, basicInfo, terminalBizConfigDTO);
        }

        TERMINAL_EVENT_NOTICE_THREAD_POOL.execute(() -> {
            LOGGER.info("?????????????????????????????????????????????[{}]", basicInfo.getTerminalId());
            doNotice(basicInfo);
        });
        LEARN_TERMINAL_MODEL_THREAD_POOL.execute(() -> basicInfoService.saveTerminalModel(basicInfo));
    }

    private void handleIdvProcess(CbbDispatcherRequest request, CbbShineTerminalBasicInfo basicInfo, CbbTerminalBizConfigDTO terminalBizConfigDTO) {
        // ????????????????????????
        TerminalVersionResultDTO versionResult = null;
        SystemUpgradeCheckResult systemUpgradeCheckResult = null;
        if (!Objects.equals(Boolean.TRUE, basicInfo.getTciEnvironment())) {
            TerminalEntity terminalEntity =
                    basicInfoService.convertBasicInfo2TerminalEntity(request.getTerminalId(), request.getNewConnection(), basicInfo);
            // ??????????????????????????????RCDC?????????????????????????????????????????????
            versionResult = componentUpgradeService.getVersion(terminalEntity, basicInfo.getValidateMd5());
            systemUpgradeCheckResult = getSystemUpgradeCheckResult(terminalEntity);

            boolean isInUpgradeProcess = isNeedUpgradeOrAbnormalUpgradeResult(versionResult, systemUpgradeCheckResult);
            TerminalAuthResult authResult = terminalAuthHelper.processTerminalAuth(isInUpgradeProcess, basicInfo);
            basicInfoService.saveBasicInfo(terminalEntity, basicInfo, authResult.isAuthed());

            TerminalAuthorizeEntity terminalAuthorizeEntity = terminalAuthorizeDAO.findByTerminalId(request.getTerminalId());

            if (authResult.getAuthResult() == TerminalAuthResultEnums.FAIL) {
                LOGGER.info("??????[{}]????????????", basicInfo.getTerminalId());
                versionResult.setResult(CbbTerminalComponentUpgradeResultEnums.NO_AUTH.getResult());

                if (terminalAuthorizeEntity.getLicenseType().equals(CbbTerminalLicenseTypeEnums.CVA_IDV.name())
                        || terminalAuthorizeEntity.getLicenseType().equals(CbbTerminalLicenseTypeEnums.CVA.name())) {
                    versionResult.setResult(CbbTerminalComponentUpgradeResultEnums.NO_CVA_AUTH.getResult());
                }
            }
        } else {
            versionResult = new TerminalVersionResultDTO();
            systemUpgradeCheckResult = new SystemUpgradeCheckResult();
            versionResult.setResult(CbbTerminalComponentUpgradeResultEnums.NOT.getResult());
            Session session = sessionManager.getSessionByAlias(request.getTerminalId());
            CbbTerminalStateEnums state = session == null ? CbbTerminalStateEnums.OFFLINE : CbbTerminalStateEnums.ONLINE;
            basicInfoService.modifyTerminalState(request.getTerminalId(), state);
        }


        responseToShine(request, terminalBizConfigDTO, versionResult, systemUpgradeCheckResult);
    }

    private void handleVdiProcess(CbbDispatcherRequest request, CbbShineTerminalBasicInfo basicInfo, CbbTerminalBizConfigDTO terminalBizConfigDTO) {

        // ????????????????????????
        TerminalEntity terminalEntity =
                basicInfoService.convertBasicInfo2TerminalEntity(request.getTerminalId(), request.getNewConnection(), basicInfo);
        basicInfoService.saveBasicInfo(terminalEntity, basicInfo, Boolean.TRUE);

        // ??????????????????????????????RCDC?????????????????????????????????????????????
        TerminalVersionResultDTO versionResult = componentUpgradeService.getVersion(terminalEntity, basicInfo.getValidateMd5());
        SystemUpgradeCheckResult systemUpgradeCheckResult = getSystemUpgradeCheckResult(terminalEntity);

        responseToShine(request, terminalBizConfigDTO, versionResult, systemUpgradeCheckResult);
    }

    private void responseToShine(CbbDispatcherRequest request, CbbTerminalBizConfigDTO terminalBizConfigDTO, TerminalVersionResultDTO versionResult,
            SystemUpgradeCheckResult systemUpgradeCheckResult) {
        TerminalUpgradeResult terminalUpgradeResult = buildTerminalUpgradeResult(terminalBizConfigDTO, versionResult, systemUpgradeCheckResult);
        try {
            CbbResponseShineMessage cbbShineMessageRequest = MessageUtils.buildResponseMessage(request, terminalUpgradeResult);

            LOGGER.info("??????[{}]?????????????????? : {}", request.getTerminalId(), versionResult.getResult().toString());
            messageHandlerAPI.response(cbbShineMessageRequest);
        } catch (Exception e) {
            LOGGER.error("??????????????????????????????", e);
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
            // ???????????????????????????????????????????????????????????????????????????????????????????????????debug
            LOGGER.error("????????????????????????????????????????????????????????????", e);
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
