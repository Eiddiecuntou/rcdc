package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/16
 *
 * @author jarman
 */
public class CbbIDVTerminalLicenseNumDTO {

    /**
     * 总的授权数
     */
    private Integer licenseNum;

    /**
     * 已用授权数
     */
    private Integer usedNum;

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
