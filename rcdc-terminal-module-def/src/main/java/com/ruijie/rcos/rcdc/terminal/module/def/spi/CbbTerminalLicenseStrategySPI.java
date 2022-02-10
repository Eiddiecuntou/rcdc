package com.ruijie.rcos.rcdc.terminal.module.def.spi;

/**
 * Description: rcdc终端授权策略
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2022/2/8 9:58
 *
 * @author chenjuan
 */
public interface CbbTerminalLicenseStrategySPI {

    /**
     * @api {SPI} CbbTerminalLicenseStrategySPI.getTerminalLicenseStrategy 获取rco终端授权策略json信息
     * @apiName getTerminalLicenseStrategy
     * @apiGroup CbbTerminalLicenseStrategySPI
     * @apiDescription 获取rco终端授权策略json信息
     */
    /**
     * 获取rco终端授权策略json信息
     * @return 终端授权策略json数据
     */
    String getTerminalLicenseStrategy();
}
