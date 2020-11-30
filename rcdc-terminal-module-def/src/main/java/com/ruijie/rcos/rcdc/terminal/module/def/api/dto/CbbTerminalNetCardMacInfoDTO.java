package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Description: 终端网卡信息dto
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/19 9:41 下午
 *
 * @author zhouhuan
 */
public class CbbTerminalNetCardMacInfoDTO {

    private String iface;

    @JSONField(name = "mac-address")
    private String macAddress;

    public String getIface() {
        return iface;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
