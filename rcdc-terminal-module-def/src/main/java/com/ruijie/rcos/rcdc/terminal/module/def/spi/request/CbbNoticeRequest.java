package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNoticeEventEnums;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherKey;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/2
 *
 * @author Jarman
 */
public class CbbNoticeRequest {

    @NotBlank
    @DispatcherKey
    private String dispatcherKey;

    @Nullable
    private CbbShineTerminalBasicInfo terminalBasicInfo;

    public CbbNoticeRequest() {
    }

    public CbbNoticeRequest(CbbNoticeEventEnums noticeEvent) {
        Assert.notNull(noticeEvent, "noticeEvent can not be null");

        this.dispatcherKey = noticeEvent.getName();
    }

    public String getDispatcherKey() {
        return dispatcherKey;
    }

    public void setDispatcherKey(String dispatcherKey) {
        this.dispatcherKey = dispatcherKey;
    }

    @Nullable
    public CbbShineTerminalBasicInfo getTerminalBasicInfo() {
        return terminalBasicInfo;
    }

    public void setTerminalBasicInfo(@Nullable CbbShineTerminalBasicInfo terminalBasicInfo) {
        this.terminalBasicInfo = terminalBasicInfo;
    }
}
