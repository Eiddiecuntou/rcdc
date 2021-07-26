package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: terminalLicenseVOIServiceImpl voi升级授权
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/19 5:35 下午
 *
 * @author lin
 */
@Service("terminalLicenseVOIUpgradeServiceImpl")
public class TerminalLicenseVoiUpgradeServiceImpl extends AbstractTerminalLicenseServiceImpl {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseVoiUpgradeServiceImpl.class);

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.VOI_PLUS_UPGRADED;
    }
    @Override
    public String getLicenseConstansKey() {
        return Constants.VOI_UPGRADE_TEMINAL_LICENSE_NUM;
    }

    @Override
    public Object getLock() {
        return usedNumLock;
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
