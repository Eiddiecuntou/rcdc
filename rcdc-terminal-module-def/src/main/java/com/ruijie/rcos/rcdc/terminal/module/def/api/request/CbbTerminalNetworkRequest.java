package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbGetNetworkModeEnums;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;

/**
 * Description: 配置终端网络请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalNetworkRequest implements Request {

    @NotBlank
    private String terminalId;

    @NotBlank
    private String ip;

    @NotBlank
    private String subnetMask;

    @NotBlank
    private String gateway;

    @NotBlank
    private String mainDns;

    @Nullable
    private String secondDns;

    @NotNull
    private CbbGetNetworkModeEnums getIpMode;

    @NotNull
    private CbbGetNetworkModeEnums getDnsMode;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getMainDns() {
        return mainDns;
    }

    public void setMainDns(String mainDns) {
        this.mainDns = mainDns;
    }

    public String getSecondDns() {
        return secondDns;
    }

    public void setSecondDns(String secondDns) {
        this.secondDns = secondDns;
    }

    public CbbGetNetworkModeEnums getGetIpMode() {
        return getIpMode;
    }

    public void setGetIpMode(CbbGetNetworkModeEnums getIpMode) {
        this.getIpMode = getIpMode;
    }

    public CbbGetNetworkModeEnums getGetDnsMode() {
        return getDnsMode;
    }

    public void setGetDnsMode(CbbGetNetworkModeEnums getDnsMode) {
        this.getDnsMode = getDnsMode;
    }
}
