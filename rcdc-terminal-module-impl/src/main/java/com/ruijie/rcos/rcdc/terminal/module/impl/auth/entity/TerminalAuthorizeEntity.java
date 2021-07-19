package com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.enums.CbbTerminalAuthModeEnums;

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

    @Enumerated(EnumType.STRING)
    private CbbTerminalLicenseTypeEnums licenseType;


    @Enumerated(EnumType.STRING)
    private CbbTerminalAuthModeEnums authMode;

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

    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(CbbTerminalLicenseTypeEnums licenseType) {
        this.licenseType = licenseType;
    }

    public CbbTerminalAuthModeEnums getAuthMode() {
        return authMode;
    }

    public void setAuthMode(CbbTerminalAuthModeEnums authMode) {
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
