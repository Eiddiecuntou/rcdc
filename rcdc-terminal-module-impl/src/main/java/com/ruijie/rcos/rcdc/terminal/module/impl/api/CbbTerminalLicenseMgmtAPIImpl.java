package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLicenseMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbIDVTerminalLicenseNumDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

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
    TerminalLicenseService terminalLicenseService;


    @Override
    public void setIDVTerminalLicenseNum(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        terminalLicenseService.updateIDVTerminalLicenseNum(licenseNum);
    }

    @Override
    public CbbIDVTerminalLicenseNumDTO getIDVTerminalLicenseNum() {
        Integer licenseNum = terminalLicenseService.getIDVTerminalLicenseNum();
        Integer usedNum = terminalLicenseService.getIDVUsedNum();

        CbbIDVTerminalLicenseNumDTO licenseNumDTO = new CbbIDVTerminalLicenseNumDTO();
        licenseNumDTO.setLicenseNum(licenseNum);
        licenseNumDTO.setUsedNum(usedNum);

        LOGGER.info("idv终端授权数量：{}", JSON.toJSONString(licenseNumDTO, SerializerFeature.PrettyFormat));
        return licenseNumDTO;
    }
}
