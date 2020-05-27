package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/5/21
 *
 * @author nt
 */
public class CbbTerminalDetectThresholdDTO {

    private Double bandwidthThreshold;

    private Double packetLossRateThreshold;

    private Double delayThreshold;

    public Double getBandwidthThreshold() {
        return bandwidthThreshold;
    }

    public void setBandwidthThreshold(Double bandwidthThreshold) {
        this.bandwidthThreshold = bandwidthThreshold;
    }

    public Double getPacketLossRateThreshold() {
        return packetLossRateThreshold;
    }

    public void setPacketLossRateThreshold(Double packetLossRateThreshold) {
        this.packetLossRateThreshold = packetLossRateThreshold;
    }

    public Double getDelayThreshold() {
        return delayThreshold;
    }

    public void setDelayThreshold(Double delayThreshold) {
        this.delayThreshold = delayThreshold;
    }
}
