package com.ruijie.rcos.rcdc.terminal.module.def.api.dto;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/6
 *
 * @author Jarman
 */
public class TerminalStatisticsDTO {

    private Long count;

    private String state;

    public TerminalStatisticsDTO(Long count, String state) {
        this.count = count;
        this.state = state;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
