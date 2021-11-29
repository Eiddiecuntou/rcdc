package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.aaa.module.def.api.AuditLogAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLicenseMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDiskInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalOcsAuthChangeSPI;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbTerminalOcsAuthChangeRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseAuthService;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalAuthorizationWhitelistDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalAuthorizationWhitelistEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/16
 *
 * @author zhangsiming
 */
@Service
public class TerminalAuthorizationWhitelistServiceImpl implements TerminalAuthorizationWhitelistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalAuthorizationWhitelistServiceImpl.class);

    private static final String CONSTANT_SYSTEM_DISK = "sysdisk";

    private static final String TERMINAL_MODE_TCI = "TCI";

    @Autowired
    private TerminalAuthorizationWhitelistDAO terminalAuthorizationWhitelistDao;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private AuditLogAPI logAPI;

    @Autowired
    private CbbTerminalOcsAuthChangeSPI cbbTerminalOcsAuthChangeSPI;

    @Autowired
    private AuditLogAPI auditLogAPI;

    @Autowired
    private TerminalLicenseAuthService terminalLicenseAuthService;

    @Override
    public Boolean isOCSFreeAuthorization(String terminalId) {
        Assert.notNull(terminalId, "terminal id can not be null");
        String ocsSn = terminalBasicInfoDAO.getOcsSnByTerminalId(terminalId);
        return StringUtils.isNotBlank(ocsSn);
    }

    @Override
    public boolean checkWhiteList(CbbShineTerminalBasicInfo terminalBasicInfo, @Nullable TerminalEntity terminalEntityInDb) {
        Assert.notNull(terminalBasicInfo, "terminalBasicInfo can not is null");
        List<TerminalAuthorizationWhitelistEntity> whitelistEntityList = terminalAuthorizationWhitelistDao.findAllByOrderByPriorityDesc();
        //只有是TCI的设备，才需要去关注是否安装了作为系统盘的OCS磁盘
        OcsDiskAuthInputInfo ocsDiskAuthInputInfo = new OcsDiskAuthInputInfo();
        if (terminalBasicInfo.getPlatform() == CbbTerminalPlatformEnums.VOI) {
            String allDiskInfo = terminalBasicInfo.getAllDiskInfo();
            ocsDiskAuthInputInfo = getOcsDiskAuthInputInfo(terminalBasicInfo.getTerminalId(), allDiskInfo);
        }

        for (TerminalAuthorizationWhitelistEntity entity : whitelistEntityList) {
            if (entity.getProductType().equals(terminalBasicInfo.getProductType())) {
                LOGGER.info("terminalId[{}] raw productType[{}] free authorization matched", terminalBasicInfo.getTerminalId(),
                        terminalBasicInfo.getProductType());
                //todo 将来业务的白名单移到cbb后，需要在这里加上回收授权的逻辑
                return true;
            }
            if (entity.getProductType().equals(ocsDiskAuthInputInfo.getCompositeProductType())) {
                LOGGER.info("OCS productType[{}] sn[{}] free authorization matched",
                        ocsDiskAuthInputInfo.getRawProductType(), ocsDiskAuthInputInfo.getDiskSn());
                List<TerminalEntity> terminalEntityList = terminalBasicInfoDAO.findByOcsSn(ocsDiskAuthInputInfo.getDiskSn());
                if (!CollectionUtils.isEmpty(terminalEntityList)) {
                    terminalEntityList.stream()
                            .filter(terminalEntity -> !terminalEntity.getMacAddr().equals(terminalBasicInfo.getMacAddr()))
                            .forEach(terminalEntity -> {
                                terminalEntity.setOcsSn(null);
                                terminalBasicInfoDAO.save(terminalEntity);
                                LOGGER.info("终端[{}] OCS授权被取消", terminalEntity.getTerminalId());
                                //通知业务层该ocs之前关联的设备被踢出
                                CbbTerminalOcsAuthChangeRequest changeRequest = new CbbTerminalOcsAuthChangeRequest();
                                changeRequest.setTerminalId(terminalEntity.getTerminalId());
                                changeRequest.setOcsAuthed(false);
                                cbbTerminalOcsAuthChangeSPI.notifyOcsAuthChange(changeRequest);
                                LOGGER.info("终端[{}({})]OCS磁盘免授权失效", terminalEntity.getTerminalName(), terminalEntity.getTerminalId());
                                logAPI.recordLog(BusinessKey.RCDC_TERMINAL_OCS_AUTHORIZATION_KICK_OUT, terminalEntity.getTerminalName(),
                                        terminalEntity.getTerminalId(), terminalBasicInfo.getTerminalName(), terminalBasicInfo.getTerminalId());
                            });
                }
                recycleWhiteListTerminalAuth(terminalEntityInDb, "OCS磁盘");
                return true;
            }

        }
        return false;
    }

    private void recycleWhiteListTerminalAuth(TerminalEntity terminalEntity, String reason) {
        if (terminalEntity != null) {
            String authType = CbbTerminalPlatformEnums.VOI.equals(terminalEntity.getAuthMode())
                    ? TERMINAL_MODE_TCI : terminalEntity.getAuthMode().name();
            try {
                if (terminalLicenseAuthService.recycle(terminalEntity.getTerminalId(), terminalEntity.getAuthMode())) {
                    //其他授权回收需要记录审计日志
                    LOGGER.info("终端[{}({})]的[{}]授权被回收", terminalEntity.getTerminalName(), terminalEntity.getTerminalId(), authType);
                    auditLogAPI.recordLog(BusinessKey.RCDC_TERMINAL_FREE_AUTHORIZATION_SELF_OTHER_AUTH_RECYCLE,
                            terminalEntity.getTerminalName(), terminalEntity.getTerminalId(), authType, reason);
                }
            } catch (BusinessException e) {
                LOGGER.error("white list auth recycle error: ", e);
                auditLogAPI.recordLog(BusinessKey.RCDC_TERMINAL_AUTHORIZATION_RECYCLE_ERROR,
                        terminalEntity.getTerminalName(), terminalEntity.getTerminalId(), authType, e.getI18nMessage());
            }
        }
    }

    @Override
    public void fillOcsSnIfExists(TerminalEntity terminalEntity) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        if (terminalEntity.getPlatform() != CbbTerminalPlatformEnums.VOI) {
            terminalEntity.setOcsSn(null);
            return;
        }
        String diskInfo = terminalEntity.getAllDiskInfo();
        if (StringUtils.isBlank(diskInfo)) {
            terminalEntity.setOcsSn(null);
            return;
        }
        String ocsSn = getOcsSnFromDiskInfo(terminalEntity.getTerminalId(), diskInfo);
        terminalEntity.setOcsSn(ocsSn);
    }

    private String getOcsSnFromDiskInfo(String terminalId, String diskInfos) {
        OcsDiskAuthInputInfo ocsDiskAuthInputInfo = getOcsDiskAuthInputInfo(terminalId, diskInfos);
        if (ocsDiskAuthInputInfo.getCompositeProductType() != null) {
            if (terminalAuthorizationWhitelistDao.findByProductType(ocsDiskAuthInputInfo.getCompositeProductType()) != null) {
                String diskSn = ocsDiskAuthInputInfo.getDiskSn();
                return StringUtils.isBlank(diskSn) ? null : diskSn;
            }
        }
        //返回null
        return null;
    }

    private OcsDiskAuthInputInfo getOcsDiskAuthInputInfo(String terminalId, String diskInfos) {
        OcsDiskAuthInputInfo ocsDiskAuthInputInfo = new OcsDiskAuthInputInfo();
        if (StringUtils.isBlank(diskInfos)) {
            LOGGER.warn("terminal[{}] diskInfos is null", terminalId);
            return ocsDiskAuthInputInfo;
        }
        try {
            JSONArray diskArray = JSONObject.parseArray(diskInfos);
            for (int i = 0; i < diskArray.size(); i++) {
                CbbTerminalDiskInfoDTO cbbTerminalDiskInfoDTO = JSONObject.toJavaObject((JSON) diskArray.get(i), CbbTerminalDiskInfoDTO.class);
                if (CONSTANT_SYSTEM_DISK.equals(cbbTerminalDiskInfoDTO.getDevType())) {
                    ocsDiskAuthInputInfo.setRawProductType(cbbTerminalDiskInfoDTO.getDevModel());
                    ocsDiskAuthInputInfo.setDiskSn(cbbTerminalDiskInfoDTO.getDevSn());
                    String diskSn = cbbTerminalDiskInfoDTO.getDevSn();
                    StringBuilder sb = new StringBuilder();
                    //获取序列号的第4、6、7位
                    String compositeProductType = sb.append(cbbTerminalDiskInfoDTO.getDevModel())
                            .append("_")
                            .append(diskSn.charAt(3))
                            .append(diskSn.charAt(5))
                            .append(diskSn.charAt(6))
                            .toString();
                    LOGGER.info("terminalId[{}], compositeProductType is [{}] ", terminalId, compositeProductType);
                    ocsDiskAuthInputInfo.setCompositeProductType(compositeProductType);
                    break;
                }
            }
            return ocsDiskAuthInputInfo;
        } catch (Exception e) {
            LOGGER.error("getCompositeProductType error happened!!!", e);
            return ocsDiskAuthInputInfo;
        }
    }

    /**
     * Ocs磁盘免授权的输入信息
     *
     * @author zhangsiming
     */
    static final class OcsDiskAuthInputInfo {

        /**
         * 磁盘型号+Sn根据某种算法组成生成
         */
        private String compositeProductType;

        /**
         * 磁盘型号
         */
        private String rawProductType;

        /**
         * 磁盘序列号
         */
        private String diskSn;


        public String getCompositeProductType() {
            return compositeProductType;
        }

        public void setCompositeProductType(String compositeProductType) {
            this.compositeProductType = compositeProductType;
        }

        public String getDiskSn() {
            return diskSn;
        }

        public void setDiskSn(String diskSn) {
            this.diskSn = diskSn;
        }

        public String getRawProductType() {
            return rawProductType;
        }

        public void setRawProductType(String rawProductType) {
            this.rawProductType = rawProductType;
        }
    }
}
