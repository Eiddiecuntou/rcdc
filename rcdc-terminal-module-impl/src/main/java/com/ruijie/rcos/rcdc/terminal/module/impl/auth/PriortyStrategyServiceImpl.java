package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.alibaba.fastjson.JSON;
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
 * Description: 优先策略
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:47
 *
 * @author TING
 */
@Service("priorityStrategyService")
public class PriortyStrategyServiceImpl extends AbstractStrategyServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriortyStrategyServiceImpl.class);

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;


    @Override
    public boolean checkAllocate(List<CbbTerminalLicenseTypeEnums> licenseTypeList, CbbTerminalPlatformEnums authMode) {
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");
        Assert.notNull(authMode, "authMode can not be null");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("优先授权策略的授权证书类型为空，不符合预期，返回不允许授权");
            return false;
        }

        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
            boolean enableAuth = licenseService.checkEnableAuth(authMode);
            if (enableAuth) {
                LOGGER.info("校验[{}]是否允许授权完成，结果为允许，授权方式[{}]", authMode, licenseType);
                return true;
            }
        }

        LOGGER.info("校验[{}]是否允许授权完成，结果为不允许，授权方式[{}]", authMode, JSON.toJSONString(licenseTypeList));
        return false;
    }

    @Override
    public boolean allocate(List<CbbTerminalLicenseTypeEnums> licenseTypeList, Boolean isNewConnection, CbbShineTerminalBasicInfo basicInfoDTO) {
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");
        Assert.notNull(isNewConnection, "isNewConnection can not be null");
        Assert.notNull(basicInfoDTO, "basicInfoDTO can not be null");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("优先授权策略的授权证书类型为空，不符合预期，返回授权失败");
            return false;
        }

        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
            boolean isAuthed = licenseService.auth(basicInfoDTO.getTerminalId(), isNewConnection, basicInfoDTO);
            if (isAuthed) {
                LOGGER.info("终端[{}]授权成功，授权方式[{}]", basicInfoDTO.getTerminalId(), licenseType);
                saveTerminalAuthorize(licenseType.name(), basicInfoDTO);
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean recycle(String terminalId, CbbTerminalPlatformEnums authMode, List<CbbTerminalLicenseTypeEnums> licenseTypeList) {
        Assert.notNull(licenseTypeList, "licenseTypeList can not be null");
        Assert.notNull(authMode, "authMode can not be null");
        Assert.hasText(terminalId, "terminalId can not be blank");

        if (CollectionUtils.isEmpty(licenseTypeList)) {
            LOGGER.info("优先授权回收策略的授权证书类型为空，不符合预期，返回授权回收失败");
            return false;
        }

        for (CbbTerminalLicenseTypeEnums licenseType : licenseTypeList) {
            TerminalLicenseService licenseService = getTerminalLicenseService(licenseType);
            int count = terminalAuthorizeDAO.countByLicenseTypeAndAuthMode(licenseType.name(), authMode);
            if (count > 0) {
                licenseService.decreaseCacheLicenseUsedNum();
                LOGGER.info("终端授权回收成功，回收授权[{}]", licenseType);

                // 如果当前终端的授权记录不是预期回收的，则将修改一个为删除终端的授权类型
                TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
                if (!authorizeEntity.getLicenseType().equals(licenseType.name())) {
                    LOGGER.info("终端的授权记录不是预期回收的， 修改一个授权类型[{}]为删除终端的授权类型[{}]", licenseType, authorizeEntity.getLicenseType());
                    convertAuthLicenseType(authMode, licenseType, authorizeEntity.getLicenseType());
                }

                terminalAuthorizeDAO.deleteByTerminalId(terminalId);

                return true;
            }
        }

        return false;
    }

    private void convertAuthLicenseType(CbbTerminalPlatformEnums authMode, CbbTerminalLicenseTypeEnums licenseType, String updateLicenseType) {
        List<TerminalAuthorizeEntity> authorizeEntityList = terminalAuthorizeDAO.findByLicenseTypeAndAuthMode(licenseType.name(), authMode);

        TerminalAuthorizeEntity authorizeEntity = authorizeEntityList.get(0);
        authorizeEntity.setLicenseType(updateLicenseType);
        terminalAuthorizeDAO.save(authorizeEntity);
    }
}
