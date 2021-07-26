package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: terminalLicenseVOIServiceImpl voi授权
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/19 5:35 下午
 *
 * @author lin
 */
@Service("terminalLicenseVOIServiceImpl")
public class TerminalLicenseVoiServiceImpl extends AbstractTerminalLicenseServiceImpl {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseVoiServiceImpl.class);

    @Autowired
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.VOI;
    }

    @Override
    public String getLicenseConstansKey() {
        return Constants.VOI_TEMINAL_LICENSE_NUM;
    }

    @Override
    public Object getLock() {
        return usedNumLock;
    }

    @Override
    public void processImportOfficialLicense(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        // 将所有已授权IDV终端置为未授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums.VOI, getLicenseConstansKey(), licenseNum);
        this.usedNum = 0;
    }

    @Override
    public void processImportTempLicense() {
        // 将所有未授权IDV终端置为已授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums.VOI, getLicenseConstansKey());
    }


}
