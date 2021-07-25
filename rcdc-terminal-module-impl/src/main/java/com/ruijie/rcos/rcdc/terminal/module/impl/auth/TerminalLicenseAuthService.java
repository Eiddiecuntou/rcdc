package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyAuthConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyAuthSupportConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/16 17:24
 *
 * @author TING
 */
@Service
public class TerminalLicenseAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseAuthService.class);

    @Autowired
    private CbbTerminalLicenseStrategyFactory licenseStrategyFactory;


    /**
     * 授权
     *
     * @param isNewConnection 是否新连接
     * @param basicInfo       终端基本信息
     * @return 授权结果
     */
    public TerminalAuthResult auth(Boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {
        Assert.notNull(isNewConnection, "isNewConnection can not be null");
        Assert.notNull(basicInfo, "basicInfo can not be null");

        TerminalLicenseStrategyConfigDTO strategyConfig = licenseStrategyFactory.getStrategyConfig();
        List<TerminalLicenseStrategyAuthConfigDTO> allocateList = strategyConfig.getAllocateList();
        if (CollectionUtils.isEmpty(allocateList)) {
            LOGGER.error("授权分配策略为空，无法授权，请检查初始化过程");
            return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
        }

        boolean isAuthSuccess = false;
        for (TerminalLicenseStrategyAuthConfigDTO allocateStratesy : allocateList) {
            if (isFitStrategy(allocateStratesy.getLicenseType(), basicInfo.getAuthMode())) {
                LOGGER.info("进行[{}]授权", basicInfo.getAuthMode());
                isAuthSuccess = doAuth(allocateStratesy.getSupportLicenseTypeList(), isNewConnection, basicInfo);
                if (isAuthSuccess) {
                    break;
                }
            }
        }

        if (isAuthSuccess) {
            LOGGER.info("授权成功");
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SUCCESS);
        }

        LOGGER.info("授权失败");
        return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
    }

    /**
     * 回收授权
     *
     * @param terminalId 终端id
     * @param authMode   授权模式
     * @return 是否回收成功
     */
    public boolean recycle(String terminalId, CbbTerminalPlatformEnums authMode) {
        Assert.notNull(authMode, "authMode can not be null");
        Assert.hasText(terminalId, "terminalId can not be blank");
        
        TerminalLicenseStrategyConfigDTO strategyConfig = licenseStrategyFactory.getStrategyConfig();
        List<TerminalLicenseStrategyAuthConfigDTO> recycleList = strategyConfig.getRecycleList();
        if (CollectionUtils.isEmpty(recycleList)) {
            LOGGER.error("授权回收策略为空，无法回收授权，请检查初始化过程");
            return false;
        }

        boolean isSuccess = false;
        for (TerminalLicenseStrategyAuthConfigDTO recycle : recycleList) {
            if (isFitStrategy(recycle.getLicenseType(), authMode)) {
                LOGGER.info("进行[{}]授权回收", authMode);
                isSuccess = doRecycle(recycle.getSupportLicenseTypeList(), terminalId, authMode);
                if (isSuccess) {
                    break;
                }
            }
        }

        return isSuccess;
    }

    private boolean doRecycle(List<TerminalLicenseStrategyAuthSupportConfigDTO> supportLicenseTypeList,
                              String terminalId, CbbTerminalPlatformEnums authMode) {
        for (TerminalLicenseStrategyAuthSupportConfigDTO supportType : supportLicenseTypeList) {
            StrategyService service = licenseStrategyFactory.getService(supportType.getStrategyType());
            boolean isSuccess = service.recycle(terminalId, authMode, supportType.getLicenseTypeList());
            if (isSuccess) {
                LOGGER.info("授权成功，返回");
                return isSuccess;
            }
        }

        return false;
    }

    private boolean doAuth(List<TerminalLicenseStrategyAuthSupportConfigDTO> supportLicenseTypeList,
                           Boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {
        for (TerminalLicenseStrategyAuthSupportConfigDTO supportType : supportLicenseTypeList) {
            StrategyService service = licenseStrategyFactory.getService(supportType.getStrategyType());
            boolean isSuccess = service.allocate(supportType.getLicenseTypeList(), isNewConnection, basicInfo);
            if (isSuccess) {
                LOGGER.info("授权成功，返回");
                return isSuccess;
            }
        }

        return false;
    }

    private boolean isFitStrategy(CbbTerminalLicenseTypeEnums licenseType, CbbTerminalPlatformEnums authMode) {

        if (licenseType.name().equals(authMode.name())) {
            return true;
        }

        return false;
    }


}
