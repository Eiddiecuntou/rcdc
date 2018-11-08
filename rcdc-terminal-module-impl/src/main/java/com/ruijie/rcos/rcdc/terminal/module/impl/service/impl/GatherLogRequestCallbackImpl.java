package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.GatherLogCacheManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.CommonResponseMsg;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.callback.AbstractRequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.base.BaseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 收集日志回调接口实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
@Service
public class GatherLogRequestCallbackImpl extends AbstractRequestCallback {

    private String terminalId;

    public GatherLogRequestCallbackImpl(String terminalId) {
        this.terminalId = terminalId;
    }

    @Autowired
    private GatherLogCacheManager gatherLogCacheManager;

    @Override
    public void success(BaseMessage msg) {
        Assert.notNull(msg, "收集日志返回消息不能为null");
        Assert.notNull(msg.getData(), "收集日志返回报文消息体不能为null");
        String data = ((String) msg.getData()).trim();
        Assert.hasLength(data, "返回的应答消息不能为空");
        CommonResponseMsg responseMsg = JSON.parseObject(data, CommonResponseMsg.class);
        Assert.notNull(responseMsg, "应答消息格式错误");
        if (StateEnums.SUCCESS == responseMsg.getErrorCode()) {
            String logZipName = responseMsg.getMsg();
            Assert.hasLength(logZipName, "返回的日志文件名称不能为空");
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
