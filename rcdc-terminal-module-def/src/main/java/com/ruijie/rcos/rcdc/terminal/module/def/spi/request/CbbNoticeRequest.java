package com.ruijie.rcos.rcdc.terminal.module.def.spi.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.NoticeEventEnums;
import com.ruijie.rcos.sk.base.annotation.NotBlank;
import com.ruijie.rcos.sk.modulekit.api.comm.DispatcherKey;
import com.ruijie.rcos.sk.modulekit.api.comm.Request;
import org.springframework.lang.Nullable;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/1/2
 *
 * @author Jarman
 */
public class CbbNoticeRequest implements Request {

    @NotBlank
    @DispatcherKey
    private String dispatcherKey;

    @Nullable
    private String terminalId;

    public CbbNoticeRequest() {
    }

    public CbbNoticeRequest(NoticeEventEnums noticeEvent, String terminalId) {
        this.dispatcherKey = noticeEvent.getName();
        this.terminalId = terminalId;
    }

    public String getDispatcherKey() {
        return dispatcherKey;
    }

    public void setDispatcherKey(String dispatcherKey) {
        this.dispatcherKey = dispatcherKey;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
