package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TempLicCreateDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory.CbbTerminalLicenseFactoryProvider;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:31
 *
 * @author TING
 */
public abstract class AbstractStrategyServiceImpl implements StrategyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategyServiceImpl.class);

    @Autowired
    private CbbTerminalLicenseFactoryProvider terminalLicenseFactoryProvider;

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    protected final Interner<String> terminalIdInterner = Interners.newWeakInterner();

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

    protected void saveTerminalAuthorize(String terminalId, String licenseTypeStr, CbbTerminalPlatformEnums authMode) {
        int countTerminalAuth = terminalAuthorizeDAO.countByTerminalId(terminalId);
        if (countTerminalAuth > 0) {
            return;
        }
        TerminalAuthorizeEntity entity = new TerminalAuthorizeEntity();
        entity.setAuthed(true);
        entity.setAuthMode(authMode);
        entity.setLicenseType(licenseTypeStr);
        entity.setTerminalId(terminalId);
        terminalAuthorizeDAO.save(entity);

    }

    protected void deleteTerminalAuthorize(String terminalId, String licenseTypeStr, CbbTerminalPlatformEnums authMode) {
        synchronized (terminalIdInterner.intern(terminalId)) {
            // 如果当前终端的授权记录不是预期回收的，则将修改一个为删除终端的授权类型
            TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
            if (authorizeEntity != null && !authorizeEntity.getLicenseType().equals(licenseTypeStr)) {
                LOGGER.info("终端的授权记录不是预期回收的， 修改一个授权类型[{}]为删除终端的授权类型[{}]", licenseTypeStr, authorizeEntity.getLicenseType());
                convertAuthLicenseType(authMode, licenseTypeStr, authorizeEntity.getLicenseType());
            }
            // 删除授权记录
            terminalAuthorizeDAO.deleteByTerminalId(terminalId);
        }
    }

    private void convertAuthLicenseType(CbbTerminalPlatformEnums authMode, String licenseType, String updateLicenseType) {
        List<TerminalAuthorizeEntity> authorizeEntityList = terminalAuthorizeDAO.findByLicenseTypeAndAuthMode(licenseType, authMode);
        if (authorizeEntityList.isEmpty()) {
            return;
        }
        TerminalAuthorizeEntity authorizeEntity = authorizeEntityList.get(0);
        authorizeEntity.setLicenseType(updateLicenseType);
        terminalAuthorizeDAO.save(authorizeEntity);
    }
}
