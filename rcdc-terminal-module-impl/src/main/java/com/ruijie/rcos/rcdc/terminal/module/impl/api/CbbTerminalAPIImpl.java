package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNetworkRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.CbbTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: 终端基本信息维护
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalAPIImpl implements CbbTerminalAPI {

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    private static final BeanCopier BEAN_COPIER = BeanCopier.create(CbbTerminalEntity.class,
            CbbTerminalBasicInfoDTO.class, false);

    @Override
    public CbbTerminalBasicInfoDTO findBasicInfoByTerminalId(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalIdRequest不能为null");
        CbbTerminalEntity basicInfoEntity =
                basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(request.getTerminalId());
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        CbbTerminalBasicInfoDTO basicInfoDTO = new CbbTerminalBasicInfoDTO();
        BEAN_COPIER.copy(basicInfoEntity, basicInfoDTO, null);

        return basicInfoDTO;
    }

    @Override
    public DefaultResponse delete(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalIdRequest不能为null");
        String terminalId = request.getTerminalId();
        int version = getVersion(terminalId);
        int effectRow = basicInfoDAO.deleteByTerminalId(terminalId, version);
        if (effectRow == 0) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse modifyTerminalName(CbbTerminalNameRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalNameRequest不能为null");
        //先发送终端名称给shine，后修改数据库
        String terminalId = request.getTerminalId();
        basicInfoService.modifyTerminalName(terminalId, request.getTerminalName());
        int version = getVersion(terminalId);
        int effectRow = basicInfoDAO.modifyTerminalName(terminalId, version, request.getTerminalName());
        if (effectRow == 0) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse modifyTerminalNetworkConfig(CbbTerminalNetworkRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalNetworkRequest不能为null");
        //先发送网络配置消息给shine，后修改数据库
        String terminalId = request.getTerminalId();
        ShineNetworkConfig shineNetworkConfig = new ShineNetworkConfig();
        BeanCopier beanCopier = BeanCopier.create(request.getClass(), shineNetworkConfig.getClass(), false);
        beanCopier.copy(request, shineNetworkConfig, null);
        shineNetworkConfig.setGetDnsMode(request.getGetDnsMode().ordinal());
        shineNetworkConfig.setGetIpMode(request.getGetIpMode().ordinal());
        basicInfoService.modifyTerminalNetworkConfig(request.getTerminalId(), shineNetworkConfig);

        int version = getVersion(terminalId);
        int effectRow = basicInfoDAO.modifyTerminalNetworkConfig(terminalId, version, request);
        if(effectRow == 0){
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return DefaultResponse.Builder.success();
    }

    private Integer getVersion(String terminalId) throws BusinessException {
        CbbTerminalEntity basicInfoEntity = basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return basicInfoEntity.getVersion();
    }
}
