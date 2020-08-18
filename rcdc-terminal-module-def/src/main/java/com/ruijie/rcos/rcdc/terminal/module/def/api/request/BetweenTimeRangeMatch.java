package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.sk.base.annotation.NotNull;
import org.springframework.lang.Nullable;

import java.util.Date;

/**
 * Description: 分页查询时间区间构建条件
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 19:53 2020/5/18
 *
 * @author yxd
 */
public class BetweenTimeRangeMatch {

    @Nullable
    private Date startTime;

    @Nullable
    private Date endTime;

    @NotNull
    private String timeKey;

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

    public String getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }

    public BetweenTimeRangeMatch(@Nullable Date startTime, @Nullable Date endTime, String timeKey) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeKey = timeKey;
    }

    public BetweenTimeRangeMatch() {
    }
}
