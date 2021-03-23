package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
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

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalAuthHelper terminalAuthHelper;

    @Override
    public void setTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, Integer licenseNum) throws BusinessException {
        Assert.notNull(licenseType, "licenseType can not be null");
        Assert.notNull(licenseNum, "licenseNum can not be null");
        licenseFactoryProvider.getService(licenseType).updateTerminalLicenseNum(licenseNum);
    }

    @Override
    public CbbTerminalLicenseNumDTO getTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(licenseType, "licenseType can not be null");
        Integer licenseNum = licenseFactoryProvider.getService(licenseType).getTerminalLicenseNum();
        Integer usedNum = licenseFactoryProvider.getService(licenseType).getUsedNum();

        CbbTerminalLicenseNumDTO licenseNumDTO = new CbbTerminalLicenseNumDTO();
        licenseNumDTO.setLicenseType(licenseType);
        licenseNumDTO.setLicenseNum(licenseNum);
        licenseNumDTO.setUsedNum(usedNum);

        LOGGER.info("终端授权数量：{}", JSON.toJSONString(licenseNumDTO, SerializerFeature.PrettyFormat));
        return licenseNumDTO;
    }

    @Override
    public void cancelTerminalAuth(String terminalId) {
        Assert.notNull(terminalId, "terminalId can not be null");
        LOGGER.info("收到取消终端:{}授权请求", terminalId);
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (terminalEntity == null) {
            LOGGER.error("不存在终端:{}信息，无需取消授权", terminalId);
            return;
        }

        if (terminalEntity.getAuthed() == Boolean.FALSE) {
            LOGGER.info("终端:{}未授权，无需取消授权", terminalId);
            return;
        }
        //处理终端授权扣除逻辑
        terminalAuthHelper.processDecreaseTerminalLicense(terminalId, terminalEntity.getPlatform(), terminalEntity.getAuthed());

        //更新数据库
        terminalEntity.setAuthed(Boolean.FALSE);
        basicInfoDAO.save(terminalEntity);
    }
}
