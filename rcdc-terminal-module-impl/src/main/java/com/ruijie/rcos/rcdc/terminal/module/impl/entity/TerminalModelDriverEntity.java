package com.ruijie.rcos.rcdc.terminal.module.impl.entity;

import java.util.UUID;

import javax.persistence.*;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

/**
 *  终端产品型号驱动映射实体
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年12月09日
 *
 * @author nt
 */
@Entity
@Table(name = "t_cbb_terminal_model_driver")
public class TerminalModelDriverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String productModel;

    private String productId;

    private String cpuType;

    @Enumerated(EnumType.STRING)
    private CbbTerminalPlatformEnums platform;

    @Version
    private Integer version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public CbbTerminalPlatformEnums getPlatform() {
        return platform;
    }

    public void setPlatform(CbbTerminalPlatformEnums platform) {
        this.platform = platform;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
