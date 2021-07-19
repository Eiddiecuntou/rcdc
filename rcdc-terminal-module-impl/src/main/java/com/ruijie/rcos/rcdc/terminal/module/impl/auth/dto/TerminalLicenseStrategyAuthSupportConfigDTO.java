package com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.enums.CbbTerminalLicenseStrategyEnums;

import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/16 16:37
 *
 * @author TING
 */
public class TerminalLicenseStrategyAuthSupportConfigDTO {

    private CbbTerminalLicenseStrategyEnums strategyType;

    @JSONField(name = "license_type")
    private List<CbbTerminalLicenseTypeEnums> licenseTypeList;

    public CbbTerminalLicenseStrategyEnums getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(CbbTerminalLicenseStrategyEnums strategyType) {
        this.strategyType = strategyType;
    }

    public List<CbbTerminalLicenseTypeEnums> getLicenseTypeList() {
        return licenseTypeList;
    }

    public void setLicenseTypeList(List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        this.licenseTypeList = licenseTypeList;
    }
}
