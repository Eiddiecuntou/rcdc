package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
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
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

    @Autowired
    private TerminalLicenseVoiUpgradeServiceImpl terminalLicenseVOIUpgradeServiceImpl;

    private Integer licenseNum;

    private Integer usedNum;

    private Object usedNumLock = new Object();

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.VOI;
    }

    @Override
    public String getLicenseConstansKey() {
        return Constants.VOI_TEMINAL_LICENSE_NUM;
    }

    @Override
    public Integer getCacheLicenseNum() {
        return licenseNum;
    }

    @Override
    public Object getLock() {
        return usedNumLock;
    }

    @Override
    public Integer getUsedNum() {
        synchronized (this.getLock()) {
            // 如果usedNum值为null，表示usedNum还没有从数据库同步数据;licenseNum为-1时，代表临时授权不会维护已授权数目，所以需要从数据库同步数据
            final Integer terminalLicenseNum = super.getTerminalLicenseNum();
            final boolean tempLicense = getTerminalLicenseNum() == Constants.TERMINAL_AUTH_DEFAULT_NUM;
            if (usedNum == null || tempLicense) {
                long count = terminalBasicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.VOI, Boolean.TRUE);
                LOGGER.info("从数据库同步voi授权已用数为：{},voi授权数为：{}", usedNum, terminalLicenseNum);
                if (tempLicense) {
                    usedNum = (int) count;
                } else {
                    Integer voiUpgradeUsedNum = terminalLicenseVOIUpgradeServiceImpl.getUsedNum();
                    usedNum = (int) count + voiUpgradeUsedNum;
                }
                LOGGER.info("从数据库同步voi授权usedNum值为:{}", usedNum);
            }
        }
        return usedNum;
    }

    @Override
    public void decreaseCacheLicenseUsedNum() {
        synchronized (usedNumLock) {
            usedNum--;
        }
    }

    @Override
    public void updateCacheLicenseNum(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        this.licenseNum = licenseNum;
    }

    @Override
    public void processImportOfficialLicense(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        // 将所有已授权IDV终端置为未授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums.VOI, getLicenseConstansKey(), licenseNum);
        this.usedNum = 0;
        this.licenseNum = licenseNum;
    }

    @Override
    public void processImportTempLicense() {
        // 将所有未授权IDV终端置为已授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums.VOI, getLicenseConstansKey());
        this.licenseNum = Constants.TERMINAL_AUTH_DEFAULT_NUM;
    }

    @Override
    public void increaseCacheLicenseUsedNum() {
        synchronized (usedNumLock) {
            usedNum++;
        }
    }

}
