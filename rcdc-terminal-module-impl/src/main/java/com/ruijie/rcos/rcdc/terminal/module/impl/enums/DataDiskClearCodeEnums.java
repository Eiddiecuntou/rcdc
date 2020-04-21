package com.ruijie.rcos.rcdc.terminal.module.impl.enums;

/**
 * Description:
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/3/1 16:07
 *
 * @author conghaifeng
 */
public enum DataDiskClearCodeEnums {

    /** 终端云桌面正在运行，不可清空数据盘 */
    DESKTOP_ON_RUNNING(-1),

    /** 通知shine前端失败，不可清空数据盘 */
    NOTIFY_SHINE_WEB_FAIL(-2),

    /** 终端上未创建数据盘 */
    DATA_DISK_NOT_CREATE(-3),

    /** 终端正在初始化，不能同时清空数据盘*/
    TERMINAL_ON_INITING(-4);

    /**shine返回状态码 */
    private int code;

    DataDiskClearCodeEnums(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
