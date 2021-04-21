package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * Description: 终端数据统计对象
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/28
 *
 * @author Jarman
 */
public class CbbTerminalStatisticsDTO {

    private TerminalStatisticsItem vdi;

    private TerminalStatisticsItem idv;

    private TerminalStatisticsItem app;

    private TerminalStatisticsItem pc;

    private TerminalStatisticsItem voi;

    private Integer total = 0;

    private Integer totalOnline = 0;

    public TerminalStatisticsItem getPc() {
        return pc;
    }

    public void setPc(TerminalStatisticsItem pc) {
        this.pc = pc;
    }

    public TerminalStatisticsItem getApp() {
        return app;
    }

    public void setApp(TerminalStatisticsItem app) {
        this.app = app;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalOnline() {
        return totalOnline;
    }

    public void setTotalOnline(Integer totalOnline) {
        this.totalOnline = totalOnline;
    }

    public TerminalStatisticsItem getVdi() {
        return vdi;
    }

    public void setVdi(TerminalStatisticsItem vdi) {
        this.vdi = vdi;
    }

    public TerminalStatisticsItem getIdv() {
        return idv;
    }

    public void setIdv(TerminalStatisticsItem idv) {
        this.idv = idv;
    }

    public TerminalStatisticsItem getVoi() {
        return voi;
    }

    public void setVoi(TerminalStatisticsItem voi) {
        this.voi = voi;
    }
}
