package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbFlashModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeModeEnums;
import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;

import java.util.UUID;


/**
 * 
 * Description: 添加终端刷机任务请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月14日
 * 
 * @author nt
 */
public class CbbAddSystemUpgradeTaskDTO {

    @NotNull
    private UUID packageId;

    @Nullable
    private CbbSystemUpgradeModeEnums upgradeMode;

    @Nullable
    private String[] terminalIdArr;

    @Nullable
    private UUID[] terminalGroupIdArr;

    @Nullable
    private CbbFlashModeEnums flashModeEnums;

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }

    @Nullable
    public String[] getTerminalIdArr() {
        return terminalIdArr;
    }

    public void setTerminalIdArr(@Nullable String[] terminalIdArr) {
        this.terminalIdArr = terminalIdArr;
    }

    public CbbSystemUpgradeModeEnums getUpgradeMode() {
        return upgradeMode;
    }

    public void setUpgradeMode(CbbSystemUpgradeModeEnums upgradeMode) {
        this.upgradeMode = upgradeMode;
    }

    @Nullable
    public UUID[] getTerminalGroupIdArr() {
        return terminalGroupIdArr;
    }

    public void setTerminalGroupIdArr(@Nullable UUID[] terminalGroupIdArr) {
        this.terminalGroupIdArr = terminalGroupIdArr;
    }

    public CbbFlashModeEnums getFlashModeEnums() {
        return flashModeEnums;
    }

    public void setFlashModeEnums(CbbFlashModeEnums flashModeEnums) {
        this.flashModeEnums = flashModeEnums;
    }
}
