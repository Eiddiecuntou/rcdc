package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;

/**
 *
 * Description: 客户端版本信息API
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021年09月27日
 *
 * @author linke
 */
public interface CbbTerminalVersionAPI {

    /**
     * 获取版本信息
     *
     * @param cbbCpuArchType cpu架构
     * @param terminalOsType    终端系统
     * @return 版本信息，为空时表示获取失败
     */
    String getTerminalVersion(CbbCpuArchType cbbCpuArchType, String terminalOsType);
}
