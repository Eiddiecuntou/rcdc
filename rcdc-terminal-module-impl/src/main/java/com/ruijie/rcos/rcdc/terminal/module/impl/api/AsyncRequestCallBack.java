package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.sk.commkit.base.callback.AbstractRequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import org.apache.commons.lang3.StringUtils;
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
        Assert.hasText(terminalId, "terminalId不能为空");
        Assert.notNull(callback, "callback不能为空");
        
        this.terminalId = terminalId;
        this.callback = callback;
    }

    @Override
    public void success(BaseMessage baseMessage) {
        Assert.notNull(baseMessage, "baseMessage参数不能为空");
        Assert.hasText(baseMessage.getAction(), "action不能为空");
        Object data = baseMessage.getData();
        if (data == null || StringUtils.isBlank(data.toString())) {
            throw new IllegalArgumentException("执行syncRequest方法后shine返回的应答消息不能为空。data:" + data);
        }
        CbbShineMessageResponse cbbShineMessageResponse = JSON.parseObject(data.toString(), CbbShineMessageResponse.class);
        callback.success(terminalId, cbbShineMessageResponse);
    }

    @Override
    public void timeout(Throwable throwable) {
        callback.timeout(terminalId);
    }
}
