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
     * ????????????-IDV??????VDI???productId (RG-CT3120???80020101???Rain400W???80060041???Rain400W V2???80060042???Rain300W???80060022)
     */
    private static final Set<String> IDV_USE_AS_VDI_PRODUCT_ID_SET = Sets.newHashSet("80020101", "80060041", "80060042", "80060022");

    @Override
    public void saveBasicInfo(TerminalEntity terminalEntity, CbbShineTerminalBasicInfo shineTerminalBasicInfo, Boolean authed) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        Assert.notNull(shineTerminalBasicInfo, "????????????????????????");
        Assert.notNull(authed, "authed can not be null");

        //???TCI????????????ocsSn??????
        terminalAuthorizationWhitelistService.fillOcsSnIfExists(terminalEntity);

        // ????????????????????????
        boolean isSaveSuccess = saveTerminalBasicInfo(terminalEntity, authed);
        int count = 0;
        // ???????????????3???
        while (!isSaveSuccess && count++ < FAIL_TRY_COUNT) {
            LOGGER.error("?????????{}??????????????????????????????terminalId=[{}]", count, terminalEntity.getTerminalId());
            isSaveSuccess = saveTerminalBasicInfo(terminalEntity, authed);
        }
    }

    private boolean saveTerminalBasicInfo(TerminalEntity basicInfoEntity, Boolean authed) {
        basicInfoEntity.setAuthed(authed);
        try {
            basicInfoDAO.save(basicInfoEntity);
            return true;
        } catch (Exception e) {
            LOGGER.error("????????????[" + basicInfoEntity.getTerminalId() + "]??????????????????????????????", e);
            return false;
        }
    }

    @Override
    public CbbTerminalTypeEnums obtainTerminalType(TerminalEntity terminalEntity) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        CbbTerminalPlatformEnums terminalPlatform = terminalEntity.getPlatform();
        String osType = terminalEntity.getTerminalOsType();

        // TODO ??????????????????????????????????????????
        if (IDV_USE_AS_VDI_PRODUCT_ID_SET.contains(terminalEntity.getProductId())) {
            LOGGER.info("??????[{}]IDV??????VDI????????????????????????IDV??????", terminalEntity.getTerminalId());
            return CbbTerminalTypeEnums.convert(CbbTerminalPlatformEnums.IDV.name(), osType);
        }

        if (terminalPlatform == CbbTerminalPlatformEnums.VOI) {
            LOGGER.info("VOI?????????????????????????????????IDV??????");
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
            LOGGER.info("???????????????,terminalId:[{}]", terminalId);
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

        // ?????????????????????cpu??????
        basicInfoEntity.setUpgradeCpuType(convertCpuType(shineTerminalBasicInfo.getCpuType()));
        if (basicInfoEntity.getCpuType().toUpperCase().contains(Constants.ARM_CPU_PREFFIX)) {
            LOGGER.info("??????[{}]???cpu[{}]???arm cpu", basicInfoEntity.getTerminalId(), basicInfoEntity.getCpuType());
            basicInfoEntity.setCpuArch(CbbCpuArchType.ARM);
        }

        return basicInfoEntity;
    }

    private String convertCpuType(String cpu) {
        if (StringUtils.isEmpty(cpu)) {
            LOGGER.debug("cpu????????????");
            return StringUtils.EMPTY;
        }

        if (cpu.toUpperCase().contains(Constants.CPU_TYPE_AMD)) {
            LOGGER.debug("cpu?????????AMD");
            return Constants.CPU_TYPE_AMD;
        }

        if (cpu.toUpperCase().contains(Constants.CPU_TYPE_INTEL)) {
            LOGGER.debug("cpu?????????INTEL");
            return Constants.CPU_TYPE_INTEL;
        }

        return cpu;

    }

    private CbbTerminalNetworkInfoDTO[] obtainNetworkInfo(CbbShineTerminalBasicInfo basicInfo) {
        CbbTerminalNetworkInfoDTO[] networkInfoArr = basicInfo.getNetworkInfoArr();
        if (ArrayUtils.isEmpty(networkInfoArr)) {
            // ????????????????????????????????????????????????????????????
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
        Assert.notNull(basicInfo, "basicInfo can not be null");
        if (StringUtils.isEmpty(basicInfo.getProductId())) {
            // ?????????id, ??????????????????
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
                    // ??????????????????????????????
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
        Assert.hasText(terminalId, "terminalId ????????????");
        Assert.notNull(terminalName, "terminalName ????????????");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }
        ChangeHostNameRequest changeRequest = new ChangeHostNameRequest(terminalName);
        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.MODIFY_TERMINAL_NAME.getName(), changeRequest);
        try {
            sender.syncRequest(message);
        } catch (Exception e) {
            LOGGER.error("???????????????????????????????????????[" + terminalId + "]??????", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_OPERATE_MSG_SEND_FAIL, e,
                    LocaleI18nResolver.resolve(BusinessKey.RCDC_TERMINAL_OPERATE_ACTION_MODIFY_NAME));
        }
    }

    @Override
    public void modifyTerminalNetworkConfig(String terminalId, ShineNetworkConfig shineNetworkConfig) throws BusinessException {
        Assert.hasText(terminalId, "terminalId ????????????");
        Assert.notNull(shineNetworkConfig, "ShineNetworkConfig ?????????null");
        DefaultRequestMessageSender sender = sessionManager.getRequestMessageSender(terminalId);
        if (sender == null) {
            throw new BusinessException(PublicBusinessKey.RCDC_TERMINAL_OFFLINE);
        }

        Message message = new Message(Constants.SYSTEM_TYPE, SendTerminalEventEnums.MODIFY_TERMINAL_NETWORK_CONFIG.getName(), shineNetworkConfig);
        sender.request(message);
    }

    @Override
    public void modifyTerminalState(String terminalId, CbbTerminalStateEnums state) {
        Assert.hasText(terminalId, "terminalId ????????????");
        Assert.notNull(state, "state ????????????");

        tryUpdateTerminalState(terminalId, state);
    }

    @Override
    public void modifyTerminalStateToOffline(String terminalId) {
        Assert.hasText(terminalId, "terminalId ????????????");
        // ??????????????????????????????????????????????????????????????????
        TerminalEntity entity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);

        Session session = sessionManager.getSessionByAlias(terminalId);
        if (session != null) {
            LOGGER.info("??????session???????????????????????????????????????????????????????????????terminalId={}", terminalId);
            return;
        }

        if (CbbTerminalStateEnums.UPGRADING == entity.getState()) {
            LOGGER.info("????????????????????????????????????????????????????????????terminalId={}, ip={}", terminalId, entity.getIp());
            return;
        }
        tryUpdateTerminalState(terminalId, CbbTerminalStateEnums.OFFLINE);
    }

    private void tryUpdateTerminalState(String terminalId, CbbTerminalStateEnums state) {
        boolean isSuccess = updateTerminalState(terminalId, state);
        int count = 0;
        // ???????????????3???
        while (!isSuccess && count++ < FAIL_TRY_COUNT) {
            LOGGER.error("?????????{}????????????????????????terminalId=[{}],????????????????????????[{}]", count, terminalId, state.name());
            isSuccess = updateTerminalState(terminalId, state);
        }
    }

    private boolean updateTerminalState(String terminalId, CbbTerminalStateEnums state) {
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            LOGGER.error("?????????terminalId=[{}]?????????", terminalId);
            return false;
        }
        int effectRow = basicInfoDAO.modifyTerminalStateOffline(state, new Date(), terminalId, basicInfoEntity.getVersion());
        if (effectRow == 0) {
            LOGGER.error("??????????????????(updateTerminalState)???terminalId=[{}],????????????????????????[{}]", terminalId, state.name());
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
