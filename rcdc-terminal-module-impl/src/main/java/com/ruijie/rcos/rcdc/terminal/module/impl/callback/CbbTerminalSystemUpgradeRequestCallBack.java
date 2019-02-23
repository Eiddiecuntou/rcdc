package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

import java.util.List;
import java.util.UUID;
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
        LOGGER.info("terminal start to upgrade system result, code is {}", code);
        if (code == UNSUPPORTED) {
            LOGGER.info(" start to upgrade system is unsupport");
            modifySystemUpgradeState(terminalId, CbbSystemUpgradeStateEnums.UNSUPPORTED);
        } else {
            LOGGER.info("terminal start to upgrade system is support");      
        }
    }

    /**
     * 修改刷机终端状态
     * @param terminalId 终端id
     * @param state 终端状态
     */
    private void modifySystemUpgradeState(String terminalId, CbbSystemUpgradeStateEnums state) {
        List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList =
                systemUpgradeTerminalDAO.findByTerminalIdAndState(terminalId, CbbSystemUpgradeStateEnums.UPGRADING);
        if (CollectionUtils.isEmpty(upgradingTerminalList)) {
            LOGGER.debug("不存在正在刷机的终端, 终端id ：{}", terminalId);
            return;
        }

        for (TerminalSystemUpgradeTerminalEntity entity : upgradingTerminalList) {
            updateUpgradeTerminalState(entity.getSysUpgradeId(), terminalId, state);
        }


    }

    /**
     * 更新刷机终端状态
     * @param sysUpgradeId 刷机任务id
     * @param terminalId 终端id
     * @param state 状态
     */
    private void updateUpgradeTerminalState(UUID sysUpgradeId, String terminalId, CbbSystemUpgradeStateEnums state) {
        try {
            terminalSystemUpgradeServiceTx.modifySystemUpgradeTerminalState(sysUpgradeId, terminalId, state);
        } catch (BusinessException e) {
            LOGGER.error("修改刷机终端状态失败", e);
        }
    }

    @Override
    public void timeout(String terminalId) {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        
        //do nothing
    }

}
