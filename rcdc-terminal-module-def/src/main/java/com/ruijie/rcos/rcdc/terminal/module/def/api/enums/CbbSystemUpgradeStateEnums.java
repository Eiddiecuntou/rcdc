package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

/**
 * Description: 终端系统升级结果状态
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/15
 *
 * @author nt
 */
public enum CbbSystemUpgradeStateEnums {
    
    /**
     * 更新成功
     */
    SUCCESS,
    
    /**
     * 正在更新中
     */
    UPGRADING,
    
    /**
     * 等待更新中
     */
    WAIT,
    
    /**
     * 更新失败
     */
    FAIL
}
