package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.sk.commkit.base.callback.AbstractRequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import org.springframework.util.Assert;

/**
 * Description: 异步请求回调接口实现
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/13
 *
 * @author Jarman
 */
public class AsyncRequestCallBack extends AbstractRequestCallback {

    private String terminalId;

    private CbbTerminalCallback callback;

    public AsyncRequestCallBack(String terminalId, CbbTerminalCallback callback) {
        this.terminalId = terminalId;
        this.callback = callback;
    }

    @Override
    public void success(BaseMessage baseMessage) {
        Assert.notNull(baseMessage, "baseMessage参数不能为空");
        Assert.notNull(baseMessage.getAction(), "action不能为空");
        CbbShineMessageResponse cbbShineMessageResponse = new CbbShineMessageResponse();
        cbbShineMessageResponse.setAction(baseMessage.getAction());
        cbbShineMessageResponse.setData(baseMessage.getData());
        cbbShineMessageResponse.setTerminalId(terminalId);
        callback.success(cbbShineMessageResponse);
    }

    @Override
    public void timeout(Throwable throwable) {
        callback.timeout();
    }
}
