package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.CommonMsg;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.callback.AbstractRequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;

/**
 * Description: 收集日志回调接口实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
public class GatherLogRequestCallbackImpl extends AbstractRequestCallback {
    private GatherLogCacheManager gatherLogCacheManager;

    private String terminalId;

    public GatherLogRequestCallbackImpl(GatherLogCacheManager gatherLogCacheManager,String terminalId) {
        this.gatherLogCacheManager = gatherLogCacheManager;
        this.terminalId = terminalId;
    }

    @Override
    public void success(BaseMessage msg) {
        Assert.notNull(msg, "收集日志返回消息不能为null");
        Assert.notNull(msg.getData(), "收集日志返回报文消息体不能为null");
        String data = ((String) msg.getData()).trim();
        Assert.hasText(data, "返回的应答消息不能为空");
        CommonMsg responseMsg = JSON.parseObject(data, CommonMsg.class);
        Assert.notNull(responseMsg, "应答消息格式错误");
        if (StateEnums.SUCCESS == responseMsg.getErrorCode()) {
            String logZipName = responseMsg.getMsg();
            Assert.hasText(logZipName, "返回的日志文件名称不能为空");
            gatherLogCacheManager.updateState(terminalId, GatherLogStateEnums.DONE, logZipName);
            return;
        }
        gatherLogCacheManager.updateState(terminalId, GatherLogStateEnums.FAILURE);
    }

    @Override
    public void timeout(Throwable throwable) {
        gatherLogCacheManager.updateState(terminalId, GatherLogStateEnums.FAILURE);

    }
}
