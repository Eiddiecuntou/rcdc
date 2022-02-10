package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;

/**
 * Description: CbbTerminalLicenseFactoryProvider抽象工厂
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/19 5:35 下午
 *
 * @author lin
 */
@Service
public class CbbTerminalLicenseFactoryProvider {

    @Autowired
    @Qualifier("terminalLicenseIDVServiceImpl")
    private TerminalLicenseService terminalLicenseIDVService;

    @Autowired
    @Qualifier("terminalLicenseVOIServiceImpl")
    private TerminalLicenseService terminalLicenseVOIService;

    @Autowired
    @Qualifier("terminalLicenseVOIUpgradeServiceImpl")
    private TerminalLicenseService terminalLicenseVOIUpgradeService;

    @Autowired
    @Qualifier("terminalLicenseCvaIdvServiceImpl")
    private TerminalLicenseService terminalLicenseCvaIdvService;

    @Autowired
    @Qualifier("terminalLicenseCvaServiceImpl")
    private TerminalLicenseService terminalLicenseCvaService;

    @Autowired
    @Qualifier("terminalLicenseIDVUpgradeServiceImpl")
    private TerminalLicenseService terminalLicenseIDVUpgradeService;
    /**
     * 根据授权类型获取授权服务器
     * 
     * @param licenseType 授权类型
     * @return service
     */
    public TerminalLicenseService getService(CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(licenseType, "licenseType can not be null");
        switch (licenseType) {
            case IDV:
                return terminalLicenseIDVService;
            case VOI:
                return terminalLicenseVOIService;
            case VOI_PLUS_UPGRADED:
                return terminalLicenseVOIUpgradeService;
            case CVA_IDV:
                return terminalLicenseCvaIdvService;
            case CVA:
                return terminalLicenseCvaService;
            case IDV_PLUS_UPGRADED:
                return terminalLicenseIDVUpgradeService;
            default:
                throw new IllegalStateException("不支持当前授权类型！licenseType=" + licenseType);
        }
    }
}
