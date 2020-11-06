package com.ruijie.rcos.rcdc.terminal.module.def.spi;

import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherInterface;

/**
 * Description: 终端事件通知SPI接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/24
 *
 * @author Jarman
 */
@DispatcherInterface
public interface CbbTerminalEventNoticeSPI {

    /**
     * @api {SPI} CbbTerminalEventNoticeSPI.notify 终端事件通知SPI
     * @apiName notify
     * @apiGroup CbbTerminalEventNoticeSPI
     * @apiDescription 终端事件通知SPI
     * @apiParam (请求体字段说明) {CbbNoticeRequest} request CbbNoticeRequest
     * @apiParam (请求体字段说明) {String} request.dispatcherKey 分发消息识别字段
     *
     *
     *
     */
    /**
     * 消息通知
     *
     * @param request 请求参数
     */
    void notify(CbbNoticeRequest request);
}
