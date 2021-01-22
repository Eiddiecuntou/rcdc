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
 * Description: terminalLicenseIDVServiceImpl idv授权
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/1/19 5:35 下午
 *
 * @author lin
 */
@Service("terminalLicenseIDVServiceImpl")
public class TerminalLicenseIDVServiceImpl extends AbstractTerminalLicenseServiceImpl {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseIDVServiceImpl.class);

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

    private Integer licenseNum;

    private Integer usedNum;

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.IDV;
    }

    @Override
    public String getLicenseConstansKey() {
        return Constants.TEMINAL_LICENSE_NUM;
    }

    @Override
    protected Integer getCacheLicenseNum() {
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
            final boolean isTempLicense = getTerminalLicenseNum() == Constants.TERMINAL_AUTH_DEFAULT_NUM;
            if (usedNum == null || isTempLicense) {
                long count = terminalBasicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
                LOGGER.info("从数据库同步idv授权已用数为：{},idv授权数为：{}", usedNum, terminalLicenseNum);
                if (isTempLicense) {
                    usedNum = (int) count;
                } else {
                    usedNum = count > terminalLicenseNum ? terminalLicenseNum : (int) count;
                }
                LOGGER.info("从数据库同步idv授权usedNum值为:{}", usedNum);
            }
        }
        return usedNum;
    }

    @Override
    public void decreaseCacheLicenseUsedNum() {
        if (usedNum == null) {
            getUsedNum();
        }
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
        terminalLicenseServiceTx.updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums.IDV, getLicenseConstansKey(), licenseNum);
        this.usedNum = 0;
        this.licenseNum = licenseNum;
    }

    @Override
    public void processImportTempLicense() {
        // 将所有未授权IDV终端置为已授权，并更新终端授权数量
        terminalLicenseServiceTx.updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums.IDV, getLicenseConstansKey());
        this.licenseNum = Constants.TERMINAL_AUTH_DEFAULT_NUM;
    }

    @Override
    public void increaseCacheLicenseUsedNum() {
        synchronized (usedNumLock) {
            usedNum++;
        }
    }

}
