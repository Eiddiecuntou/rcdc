package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.PublicBusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalNetworkInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalModelDriverDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalModelDriverEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeArchType;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ChangeHostNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.ShineNetworkConfig;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.i18n.LocaleI18nResolver;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;


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
    private TerminalModelDriverDAO terminalModelDriverDAO;

    @Autowired
    private TerminalLockHelper terminalLockHelper;

    @Autowired
    private TerminalAuthorizationWhitelistService terminalAuthorizationWhitelistService;

    private static final int FAIL_TRY_COUNT = 3;


    /**
     * 特殊终端-IDV用作VDI的productId (RG-CT3120：80020101、Rain400W：80060041、Rain400W V2：80060042、Rain300W：80060022)
     */
    private static final Set<String> IDV_USE_AS_VDI_PRODUCT_ID_SET = Sets.newHashSet("80020101", "80060041", "80060042", "80060022");

    @Override
    public void saveBasicInfo(TerminalEntity terminalEntity, CbbShineTerminalBasicInfo shineTerminalBasicInfo, Boolean authed) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        Assert.notNull(shineTerminalBasicInfo, "终端信息不能为空");
        Assert.notNull(authed, "authed can not be null");

        //为TCI设置字段ocsSn的值
        terminalAuthorizationWhitelistService.fillOcsSnIfExists(terminalEntity);

        // 保存终端基础信息
        boolean isSaveSuccess = saveTerminalBasicInfo(terminalEntity, authed);
        int count = 0;
        // 失败，尝试3次
        while (!isSaveSuccess && count++ < FAIL_TRY_COUNT) {
            LOGGER.error("开始第{}次保存终端基础信息，terminalId=[{}]", count, terminalEntity.getTerminalId());
            isSaveSuccess = saveTerminalBasicInfo(terminalEntity, authed);
        }
    }

    private boolean saveTerminalBasicInfo(TerminalEntity basicInfoEntity, Boolean authed) {
        basicInfoEntity.setAuthed(authed);
        try {
            basicInfoDAO.save(basicInfoEntity);
            return true;
        } catch (Exception e) {
            LOGGER.error("保存终端[" + basicInfoEntity.getTerminalId() + "]信息失败！将进行重试", e);
            return false;
        }
    }

    @Override
    public CbbTerminalTypeEnums obtainTerminalType(TerminalEntity terminalEntity) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        CbbTerminalPlatformEnums terminalPlatform = terminalEntity.getPlatform();
        String osType = terminalEntity.getTerminalOsType();

        // TODO 临时解决方案，后续版本需修订
        if (IDV_USE_AS_VDI_PRODUCT_ID_SET.contains(terminalEntity.getProductId())) {
            LOGGER.info("终端[{}]IDV用作VDI终端系统升级返回IDV平台", terminalEntity.getTerminalId());
            return CbbTerminalTypeEnums.convert(CbbTerminalPlatformEnums.IDV.name(), osType);
        }

        if (terminalPlatform == CbbTerminalPlatformEnums.VOI) {
            LOGGER.info("VOI平台类型终端快刷转换成IDV类型");
            return CbbTerminalTypeEnums.convert(CbbTerminalPlatformEnums.IDV.name(), osType);
        }

        return CbbTerminalTypeEnums.convert(terminalPlatform.name(), osType);

    }

    @Override
    public TerminalEntity convertBasicInfo2TerminalEntity(String terminalId, boolean isNewConnection,
                                                          CbbShineTerminalBasicInfo shineTerminalBasicInfo) {
        Assert.hasText(terminalId, "terminalId can not be empty");
        Assert.notNull(shineTerminalBasicInfo, "shineTerminalBasicInfo can not be null");

        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        Date now = new Date();
        if (basicInfoEntity == null) {
            LOGGER.info("新终端接入,terminalId:[{}]", terminalId);
            basicInfoEntity = new TerminalEntity();
            basicInfoEntity.setCreateTime(now);
            basicInfoEntity.setGroupId(Constants.DEFAULT_TERMINAL_GROUP_UUID);
        }
        BeanUtils.copyProperties(shineTerminalBasicInfo, basicInfoEntity, TerminalEntity.BEAN_COPY_IGNORE_NETWORK_INFO_ARR);
        if (isNewConnection) {
            basicInfoEntity.setLastOnlineTime(now);
        }

        Session session = sessionManager.getSessionByAlias(terminalId);
        CbbTerminalStateEnums state = session == null ? CbbTerminalStateEnums.OFFLINE : CbbTerminalStateEnums.ONLINE;
        basicInfoEntity.setState(state);
        CbbTerminalNetworkInfoDTO[] networkInfoDTOArr = obtainNetworkInfo(shineTerminalBasicInfo);
        basicInfoEntity.setNetworkInfoArr(networkInfoDTOArr);
        basicInfoEntity.setAuthed(Boolean.TRUE);
        if (shineTerminalBasicInfo.getTerminalWorkSupportModeArr() != null) {
            basicInfoEntity.setSupportWorkMode(JSON.toJSONString(shineTerminalBasicInfo.getTerminalWorkSupportModeArr()));
        }

        // 设置支持升级的cpu类型
        basicInfoEntity.setUpgradeCpuType(convertCpuType(shineTerminalBasicInfo.getCpuType()));
        if (basicInfoEntity.getCpuType().toUpperCase().contains(Constants.ARM_CPU_PREFFIX)) {
            LOGGER.info("终端[{}]的cpu[{}]是arm cpu", basicInfoEntity.getTerminalId(), basicInfoEntity.getCpuType());
            basicInfoEntity.setCpuArch(CbbCpuArchType.ARM);
        }

        return basicInfoEntity;
    }

    private String convertCpuType(String cpu) {
        if (StringUtils.isEmpty(cpu)) {
            LOGGER.debug("cpu型号为空");
            return StringUtils.EMPTY;
        }

        if (cpu.toUpperCase().contains(Constants.CPU_TYPE_AMD)) {
            LOGGER.debug("cpu型号为AMD");
            return Constants.CPU_TYPE_AMD;
        }

        if (cpu.toUpperCase().contains(Constants.CPU_TYPE_INTEL)) {
            LOGGER.debug("cpu型号为INTEL");
            return Constants.CPU_TYPE_INTEL;
        }

        return cpu;

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
        return new CbbTerminalNetworkInfoDTO[]{networkInfoDTO};
    }

    @Override
    public void saveTerminalModel(CbbShineTerminalBasicInfo basicInfo) {

        if (StringUtils.isEmpty(basicInfo.getProductId())) {
            // 无产品id, 一般为软终端
            return;
        }

        String lockKey = basicInfo.getProductId() + basicInfo.getPlatform();
        Lock lock = terminalLockHelper.putAndGetLock(lockKey);
        lock.lock();

        try {
            List<TerminalModelDriverEntity> modelEntityList =
                    terminalModelDriverDAO.findByProductIdAndPlatform(basicInfo.getProductId(), basicInfo.getPlatform());

            if (!CollectionUtils.isEmpty(modelEntityList)) {
                boolean isDriverModeExist = modelEntityList.stream()
                        .anyMatch(modelDriver -> Objects.equals(modelDriver.getProductId(), basicInfo.getProductId())
                                && Objects.equals(modelDriver.getProductModel(), basicInfo.getProductType())
                                && Objects.equals(modelDriver.getCpuType(), basicInfo.getCpuType()));
                if (isDriverModeExist) {
                    // 已存在类型，无需处理
                    return;
                }
            }

            TerminalModelDriverEntity modelDriverEntity = new TerminalModelDriverEntity();
            modelDriverEntity.setProductId(basicInfo.getProductId());
            modelDriverEntity.setCpuType(basicInfo.getCpuType());
            modelDriverEntity.setProductModel(basicInfo.getProductType());
            modelDriverEntity.setPlatform(basicInfo.getPlatform());
            terminalModelDriverDAO.save(modelDriverEntity);

        } finally {
            lock.unlock();
        }

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
                    LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_MODIFY_NAME));
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

        Session session = sessionManager.getSessionByAlias(terminalId);
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
        Session session = sessionManager.getSessionByAlias(terminalId);
        return session != null;
    }

    @Override
    public TerminalTypeArchType obtainTerminalArchType(TerminalEntity basicInfoEntity) {
        Assert.notNull(basicInfoEntity, "basicInfoEntity can not be null");

        return TerminalTypeArchType.convert(obtainTerminalType(basicInfoEntity), basicInfoEntity.getCpuArch());
    }
}
