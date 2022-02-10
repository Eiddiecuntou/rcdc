package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalCvaLicenseSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalCvaLicenseUpdateSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalDesktopHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/8/23 10:54
 *
 * @author yanlin
 */
@Service("terminalLicenseCvaServiceImpl")
public class TerminalLicenseCvaServiceImpl extends AbstractTerminalLicenseServiceImpl {
    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseCvaServiceImpl.class);

    @Autowired
    private CbbTerminalCvaLicenseSPI terminalCvaLicenseSPI;

    @Autowired
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

    @Autowired
    private CbbTerminalDesktopHandlerSPI cbbTerminalDesktopHandlerSPI;

    @Autowired
    private CbbTerminalCvaLicenseUpdateSPI terminalCvaLicenseUpdateSPI;

    @Override
    public Object getLock() {
        return terminalCvaLicenseSPI.getCvaLicenseUsedNumLock();
    }

    @Override
    public Object getLicenseNumLock() {
        return terminalCvaLicenseSPI.getCvaLicenseNumLock();
    }

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.CVA;
    }

    @Override
    public String getLicenseConstansKey() {
        return Constants.CVA_LICENSE_NUM;
    }

    @Override
    public Integer getUsedNum() {
        return terminalCvaLicenseSPI.getCvaLicenseUsedNum();
    }

    @Override
    public void processImportOfficialLicense(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        // 将所有已授权IDV终端置为未授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums.IDV, getLicenseConstansKey(),
            licenseNum, CbbTerminalLicenseTypeEnums.CVA);

        // 向clouddesktop组件获取使用中的vdi云桌面数量
        int usingVdiDesktopNum = cbbTerminalDesktopHandlerSPI.obtainUsingVdiDesktopNum(CbbTerminalLicenseTypeEnums.CVA.name());
        terminalCvaLicenseSPI.updateCvaLicenseNumCache(licenseNum);
        terminalCvaLicenseSPI.updateCvaLicenseUsedNumCache(usingVdiDesktopNum);
    }

    @Override
    public void processImportTempLicense() {
        // 将所有未授权IDV终端置为已授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums.IDV, getLicenseConstansKey(),
            CbbTerminalLicenseTypeEnums.CVA);
        terminalCvaLicenseSPI.updateCvaLicenseNumCache(Constants.TERMINAL_AUTH_DEFAULT_NUM);
    }

    @Override
    public void increaseCacheLicenseUsedNum() {
        terminalCvaLicenseSPI.canUseCvaLicense();
    }

    @Override
    public void decreaseCacheLicenseUsedNum() {
        terminalCvaLicenseUpdateSPI.releaseCvaLicenseUsedNum();
    }
}
