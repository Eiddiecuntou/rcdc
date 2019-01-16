package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * 
 * Description: 终端检测记录DTO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class CbbTerminalDetectDTO {


    private String terminalId;

    private String terminalName;

    private String ip;

    /**
     * ip冲突结果，0 不冲突，1 冲突，如果有冲突则ipConflictMac字段保存冲突的mac地址，否则为空值
     */
    private Integer ipConflict;

    /**
     * 是否可访问外网，0不能访问，1可访问
     */
    private Integer accessInternet;

    /**
     * 带宽大小
     */
    private Double bandwidth;

    /**
     * 丢包率
     */
    private Double packetLossRate;

    /**
     * 网络时延
     */
    private Double delay;

    /**
     * 检测状态
     */
    private DetectState checkState = new DetectState();

    /**
     * 
     * Description: 检测状态
     * Copyright: Copyright (c) 2018
     * Company: Ruijie Co., Ltd.
     * Create Time: 2019年1月15日
     * 
     * @author nt
     */
    public class DetectState {

        private String state;

        private String message;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }


    public String getTerminalId() {
        return terminalId;
    }


    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }


    public String getTerminalName() {
        return terminalName;
    }


    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }


    public Integer getIpConflict() {
        return ipConflict;
    }


    public void setIpConflict(Integer ipConflict) {
        this.ipConflict = ipConflict;
    }


    public Integer getAccessInternet() {
        return accessInternet;
    }


    public void setAccessInternet(Integer accessInternet) {
        this.accessInternet = accessInternet;
    }


    public Double getBandwidth() {
        return bandwidth;
    }


    public void setBandwidth(Double bandwidth) {
        this.bandwidth = bandwidth;
    }


    public Double getPacketLossRate() {
        return packetLossRate;
    }


    public void setPacketLossRate(Double packetLossRate) {
        this.packetLossRate = packetLossRate;
    }


    public Double getDelay() {
        return delay;
    }


    public void setDelay(Double delay) {
        this.delay = delay;
    }


    public DetectState getCheckState() {
        return checkState;
    }


    public void setCheckState(DetectState checkState) {
        this.checkState = checkState;
    }

}
