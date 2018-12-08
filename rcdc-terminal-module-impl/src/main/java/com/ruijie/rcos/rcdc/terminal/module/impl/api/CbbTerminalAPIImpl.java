package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbSearchTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNetworkRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description: 终端基本信息维护
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalAPIImpl implements CbbTerminalAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalAPIImpl.class);

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    private static final BeanCopier BEAN_COPIER = BeanCopier.create(TerminalEntity.class,
            CbbTerminalBasicInfoDTO.class, false);

    @Override
    public CbbTerminalBasicInfoDTO findBasicInfoByTerminalId(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalIdRequest不能为null");
        TerminalEntity basicInfoEntity =
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
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalBasicInfoEntitiesByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return basicInfoEntity.getVersion();
    }

    @Override
    public DefaultPageResponse<CbbTerminalBasicInfoDTO> search(CbbSearchTerminalRequest request) {
        Assert.notNull(request,"CbbSearchTerminalRequest不能为null");
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getLimit());
        SearchTerminalSpecification specification = new SearchTerminalSpecification(request.getKeyword());
        Page<TerminalEntity> page = basicInfoDAO.findAll(specification,pageable);
        long total = page.getTotalElements();
        DefaultPageResponse response = new DefaultPageResponse();
        response.setTotal(total);
        if(total == 0){
            response.setItemArr(new Object[]{});
            return response;
        }
        List<TerminalEntity> entityList = page.getContent();
        List<CbbTerminalBasicInfoDTO> dtoList = new ArrayList<>(entityList.size());
        entityList.forEach((item->{
            CbbTerminalBasicInfoDTO dto = new CbbTerminalBasicInfoDTO();
            BEAN_COPIER.copy(item,dto,null);
            dtoList.add(dto);
        }));

        response.setItemArr(dtoList.toArray());

        return response;
    }
}
