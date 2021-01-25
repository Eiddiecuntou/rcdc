package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/16
 *
 * @author jarman
 */
public class CbbTerminalLicenseNumDTO {

    private CbbTerminalLicenseTypeEnums licenseType;

    /**
     * 总的授权数
     */
    private Integer licenseNum;

    /**
     * 已用授权数
     */
    private Integer usedNum;

    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(CbbTerminalLicenseTypeEnums licenseType) {
        this.licenseType = licenseType;
    }

    public Integer getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(Integer licenseNum) {
        this.licenseNum = licenseNum;
    }

    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }
}
