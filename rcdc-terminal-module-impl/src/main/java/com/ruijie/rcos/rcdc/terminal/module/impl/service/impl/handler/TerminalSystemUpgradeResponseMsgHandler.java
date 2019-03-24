package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;

/**
 * 
 * Description: 终端系统升级响应消息处理类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月19日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeResponseMsgHandler {

    public static final int SUCCESS = 0;

    public static final int UNSUPPORTED = -1;

    public static final int FAILURE = -99;

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeResponseMsgHandler.class);

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    /**
     * 处理终端升级响应消息
     * 
     * @param terminalId 终端id
     * @param baseMessage 响应消息
     * @throws BusinessException 业务异常
     */
    public void handle(String terminalId, BaseMessage baseMessage) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(baseMessage, "baseMessage 不能为空");

        Object data = baseMessage.getData();
        if (data == null || StringUtils.isBlank(data.toString())) {
            throw new IllegalArgumentException("执行syncRequest方法后shine返回的应答消息不能为空。data:" + data);
        }
        CbbShineMessageResponse msg = JSON.parseObject(data.toString(), CbbShineMessageResponse.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("system upgrade callback success, msg: {}", JSON.toJSONString(msg));
        }
        // 根据响应信息判断终端是否进行升级，不升级则将升级队列中的任务移除
        final int code = msg.getCode();
        LOGGER.info("terminal start to upgrade system result, code is {}", code);
        if (code == UNSUPPORTED) {
            LOGGER.info(" start to upgrade system is unsupport");
            modifySystemUpgradeState(terminalId, CbbSystemUpgradeStateEnums.UNSUPPORTED);
        }
        if (code == FAILURE) {
            LOGGER.info(" start to upgrade system is fail");
            modifySystemUpgradeState(terminalId, CbbSystemUpgradeStateEnums.FAIL);
        }

        LOGGER.info("terminal start to upgrade system is support");
    }

    /**
     * 修改刷机终端状态
     * 
     * @param terminalId 终端id
     * @param state 终端状态
     * @throws BusinessException 
     */
    private void modifySystemUpgradeState(String terminalId, CbbSystemUpgradeStateEnums state) throws BusinessException {
        List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList =
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
        if (CollectionUtils.isEmpty(upgradingTerminalList)) {
            LOGGER.debug("不存在正在刷机的终端, 终端id ：{}", terminalId);
            return;
        }

        for (TerminalSystemUpgradeTerminalEntity entity : upgradingTerminalList) {
            entity.setState(state);
            terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(entity);
        }
    }
}
