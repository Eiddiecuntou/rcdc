package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalModelAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalModelService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @author nt
 */
public class CbbTerminalModelAPIImpl implements CbbTerminalModelAPI {

    @Autowired
    private TerminalModelService terminalModelService;

    @Override
    public CbbTerminalModelDTO[] listTerminalModel(CbbTerminalPlatformEnums[] platformArr) {
        Assert.notNull(platformArr, "platformArr can not be null");

        CbbTerminalModelDTO[] terminalModelArr = terminalModelService.queryTerminalModelByPlatform(platformArr);
        return terminalModelArr;
    }

    @Override
    public CbbTerminalModelDTO findByProductIdAndPlatform(String productId, CbbTerminalPlatformEnums platformEnums) throws BusinessException {
        Assert.notNull(productId, "productId can not be null");
        Assert.notNull(platformEnums, "platformEnums can not be null");

        CbbTerminalModelDTO terminalModelDTO = terminalModelService.queryByProductIdAndPlatform(productId, platformEnums);
        return terminalModelDTO;
    }

    @Override
    public List<String> listTerminalOsType(CbbTerminalPlatformEnums[] platformArr) {
        Assert.notNull(platformArr, "platformArr can not be null");

        List<String> osTypeList = terminalModelService.queryTerminalOsTypeByPlatform(platformArr);
        return osTypeList;
    }
}
