package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.CollectLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.CommonMsg;
import com.ruijie.rcos.sk.commkit.base.callback.AbstractRequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import org.springframework.util.Assert;

/**
 * Description: 收集日志回调接口实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
public class CollectLogRequestCallbackImpl extends AbstractRequestCallback {
    private CollectLogCacheManager collectLogCacheManager;

    private String terminalId;

    public CollectLogRequestCallbackImpl(CollectLogCacheManager collectLogCacheManager, String terminalId) {
        this.collectLogCacheManager = collectLogCacheManager;
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
            collectLogCacheManager.updateState(terminalId, CollectLogStateEnums.DONE, logZipName);
            return;
        }
        collectLogCacheManager.updateState(terminalId, CollectLogStateEnums.FAILURE);
    }

    @Override
    public void timeout(Throwable throwable) {
        collectLogCacheManager.updateState(terminalId, CollectLogStateEnums.FAILURE);

    }
}
