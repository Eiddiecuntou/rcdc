package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.base.aaa.module.def.api.AuditLogAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalAuthorizationWhitelistDao;
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
    private static final String SYSTEM_DISK = "sysdisk";

    @Autowired
    private TerminalAuthorizationWhitelistDao terminalAuthorizationWhitelistDao;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private AuditLogAPI logAPI;

    @Override
    public Boolean isOCSFreeAuthorization(String terminalId) {
        Assert.notNull(terminalId, "terminal id is null");
        String ocsSn = terminalBasicInfoDAO.getOcsSnByTerminalId(terminalId);
        return !StringUtils.isEmpty(ocsSn);
    }

    @Override
    public boolean checkWhiteList(CbbShineTerminalBasicInfo terminalBasicInfo) {
        List<TerminalAuthorizationWhitelistEntity> whitelist = terminalAuthorizationWhitelistDao.findAllByOrderByPriorityDesc();
        //只有是TCI的设备，才需要去关注是否安装了OCS磁盘
        String[] compositeProductTypeAndSn = null;
        if (terminalBasicInfo.getPlatform() == CbbTerminalPlatformEnums.VOI) {
            String allDiskInfo = terminalBasicInfo.getAllDiskInfo();
            compositeProductTypeAndSn = getCompositeProductType(allDiskInfo);
        }

        for (TerminalAuthorizationWhitelistEntity entity : whitelist) {
            if (entity.getProductType().equals(terminalBasicInfo.getProductType())) {
                LOGGER.info("raw productType[{}] free authorization matched", terminalBasicInfo.getProductType());
                return true;
            } else {
                if (compositeProductTypeAndSn != null && entity.getProductType().equals(compositeProductTypeAndSn[0])) {
                    LOGGER.info("OCS productType[{}] free authorization matched", compositeProductTypeAndSn[0]);
                    TerminalEntity terminalEntity = terminalBasicInfoDAO.findByOcsSn(compositeProductTypeAndSn[1]);
                    if (terminalEntity != null && !terminalEntity.getMacAddr().equals(terminalBasicInfo.getMacAddr())) {
                        terminalEntity.setOcsSn(null);
                        terminalBasicInfoDAO.save(terminalEntity);
                        logAPI.recordLog(BusinessKey.RCDC_TERMINAL_OCS_AUTHORIZATION_KICK_OUT, terminalEntity.getTerminalName(),
                                terminalEntity.getTerminalId(), terminalBasicInfo.getTerminalName(), terminalBasicInfo.getTerminalId());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getOcsSnFromDiskInfo(String diskInfos) {
        String[] compositeProductTypeAndSn = getCompositeProductType(diskInfos);
        if (compositeProductTypeAndSn != null) {
            TerminalAuthorizationWhitelistEntity entity = terminalAuthorizationWhitelistDao.findByProductType(compositeProductTypeAndSn[0]);
            if (entity != null) {
                return compositeProductTypeAndSn[1];
            }
        }
        return null;
    }

    private String[] getCompositeProductType(String diskInfos) {
        try {
            JSONArray diskArray = JSONObject.parseArray(diskInfos);
            for (int i = 0; i < diskArray.size(); i++) {
                JSONObject disk = (JSONObject) diskArray.get(i);
                String diskType = (String) disk.get("dev_type");
                if (SYSTEM_DISK.equals(diskType)) {
                    String diskModel = disk.getString("dev_model");
                    String diskSn = disk.getString("dev_sn");
                    StringBuilder sb = new StringBuilder();
                    //获取序列号的第4、6、7位
                    String cpt = sb.append(diskModel)
                            .append("_")
                            .append(diskSn.charAt(3))
                            .append(diskSn.charAt(5))
                            .append(diskSn.charAt(6))
                            .toString();
                    LOGGER.info("composite productType[{}] is ", cpt);
                    return new String[]{cpt, diskSn};

                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("getCompositeProductType error happened!!!", e);
            return null;
        }
    }
}
