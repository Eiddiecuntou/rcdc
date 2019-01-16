package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCache;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalDetectionEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.DetectStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeTerminalPasswordRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalDetectService;
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
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

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
        globalParameterAPI.updateParameter(Constants.RCDC_TERMINAL_ADMIN_PWD_GLOBAL_PARAMETER_KEY, password);

        // 向在线终端发送新管理员密码
        sendNewPwdToOnlineTerminal(password);
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
        List<TerminalEntity> onlineTerminalList =
                terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
        if (CollectionUtils.isEmpty(onlineTerminalList)) {
            LOGGER.debug("无在线终端");
            return;
        }
        for (TerminalEntity terminalEntity : onlineTerminalList) {
            ChangeTerminalPasswordRequest request = new ChangeTerminalPasswordRequest(password);
            String terminalId = terminalEntity.getTerminalId();
            try {
                operateTerminal(terminalId, SendTerminalEventEnums.CHANGE_TERMINAL_PASSWORD, request);
            } catch (BusinessException e) {
                LOGGER.error("send new password to terminal failed, terminalId[{}], password[{}]", terminalId,
                        password);
            }
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

    @Override
    public void collectLog(final String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        CollectLogCache collectLogCache = collectLogCacheManager.getCache(terminalId);
        if (collectLogCache == null) {
            collectLogCache = collectLogCacheManager.addCache(terminalId);
        }
        // 正在收集中,不允许重复执行
        if (CollectLogStateEnums.DOING == collectLogCache.getState()) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_COLLECT_LOG_DOING);
        }

        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.COLLECT_TERMINAL_LOG.getName(), "");
        // 发消息给shine，执行日志收集
        sender.request(message);
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
            terminalDetectService.save(terminalId);
            sendDetectRequest(terminalId);
            return;
        }

        if (detection.getDetectState() == DetectStateEnums.CHECKING) {
            return;
        }

        // 删除原记录，重新添加检测记录
        terminalDetectService.delete(detection.getId());
        terminalDetectService.save(terminalId);
        sendDetectRequest(terminalId);
    }

    private void sendDetectRequest(String terminalId) throws BusinessException {
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.DETECT_TERMINAL.getName(), "");
        sender.request(message);
    }
}
