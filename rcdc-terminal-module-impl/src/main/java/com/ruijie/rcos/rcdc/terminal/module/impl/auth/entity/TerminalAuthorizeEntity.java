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
@Table(name = "t_cbb_terminal_authorize")
public class TerminalAuthorizeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String terminalId;

    private String licenseType;


    @Enumerated(EnumType.STRING)
    private CbbTerminalPlatformEnums authMode;

    private Boolean authed;

    @Version
    private Integer version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public CbbTerminalPlatformEnums getAuthMode() {
        return authMode;
    }

    public void setAuthMode(CbbTerminalPlatformEnums authMode) {
        this.authMode = authMode;
    }

    public Boolean getAuthed() {
        return authed;
    }

    public void setAuthed(Boolean authed) {
        this.authed = authed;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
