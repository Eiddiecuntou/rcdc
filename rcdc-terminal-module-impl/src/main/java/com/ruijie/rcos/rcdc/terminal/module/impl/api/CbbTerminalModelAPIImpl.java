package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalModelAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPlatformRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalProductIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbItemArrResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalModelService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
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
    public CbbItemArrResponse<CbbTerminalModelDTO> listTerminalModel(CbbTerminalPlatformRequest request) {
        Assert.notNull(request, "request can not be null");

        CbbTerminalModelDTO[] terminalModelArr = terminalModelService.queryTerminalModelByPlatform(request.getPlatformArr());
        return new CbbItemArrResponse<>(terminalModelArr);
    }

    @Override
    public DtoResponse<CbbTerminalModelDTO> queryByProductId(CbbTerminalProductIdRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbTerminalModelDTO terminalModelDTO = terminalModelService.queryByProductId(request.getProductId());
        return DtoResponse.success(terminalModelDTO);
    }

    @Override
    public DtoResponse<List<String>> listTerminalOsType(CbbTerminalPlatformRequest request) {
        Assert.notNull(request, "request can not be null");

        List<String> osTypeList = terminalModelService.queryTerminalOsTypeByPlatform(request.getPlatformArr());
        return DtoResponse.success(osTypeList);
    }
}
