package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.PackageObtainModeEnums;
import com.ruijie.rcos.sk.base.support.EqualsHashcodeSupport;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @param  <T> 返回的升级对象信息
 *
 * @author nt
 */
public class SystemUpgradeCheckResult<T> extends EqualsHashcodeSupport {

    private Integer systemUpgradeCode;

    private PackageObtainModeEnums packageObtainMode;

    private T content;

    public Integer getSystemUpgradeCode() {
        return systemUpgradeCode;
    }

    public void setSystemUpgradeCode(Integer systemUpgradeCode) {
        this.systemUpgradeCode = systemUpgradeCode;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public PackageObtainModeEnums getPackageObtainMode() {
        return packageObtainMode;
    }

    public void setPackageObtainMode(PackageObtainModeEnums packageObtainMode) {
        this.packageObtainMode = packageObtainMode;
    }
}
