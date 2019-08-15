package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

/**
 * Description: 终端通知事件定义
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/1
 *
 * @author Jarman
 */
public enum CbbNoticeEventEnums {

    /**
     * 连接成功，在线状态
     */
    ONLINE("online"),

    /**
     * 连接关闭，离线状态
     */
    OFFLINE("offline"),

    /**
     * 接收usb信息
     */
    RECEIVE_USB_INFO("receive_usb_info");

    private String name;

    CbbNoticeEventEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
