package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import org.springframework.lang.Nullable;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.base.annotation.NotNull;

/**
 * Description: 授权证书信息
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 18:36
 *
 * @author TING
 */
public class CbbTerminalLicenseInfoDTO {

    @NotBlank
    private String licenseCode;

    @NotNull
    private Integer totalNum;

    @Nullable
    private Integer usedNum;

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }
}
