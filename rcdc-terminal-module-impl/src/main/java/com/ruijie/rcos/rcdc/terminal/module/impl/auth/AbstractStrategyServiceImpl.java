package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TempLicCreateDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalLockHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory.CbbTerminalLicenseFactoryProvider;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:31
 *
 * @author TING
 */
public abstract class AbstractStrategyServiceImpl implements StrategyService {

    @Autowired
    private CbbTerminalLicenseFactoryProvider terminalLicenseFactoryProvider;

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Autowired
    private TerminalLockHelper terminalLockHelper;

    @Override
    public void init(TempLicCreateDTO tempLicCreateDTO) {
        // TODO 初始化临时授权证书
    }

    /**
     * 获取授权认证服务对象
     *
     * @param licenseType 授权认证类型
     * @return 授权认证服务对象
     */
    public TerminalLicenseService getTerminalLicenseService(CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(licenseType, "licenseType can not be null");

        return terminalLicenseFactoryProvider.getService(licenseType);
    }

    protected void saveTerminalAuthorize(String licenseTypeStr, CbbShineTerminalBasicInfo basicInfoDTO) {

        Lock lock = terminalLockHelper.putAndGetLock(basicInfoDTO.getTerminalId());
        lock.lock();
        try {
            int countTerminalAuth = terminalAuthorizeDAO.countByTerminalId(basicInfoDTO.getTerminalId());
            if (countTerminalAuth > 0) {
                return;
            }
            TerminalAuthorizeEntity entity = new TerminalAuthorizeEntity();
            entity.setAuthed(true);
            entity.setAuthMode(basicInfoDTO.getAuthMode());
            entity.setLicenseType(licenseTypeStr);
            entity.setTerminalId(basicInfoDTO.getTerminalId());

            terminalAuthorizeDAO.save(entity);
        } finally {
            lock.unlock();
        }

    }
}
