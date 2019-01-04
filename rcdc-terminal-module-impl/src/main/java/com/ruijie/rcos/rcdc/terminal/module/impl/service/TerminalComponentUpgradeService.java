package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalVersionResultDTO;

/**
 * 
 * Description: 终端组件升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月3日
 * 
 * @author nt
 */
public interface TerminalComponentUpgradeService {

    /**
     * 获取终端组件升级版本信息
     * @param rainUpgradeVersion 终端组件版本
     * @param terminalType 终端类型
     * @return 组件升级版本信息
     */
    TerminalVersionResultDTO getVersion(String rainUpgradeVersion, CbbTerminalTypeEnums terminalType);
}
