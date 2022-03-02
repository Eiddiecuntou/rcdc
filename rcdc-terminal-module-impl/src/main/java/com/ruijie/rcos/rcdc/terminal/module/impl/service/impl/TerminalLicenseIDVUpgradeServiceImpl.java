package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: CVA_IDV升级授权
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2022/2/9 16:01
 *
 * @author chenjuan
 */
@Service("terminalLicenseIDVUpgradeServiceImpl")
public class TerminalLicenseIDVUpgradeServiceImpl extends AbstractTerminalLicenseServiceImpl {

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.IDV_PLUS_UPGRADED;
    }

    @Override
    public String getLicenseConstansKey() {
        return Constants.IDV_UPGRADE_TEMINAL_LICENSE_NUM;
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
    public Integer getUsedNum() {

        synchronized (this.getLock()) {
            // 如果usedNum值为null，表示usedNum还没有从数据库同步数据;licenseNum为-1时，代表临时授权不会维护已授权数目，所以需要从数据库同步数据
            final Integer terminalLicenseNum = this.getAllTerminalLicenseNum();
            final boolean isTempLicense = isTempLicense(terminalLicenseNum);
            if (usedNum == null || isTempLicense) {
                countUpgradeLicenseUsedNumFromDB();
            }
        }

        return usedNum;
    }

    @Override
    public void refreshLicenseUsedNum() {
        synchronized (this.getLock()) {
            countUpgradeLicenseUsedNumFromDB();
        }
    }

    @Override
    public void processImportOfficialLicense(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        // 将所有已授权IDV终端置为未授权，并更新终端授权数量
        usedNum = 0;
    }

    @Override
    public void processImportTempLicense() {
        // 将所有未授权IDV终端置为已授权，并更新终端授权数量

    }
}
