package com.ruijie.rcos.rcdc.terminal.module.impl.message;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.StateEnums;

/**
 * Description: 终端检测返回消息对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/8
 *
 * @author Jarman
 */
public class TerminalDetectResponse {
    /**
     * 0 成功，1失败
     */
    private StateEnums errorCode;

    private DetectResult result;

    public StateEnums getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(StateEnums errorCode) {
        this.errorCode = errorCode;
    }

    public DetectResult getResult() {
        return result;
    }

    public void setResult(DetectResult result) {
        this.result = result;
    }

    /**
     * 检测结果对象
     */
    public static class DetectResult {
        /**
         * ip冲突结果，如果有冲突则ipConflictMac字段保存冲突的mac地址，否则为空值
         */
        private Integer ipConflict;
        
        /**
         * ip冲突的mac地址，未冲突时为空值
         */
        private String ipConflictMac;

        /**
         * 是否可访问外网
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

        
        public Integer getIpConflict() {
            return ipConflict;
        }

        public void setIpConflict(Integer ipConflict) {
            this.ipConflict = ipConflict;
        }

        public String getIpConflictMac() {
            return ipConflictMac;
        }

        public void setIpConflictMac(String ipConflictMac) {
            this.ipConflictMac = ipConflictMac;
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

    }
}
