package com.ruijie.rcos.rcdc.terminal.module.impl.model;

/**
 * Description: 请求背景图片的回应
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/6
 *
 * @author songxiang
 */
public class TerminalSyncBackgroundResponse {

    private Integer needSync;

    private String name;

    public Integer getNeedSync() {
        return needSync;
    }

    public void setNeedSync(Integer needSync) {
        this.needSync = needSync;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
