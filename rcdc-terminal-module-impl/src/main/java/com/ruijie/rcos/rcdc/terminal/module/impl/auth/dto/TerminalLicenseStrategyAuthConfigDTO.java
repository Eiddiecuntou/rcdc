package com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;

import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/16 16:37
 *
 * @author TING
 */
public class TerminalLicenseStrategyAuthConfigDTO {


    private CbbTerminalLicenseTypeEnums licenseType;

    @JSONField(name = "support_license_type")
    private List<TerminalLicenseStrategyAuthSupportConfigDTO> supportLicenseTypeList;

    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(CbbTerminalLicenseTypeEnums licenseType) {
        this.licenseType = licenseType;
    }

    public List<TerminalLicenseStrategyAuthSupportConfigDTO> getSupportLicenseTypeList() {
        return supportLicenseTypeList;
    }

    public void setSupportLicenseTypeList(List<TerminalLicenseStrategyAuthSupportConfigDTO> supportLicenseTypeList) {
        this.supportLicenseTypeList = supportLicenseTypeList;
    }
}
