package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.aaa.module.def.api.AuditLogAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalDiskInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalAuthorizationWhitelistDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalAuthorizationWhitelistEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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

    @Autowired
    private TerminalAuthorizationWhitelistDAO terminalAuthorizationWhitelistDao;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;


    @Autowired
    private AuditLogAPI logAPI;

    @Override
    public Boolean isOCSFreeAuthorization(String terminalId) {
        Assert.notNull(terminalId, "terminal id can not be null");
        String ocsSn = terminalBasicInfoDAO.getOcsSnByTerminalId(terminalId);
        return !StringUtils.isEmpty(ocsSn);
    }

    @Override
    public boolean checkWhiteList(CbbShineTerminalBasicInfo terminalBasicInfo) {
        Assert.notNull(terminalBasicInfo, "terminalBasicInfo can not is null");
        List<TerminalAuthorizationWhitelistEntity> whitelistEntityList = terminalAuthorizationWhitelistDao.findAllByOrderByPriorityDesc();
        //只有是TCI的设备，才需要去关注是否安装了OCS磁盘
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
                        terminalBasicInfo.getProductType(), ocsDiskAuthInputInfo.getDiskSn());
                TerminalEntity terminalEntity = terminalBasicInfoDAO.findByOcsSn(ocsDiskAuthInputInfo.getDiskSn());
                if (terminalEntity != null && !terminalEntity.getMacAddr().equals(terminalBasicInfo.getMacAddr())) {
                    terminalEntity.setOcsSn(null);
                    terminalBasicInfoDAO.save(terminalEntity);
                    logAPI.recordLog(BusinessKey.RCDC_TERMINAL_OCS_AUTHORIZATION_KICK_OUT, terminalEntity.getTerminalName(),
                            terminalEntity.getTerminalId(), terminalBasicInfo.getTerminalName(), terminalBasicInfo.getTerminalId());
                }
                return true;
            }

        }
        return false;
    }

    @Override
    public String getOcsSnFromDiskInfo(String diskInfos) {
        Assert.notNull(diskInfos, "diskInfos can not be null");
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
                    LOGGER.info("compositeProductType[{}] is ", compositeProductType);
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
