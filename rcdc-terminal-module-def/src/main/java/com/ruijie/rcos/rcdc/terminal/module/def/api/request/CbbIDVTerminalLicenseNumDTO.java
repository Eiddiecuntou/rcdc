package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

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
    private int licenseNum;

    /**
     * 已用授权数
     */
    private int usedNum;

    public int getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(int licenseNum) {
        this.licenseNum = licenseNum;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }
}
