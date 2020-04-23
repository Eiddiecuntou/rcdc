package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetworkInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbNoticeEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalModelDriverDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalModelDriverEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeHostNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.Session;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/31
 *
 * @author Jarman
 */
@Service
public class TerminalBasicInfoServiceImpl implements TerminalBasicInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalBasicInfoServiceImpl.class);


    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;

    @Autowired
    private TerminalModelDriverDAO terminalModelDriverDAO;

    private static final int FAIL_TRY_COUNT = 3;

    @Override
    public void saveBasicInfo(String terminalId, CbbShineTerminalBasicInfo shineTerminalBasicInfo) {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(shineTerminalBasicInfo, "终端信息不能为空");

        // 自学习终端型号
        saveTerminalModel(shineTerminalBasicInfo);

        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        Date now = new Date();
        if (basicInfoEntity == null) {
            LOGGER.info("新终端接入,terminalId:[{}]", terminalId);
            basicInfoEntity = new TerminalEntity();
            basicInfoEntity.setCreateTime(now);
            basicInfoEntity.setGroupId(Constants.DEFAULT_TERMINAL_GROUP_UUID);
        }
        BeanUtils.copyProperties(shineTerminalBasicInfo, basicInfoEntity, TerminalEntity.BEAN_COPY_IGNORE_NETWORK_INFO_ARR);
        basicInfoEntity.setLastOnlineTime(now);
        basicInfoEntity.setState(CbbTerminalStateEnums.ONLINE);
        CbbTerminalNetworkInfoDTO[] networkInfoDTOArr = obtainNetworkInfo(shineTerminalBasicInfo);
        basicInfoEntity.setNetworkInfoArr(networkInfoDTOArr);
        basicInfoDAO.save(basicInfoEntity);

        // 通知其他组件终端为在线状态
        CbbNoticeRequest noticeRequest = new CbbNoticeRequest(CbbNoticeEventEnums.ONLINE);
        noticeRequest.setTerminalBasicInfo(shineTerminalBasicInfo);
        terminalEventNoticeSPI.notify(noticeRequest);
    }

    private CbbTerminalNetworkInfoDTO[] obtainNetworkInfo(CbbShineTerminalBasicInfo basicInfo) {
        CbbTerminalNetworkInfoDTO[] networkInfoArr = basicInfo.getNetworkInfoArr();
        if (ArrayUtils.isEmpty(networkInfoArr)) {
            // 兼容旧版本终端将网络信息保存在基本信息中
            return buildNetworkInfoArr(basicInfo);
        }

        return networkInfoArr;
    }

    private CbbTerminalNetworkInfoDTO[] buildNetworkInfoArr(CbbShineTerminalBasicInfo basicInfo) {
        CbbTerminalNetworkInfoDTO networkInfoDTO = new CbbTerminalNetworkInfoDTO();
        BeanUtils.copyProperties(basicInfo, networkInfoDTO);
        return new CbbTerminalNetworkInfoDTO[] {networkInfoDTO};
    }

    private void saveTerminalModel(CbbShineTerminalBasicInfo basicInfo) {
        if (StringUtils.isEmpty(basicInfo.getProductId())) {
            // 无产品id, 一般为软终端
            return;
        }

        List<TerminalModelDriverEntity> modelEntityList = terminalModelDriverDAO.findByProductId(basicInfo.getProductId());
        if (!CollectionUtils.isEmpty(modelEntityList)) {
            // 已存在，不需处理
            return;
        }

        TerminalModelDriverEntity modelDriverEntity = new TerminalModelDriverEntity();
        modelDriverEntity.setProductId(basicInfo.getProductId());
        modelDriverEntity.setCpuType(basicInfo.getCpuType());
        modelDriverEntity.setProductModel(basicInfo.getProductType());
        modelDriverEntity.setPlatform(basicInfo.getPlatform());
        terminalModelDriverDAO.save(modelDriverEntity);
    }

    @Override
    public void modifyTerminalName(String terminalId, String terminalName) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(terminalName, "terminalName 不能为空");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        ChangeHostNameRequest changeRequest = new ChangeHostNameRequest(terminalName);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.MODIFY_TERMINAL_NAME.getName(), changeRequest);
        try {
            sender.syncRequest(message);
        } catch (Exception e) {
            LOGGER.error("发送修改终端名称消息给终端[" + terminalId + "]失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    new String[] {LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_MODIFY_NAME, new String[] {})});
        }
    }

    @Override
    public void modifyTerminalNetworkConfig(String terminalId, ShineNetworkConfig shineNetworkConfig) throws BusinessException {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(shineNetworkConfig, "ShineNetworkConfig 不能为null");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.MODIFY_TERMINAL_NETWORK_CONFIG.getName(), shineNetworkConfig);
        sender.request(message);
    }

    @Override
    public void modifyTerminalState(String terminalId, CbbTerminalStateEnums state) {
        Assert.hasText(terminalId, "terminalId 不能为空");
        Assert.notNull(state, "state 不能为空");

        tryUpdateTerminalState(terminalId, state);
    }

    @Override
    public void modifyTerminalStateToOffline(String terminalId) {
        Assert.hasText(terminalId, "terminalId 不能为空");
        // 如果当前终端状态为升级中，则不更新为离线状态
        TerminalEntity entity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);

        Session session = sessionManager.getSession(terminalId);
        if (session != null) {
            LOGGER.info("存在session连接，终端处于在线状态，不做离线状态更新；terminalId={}", terminalId);
            return;
        }

        if (CbbTerminalStateEnums.UPGRADING == entity.getState()) {
            LOGGER.info("当前终端处于升级状态，不做离线状态修改；terminalId={}, ip={}", terminalId, entity.getIp());
            return;
        }
        tryUpdateTerminalState(terminalId, CbbTerminalStateEnums.OFFLINE);
    }

    private void tryUpdateTerminalState(String terminalId, CbbTerminalStateEnums state) {
        boolean isSuccess = updateTerminalState(terminalId, state);
        int count = 0;
        // 失败，尝试3次
        while (!isSuccess && count++ < FAIL_TRY_COUNT) {
            LOGGER.error("开始第{}次修改终端状态，terminalId=[{}],需要修改状态为：[{}]", count, terminalId, state.name());
            isSuccess = updateTerminalState(terminalId, state);
        }
    }

    private boolean updateTerminalState(String terminalId, CbbTerminalStateEnums state) {
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            LOGGER.error("不存在terminalId=[{}]的终端", terminalId);
            return false;
        }
        int effectRow = basicInfoDAO.modifyTerminalStateOffline(state, new Date(), terminalId, basicInfoEntity.getVersion());
        if (effectRow == 0) {
            LOGGER.error("修改终端状态(updateTerminalState)，terminalId=[{}],需要修改状态为：[{}]", terminalId, state.name());
            return false;
        }
        return true;
    }

    @Override
    public boolean isTerminalOnline(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not empty");
        Session session = sessionManager.getSession(terminalId);
        return session == null ? false : true;
    }
}
