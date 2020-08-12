package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbResponseShineMessage;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbShineMessageRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbShineMessageResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.callback.CbbTerminalCallback;
import com.ruijie.rcos.sk.base.exception.BusinessException;

import java.io.IOException;

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
     * @api {POST} CbbTranspondMessageHandlerAPI.request 发送消息
     * @apiName request
     * @apiGroup CbbTranspondMessageHandlerAPI
     * @apiDescription 发送消息
     * @apiParam (请求体字段说明) {CbbShineMessageRequest} msg CbbShineMessageRequest
     * @apiParam (请求体字段说明) {String} msg.action 请求action
     * @apiParam (请求体字段说明) {String} msg.terminalId 终端id
     * @apiParam (请求体字段说明) {T} [msg.content] 内容
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     * 发送消息
     *
     * @param msg shine消息请求参数
     * @throws BusinessException 业务异常
     */
    void request(CbbShineMessageRequest msg) throws BusinessException;

    /**
     * @api {POST} CbbTranspondMessageHandlerAPI.syncRequest 同步发送消息
     * @apiName syncRequest
     * @apiGroup CbbTranspondMessageHandlerAPI
     * @apiDescription 同步发送消息
     * @apiParam (请求体字段说明) {CbbShineMessageRequest} msg CbbShineMessageRequest
     * @apiParam (请求体字段说明) {String} msg.action action
     * @apiParam (请求体字段说明) {String} msg.terminalId 终端id
     * @apiParam (请求体字段说明) {T} [msg.content] content
     *
     * @apiSuccess (响应字段说明) {CbbShineMessageResponse} result CbbShineMessageResponse
     * @apiSuccess (响应字段说明) {int} result.code code
     * @apiSuccess (响应字段说明) {T} result.content content
     */
    /**
     * 同步发送消息
     *
     * @param msg 请求消息对象
     * @return 消息对象
     * @throws InterruptedException 线程中断异常
     * @throws IOException 请求超时
     * @throws BusinessException 业务异常
     */
    CbbShineMessageResponse syncRequest(CbbShineMessageRequest msg) throws InterruptedException, IOException, BusinessException;

    /**
     * @api {POST} CbbTranspondMessageHandlerAPI.asyncRequest 异步发送消息
     * @apiName asyncRequest
     * @apiGroup CbbTranspondMessageHandlerAPI
     * @apiDescription 异步发送消息
     * @apiParam (请求体字段说明) {CbbShineMessageRequest} msg CbbShineMessageRequest
     * @apiParam (请求体字段说明) {String} msg.action action
     * @apiParam (请求体字段说明) {String} msg.terminalId 终端id
     * @apiParam (请求体字段说明) {T} [msg.content] content
     * @apiParam (请求体字段说明) {CbbTerminalCallback} requestCallback 此接口包含回调函数
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     * 异步发送消息
     *
     * @param msg 请求消息对象
     * @param requestCallback 请求回调对象
     * @throws BusinessException 业务异常
     */
    void asyncRequest(CbbShineMessageRequest msg, CbbTerminalCallback requestCallback) throws BusinessException;

    /**
     * @api {POST} CbbTranspondMessageHandlerAPI.asyncRequest 异步发送消息
     * @apiName asyncRequest
     * @apiGroup CbbTranspondMessageHandlerAPI
     * @apiDescription 异步发送消息（不含回调函数接口）
     * @apiParam (请求体字段说明) {CbbResponseShineMessage} msg CbbResponseShineMessage
     * @apiParam (请求体字段说明) {String} msg.requestId 请求id
     * @apiParam (请求体字段说明) {String} msg.action action
     * @apiParam (请求体字段说明) {String} msg.terminalId 终端id
     * @apiParam (请求体字段说明) {String} msg.code code
     * @apiParam (请求体字段说明) {T} [msg.content] content
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     * 应答消息
     *
     * @param msg shine消息请参数
     */
    void response(CbbResponseShineMessage msg);

}
