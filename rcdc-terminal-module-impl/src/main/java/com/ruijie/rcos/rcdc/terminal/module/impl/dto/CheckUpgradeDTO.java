package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import org.springframework.lang.Nullable;

import java.util.Date;

public class CheckUpgradeDTO {

    @Nullable
    private Date receiveDate;

    @Nullable
    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(@Nullable Date receiveDate) {
        this.receiveDate = receiveDate;
    }
}
