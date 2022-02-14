package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
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
 * Create Time: 2021/6/21 11:35
 *
 * @author yanlin
 */
@Service("terminalLicenseCvaIdvServiceImpl")
public class TerminalLicenseCvaIdvServiceImpl extends AbstractTerminalLicenseServiceImpl {
    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseCvaIdvServiceImpl.class);

    @Autowired
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

    private Integer licenseNum;

    private Integer usedNum;

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.CVA_IDV;
    }

    @Override
    public String getLicenseConstansKey() {
        return Constants.CVA_TERMINAL_LICENSE_NUM;
    }

    @Override
    public Object getLock() {
        return usedNumLock;
    }

    @Override
    public Object getLicenseNumLock() {
        return usedNumLock;
    }

    @Override
    public void processImportOfficialLicense(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        // 将所有已授权IDV终端置为未授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums.IDV, getLicenseConstansKey(),
            licenseNum, CbbTerminalLicenseTypeEnums.CVA_IDV);
        this.usedNum = 0;
        this.licenseNum = licenseNum;
    }

    @Override
    public void processImportTempLicense() {
        // 将所有未授权IDV终端置为已授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums.IDV, getLicenseConstansKey(),
            CbbTerminalLicenseTypeEnums.CVA_IDV);
        this.licenseNum = Constants.TERMINAL_AUTH_DEFAULT_NUM;
    }
}
