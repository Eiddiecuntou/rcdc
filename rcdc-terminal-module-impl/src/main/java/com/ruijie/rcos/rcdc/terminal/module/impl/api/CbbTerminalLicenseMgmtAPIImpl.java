package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLicenseMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseNumDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory.CbbTerminalLicenseFactoryProvider;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

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

    @Override
    public void setTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, Integer licenseNum) throws BusinessException {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        licenseFactoryProvider.getService(licenseType).updateTerminalLicenseNum(licenseNum);
    }

    @Override
    public CbbTerminalLicenseNumDTO getTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType) {
        Integer licenseNum = licenseFactoryProvider.getService(licenseType).getTerminalLicenseNum();
        Integer usedNum = licenseFactoryProvider.getService(licenseType).getUsedNum();

        CbbTerminalLicenseNumDTO licenseNumDTO = new CbbTerminalLicenseNumDTO();
        licenseNumDTO.setLicenseNum(licenseNum);
        licenseNumDTO.setUsedNum(usedNum);

        LOGGER.info("idv终端授权数量：{}", JSON.toJSONString(licenseNumDTO, SerializerFeature.PrettyFormat));
        return licenseNumDTO;
    }
}
