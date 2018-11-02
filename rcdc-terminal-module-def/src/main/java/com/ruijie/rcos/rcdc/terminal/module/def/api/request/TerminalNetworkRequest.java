package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.GetNetworkModeEnums;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;

import javax.validation.constraints.NotNull;

/**
 * Description: 配置终端网络请求参数对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class TerminalNetworkRequest implements Request {

    @NotNull
    private String terminalId;
    @NotNull
    private String ip;
    @NotNull
    private String subnetMask;
    @NotNull
    private String gateway;
    @NotNull
    private String mainDns;
    private String secondDns;
    @NotNull
    private GetNetworkModeEnums getIpMode;
    @NotNull
    private GetNetworkModeEnums getDnsMode;

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

    public GetNetworkModeEnums getGetIpMode() {
        return getIpMode;
    }

    public void setGetIpMode(GetNetworkModeEnums getIpMode) {
        this.getIpMode = getIpMode;
    }

    public GetNetworkModeEnums getGetDnsMode() {
        return getDnsMode;
    }

    public void setGetDnsMode(GetNetworkModeEnums getDnsMode) {
        this.getDnsMode = getDnsMode;
    }
}
