package com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal;

import com.ruijie.rcos.sk.base.support.EqualsHashcodeSupport;

import java.util.Date;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @author nt
 */
public class CbbTerminalModelDTO extends EqualsHashcodeSupport {

    private String productModel;

    private String productId;

    private String cpuType;

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCpuType() {
        return cpuType;
    }

    public void setCpuType(String cpuType) {
        this.cpuType = cpuType;
    }
}
