package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: TerminalService实现类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/17 5:35 下午
 *
 * @author zhouhuan
 */
@Service
public class TerminalLicenseServiceImpl implements TerminalLicenseService {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseServiceImpl.class);

    /**
     * 终端证书默认数量，-1表示授权不受限制。同时也是授权数量的最小值
     */
    private static Integer TERMINAL_AUTH_DEFAULT_NUM = -1;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    private Integer licenseNum;

    private Integer usedNum;

    private Object usedNumLock = new Object();

    @Override
    public Integer getIDVTerminalLicenseNum() {
        // licenseNum如果为null，表示licenseNum还没有从数据库同步数据。
        if (licenseNum == null) {
            String terminalLicenseNum = globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
            Assert.hasText(terminalLicenseNum, "terminalLicenseNum can not be empty");
            licenseNum = Integer.valueOf(terminalLicenseNum);
            LOGGER.info("从数据库同步licenseNum的值为:{}", licenseNum);
        }
        return licenseNum;
    }

    @Override
    public Integer getIDVUsedNum() {
        synchronized (usedNumLock) {
            // 如果usedNum值为null，表示usedNum还没有从数据库同步数据;licenseNum为-1时，不会维护已授权数目，所以需要从数据库同步数据
            if (usedNum == null || getIDVTerminalLicenseNum() == -1) {
                usedNum = (int) terminalBasicInfoDAO.countByPlatform(CbbTerminalPlatformEnums.IDV);
                LOGGER.info("从数据库同步idv授权usedNum值为:{}", usedNum);
            }
        }
        return usedNum;
    }

    @Override
    public void decreaseIDVTerminalLicenseUsedNum() {
        synchronized (usedNumLock) {
            usedNum--;
        }
    }

    @Override
    public void updateIDVTerminalLicenseNum(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        Assert.isTrue(licenseNum >= TERMINAL_AUTH_DEFAULT_NUM, "licenseNum must gt " + TERMINAL_AUTH_DEFAULT_NUM);

        LOGGER.info("licenseNum 更新为 {}", licenseNum);
        synchronized (usedNumLock) {
            globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, String.valueOf(licenseNum));
            if (getIDVTerminalLicenseNum() == -1 && licenseNum != -1) {
                LOGGER.info("licenseNum为-1时不实时维护usedNum的值，不为-1时实时维护usedNum的值。licenseNum由-1变更为非-1，从数据库同步已授权终端数");
                this.usedNum = (int) terminalBasicInfoDAO.countByPlatform(CbbTerminalPlatformEnums.IDV);
            }
            this.licenseNum = licenseNum;
        }
    }

    @Override
    public boolean authIDV(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {
        Assert.hasText(terminalId, "terminalId can not be empty");
        Assert.notNull(basicInfo, "basicInfo can not be null");
        synchronized (usedNumLock) {
            if (!basicInfoService.isNewTerminal(terminalId)) {
                LOGGER.info("终端[{}]已授权成功，无须再次授权", terminalId);
                return true;
            }
            Integer idvLicenseNum = getIDVTerminalLicenseNum();
            Integer idvUsedNum = getIDVUsedNum();
            if (!Objects.equals(idvLicenseNum, TERMINAL_AUTH_DEFAULT_NUM) && idvUsedNum >= idvLicenseNum) {
                LOGGER.info("idv终端授权已经没有剩余，当前licenseNum：{}，usedNum：{}", licenseNum, usedNum);
                return false;
            }
            LOGGER.info("终端[{}]可以授权，当前licenseNum：{}，usedNum：{}", terminalId, licenseNum, usedNum);
            basicInfoService.saveBasicInfo(terminalId, isNewConnection, basicInfo);
            usedNum++;
            return true;
        }
    }

}
