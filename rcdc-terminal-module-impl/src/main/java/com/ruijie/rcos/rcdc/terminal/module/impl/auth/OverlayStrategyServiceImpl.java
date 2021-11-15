package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description: 叠加策略
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:46
 *
 * @author TING
 */
@Service("overlayStrategyService")
public class OverlayStrategyServiceImpl extends AbstractStrategyServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayStrategyServiceImpl.class);

    private static final String LICENSE_TYPE_SPLIT = ",";

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Override
    public boolean allocate(List<CbbTerminalLicenseTypeEnums> licenseTypeList, Boolean isNewConnection, CbbShineTerminalBasicInfo basicInfoDTO) {
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");
        Assert.notNull(isNewConnection, "isNewConnection can not be null");
        Assert.notNull(basicInfoDTO, "basicInfoDTO can not be null");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("授权策略的授权证书类型为空，不符合预期，返回授权失败");
            return false;
        }

        List<CbbTerminalLicenseTypeEnums> authedList = Lists.newArrayList();
        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
            boolean isAuthed = licenseService.auth(basicInfoDTO.getTerminalId(), isNewConnection, basicInfoDTO);
            if (isAuthed) {
                authedList.add(licenseType);
            } else {
                break;
            }
        }
        if (authedList.size() == licenseTypeList.size()) {
            LOGGER.info("终端[{}]叠加授权成功", basicInfoDTO.getTerminalId());
            saveTerminalAuthorize(buildQueryLicenseType(licenseTypeList), basicInfoDTO);
            return true;
        }

        // TODO 考虑优化
        LOGGER.info("终端[{}]叠加授权失败", basicInfoDTO.getTerminalId());
        for (CbbTerminalLicenseTypeEnums licenseType : authedList) {
            LOGGER.info("叠加授权失败，减去已用[{}]的授权数", licenseType);
            getTerminalLicenseService(licenseType).decreaseCacheLicenseUsedNum();
        }


        return false;
    }

    @Override
    public boolean checkAllocate(List<CbbTerminalLicenseTypeEnums> licenseTypeList, CbbTerminalPlatformEnums authMode) {
        Assert.notNull(authMode, "authMode can not be null");
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("授权策略的授权证书类型为空，不符合预期，返回授权失败");
            return false;
        }

        List<CbbTerminalLicenseTypeEnums> authedList = Lists.newArrayList();
        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
            boolean enableAuth = licenseService.checkEnableAuth(authMode);
            if (enableAuth) {
                authedList.add(licenseType);
            } else {
                break;
            }
        }

        if (authedList.size() == licenseTypeList.size()) {
            LOGGER.info("[{}]允许叠加授权", authMode);
            return true;
        }

        LOGGER.info("[{}]不允许叠加授权", authMode);
        return false;
    }

    @Override
    public boolean recycle(String terminalId, CbbTerminalPlatformEnums authMode, List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");
        Assert.notNull(authMode, "authMode can not be null");
        Assert.hasText(terminalId, "terminalId can not be blank");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("授权策略的授权证书类型为空，不符合预期，返回授权回收失败");
            return false;
        }

        String licenseTypeStr = buildQueryLicenseType(licenseTypeList);
        int count = terminalAuthorizeDAO.countByLicenseTypeAndAuthMode(licenseTypeStr, authMode);
        if (count > 0) {
            for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
                TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
                licenseService.decreaseCacheLicenseUsedNum();
            }
            LOGGER.info("终端授权回收成功");
            // 如果当前终端的授权记录不是预期回收的，则将修改一个为删除终端的授权类型
            TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
            if (authorizeEntity != null && !authorizeEntity.getLicenseType().equals(licenseTypeStr)) {
                LOGGER.info("终端的授权记录不是预期回收的， 修改一个授权类型[{}]为删除终端的授权类型[{}]", licenseTypeStr, authorizeEntity.getLicenseType());
                convertAuthLicenseType(authMode, licenseTypeStr, authorizeEntity.getLicenseType());
            }
            // 删除授权记录
            terminalAuthorizeDAO.deleteByTerminalId(terminalId);

            return true;
        }

        return false;
    }

    private String buildQueryLicenseType(List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        StringBuilder licenseTypeStr  = new StringBuilder();
        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            licenseTypeStr.append(LICENSE_TYPE_SPLIT + licenseType.name());
        }

        return licenseTypeStr.toString().substring(1, licenseTypeStr.length());
    }

    private void convertAuthLicenseType(CbbTerminalPlatformEnums authMode, String licenseType, String updateLicenseType) {
        List<TerminalAuthorizeEntity> authorizeEntityList = terminalAuthorizeDAO.findByLicenseTypeAndAuthMode(licenseType, authMode);

        TerminalAuthorizeEntity authorizeEntity = authorizeEntityList.get(0);
        authorizeEntity.setLicenseType(updateLicenseType);
        terminalAuthorizeDAO.save(authorizeEntity);
    }
}
