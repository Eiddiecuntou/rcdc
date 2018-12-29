package com.ruijie.rcos.rcdc.terminal.module.def.enums;

/**
 * Description: 终端类型枚举
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/4
 *
 * @author Jarman
 */
public enum  TerminalTypeEnums {

    /**
     * VDI Linux硬终端
     */
    VDI_LINUX_HARDWARE,

    /**
     * VDI Android硬终端
     */
    VDI_ANDROID_HARDWARE,

    /**
     * VDI Window硬终端
     */
    VDI_WINDOW_HARDWARE,

    /**
     * IDV Linux硬终端
     */
    IDV_LINUX_HARDWARE,

    /**
     * VDI Linux软终端
     */
    VDI_LINUX_SOFTWARE,

    /**
     * VDI Android软终端
     */
    VDI_ANDROID_SOFTWARE,

    /**
     * VDI Window软终端
     */
    VDI_WINDOW_SOFTWARE,

    /**
     * VDI ios软终端
     */
    VDI_IOS_SOFTWARE,

    /**
     * VDI macos软终端
     */
    VDI_MACOS_SOFTWARE,

    /**
     * 其他终端,需要注意此类终端，给出提示信息
     */
    OTHER

}
