package com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;

import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/16 16:35
 *
 * @author TING
 */
public class TerminalLicenseStrategyUpdateConfigDTO {

    private CbbTerminalLicenseTypeEnums licenseType;

    @JSONField(name = "support_license_feature_id")
    private List<String> supportLicenseFeatureIdList;

    private Integer freeLicenseNum;

    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(CbbTerminalLicenseTypeEnums licenseType) {
        this.licenseType = licenseType;
    }

    public List<String> getSupportLicenseFeatureIdList() {
        return supportLicenseFeatureIdList;
    }

    public void setSupportLicenseFeatureIdList(List<String> supportLicenseFeatureIdList) {
        this.supportLicenseFeatureIdList = supportLicenseFeatureIdList;
    }

    public Integer getFreeLicenseNum() {
        return freeLicenseNum;
    }

    public void setFreeLicenseNum(Integer freeLicenseNum) {
        this.freeLicenseNum = freeLicenseNum;
    }
}
