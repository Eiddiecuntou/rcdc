package com.ruijie.rcos.rcdc.terminal.module.def.spi;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/25 20:20
 *
 * @author yanlin
 */
public interface CbbTerminalCvaLicenseUpdateSPI {

    /**
     * @api {SPI} CbbTerminalCvaLicenseUpdateSPI.releaseCvaLicenseUsedNum 释放应用虚拟化合并授权使用数
     * @apiName releaseCvaLicenseUsedNum
     * @apiGroup CbbTerminalCvaLicenseUpdateSPI
     * @apiDescription 释放应用虚拟化合并授权使用数
     */
    /**
     * 释放应用虚拟化合并授权使用数
     */
    void releaseCvaLicenseUsedNum();
}