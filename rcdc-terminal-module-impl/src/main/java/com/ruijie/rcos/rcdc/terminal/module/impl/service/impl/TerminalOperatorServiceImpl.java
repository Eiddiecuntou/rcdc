package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalDetectionDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeTerminalPasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalDetectService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description: 终端操作
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
     * 终端通知处理线程池,分配1个线程数
     */
    private static final ThreadExecutor NOTICE_HANDLER_THREAD_POOL =
            ThreadExecutors.newBuilder(TerminalOperatorServiceImpl.class.getName()).maxThreadNum(1).queueSize(1).build();

    @Override
    public void shutdown(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        checkAllowOperate(terminalId, BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_SHUTDOWN);
        operateTerminal(terminalId, SendTerminalEventEnums.SHUTDOWN_TERMINAL, "", BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_SHUTDOWN);
    }

    @Override
    public void restart(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        checkAllowOperate(terminalId, BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_RESTART);
        operateTerminal(terminalId, SendTerminalEventEnums.RESTART_TERMINAL, "", BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_RESTART);
    }

    @Override
    public void changePassword(String password) throws BusinessException {
        Assert.hasText(password, "password 不能为空");

        getTerminalAdminPassword();
        String encryptPwd = AesUtil.encrypt(password, Constants.TERMINAL_ADMIN_PASSWORD_AES_KEY);
        globalParameterAPI.updateParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY, encryptPwd);

        // 向在线终端发送新管理员密码
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
        LOGGER.debug("向在线终端发送管理员密码改变通知");
        List<String> onlineTerminalIdList = sessionManager.getOnlineTerminalId();
        if (CollectionUtils.isEmpty(onlineTerminalIdList)) {
            LOGGER.debug("无在线终端");
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
    public void collectLog(final String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        checkAllowOperate(terminalId, BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_COLLECT_LOG);

        checkStoreLogDirExist();

        CollectLogCache collectLogCache = collectLogCacheManager.getCache(terminalId);
        if (collectLogCache != null && CbbCollectLogStateEnums.DOING == collectLogCache.getState()) {
            LOGGER.info("终端[{}]正在收集日志中，不允许重复收集", terminalId);
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
            storeLogDir.mkdirs();
        }
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        checkAllowOperate(terminalId, BusinessKey.RCDC_TERMINAL_OFFLINE_CANNOT_DETECT);

        // 当天是否含有该终端检测记录，若有且检测已完成，重新开始检测，正在检测则提示正在检测中
        TerminalDetectionEntity detection = terminalDetectService.findInCurrentDate(terminalId);
        if (detection == null) {
            terminalDetectService.save(terminalId);
            return;
        }

        if (detection.getDetectState() == DetectStateEnums.CHECKING || detection.getDetectState() == DetectStateEnums.WAIT) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_DETECT_IS_DOING);
        }

        // 删除原记录，重新添加检测记录
        terminalDetectService.delete(detection.getId());
        terminalDetectService.save(terminalId);
    }

    @Override
    public void sendDetectRequest(TerminalDetectionEntity detection) throws BusinessException {
        Assert.notNull(detection, "detect entity can not be null");
        String terminalId = detection.getTerminalId();
        Assert.hasText(terminalId, "terminalId can not be empty");

        LOGGER.debug("向终端{}发送检测指令", terminalId);
        try {
            operateTerminal(terminalId, SendTerminalEventEnums.DETECT_TERMINAL, "", BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_DETECT);
            // 发送成功后更新记录开始检测时间及状态
            updateDetectState(detection.getId(), DetectStateEnums.CHECKING);
        } catch (BusinessException e) {
            LOGGER.error("向终端" + terminalId + "发送检测指令失败", e);
            // 发送消息异常，将检测记录设置为失败
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

    private void operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object data, String operateActionKey)
            throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        Message message = new Message(Constants.SYSTEM_TYPE, terminalEvent.getName(), data);
        try {
            sender.syncRequest(message);
        } catch (Exception e) {
            LOGGER.error("发送消息给终端[" + terminalId + "]失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    new String[] {LocaleI18nResolver.resolve(operateActionKey, new String[] {})});
        }
    }

    private void checkAllowOperate(String terminalId, String businessKey) throws BusinessException {
        boolean isOnline = sessionManager.getSession(terminalId) == null ? false : true;
        if (isOnline) {
            // 在线状态允许操作
            return;
        }
        String terminalName = terminalBasicInfoDAO.getTerminalNameByTerminalId(terminalId);
        LOGGER.warn("终端[{}({})]离线,不允许操作", terminalName, terminalId);
        throw new BusinessException(businessKey, new String[] {terminalName, terminalId});
    }

}
