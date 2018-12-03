package com.ruijie.rcos.rcdc.terminal.module.impl.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.SystemUpgradeTaskManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.sk.base.util.Assert;

/**
 * 
 * Description: 终端系统升级回调
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月26日
 * 
 * @author nieti
 */
@Component
public class CbbTerminalSystemUpgradeRequestCallBack implements CbbTerminalCallback {
    
    @Autowired
    private SystemUpgradeTaskManager manager;
    

    @Override
    public void success(CbbShineMessageResponse msg) {
        // TODO 根据响应信息判断终端是否进行升级，不升级则将升级队列中的任务移除
        Assert.notNull(msg, "TerminalSystemUpgradeRequest 不能为空");

        // 事件类型不对
        if (!SendTerminalEventEnums.UPGRADE_TERMINAL_SYSTEM.getName().equals(msg.getAction())) {
            return;
        }

        Object data = msg.getData();

    }

    @Override
    public void timeout(String terminalId) {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        manager.removeTaskByTerminalId(terminalId);
    }

}
