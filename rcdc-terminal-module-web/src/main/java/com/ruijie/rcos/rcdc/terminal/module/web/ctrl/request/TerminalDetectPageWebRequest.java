package com.ruijie.rcos.rcdc.terminal.module.web.ctrl.request;

import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
import org.springframework.lang.Nullable;

import java.util.Date;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/4/26
 *
 * @author nt
 */
public class TerminalDetectPageWebRequest extends PageWebRequest {

    @Nullable
    private Date startTime;

    @Nullable
    private Date endTime;

    @Nullable
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(@Nullable Date startTime) {
        this.startTime = startTime;
    }

    @Nullable
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(@Nullable Date endTime) {
        this.endTime = endTime;
    }
}
