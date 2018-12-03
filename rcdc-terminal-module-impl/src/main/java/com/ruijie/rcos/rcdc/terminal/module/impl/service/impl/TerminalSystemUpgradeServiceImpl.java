package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.callback.AsyncRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.callback.CbbTerminalSystemUpgradeRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TermianlSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TermianlSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

/**
 * 
 * Description: 终端升级服务实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author "nt"
 */
@Service
public class TerminalSystemUpgradeServiceImpl implements TerminalSystemUpgradeService {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TermianlSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;
    
    @Autowired
    private CbbTerminalSystemUpgradeRequestCallBack callback;

    @Override
    public void modifyTerminalUpgradePackageVersion(TerminalUpgradeVersionFileInfo versionInfo)
            throws BusinessException {
        Assert.notNull(versionInfo, "terminalUpgradeVersionFileInfo 不能为空");

        TermianlSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO
                .findTermianlSystemUpgradePackageByPackageType(versionInfo.getPackageType());
        if (upgradePackage == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }

        int effectRow = termianlSystemUpgradePackageDAO.modifyTerminalUpgradePackageVersion(
                versionInfo.getPackageType(), versionInfo.getInternalVersion(), versionInfo.getExternalVersion());
        if (effectRow == 0) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }
    }

    @Override
    public void addTerminalUpgradePackage(TerminalUpgradeVersionFileInfo versionInfo)
            throws BusinessException {
        Assert.notNull(versionInfo, "terminalUpgradeVersionFileInfo 不能为空");

        TermianlSystemUpgradePackageEntity upgradePackage = termianlSystemUpgradePackageDAO
                .findTermianlSystemUpgradePackageByPackageType(versionInfo.getPackageType());
        if (upgradePackage != null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_HAS_EXIST);
        }
        TermianlSystemUpgradePackageEntity entity = buildTerminalSystemUpgradePackageEntity(versionInfo);
        termianlSystemUpgradePackageDAO.save(entity);
    }

    private TermianlSystemUpgradePackageEntity buildTerminalSystemUpgradePackageEntity(
            TerminalUpgradeVersionFileInfo versionInfo) {
        TermianlSystemUpgradePackageEntity entity = new TermianlSystemUpgradePackageEntity();
        entity.setName(versionInfo.getPackageName());
        entity.setPackageType(versionInfo.getPackageType());
        entity.setExternalVersion(versionInfo.getExternalVersion());
        entity.setInternalVersion(versionInfo.getInternalVersion());
        //TODO
        entity.setStorePath(Constants.TERMINAL_UPGRADE_ISO_PATH_IDV);
        entity.setUploadTime(new Date());
        entity.setId(UUID.randomUUID());
        return entity;
    }

    @Override
    public List<TerminalSystemUpgradeInfo> readSystemUpgradeStateFromFile() {
        // TODO FIXME 读取升级文件中的升级信息
        return null;
    }

    @Override
    public void systemUpgrade(String terminalId, TerminalSystemUpgradeMsg upgradeMsg) throws BusinessException {
        Assert.hasLength(terminalId, "terminalId 不能为空");
        Assert.notNull(upgradeMsg, "systemUpgradeMsg 不能为空");

        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.UPGRADE_TERMINAL_SYSTEM.getName(),
                upgradeMsg);
        sender.asyncRequest(message,
                new AsyncRequestCallBack(terminalId, callback));
    }

}
