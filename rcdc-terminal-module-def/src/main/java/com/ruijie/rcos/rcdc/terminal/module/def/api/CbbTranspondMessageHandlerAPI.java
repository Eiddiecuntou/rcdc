package com.ruijie.rcos.rcdc.terminal.module.def.api;

import java.io.IOException;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

/**
 * Description: 转发消息给终端（Shine）
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/25
 *
 * @author Jarman
 */
public interface CbbTranspondMessageHandlerAPI {

    /**
     * 发送消息
     *
     * @param msg shine消息请求参数
     * @return 返回成功失败状态
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse request(CbbShineMessageRequest msg) throws BusinessException;

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
    CbbShineMessageResponse syncRequest(CbbShineMessageRequest msg) throws InterruptedException, IOException,
            BusinessException;

    /**
     * 异步发送消息
     *
     * @param msg             请求消息对象
     * @param requestCallback 请求回调对象
     * @return 返回成功失败状态
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse asyncRequest(CbbShineMessageRequest msg, CbbTerminalCallback requestCallback) throws BusinessException;

    /**
     * 应答消息
     *
     * @param msg shine消息请参数
     * @return 返回成功失败状态
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse response(CbbResponseShineMessage msg) throws Exception;

}
