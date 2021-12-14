package com.ruijie.rcos.rcdc.terminal.module.impl.dto;

import org.springframework.lang.Nullable;

import java.util.Date;

/**
 * 
 * Description: 升级处理类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/12/15
 *
 * @author zhiweiHong
 */
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
