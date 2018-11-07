package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.ShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.ShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.TerminalCallback;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

import java.io.IOException;

/**
 * Description: 转发消息给终端（Shine）
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public interface TranspondMessageHandlerAPI {

    /**
     * 发送消息
     *
     * @param msg shine消息请求参数
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void request(ShineMessageRequest msg) throws BusinessException;

    /**
     * 同步发送消息
     *
     * @param msg 请求消息对象
     * @return 消息对象
     * @throws InterruptedException 线程中断异常
     * @throws IOException          请求超时
     * @throws BusinessException    业务异常
     */
    @NoRollback
    ShineMessageResponse syncRequest(ShineMessageRequest msg) throws InterruptedException, IOException,
            BusinessException;

    /**
     * 异步发送消息
     *
     * @param msg             请求消息对象
     * @param requestCallback 请求回调对象
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void asyncRequest(ShineMessageRequest msg, TerminalCallback requestCallback) throws BusinessException;

    /**
     * 应答消息
     *
     * @param msg shine消息请参数
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void response(ShineMessageRequest msg) throws BusinessException;

}
