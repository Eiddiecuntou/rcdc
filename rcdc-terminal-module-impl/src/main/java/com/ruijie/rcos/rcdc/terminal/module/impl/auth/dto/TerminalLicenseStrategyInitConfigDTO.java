package com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/16 16:33
 *
 * @author TING
 */
public class TerminalLicenseStrategyInitConfigDTO {

    private String licenseFeatureId;

    private Integer trialDuration;

    private Integer licenseNum;

    private String licenseCode;

    public String getLicenseFeatureId() {
        return licenseFeatureId;
    }

    public void setLicenseFeatureId(String licenseFeatureId) {
        this.licenseFeatureId = licenseFeatureId;
    }

    public Integer getTrialDuration() {
        return trialDuration;
    }

    public void setTrialDuration(Integer trialDuration) {
        this.trialDuration = trialDuration;
    }

    public Integer getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(Integer licenseNum) {
        this.licenseNum = licenseNum;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }
}
