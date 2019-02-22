package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 终端系统升级回调
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月26日
 * 
 * @author nt
 */
@Service
public class CbbTerminalSystemUpgradeRequestCallBack implements CbbTerminalCallback {

    public static final int SUCCESS = 0;

    public static final int UNSUPPORTED = -1;

    public static final int FAILURE = -99;

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradeRequestCallBack.class);

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalSystemUpgradeServiceTx terminalSystemUpgradeServiceTx;

    @Override
    public void success(String terminalId, CbbShineMessageResponse msg) {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(msg, "TerminalSystemUpgradeRequest 不能为空");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("system upgrade callback success, msg: {}", JSON.toJSONString(msg));
        }
        // 根据响应信息判断终端是否进行升级，不升级则将升级队列中的任务移除
        final int code = msg.getCode();
        if (code == SUCCESS) {
            LOGGER.info("terminal start to upgrade system success, code is {}", code);
        } else {
            LOGGER.info("terminal start to upgrade system failed");
            setUpgradeTaskFailed(terminalId);
        }
    }

    @Override
    public void timeout(String terminalId) {
        Assert.hasLength(terminalId, "terminalId 不能为空");

        LOGGER.info("system upgrade callback timeout");
        setUpgradeTaskFailed(terminalId);
    }

    /**
     * 将刷机终端状态设为失败
     * 
     * @param terminalId 终端id
     */
    private void setUpgradeTaskFailed(String terminalId) {
        List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList =
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
        if (CollectionUtils.isEmpty(upgradingTerminalList)) {
            LOGGER.debug("不存在正在刷机的终端, 终端id ：{}", terminalId);
            return;
        }

        for (TerminalSystemUpgradeTerminalEntity entity : upgradingTerminalList) {
            setUpgradeTerminalFail(entity);
        }
    }

    /**
     * 修改终端刷机记录状态为失败
     * 
     * @param entity 终端记录对象
     */
    private void setUpgradeTerminalFail(TerminalSystemUpgradeTerminalEntity entity) {
        try {
            terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(entity.getSysUpgradeId(),
                    entity.getTerminalId(), CbbSystemUpgradeStateEnums.FAIL);
        } catch (BusinessException e) {
            LOGGER.error("更新刷机终端状态失败", e);
        }
    }

}
