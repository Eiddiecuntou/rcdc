package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetworkInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbModifyTerminalRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalBasicInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbTerminalNetworkInfoResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalNetworkInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalNetworkInfoEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalBasicInfoServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;

/**
 * Description: 终端基本信息维护
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/1
 *
 * @author Jarman
 */
public class CbbTerminalBasicInfoAPIImpl implements CbbTerminalBasicInfoAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalBasicInfoAPIImpl.class);

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalBasicInfoServiceTx terminalBasicInfoServiceTx;

    @Autowired
    private TerminalGroupService terminalGroupService;

    @Autowired
    private TerminalNetworkInfoDAO terminalNetworkInfoDAO;

    @Override
    public CbbTerminalBasicInfoResponse findBasicInfoByTerminalId(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalIdRequest不能为null");
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(request.getTerminalId());
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        CbbTerminalBasicInfoResponse basicInfoDTO = new CbbTerminalBasicInfoResponse();
        BeanUtils.copyProperties(basicInfoEntity, basicInfoDTO);
        basicInfoDTO.setTerminalPlatform(basicInfoEntity.getPlatform());
        return basicInfoDTO;
    }

    @Override
    public DefaultResponse delete(CbbTerminalIdRequest request) throws BusinessException {
        Assert.notNull(request, "TerminalIdRequest不能为null");
        String terminalId = request.getTerminalId();
        // 在线终端不允许删除
        boolean isOnline = basicInfoService.isTerminalOnline(terminalId);
        if (isOnline) {
            CbbTerminalBasicInfoResponse basicInfo = findBasicInfoByTerminalId(new CbbTerminalIdRequest(terminalId));
            String terminalName = basicInfo.getTerminalName();
            String macAddr = basicInfo.getMacAddr();
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_ONLINE_CANNOT_DELETE, new String[] {terminalName, macAddr});
        }

        terminalBasicInfoServiceTx.deleteTerminal(terminalId);

        return DefaultResponse.Builder.success();
    }

    @Override
    public DefaultResponse modifyTerminal(CbbModifyTerminalRequest request) throws BusinessException {
        Assert.notNull(request, "request不能为空");

        String terminalId = request.getCbbTerminalId();
        TerminalEntity entity = getTerminalEntity(terminalId);

        // 校验终端分组
        terminalGroupService.checkGroupExist(request.getGroupId());

        // 终端名称有变更，发送名称变更消息给终端
        if (!request.getTerminalName().equals(entity.getTerminalName())) {
            try {
                basicInfoService.modifyTerminalName(terminalId, request.getTerminalName());
            } catch (BusinessException e) {
                LOGGER.error("修改终端名称失败，terminaId:" + terminalId, e);
                throw e;
            }
        }

        entity.setGroupId(request.getGroupId());
        entity.setTerminalName(request.getTerminalName());
        basicInfoDAO.save(entity);

        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbTerminalNetworkInfoResponse getTerminalNetworkInfo(CbbTerminalIdRequest request) {
        Assert.notNull(request, "request can not be null");

        List<TerminalNetworkInfoEntity> networkInfoList = terminalNetworkInfoDAO.findByTerminalId(request.getTerminalId());
        if (CollectionUtils.isEmpty(networkInfoList)) {
            return new CbbTerminalNetworkInfoResponse(new CbbTerminalNetworkInfoDTO[] {});
        }

        List<CbbTerminalNetworkInfoDTO> dtoList = Lists.newArrayList();
        for (TerminalNetworkInfoEntity entity : networkInfoList) {
            CbbTerminalNetworkInfoDTO networkInfoDTO = new CbbTerminalNetworkInfoDTO();
            BeanUtils.copyProperties(entity, networkInfoDTO);
            dtoList.add(networkInfoDTO);
        }
        return new CbbTerminalNetworkInfoResponse(dtoList.toArray(new CbbTerminalNetworkInfoDTO[dtoList.size()]));
    }

    private TerminalEntity getTerminalEntity(String terminalId) throws BusinessException {
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return basicInfoEntity;
    }
}
