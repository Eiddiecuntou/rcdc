package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description: 终端网络配置消息定义
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class ShineNetworkConfig {

    private String terminalId;
    private String ip;
    private String subnetMask;
    private String gateway;
    private String mainDns;
    private String secondDns;
    private Integer getIpMode;
    private Integer getDnsMode;

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

    public Integer getGetIpMode() {
        return getIpMode;
    }

    public void setGetIpMode(Integer getIpMode) {
        this.getIpMode = getIpMode;
    }

    public Integer getGetDnsMode() {
        return getDnsMode;
    }

    public void setGetDnsMode(Integer getDnsMode) {
        this.getDnsMode = getDnsMode;
    }
}
