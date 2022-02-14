package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

/**
 * Description: 终端授权类型
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/18
 *
 * @author lin
 */
public enum CbbTerminalLicenseTypeEnums {

    /**
     * IDV
     */
    IDV,

    /**
     * VOI
     */
    VOI,

    /**
     * VOI+升级=IDV
     */
    VOI_PLUS_UPGRADED,

    /**
     * IDV本地应用虚拟化授权
     */
    CVA_IDV,

    /**
     * 应用虚拟化合并授权
     */
    CVA,

    /**
     * IDV + 升级 = CVA
     */
    IDV_PLUS_UPGRADED;
}
