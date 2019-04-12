package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * 
 * Description: 终端检测结果DTO
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class CbbTerminalDetectResultDTO {

    private int ipConflict;

    private int bandwidth;

    private int accessInternet;

    private int packetLossRate;

    private int delay;

    private int checking;

    private int all;

    public int getIpConflict() {
        return ipConflict;
    }

    public void setIpConflict(int ipConflict) {
        this.ipConflict = ipConflict;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getAccessInternet() {
        return accessInternet;
    }

    public void setAccessInternet(int accessInternet) {
        this.accessInternet = accessInternet;
    }

    public int getPacketLossRate() {
        return packetLossRate;
    }

    public void setPacketLossRate(int packetLossRate) {
        this.packetLossRate = packetLossRate;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getChecking() {
        return checking;
    }

    public void setChecking(int checking) {
        this.checking = checking;
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

}
