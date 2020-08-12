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
     * @api {POST} CbbTerminalStatisticsAPI.statisticsTerminal 统计终端数据
     * @apiName statisticsTerminal
     * @apiGroup CbbTerminalStatisticsAPI
     * @apiDescription 统计终端新
     * @apiParam (请求体字段说明) {UUID[]} groupIdArr 终端组id数组
     *
     *@apiSuccess (响应字段说明) {CbbTerminalStatisticsResponse} response CbbTerminalStatisticsResponse
     *@apiSuccess (响应字段说明) {TerminalStatisticsItem} response.TerminalStatisticsItem 终端统计项
     *@apiSuccess (响应字段说明) {Integer} response.TerminalStatisticsItem.total 终端总数
     *@apiSuccess (响应字段说明) {Integer} response.TerminalStatisticsItem.online 在线终端数
     *@apiSuccess (响应字段说明) {Integer} response.TerminalStatisticsItem.offline 离线终端数量
     *@apiSuccess (响应字段说明) {Integer} response.TerminalStatisticsItem.neverLogin 从未登录终端数量
     *@apiSuccess (响应字段说明) {Integer} response.total 终端总数
     *@apiSuccess (响应字段说明) {Integer} response.totalOnline 在线终端总数
     */
    /**
     * 统计终端数据
     *
     * @param groupIdArr 终端类型请求
     * @return 返回统计结果
     */
    CbbTerminalStatisticsResponse statisticsTerminal(UUID[] groupIdArr);

}
