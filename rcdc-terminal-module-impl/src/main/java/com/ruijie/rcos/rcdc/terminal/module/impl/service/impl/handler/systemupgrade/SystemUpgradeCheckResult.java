package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/11
 *
 * @param  <T> 返回的升级对象信息
 *
 * @author nt
 */
public class SystemUpgradeCheckResult<T> {

    private Integer systemUpgradeCode;

    private T content;

    public Integer getSystemUpgradeCode() {
        return systemUpgradeCode;
    }

    public void setSystemUpgradeCode(Integer systemUpgradeCode) {
        this.systemUpgradeCode = systemUpgradeCode;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
