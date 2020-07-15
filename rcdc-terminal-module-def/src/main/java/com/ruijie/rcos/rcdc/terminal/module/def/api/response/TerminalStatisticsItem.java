package com.ruijie.rcos.rcdc.terminal.module.def.api.response;

/**
 * Description: 终端统计项
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/28
 *
 * @author Jarman
 */
public class TerminalStatisticsItem {

    /**
     * 终端总数量 online + offline
     */
    private Integer total = 0;

    /**
     * 终端在线数量
     */
    private Integer online = 0;

    /**
     * 终端离线数量
     */
    private Integer offline = 0;

    /**
     * 从未登录的终端数量 FIXME 跨组件查询，需要在rco查询
     */
    private Integer neverLogin = 0;


    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getOffline() {
        return offline;
    }

    public void setOffline(Integer offline) {
        this.offline = offline;
    }

    public Integer getNeverLogin() {
        return neverLogin;
    }

    public void setNeverLogin(Integer neverLogin) {
        this.neverLogin = neverLogin;
    }
}
