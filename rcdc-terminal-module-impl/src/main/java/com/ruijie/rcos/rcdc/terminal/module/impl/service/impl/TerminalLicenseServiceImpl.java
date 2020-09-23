package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
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
    private static int TERMINAL_AUTH_DEFAULT_NUM = -1;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    /**
     * 授权数量、已授权数量初始化的值，此时还没有从数据库同步数据
     */
    private static final int INITIAL_NUM = -2;

    private int licenseNum = INITIAL_NUM;

    private int usedNum = INITIAL_NUM;


    @Override
    public int getTerminalLicenseNum() {
        // 业务中licenseNum不可能值为INITIAL_NUM。如果licenseNum值为INITIAL_NUM，表示licenseNum还没有从数据库同步数据。
        if (licenseNum == INITIAL_NUM) {
            String terminalLicenseNum = globalParameterAPI.findParameter(Constants.TEMINAL_LICENSE_NUM);
            Assert.hasText(terminalLicenseNum, "terminalLicenseNum can not be empty");
            licenseNum = Integer.parseInt(terminalLicenseNum);
        }
        return licenseNum;
    }

    @Override
    public int getUsedNum() {
        // 如果usedNum值为INITIAL_NUM，表示usedNum还没有从数据库同步数据
        if (usedNum == INITIAL_NUM) {
            usedNum = (int)terminalBasicInfoDAO.count();
        }
        return usedNum;
    }

    @Override
    public void updateTerminalLicenseNum(int licenseNum) {
        Assert.isTrue(licenseNum >= TERMINAL_AUTH_DEFAULT_NUM, "licenseNum must gt " + TERMINAL_AUTH_DEFAULT_NUM);

        LOGGER.info("licenseNum 更新为 {}", licenseNum);
        globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, String.valueOf(licenseNum));
        this.licenseNum = licenseNum;
    }

    @Override
    public synchronized boolean isAuthedOrAuthSuccess(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {
        Assert.hasText(terminalId,  "terminalId can not be empty");
        Assert.notNull(basicInfo, "basicInfo can not be null");
        // 不是新终端，已授权成功
        if (!basicInfoService.isNewTerminal(terminalId)) {
            LOGGER.info("终端{}[{}]已授权", terminalId, basicInfo.getTerminalName());
            basicInfoService.saveBasicInfo(terminalId, isNewConnection, basicInfo);
            return true;
        }

        int idvLicenseNum = getTerminalLicenseNum();
        int idvUsedNum = getUsedNum();

        if (idvLicenseNum == TERMINAL_AUTH_DEFAULT_NUM || idvLicenseNum > idvUsedNum) {
            LOGGER.info("终端{}[{}]可以授权，当前licenseNum：{}，usedNum：{}", basicInfo.getTerminalName(), terminalId, licenseNum, usedNum);
            basicInfoService.saveBasicInfo(terminalId, isNewConnection, basicInfo);
            usedNum++;
            return true;
        }
        LOGGER.info("idv终端授权已经没有剩余，当前licenseNum：{}，usedNum：{}", licenseNum, usedNum);
        return false;
    }

}
