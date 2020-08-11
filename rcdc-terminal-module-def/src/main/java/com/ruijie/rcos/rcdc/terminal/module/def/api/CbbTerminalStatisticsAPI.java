package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalStatisticsResponse;

import java.util.UUID;

/**
 * Description: 统计接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/4
 *
 * @author Jarman
 */
public interface CbbTerminalStatisticsAPI {

    /**
     * @api {POST} CbbTerminalStatisticsAPI.statisticsTerminal 统计终端新
     * @apiName statisticsTerminal
     * @apiGroup CbbTerminalStatisticsAPI
     * @apiDescription 统计终端新
     * @apiParam (请求体字段说明) {UUID[]} groupIdArr TODO
     *
     *@apiSuccess (响应字段说明) {CbbTerminalStatisticsResponse} return CbbTerminalStatisticsResponse
     *@apiSuccess (响应字段说明) {TerminalStatisticsItem} return.TerminalStatisticsItem TODO
     *@apiSuccess (响应字段说明) {Integer} return.TerminalStatisticsItem.total TODO
     *@apiSuccess (响应字段说明) {Integer} return.TerminalStatisticsItem.online TODO
     *@apiSuccess (响应字段说明) {Integer} return.TerminalStatisticsItem.offline TODO
     *@apiSuccess (响应字段说明) {Integer} return.TerminalStatisticsItem.neverLogin TODO
     *@apiSuccess (响应字段说明) {Integer} return.total TODO
     *@apiSuccess (响应字段说明) {Integer} return.totalOnline TODO
     *
    /**
     * 统计终端新
     *
     * @param groupIdArr 终端类型请求
     * @return 返回统计结果
     */
    CbbTerminalStatisticsResponse statisticsTerminal(UUID[] groupIdArr);

}
