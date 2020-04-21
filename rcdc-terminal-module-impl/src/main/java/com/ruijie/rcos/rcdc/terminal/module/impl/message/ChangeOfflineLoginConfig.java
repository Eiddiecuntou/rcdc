package com.ruijie.rcos.rcdc.terminal.module.impl.message;

/**
 * Description: 修改离线登录配置报文
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/9 15:59
 *
 * @author conghaifeng
 */
public class ChangeOfflineLoginConfig {

    private Integer disconnectServerUseDay;

    public Integer getDisconnectServerUseDay() {
        return disconnectServerUseDay;
    }

    public void setDisconnectServerUseDay(Integer disconnectServerUseDay) {
        this.disconnectServerUseDay = disconnectServerUseDay;
    }

    public ChangeOfflineLoginConfig(Integer disconnectServerUseDay) {
        this.disconnectServerUseDay = disconnectServerUseDay;
    }
}
