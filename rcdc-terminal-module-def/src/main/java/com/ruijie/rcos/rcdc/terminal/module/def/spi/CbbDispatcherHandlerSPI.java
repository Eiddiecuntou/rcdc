package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbDispatcherRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherInterface;


/**
 * Description: 消息分发处理器SPI接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@DispatcherInterface
public interface CbbDispatcherHandlerSPI {

    /**
     * @api {SPI} CbbDispatcherHandlerSPI.dispatch 消息分发方法
     * @apiName dispatch
     * @apiGroup CbbDispatcherHandlerSPI
     * @apiDescription 消息分发方法
     * @apiParam (请求体字段说明) {CbbDispatcherRequest} request CbbDispatcherRequest
     * @apiParam (请求体字段说明) {String} request.dispatcherKey TODO
     * @apiParam (请求体字段说明) {String} request.terminalId TODO
     * @apiParam (请求体字段说明) {String} [request.requestId] TODO
     * @apiParam (请求体字段说明) {String} [request.data] TODO
     * @apiParam (请求体字段说明) {Boolean} [request.isNewConnection] TODO
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     * 消息分发方法
     *
     * @param request 请求参数对象 请求参数
     */
    void dispatch(CbbDispatcherRequest request);
}
