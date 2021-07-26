package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
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

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private TerminalLicenseIDVServiceImpl terminalLicenseIDVServiceImpl;

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    private Integer licenseNum;

    private Integer usedNum;

    @Override
    public CbbTerminalLicenseTypeEnums getLicenseType() {
        return CbbTerminalLicenseTypeEnums.VOI_PLUS_UPGRADED;
    }

    @Override
    public String getLicenseConstansKey() {
        return Constants.VOI_UPGRADE_TEMINAL_LICENSE_NUM;
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
            final Integer terminalLicenseNum = this.getAllTerminalLicenseNum();
            final boolean isTempLicense = isTempLicense(terminalLicenseNum);
            if (usedNum == null || isTempLicense) {
                LOGGER.info("从数据库同步voi升级授权已用数为：{},voi升级授权数为：{}", usedNum, terminalLicenseNum);
                if (isTempLicense) {
                    usedNum = Constants.TERMINAL_AUTH_VOI_UPGRADE_USED_DEFAULT_NUM;
                    return usedNum;
                }
                final Integer idvLicenseNum = terminalLicenseIDVServiceImpl.getAllTerminalLicenseNum();
                long count = terminalAuthorizeDAO.countByLicenseType(CbbTerminalLicenseTypeEnums.VOI_PLUS_UPGRADED.name());
                LOGGER.info("从数据库同步idv授权已用数为：{},idv授权数为：{}", count, idvLicenseNum);

                boolean isIdvTempLicense = isTempLicense(idvLicenseNum);
                if (isIdvTempLicense) {
                    LOGGER.info("查询数据库，idv授权类型为临时授权，不存在voi升级授权被idv使用的情况");
                    usedNum = Constants.TERMINAL_AUTH_VOI_UPGRADE_USED_DEFAULT_NUM;
                } else {
                    usedNum = count > idvLicenseNum ? (int) (count - idvLicenseNum) : Constants.TERMINAL_AUTH_VOI_UPGRADE_USED_DEFAULT_NUM;
                }
                LOGGER.info("从数据库同步voi升级授权usedNum值为:{}", usedNum);
            }
        }
        return usedNum;
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
        this.usedNum = 0;
        this.licenseNum = licenseNum;

        globalParameterAPI.updateParameter(getLicenseConstansKey(), String.valueOf(licenseNum));
    }

    @Override
    public void processImportTempLicense() {
        // 将所有未授权IDV终端置为已授权，并更新终端授权数量
        this.licenseNum = Constants.TERMINAL_AUTH_DEFAULT_NUM;
    }

}
