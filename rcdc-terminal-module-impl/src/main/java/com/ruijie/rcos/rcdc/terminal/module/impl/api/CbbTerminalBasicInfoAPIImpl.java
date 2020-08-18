package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBasicInfoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbModifyTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalBasicInfoServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

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

    @Override
    public CbbTerminalBasicInfoDTO findBasicInfoByTerminalId(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        CbbTerminalBasicInfoDTO basicInfoDTO = new CbbTerminalBasicInfoDTO();
        BeanUtils.copyProperties(basicInfoEntity, basicInfoDTO, TerminalEntity.BEAN_COPY_IGNORE_NETWORK_INFO_ARR);
        basicInfoDTO.setTerminalPlatform(basicInfoEntity.getPlatform());
        basicInfoDTO.setNetworkInfoArr(basicInfoEntity.getNetworkInfoArr());
        return basicInfoDTO;
    }

    @Override
    public void delete(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        // 在线终端不允许删除
        boolean isOnline = basicInfoService.isTerminalOnline(terminalId);
        if (isOnline) {
            CbbTerminalBasicInfoDTO basicInfo = findBasicInfoByTerminalId(terminalId);
            String terminalName = basicInfo.getTerminalName();
            String macAddr = basicInfo.getMacAddr();
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_ONLINE_CANNOT_DELETE, new String[] {terminalName, macAddr});
        }

        terminalBasicInfoServiceTx.deleteTerminal(terminalId);

    }

    @Override
    public void modifyTerminal(CbbModifyTerminalDTO request) throws BusinessException {
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

    }

    private TerminalEntity getTerminalEntity(String terminalId) throws BusinessException {
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return basicInfoEntity;
    }
}
