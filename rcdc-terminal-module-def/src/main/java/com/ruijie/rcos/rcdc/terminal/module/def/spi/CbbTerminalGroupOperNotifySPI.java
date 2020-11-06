package com.ruijie.rcos.rcdc.terminal.module.def.spi;


import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbTerminalGroupOperNotifyRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * 终端组操作通知SPI接口定义
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年1月7日
 *
 * @author wjp
 */
public interface CbbTerminalGroupOperNotifySPI {

    /**
     * @api {SPI} CbbTerminalEventNoticeSPI.notify 终端组变更通知SPI
     * @apiName notify
     * @apiGroup CbbTerminalEventNoticeSPI
     * @apiDescription 消息通知
     * @apiParam (请求体字段说明) {CbbTerminalGroupOperNotifyRequest} terminalGroupOperNotifyRequest CbbTerminalGroupOperNotifyRequest
     * @apiParam (请求体字段说明) {UUID} terminalGroupOperNotifyRequest.id 终端组id
     * @apiParam (请求体字段说明) {UUID} [terminalGroupOperNotifyRequest.moveGroupId] 变更终端分组id
     *
     * @apiSuccess (响应字段说明) {DefaultResponse} result DefaultResponse
     *
     *
     *
     */
    /**
     * 消息通知：终端组织架构发生变更（删）
     *
     * @param terminalGroupOperNotifyRequest 入参
     * @return 响应
     */
    DefaultResponse notifyTerminalGroupChange(CbbTerminalGroupOperNotifyRequest terminalGroupOperNotifyRequest);

}
