package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLicenseMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseNumDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseAuthService;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory.CbbTerminalLicenseFactoryProvider;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Description: 终端授权管理apiImpl
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/17 9:51 下午
 *
 * @author zhouhuan
 */
public class CbbTerminalLicenseMgmtAPIImpl implements CbbTerminalLicenseMgmtAPI {

    public static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalLicenseMgmtAPIImpl.class);

    @Autowired
    CbbTerminalLicenseFactoryProvider licenseFactoryProvider;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalAuthHelper terminalAuthHelper;

    @Autowired
    private TerminalLicenseAuthService terminalLicenseAuthService;

    @Override
    public void setTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, List<CbbTerminalLicenseInfoDTO> licenseInfoList)
            throws BusinessException {
        Assert.notNull(licenseType, "licenseType can not be null");
        Assert.notNull(licenseInfoList, "licenseInfoList can not be null");
        licenseFactoryProvider.getService(licenseType).updateTerminalLicenseNum(licenseInfoList);
    }

    @Override
    public CbbTerminalLicenseNumDTO getTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, @Nullable List<String> licenseCodeList) {
        Assert.notNull(licenseType, "licenseType can not be null");

        Integer licenseNum;
        if (CollectionUtils.isEmpty(licenseCodeList)) {
            licenseNum = licenseFactoryProvider.getService(licenseType).getAllTerminalLicenseNum();
        } else {
            licenseNum = licenseFactoryProvider.getService(licenseType).getTerminalLicenseNum(licenseCodeList);
        }

        Integer usedNum = licenseFactoryProvider.getService(licenseType).getUsedNum();

        List<CbbTerminalLicenseInfoDTO> licenseInfoList = licenseFactoryProvider.getService(licenseType)
                .getTerminalLicenseInfo(CollectionUtils.isEmpty(licenseCodeList) ? Lists.newArrayList() : licenseCodeList);
        setLicenseUsedNum(licenseInfoList, usedNum);

        CbbTerminalLicenseNumDTO licenseNumDTO = new CbbTerminalLicenseNumDTO();
        licenseNumDTO.setLicenseType(licenseType);
        licenseNumDTO.setLicenseNum(licenseNum);
        licenseNumDTO.setUsedNum(usedNum);
        licenseNumDTO.setLicenseInfoList(licenseInfoList);

        LOGGER.info("终端授权数量：{}", JSON.toJSONString(licenseNumDTO, SerializerFeature.PrettyFormat));
        return licenseNumDTO;
    }

    private void setLicenseUsedNum(List<CbbTerminalLicenseInfoDTO> licenseInfoList, Integer usedNum) {
        if (CollectionUtils.isEmpty(licenseInfoList)) {
            LOGGER.info("证书信息为空，无需处理");
            return;
        }

        int leftNum = usedNum;
        for (CbbTerminalLicenseInfoDTO licenseInfoDTO : licenseInfoList) {
            if (licenseInfoDTO.getTotalNum() >= leftNum) {
                LOGGER.info("证书数量大于等于使用数量,{}", JSON.toJSONString(licenseInfoDTO));
                licenseInfoDTO.setUsedNum(leftNum);
                break;
            }

            LOGGER.info("证书数量小于等于使用数量,{}", JSON.toJSONString(licenseInfoDTO));
            licenseInfoDTO.setUsedNum(licenseInfoDTO.getTotalNum());
            leftNum = leftNum - licenseInfoDTO.getTotalNum();
        }

    }

    @Override
    public Boolean checkEnableAuthTerminal(CbbTerminalPlatformEnums authMode) throws BusinessException {
        Assert.notNull(authMode, "authMode can not be null");

        return terminalLicenseAuthService.checkEnableAuth(authMode);
    }

    @Override
    public void cancelTerminalAuth(String terminalId) throws BusinessException {
        Assert.notNull(terminalId, "terminalId can not be null");

        LOGGER.info("收到取消终端:{}授权请求", terminalId);
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (terminalEntity == null) {
            LOGGER.error("不存在终端:{}信息，无需取消授权", terminalId);
            return;
        }

        if (Boolean.FALSE.equals(terminalEntity.getAuthed())) {
            LOGGER.info("终端:{}未授权，无需取消授权", terminalId);
            return;
        }

        cancelAuth(terminalEntity);
    }

    private void cancelAuth(TerminalEntity terminalEntity) throws BusinessException {
        try {
            //更新数据库
            terminalEntity.setAuthed(Boolean.FALSE);
            basicInfoDAO.save(terminalEntity);
        } catch (Exception e) {
            LOGGER.error("保存终端信息失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_CANCEL_AUTH_FAIL, e);
        }

        //处理终端授权扣除逻辑
        terminalAuthHelper.processDecreaseTerminalLicense(terminalEntity.getTerminalId(), terminalEntity.getAuthMode(), Boolean.TRUE);
    }
}
