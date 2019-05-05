package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import java.util.Date;

import org.springframework.lang.Nullable;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageRequest;

/**
 * 
 * Description: 终端检测记录分页请求参数
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月28日
 * 
 * @author nt
 */
public class CbbTerminalDetectPageRequest extends DefaultPageRequest {

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
