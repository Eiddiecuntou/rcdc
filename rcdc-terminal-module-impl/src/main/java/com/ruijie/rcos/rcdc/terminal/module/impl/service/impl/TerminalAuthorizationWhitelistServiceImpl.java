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
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
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
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private CbbTerminalLicenseMgmtAPI cbbTerminalLicenseMgmtAPI;

    @Autowired
    private AuditLogAPI auditLogAPI;

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Override
    public Boolean isOCSFreeAuthorization(String terminalId) {
        Assert.notNull(terminalId, "terminal id can not be null");
        String ocsSn = terminalBasicInfoDAO.getOcsSnByTerminalId(terminalId);
        return StringUtils.isNotBlank(ocsSn);
    }

    @Override
    public boolean checkWhiteList(CbbShineTerminalBasicInfo terminalBasicInfo) {
        Assert.notNull(terminalBasicInfo, "terminalBasicInfo can not is null");
        List<TerminalAuthorizationWhitelistEntity> whitelistEntityList = terminalAuthorizationWhitelistDao.findAllByOrderByPriorityDesc();
        //只有是TCI的设备，才需要去关注是否安装了作为系统盘的OCS磁盘
        OcsDiskAuthInputInfo ocsDiskAuthInputInfo = new OcsDiskAuthInputInfo();
        if (terminalBasicInfo.getPlatform() == CbbTerminalPlatformEnums.VOI) {
            String allDiskInfo = terminalBasicInfo.getAllDiskInfo();
            ocsDiskAuthInputInfo = getOcsDiskAuthInputInfo(allDiskInfo);
        }

        for (TerminalAuthorizationWhitelistEntity entity : whitelistEntityList) {
            if (entity.getProductType().equals(terminalBasicInfo.getProductType())) {
                LOGGER.info("raw productType[{}] free authorization matched", terminalBasicInfo.getProductType());
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
                                LOGGER.info("终端[{}({})]OCS磁盘免费授权失效", terminalEntity.getTerminalName(), terminalEntity.getTerminalId());
                                logAPI.recordLog(BusinessKey.RCDC_TERMINAL_OCS_AUTHORIZATION_KICK_OUT, terminalEntity.getTerminalName(),
                                        terminalEntity.getTerminalId(), terminalBasicInfo.getTerminalName(), terminalBasicInfo.getTerminalId());
                            });
                }
                return true;
            }

        }
        return false;
    }

    @Override
    public void fillOcsSnAndRecycleIfAuthed(TerminalEntity terminalEntity, @Nullable String diskInfo) {
        Assert.notNull(terminalEntity, "terminalEntity can not be null");
        if (terminalEntity.getPlatform() != CbbTerminalPlatformEnums.VOI) {
            return;
        }
        if (StringUtils.isBlank(diskInfo)) {
            terminalEntity.setOcsSn(null);
            return;
        }
        String ocsSn = getOcsSnFromDiskInfo(diskInfo);
        terminalEntity.setOcsSn(ocsSn);
        if (StringUtils.isBlank(ocsSn)) {
            return;
        }

        //回收本终端的其他授权
        String terminalId = terminalEntity.getTerminalId();
        TerminalEntity terminalEntityInDb = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        String authType = "";
        try {
            if (terminalEntityInDb != null && terminalEntityInDb.getAuthed()) {
                TerminalAuthorizeEntity terminalAuthorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
                if (terminalAuthorizeEntity != null) {
                    cbbTerminalLicenseMgmtAPI.cancelTerminalAuth(terminalId);
                    authType = CbbTerminalPlatformEnums.VOI.equals(terminalAuthorizeEntity.getAuthMode())
                            ? TERMINAL_MODE_TCI : terminalAuthorizeEntity.getAuthMode().name();
                    //其他授权回收需要记录审计日志
                    LOGGER.info("终端[{}({})]的[{}]授权被回收", terminalEntityInDb.getTerminalName(), terminalEntityInDb.getTerminalId(), authType);
                    auditLogAPI.recordLog(BusinessKey.RCDC_TERMINAL_OCS_AUTHORIZATION_SELF_OTHER_AUTH_RECYCLE,
                            terminalEntityInDb.getTerminalName(), terminalEntityInDb.getTerminalId(), authType);
                }
            }
        } catch (BusinessException e) {
            LOGGER.error("ocs auth recycle error: ", e);
            auditLogAPI.recordLog(BusinessKey.RCDC_TERMINAL_OCS_AUTHORIZATION_RECYCLE_ERROR,
                    terminalEntityInDb.getTerminalName(), terminalEntityInDb.getTerminalId(), authType, e.getI18nMessage());
        }
    }

    private String getOcsSnFromDiskInfo(String diskInfos) {
        OcsDiskAuthInputInfo ocsDiskAuthInputInfo = getOcsDiskAuthInputInfo(diskInfos);
        if (ocsDiskAuthInputInfo.getCompositeProductType() != null) {
            if (terminalAuthorizationWhitelistDao.findByProductType(ocsDiskAuthInputInfo.getCompositeProductType()) != null) {
                return ocsDiskAuthInputInfo.getDiskSn();
            }
        }
        return "";
    }

    private OcsDiskAuthInputInfo getOcsDiskAuthInputInfo(String diskInfos) {
        OcsDiskAuthInputInfo ocsDiskAuthInputInfo = new OcsDiskAuthInputInfo();
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
                    LOGGER.info("compositeProductType is [{}] ", compositeProductType);
                    ocsDiskAuthInputInfo.setCompositeProductType(compositeProductType);
                    return ocsDiskAuthInputInfo;
                }
            }
            return new OcsDiskAuthInputInfo();
        } catch (Exception e) {
            LOGGER.error("getCompositeProductType error happened!!!", e);
            return new OcsDiskAuthInputInfo();
        }
    }

    /**
     * Ocs磁盘免费授权的输入信息
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
