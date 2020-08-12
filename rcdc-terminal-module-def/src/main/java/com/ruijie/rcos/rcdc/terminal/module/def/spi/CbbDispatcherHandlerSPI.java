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
     * @api {SPI} CbbDispatcherHandlerSPI.dispatch 终端消息分发SPI
     * @apiName dispatch
     * @apiGroup CbbDispatcherHandlerSPI
     * @apiDescription 终端消息分发SPI
     * @apiParam (请求体字段说明) {CbbDispatcherRequest} request CbbDispatcherRequest
     * @apiParam (请求体字段说明) {String} request.dispatcherKey 分发识别字段
     * @apiParam (请求体字段说明) {String} request.terminalId 终端id
     * @apiParam (请求体字段说明) {String} [request.requestId] 请求id
     * @apiParam (请求体字段说明) {String} [request.data] 内容
     * @apiParam (请求体字段说明) {Boolean} [request.isNewConnection] 是否为全新内容
     *
     */
    /**
     * 消息分发方法
     *
     * @param request 请求参数对象 请求参数
     */
    void dispatch(CbbDispatcherRequest request);
}
