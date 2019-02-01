package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.api.AsyncRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.callback.CbbTerminalSystemUpgradeRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradeService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

/**
 * 
 * Description: 终端升级服务实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月20日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeServiceImpl implements TerminalSystemUpgradeService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeServiceImpl.class);
    
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Autowired
    private CbbTerminalSystemUpgradeRequestCallBack callback;

    @Override
    public void modifyTerminalUpgradePackageVersion(TerminalUpgradeVersionFileInfo versionInfo)
            throws BusinessException {
        Assert.notNull(versionInfo, "terminalUpgradeVersionFileInfo 不能为空");

        TerminalSystemUpgradePackageEntity upgradePackage =
                termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
        if (upgradePackage == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }

        int effectRow = termianlSystemUpgradePackageDAO.modifyTerminalUpgradePackageVersion(versionInfo.getImgName(),
                versionInfo.getPackageType(), versionInfo.getVersion(), upgradePackage.getVersion());
        if (effectRow == 0) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST);
        }
    }

    @Override
    public void addTerminalUpgradePackage(TerminalUpgradeVersionFileInfo versionInfo) throws BusinessException {
        Assert.notNull(versionInfo, "terminalUpgradeVersionFileInfo 不能为空");

        TerminalSystemUpgradePackageEntity upgradePackage =
                termianlSystemUpgradePackageDAO.findFirstByPackageType(versionInfo.getPackageType());
        if (upgradePackage != null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_HAS_EXIST);
        }
        TerminalSystemUpgradePackageEntity entity = buildTerminalSystemUpgradePackageEntity(versionInfo);
        termianlSystemUpgradePackageDAO.save(entity);
    }

    private TerminalSystemUpgradePackageEntity buildTerminalSystemUpgradePackageEntity(
            TerminalUpgradeVersionFileInfo versionInfo) {
        TerminalSystemUpgradePackageEntity entity = new TerminalSystemUpgradePackageEntity();
        entity.setImgName(versionInfo.getImgName());
        entity.setPackageType(versionInfo.getPackageType());
        entity.setPackageVersion(versionInfo.getVersion());
        entity.setUploadTime(new Date());
        entity.setId(UUID.randomUUID());
        return entity;
    }

    @Override
    public List<TerminalSystemUpgradeInfo> readSystemUpgradeStateFromFile() throws BusinessException {
        //读取升级成功文件夹，通过文件名称获取mac即终端id
        File upgradeSuccessDir = new File(Constants.TERMINAL_UPGRADE_END_SATTUS_FILE_PATH);
        if (!upgradeSuccessDir.isDirectory()) {
            LOGGER.error("terminal upgrade success status directory not exist, file path: {}", Constants.TERMINAL_UPGRADE_END_SATTUS_FILE_PATH);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPGRADE_SUCCESS_STATUS_DIRECTORY_NOT_EXIST);
        }
        
        List<TerminalSystemUpgradeInfo> upgradeInfoList = new ArrayList<>();
        String[] fileNameArr = upgradeSuccessDir.list();
        for(String fileName : fileNameArr) {
            String fileNameWithoutSuffix = getFileNameWithoutSuffix(fileName);
            TerminalSystemUpgradeInfo upgradeInfo = new TerminalSystemUpgradeInfo();
            upgradeInfo.setTerminalId(fileNameWithoutSuffix);
            upgradeInfo.setState(CbbSystemUpgradeStateEnums.SUCCESS);
            upgradeInfoList.add(upgradeInfo);
        }
        
        return upgradeInfoList;
    }
    
    private String getFileNameWithoutSuffix(String fileName){
        int index = fileName.lastIndexOf(Constants.FILE_SUFFIX_DOT);
        if (index == -1) {
            return fileName;
        }
        
        return fileName.substring(0, index);
    }
    
    @Override
    public void systemUpgrade(String terminalId, TerminalSystemUpgradeMsg upgradeMsg) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(upgradeMsg, "systemUpgradeMsg 不能为空");
        
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.UPGRADE_TERMINAL_SYSTEM.getName(),
                upgradeMsg);
        sender.asyncRequest(message, new AsyncRequestCallBack(terminalId, callback));
    }

}
