package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.codec.adapter.def.dto.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DataDiskClearCodeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeOfflineLoginConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeTerminalPasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Description: ????????????
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
@Service
public class TerminalOperatorServiceImpl implements TerminalOperatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalOperatorServiceImpl.class);

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private CollectLogCacheManager collectLogCacheManager;

    @Autowired
    private TerminalDetectService terminalDetectService;

    @Autowired
    private TerminalDetectionDAO terminalDetectionDAO;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;


    /**
     * ???????????????????????????,??????1????????????
     */
    private static final ThreadExecutor NOTICE_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder(TerminalOperatorServiceImpl.class.getName()).maxThreadNum(1).queueSize(20).build();

    @Override
    public void shutdown(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId ????????????");
        checkAllowOperate(terminalId, PublicBusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_SHUTDOWN);
        operateTerminal(terminalId, SendTerminalEventEnums.SHUTDOWN_TERMINAL, "", BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SHUTDOWN);
    }

    @Override
    public void restart(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId ????????????");
        checkAllowOperate(terminalId, BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_RESTART);
        operateTerminal(terminalId, SendTerminalEventEnums.RESTART_TERMINAL, "", BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_RESTART);
    }

    @Override
    public void changePassword(String password) throws BusinessException {
        Assert.hasText(password, "password ????????????");

        getTerminalAdminPassword();
        String encryptPwd = AesUtil.encrypt(password, Constants.TERMINAL_ADMIN_PASSWORD_AES_KEY);
        globalParameterAPI.updateParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY, encryptPwd);

        // ???????????????????????????????????????
        NOTICE_HANDLER_THREAD_POOL.execute(() -> sendNewPwdToOnlineTerminal(encryptPwd));
    }

    @Override
    public String getTerminalPassword() throws BusinessException {
        return getTerminalAdminPassword();
    }

    private String getTerminalAdminPassword() throws BusinessException {
        String adminPwd = globalParameterAPI.findParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY);
        if (StringUtils.isBlank(adminPwd)) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_ADMIN_PWD_RECORD_NOT_EXIST);
        }

        return adminPwd;
    }

    private void sendNewPwdToOnlineTerminal(String password) {
        LOGGER.debug("????????????????????????????????????????????????");
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();

        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.debug("???????????????");
            return;
        }

        for (String terminalId : onlineTerminalIdList) {
            ChangeTerminalPasswordRequest request = new ChangeTerminalPasswordRequest(password);
            try {
                operateTerminal(terminalId, SendTerminalEventEnums.CHANGE_TERMINAL_PASSWORD, request,
                        BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SEND_PASSWORD_CHANGE);
            } catch (Exception e) {
                LOGGER.error("send new password to terminal failed, terminalId[" + terminalId + "], password[" + password + "]", e);
            }
        }
    }


    @Override
    public void offlineLoginSetting(Integer offlineAutoLocked) throws BusinessException {
        Assert.notNull(offlineAutoLocked, "offlineAutoLocked ????????????");

        // ??????????????????????????????
        globalParameterAPI.updateParameter(Constants.OFFLINE_LOGIN_TIME_KEY, offlineAutoLocked.toString());
        // ????????????IDV??????
        List<String> onlineIdvTerminalIdList = getOnlineIdvTerminal();
        // ?????????IDV??????????????????????????????
        NOTICE_HANDLER_THREAD_POOL.execute(() -> sendOfflineSettingToOnlineIdvTerminal(offlineAutoLocked, onlineIdvTerminalIdList));
    }

    /**
     * ??????????????????????????????
     *
     * @throws BusinessException ????????????
     */
    @Override
    public String queryOfflineLoginSetting() {
        return globalParameterAPI.findParameter(Constants.OFFLINE_LOGIN_TIME_KEY);
    }

    private List<String> getOnlineIdvTerminal() {
        List<String> onlineIdvTerminalIdList = Lists.newArrayList();
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();
        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.debug("???????????????");
            return onlineIdvTerminalIdList;
        }
        for (String terminalId : onlineTerminalIdList) {
            TerminalEntity entity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
            if (entity == null) {
                LOGGER.error("terminal not exist, terminalId[" + terminalId + "]");
                continue;
            }
            if (entity.getPlatform() == CbbTerminalPlatformEnums.IDV) {
                onlineIdvTerminalIdList.add(terminalId);
            }

        }
        return onlineIdvTerminalIdList;
    }

    private void sendOfflineSettingToOnlineIdvTerminal(Integer offlineAutoLocked, List<String> onlineIdvTerminalIdList) {
        LOGGER.debug("???IDV??????????????????????????????");
        // ?????????IDV??????????????????????????????
        for (String terminalId : onlineIdvTerminalIdList) {
            try {
                ChangeOfflineLoginConfig configRequest =
                        new ChangeOfflineLoginConfig(offlineAutoLocked);
                operateTerminal(terminalId, SendTerminalEventEnums.SET_DISCONNECT_SERVER_USE_DAY, configRequest,
                        BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SEND_OFFLINE_LOGIN_CONFIG);
            } catch (Exception e) {
                LOGGER.error("send offline login config to terminal failed, terminalId[" + terminalId + "]", e);
            }
        }
    }

    @Override
    public void collectLog(final String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId????????????");
        checkAllowOperate(terminalId, BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_COLLECT_LOG);

        checkStoreLogDirExist();

        CollectLogCache collectLogCache = collectLogCacheManager.getCache(terminalId);
        if (collectLogCache != null && CbbCollectLogStateEnums.DOING == collectLogCache.getState()) {
            LOGGER.info("??????[{}]?????????????????????????????????????????????", terminalId);
            return;
        }
        collectLogCacheManager.addCache(terminalId);

        try {
            operateTerminal(terminalId, SendTerminalEventEnums.COLLECT_TERMINAL_LOG, "", BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_COLLECT_LOG);
        } catch (BusinessException e) {
            collectLogCacheManager.removeCache(terminalId);
            throw e;
        }
    }

    private void checkStoreLogDirExist() {
        File storeLogDir = new File(Constants.STORE_TERMINAL_LOG_PATH);
        if (!storeLogDir.isDirectory()) {
            FileOperateUtil.createFileDirectory(storeLogDir);
        }
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId????????????");
        checkAllowOperate(terminalId, BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_DETECT);

        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        TerminalDetectionEntity detection = terminalDetectService.findInCurrentDate(terminalId);
        if (detection == null) {
            terminalDetectService.save(terminalId);
            return;
        }

        if (detection.getDetectState() == DetectStateEnums.CHECKING || detection.getDetectState() == DetectStateEnums.WAIT) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_DETECT_IS_DOING);
        }

        // ??????????????????????????????????????????
        terminalDetectService.delete(detection.getId());
        terminalDetectService.save(terminalId);
    }

    @Override
    public void sendDetectRequest(TerminalDetectionEntity detection) throws BusinessException {
        Assert.notNull(detection, "detect entity can not be null");
        String terminalId = detection.getTerminalId();
        Assert.hasText(terminalId, "terminalId can not be empty");

        LOGGER.debug("?????????{}??????????????????", terminalId);
        try {
            operateTerminal(terminalId, SendTerminalEventEnums.DETECT_TERMINAL, "", BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_DETECT);
            // ??????????????????????????????????????????????????????
            updateDetectState(detection.getId(), DetectStateEnums.CHECKING);
        } catch (BusinessException e) {
            LOGGER.error("?????????" + terminalId + "????????????????????????", e);
            // ???????????????????????????????????????????????????
            updateDetectState(detection.getId(), DetectStateEnums.ERROR);
            throw e;
        }
    }

    private void updateDetectState(UUID id, DetectStateEnums state) {
        TerminalDetectionEntity updateEntity = terminalDetectionDAO.getOne(id);
        updateEntity.setDetectTime(new Date());
        updateEntity.setDetectState(state);
        terminalDetectionDAO.save(updateEntity);
    }

    @Override
    public void relieveFault(String terminalId, @Nullable Object content) throws BusinessException {
        Assert.hasText(terminalId, "terminalId ????????????");

        // ??????????????????????????????????????????????????????????????????????????????????????????
        operateTerminal(terminalId, SendTerminalEventEnums.RELIEVE_FAULT, content, BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_RELIEVE_FAULT);
    }

    /**
     * ??????idv???????????????
     *
     * @param terminalId ??????id
     * @throws BusinessException ????????????
     */
    @Override
    public void diskClear(String terminalId) throws BusinessException {
        Assert.notNull(terminalId,"request can not be null");
        //????????????????????????????????????IDV?????????????????????
        checkTerminal(terminalId);
        int responseCode = operateTerminal(terminalId, SendTerminalEventEnums.CLEAR_DATA, "",
                BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_CLEAR_DISK);
        LOGGER.info("shine?????????code??? " + responseCode);
        //??????????????????,?????????????????????
        if (responseCode == DataDiskClearCodeEnums.DESKTOP_ON_RUNNING.getCode()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_DESKTOP_RUNNING_CANNOT_CLEAR_DISK, terminalId);
        }
        //??????shine????????????????????????????????????
        if (responseCode == DataDiskClearCodeEnums.NOTIFY_SHINE_WEB_FAIL.getCode()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOTIFY_SHINE_WEB_FAIL, terminalId);
        }
        //???????????????????????????????????????????????????
        if (responseCode == DataDiskClearCodeEnums.DATA_DISK_NOT_CREATE.getCode()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_DATA_DISK_NOT_CREATE, terminalId);
        }
        //???????????????????????????????????????????????????
        if (responseCode == DataDiskClearCodeEnums.TERMINAL_ON_INITING.getCode()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_TERMINAL_ON_INITING, terminalId);
        }
        //???????????????????????????????????????????????????????????????
        if (responseCode == DataDiskClearCodeEnums.TERMINAL_ON_DATA_DISK_CLEARING.getCode()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_TERMINAL_ON_DATA_DISK_CLEARING, terminalId);
        }
        //?????????????????????????????????????????????????????????
        if (responseCode == DataDiskClearCodeEnums.TERMINAL_ON_RESTORE_DESKTOP.getCode()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_TERMINAL_ON_RESTORE_DESKTOP, terminalId);
        }

    }

    private void checkTerminal(String terminalId) throws BusinessException {
        TerminalEntity entity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (entity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_EXIST, terminalId);
        }
        if (entity.getState() == CbbTerminalStateEnums.OFFLINE) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_ONLINE_CANNOT_CLEAR_DISK,
                    entity.getTerminalName(), terminalId);
        }
        if (entity.getState() == CbbTerminalStateEnums.UPGRADING) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_ONLINE_CANNOT_CLEAR_DISK,
                    entity.getTerminalName(), terminalId);
        }
        CbbTerminalPlatformEnums terminalPlatform = entity.getPlatform();
        LOGGER.info("????????????????????????????????????????????????{}", terminalPlatform);
        if (terminalPlatform != CbbTerminalPlatformEnums.IDV && terminalPlatform != CbbTerminalPlatformEnums.VOI) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_IDV_CANNOT_CLEAR_DISK,
                    entity.getTerminalName(), terminalId);
        }
    }

    private int operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object content, String operateActionKey)
            throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        Message message = new Message(Constants.SYSTEM_TYPE, terminalEvent.getName(), content);
        try {
            BaseMessage baseMessage = sender.syncRequest(message);
            Object data = baseMessage.getData();
            if (data == null || StringUtils.isBlank(data.toString())) {
                throw new IllegalArgumentException("??????syncRequest?????????shine????????????????????????????????????data:" + data);
            }
            CbbShineMessageResponse cbbShineMessageResponse = JSON.parseObject(data.toString(), CbbShineMessageResponse.class);
            return cbbShineMessageResponse.getCode();
        } catch (Exception e) {
            LOGGER.error("?????????????????????[" + terminalId + "]??????", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    LocaleI18nResolver.resolve(operateActionKey));
        }
    }

    private void checkAllowOperate(String terminalId, String businessKey) throws BusinessException {
        boolean isOnline = sessionManager.getSessionByAlias(terminalId) != null;
        if (isOnline) {
            // ????????????????????????
            return;
        }
        TerminalEntity terminalEntity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        Assert.notNull(terminalEntity, "terminalEntity must not be null, terminalId = " + terminalId);
        
        LOGGER.warn("??????[{}({})]??????,???????????????", terminalEntity.getTerminalName(), terminalEntity.getMacAddr());
        throw new BusinessException(businessKey, terminalEntity.getTerminalName(), terminalEntity.getMacAddr());
    }

}
