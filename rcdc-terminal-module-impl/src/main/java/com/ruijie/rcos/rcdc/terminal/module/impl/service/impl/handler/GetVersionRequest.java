package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/3
 *
 * @author nt
 */
public class GetVersionRequest {

    @Nullable
    private String terminalId;

    @NotNull
    private String rainUpgradeVersion;

    @Nullable
    private String validateMd5;

    @Nullable
    private String rainOsVersion;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getRainUpgradeVersion() {
        return rainUpgradeVersion;
    }

    public void setRainUpgradeVersion(String rainUpgradeVersion) {
        this.rainUpgradeVersion = rainUpgradeVersion;
    }

    @Nullable
    public String getValidateMd5() {
        return validateMd5;
    }

    public void setValidateMd5(@Nullable String validateMd5) {
        this.validateMd5 = validateMd5;
    }

    @Nullable
    public String getRainOsVersion() {
        return rainOsVersion;
    }

    public void setRainOsVersion(@Nullable String rainOsVersion) {
        this.rainOsVersion = rainOsVersion;
    }

}
