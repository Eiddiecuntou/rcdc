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

    /**
     * 子网掩码
     */
    private String subnetMask;

    /**
     * 网关
     */
    private String gateway;

    /**
     * 首选DNS
     */
    private String mainDns;

    /**
     * 备选DNS
     */
    private String secondDns;

    /**
     * 获取IP方式，包括自动和手动
     */
    private Integer getIpMode;

    /**
     * 获取DNS方式，包括自动和手动
     */
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
