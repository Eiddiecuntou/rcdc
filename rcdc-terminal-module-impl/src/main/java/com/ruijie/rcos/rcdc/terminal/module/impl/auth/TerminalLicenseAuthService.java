package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyAuthConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyAuthSupportConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TerminalLicenseStrategyConfigDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
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
     *  校验是否能够授权
     *
     * @param authMode 工作模式
     * @return 是否能够授权
     * @throws BusinessException 业务异常
     */
    public boolean checkEnableAuth(CbbTerminalPlatformEnums authMode) throws BusinessException {
        Assert.notNull(authMode, "authMode can not be null");

        TerminalLicenseStrategyConfigDTO strategyConfig = licenseStrategyFactory.getStrategyConfig();
        List<TerminalLicenseStrategyAuthConfigDTO> allocateList = strategyConfig.getAllocateList();

        for (TerminalLicenseStrategyAuthConfigDTO allocateStrategy : allocateList) {
            if (isFitStrategy(allocateStrategy.getLicenseType(), authMode)) {
                LOGGER.info("进行[{}]是否允许授权校验", authMode);
                boolean enableAuth = checkEnableAuth(allocateStrategy.getSupportLicenseTypeList(), authMode);
                if (enableAuth) {
                    LOGGER.info("进行[{}]是否允许授权校验,允许授权", authMode);
                    return true;
                }
            }
        }

        LOGGER.info("进行[{}]是否允许授权校验,不允许授权", authMode);
        return false;
    }

    /**
     * 授权
     *
     * @param isNewConnection 是否新连接
     * @param basicInfo       终端基本信息
     * @return 授权结果
     * @throws BusinessException 业务异常
     */
    public TerminalAuthResult auth(Boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) throws BusinessException {
        Assert.notNull(isNewConnection, "isNewConnection can not be null");
        Assert.notNull(basicInfo, "basicInfo can not be null");

        TerminalLicenseStrategyConfigDTO strategyConfig = licenseStrategyFactory.getStrategyConfig();
        List<TerminalLicenseStrategyAuthConfigDTO> allocateList = strategyConfig.getAllocateList();

        for (TerminalLicenseStrategyAuthConfigDTO allocateStrategy : allocateList) {
            if (isFitStrategy(allocateStrategy.getLicenseType(), basicInfo.getAuthMode())) {
                LOGGER.info("终端[{}]进行[{}]授权", basicInfo.getTerminalId(), basicInfo.getAuthMode());
                boolean isAuthSuccess = doAuth(allocateStrategy.getSupportLicenseTypeList(), isNewConnection, basicInfo);
                if (isAuthSuccess) {
                    LOGGER.info("终端[{}]进行[{}]授权成功", basicInfo.getTerminalId(), basicInfo.getAuthMode());
                    return new TerminalAuthResult(true, TerminalAuthResultEnums.SUCCESS);
                }
            }
        }

        LOGGER.info("终端[{}]进行[{}]授权失败", basicInfo.getTerminalId(), basicInfo.getAuthMode());
        return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
    }

    /**
     * 回收授权
     *
     * @param terminalId 终端id
     * @param authMode   授权模式
     * @return 是否回收成功
     * @throws BusinessException 业务异常
     */
    public boolean recycle(String terminalId, CbbTerminalPlatformEnums authMode) throws BusinessException {
        Assert.notNull(authMode, "authMode can not be null");
        Assert.hasText(terminalId, "terminalId can not be blank");

        TerminalLicenseStrategyConfigDTO strategyConfig = licenseStrategyFactory.getStrategyConfig();
        List<TerminalLicenseStrategyAuthConfigDTO> recycleList = strategyConfig.getRecycleList();
        if (CollectionUtils.isEmpty(recycleList)) {
            LOGGER.error("授权回收策略为空，无法回收授权，请检查初始化过程");
            return false;
        }

        for (TerminalLicenseStrategyAuthConfigDTO recycle : recycleList) {
            if (isFitStrategy(recycle.getLicenseType(), authMode)) {
                LOGGER.info("终端[{}]进行[{}]授权回收", terminalId, authMode);
                boolean isSuccess = doRecycle(recycle.getSupportLicenseTypeList(), terminalId, authMode);
                if (isSuccess) {
                    LOGGER.info("终端[{}]进行[{}]授权回收成功", terminalId, authMode);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean doRecycle(List<TerminalLicenseStrategyAuthSupportConfigDTO> supportLicenseTypeList,
                              String terminalId, CbbTerminalPlatformEnums authMode) {

        return supportLicenseTypeList.stream().anyMatch(supportType -> {
            LOGGER.info("终端[{}][{}]进行授权回收, 授权回收使用策略[{}]", terminalId, authMode, JSON.toJSONString(supportType));
            StrategyService service = licenseStrategyFactory.getService(supportType.getStrategyType());
            return service.recycle(terminalId, authMode, supportType.getLicenseTypeList());
        });

    }

    private boolean doAuth(List<TerminalLicenseStrategyAuthSupportConfigDTO> supportLicenseTypeList,
                           Boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {

        return supportLicenseTypeList.stream().anyMatch(supportType -> {
            LOGGER.info("终端[{}][{}]进行授权, 授权使用策略[{}]", basicInfo.getTerminalId(), basicInfo.getAuthMode(), JSON.toJSONString(supportType));
            StrategyService service = licenseStrategyFactory.getService(supportType.getStrategyType());
            return service.allocate(supportType.getLicenseTypeList(), isNewConnection, basicInfo);
        });
    }

    private boolean checkEnableAuth(List<TerminalLicenseStrategyAuthSupportConfigDTO> supportLicenseTypeList,
                           CbbTerminalPlatformEnums authMode) {

        return supportLicenseTypeList.stream().anyMatch(supportType -> {
            LOGGER.info("进行是否允许【{}】授权校验, 授权使用策略[{}]", authMode, JSON.toJSONString(supportType));
            StrategyService service = licenseStrategyFactory.getService(supportType.getStrategyType());
            return service.checkAllocate(supportType.getLicenseTypeList(), authMode);
        });
    }

    private boolean isFitStrategy(CbbTerminalLicenseTypeEnums licenseType, CbbTerminalPlatformEnums authMode) {
        return licenseType.name().equals(authMode.name());
    }




}
