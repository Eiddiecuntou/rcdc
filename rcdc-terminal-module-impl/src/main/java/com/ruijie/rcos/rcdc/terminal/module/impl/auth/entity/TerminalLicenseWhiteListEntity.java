package com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity;

import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

import javax.persistence.*;
import java.util.UUID;

/**
 * 终端证书授权白名单
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年12月09日
 *
 * @author nt
 */
@Entity
@Table(name = "t_cbb_terminal_license_white_list")
public class TerminalLicenseWhiteListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


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
