package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTask;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalSystemUpgradeRequestCallBack.class);

    @Autowired
    private SystemUpgradeTaskManager manager;


    @Override
    public void success(String terminalId, CbbShineMessageResponse msg) {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(msg, "TerminalSystemUpgradeRequest 不能为空");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("system upgrade callback success, msg: {}", JSON.toJSONString(msg));
        }
        SystemUpgradeResponseResult result =
                JSON.parseObject(msg.getContent().toString(), SystemUpgradeResponseResult.class);
        // 根据响应信息判断终端是否进行升级，不升级则将升级队列中的任务移除
        if (result.getCode() == SystemUpgradeResponseResult.SUCCESS) {
            LOGGER.debug("terminal start to upgrade system success");
        } else {
            LOGGER.debug("terminal start to upgrade system failed");
            setUpgradeTaskFailed(terminalId);
        }

    }


    @Override
    public void timeout(String terminalId) {
        Assert.hasLength(terminalId, "terminalId 不能为空");

        LOGGER.debug("system upgrade callback timeout");
        setUpgradeTaskFailed(terminalId);
    }

    /**
     * 将系统升级任务状态设为失败
     * 
     * @param terminalId 终端id
     */
    private void setUpgradeTaskFailed(String terminalId) {
        SystemUpgradeTask task = manager.getTaskByTerminalId(terminalId);
        if (task == null) {
            LOGGER.debug("task not exist, terminalId[{}]", terminalId);
            return;
        }
        task.setState(CbbSystemUpgradeStateEnums.FAIL);
    }

}
