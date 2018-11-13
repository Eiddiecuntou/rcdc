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
         * ip冲突结果，如果有冲突则保存冲突的mac地址，否则为空值
         */
        private String ipConflict;

        /**
         * 是否可访问外网，0不能访问，1可访问
         */
        private Integer canAccessInternet;

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
        private Double networkDelay;

        public String getIpConflict() {
            return ipConflict;
        }

        public void setIpConflict(String ipConflict) {
            this.ipConflict = ipConflict;
        }

        public Integer getCanAccessInternet() {
            return canAccessInternet;
        }

        public void setCanAccessInternet(Integer canAccessInternet) {
            this.canAccessInternet = canAccessInternet;
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

        public Double getNetworkDelay() {
            return networkDelay;
        }

        public void setNetworkDelay(Double networkDelay) {
            this.networkDelay = networkDelay;
        }
    }
}
