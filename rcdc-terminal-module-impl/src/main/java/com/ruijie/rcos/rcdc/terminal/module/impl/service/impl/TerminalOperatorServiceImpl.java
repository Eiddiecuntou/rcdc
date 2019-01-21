package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CollectLogStateEnums;
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
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
import com.ruijie.rcos.sk.base.concorrent.executor.SkyengineScheduledThreadPoolExecutor;
import com.ruijie.rcos.sk.base.crypto.AesUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
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
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;
    
    /**
     * 终端通知处理线程池,分配1个线程数
     */
    private static final SkyengineScheduledThreadPoolExecutor NOTICE_HANDLER_THREAD_POOL
            = new SkyengineScheduledThreadPoolExecutor(1, TerminalOperatorServiceImpl.class.getName());

    @Override
    public void shutdown(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        operateTerminal(terminalId, SendTerminalEventEnums.SHUTDOWN_TERMINAL, "");
    }

    @Override
    public void restart(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        operateTerminal(terminalId, SendTerminalEventEnums.RESTART_TERMINAL, "");
    }

    @Override
    public void changePassword(String password) throws BusinessException {
        Assert.hasText(password, "password 不能为空");

        getTerminalAdminPassword();
        String encryptPwd = AesUtil.encrypt(password, Constants.TERMINAL_ADMIN_PASSWORD_AES_KEY);
        globalParameterAPI.updateParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY, encryptPwd);

        //向在线终端发送新管理员密码
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
                operateTerminal(terminalId, SendTerminalEventEnums.CHANGE_TERMINAL_PASSWORD, request);
            } catch (BusinessException e) {
                LOGGER.error("send new password to terminal failed, terminalId[{}], password[{}]", terminalId,
                        password);
            }
        }
    }

    @Override
    public void collectLog(final String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        CollectLogCache collectLogCache = collectLogCacheManager.getCache(terminalId);
        if (collectLogCache != null && CollectLogStateEnums.DOING == collectLogCache.getState()) {
            LOGGER.debug("终端[{}]正在收集日志中，不允许重复收集", terminalId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DOING);
        }
        collectLogCacheManager.addCache(terminalId);

        try {
            operateTerminal(terminalId, SendTerminalEventEnums.COLLECT_TERMINAL_LOG, "");
        } catch (BusinessException e) {
            collectLogCacheManager.removeCache(terminalId);
            throw e;
        }
    }

    @Override
    public void detect(String[] terminalIdArr) throws BusinessException {
        Assert.notEmpty(terminalIdArr, "terminalIdArr大小不能为0");
        for (String terminalId : terminalIdArr) {
            detect(terminalId);
        }
    }

    @Override
    public void detect(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");

        // 当天是否含有该终端检测记录，若有且检测已完成，重新开始检测，正在检测则忽略
        TerminalDetectionEntity detection = terminalDetectService.findInCurrentDate(terminalId);
        if (detection == null) {
            detection = terminalDetectService.save(terminalId);
            sendDetectRequest(detection);
            return;
        }

        if (detection.getDetectState() == DetectStateEnums.CHECKING) {
            return;
        }

        // 删除原记录，重新添加检测记录
        terminalDetectService.delete(detection.getId());
        detection = terminalDetectService.save(terminalId);
        sendDetectRequest(detection);
    }

    private void sendDetectRequest(TerminalDetectionEntity detection) throws BusinessException {
        String terminalId = detection.getTerminalId();
        LOGGER.debug("send detect request, terminalId[{}]", terminalId);
        try {
            operateTerminal(terminalId, SendTerminalEventEnums.DETECT_TERMINAL, "");
        } catch (BusinessException e) {
            //发送消息异常，将检测记录设置为失败
            detection.setDetectState(DetectStateEnums.ERROR);
            terminalDetectionDAO.save(detection);
        }
    }
    
    private void operateTerminal(String terminalId, SendTerminalEventEnums terminalEvent, Object data)
            throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE, terminalEvent.getName(), data);
        sender.request(message);
    }

}
