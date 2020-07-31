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
     * 统计终端新
     *
     * @param groupIdArr 终端类型请求
     * @return 返回统计结果
     */
    CbbTerminalStatisticsResponse statisticsTerminal(UUID[] groupIdArr);

}
