package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import org.springframework.data.domain.Sort.Direction;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/12/10
 *
 * @author Jarman
 */
public class DefaultDataSort {

    private String sortField;

    private Direction direction;

    public DefaultDataSort(String sortField, Direction direction) {
        this.sortField = sortField;
        this.direction = direction;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
