package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.GatherLogStateEnums;

import java.time.Instant;

/**
 * Description: 收集日志缓存对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/6
 *
 * @author Jarman
 */
public class GatherLogCache {
    /**
     * 缓存有效时间，单位毫秒，默认10分钟
     */
    private static final long EXPIRE = 1000 * 60 * 10;

    /**
     * 状态
     */
    private GatherLogStateEnums state;

    /**
     * 只有状态为已完成时才有值
     */
    private String logFileName;

    /**
     * 缓存过期时间
     */
    private Long expireTime;

    public GatherLogCache() {
        //记录过期时间
        long millisecond = Instant.now().toEpochMilli();
        this.expireTime = millisecond + EXPIRE;
    }

    public GatherLogStateEnums getState() {
        return state;
    }

    public void setState(GatherLogStateEnums state) {
        this.state = state;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
