package com.ruijie.rcos.rcdc.terminal.module.impl.cache;

import com.ruijie.rcos.rcdc.terminal.module.impl.enums.CollectLogStateEnums;

import java.time.Instant;

/**
 * Description: 收集日志缓存对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/6
 *
 * @author Jarman
 */
public class CollectLogCache {

    public CollectLogCache() {
    }

    public CollectLogCache(CollectLogStateEnums state) {
        this.state = state;
    }

    /**
     * 状态
     */
    private CollectLogStateEnums state;

    /**
     * 只有状态为已完成时才有值
     */
    private String logFileName;


    public CollectLogStateEnums getState() {
        return state;
    }

    public void setState(CollectLogStateEnums state) {
        this.state = state;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

}
