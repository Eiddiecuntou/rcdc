package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.CbbTerminalModelDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalModelDriverDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalModelDriverEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalModelService;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/12/10
 *
 * @author nt
 */
@Service
public class TerminalModelServiceImpl implements TerminalModelService {

    @Autowired
    private TerminalModelDriverDAO terminalModelDriverDAO;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Override
    public CbbTerminalModelDTO[] queryTerminalModelByPlatform(CbbTerminalPlatformEnums platform) {
        Assert.notNull(platform, "platform can not be null");

        List<TerminalModelDriverEntity> entityList = terminalModelDriverDAO.findByPlatform(platform);
        if (CollectionUtils.isEmpty(entityList)) {
            return new CbbTerminalModelDTO[0];
        }
        Set<String> productModelSet = new HashSet<>();
        return entityList.stream().map(entity -> convertToDTO(entity, productModelSet)).filter(Objects::nonNull).toArray(CbbTerminalModelDTO[]::new);
    }

    private static CbbTerminalModelDTO convertToDTO(TerminalModelDriverEntity entity, Set<String> productModelSet) {
        if (StringUtils.isBlank(entity.getProductModel())) {
            // 产品型号为空返回null, 过滤掉
            return null;
        }

        if (productModelSet.contains(entity.getProductModel().trim())) {
            // 产品型号重复了返回null, 过滤掉
            return null;
        }

        CbbTerminalModelDTO terminalModelDTO = new CbbTerminalModelDTO();
        BeanUtils.copyProperties(entity, terminalModelDTO);
        productModelSet.add(entity.getProductModel().trim());
        return terminalModelDTO;
    }

    @Override
    public CbbTerminalModelDTO queryByProductId(String productId) throws BusinessException {
        Assert.hasText(productId, "productId can not be null");

        List<TerminalModelDriverEntity> entityList = terminalModelDriverDAO.findByProductId(productId);
        if (CollectionUtils.isEmpty(entityList)) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_MODEL_NOT_EXIST_ERROR, new String[] {productId});
        }

        CbbTerminalModelDTO terminalModelDTO = new CbbTerminalModelDTO();
        BeanUtils.copyProperties(entityList.get(0), terminalModelDTO);
        return terminalModelDTO;
    }

    @Override
    public List<String> queryTerminalOsTypeByPlatform(CbbTerminalPlatformEnums platform) {
        Assert.notNull(platform, "platform can not be null");
        List<String> terminalOsTypeList = terminalBasicInfoDAO.getTerminalOsTypeByPlatform(platform);
        if (CollectionUtils.isEmpty(terminalOsTypeList)) {
            return Collections.emptyList();
        }
        return terminalOsTypeList;
    }
}
