package com.ruijie.rcos.rcdc.terminal.module.def.spi;

/**
 * Description: 终端获取云桌面信息SPI接口
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/23 16:24
 *
 * @author brq
 */
public interface CbbTerminalDesktopHandlerSPI {

    /**
     * 根据授权类型获取运行中的VDI云桌面
     * @param licenseTypeStr 授权类型
     * @return int
     */
    int obtainUsingVdiDesktopNum(String licenseTypeStr);

}
