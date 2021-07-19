package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * Description: 授权证书信息
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 18:36
 *
 * @author TING
 */
public class CbbTerminalLicenseInfoDTO {

    private String licenseCode;

    private int totalNum;

    private int usedNum;

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }
}
