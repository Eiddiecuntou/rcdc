package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

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


    @Override
    public Integer getTerminalLicenseNum() {
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
    public Integer getUsedNum() {
        // 如果usedNum值为null，表示usedNum还没有从数据库同步数据
        if (usedNum == null) {
            usedNum = (int) terminalBasicInfoDAO.countByPlatform(CbbTerminalPlatformEnums.IDV);
            LOGGER.info("从数据库同步idv授权usedNum值为:{}", usedNum);
        }
        return usedNum;
    }

    @Override
    public void updateTerminalLicenseNum(Integer licenseNum) {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        Assert.isTrue(licenseNum >= TERMINAL_AUTH_DEFAULT_NUM, "licenseNum must gt " + TERMINAL_AUTH_DEFAULT_NUM);

        LOGGER.info("licenseNum 更新为 {}", licenseNum);
        globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, String.valueOf(licenseNum));
        this.licenseNum = licenseNum;
    }

    @Override
    public synchronized boolean auth(String terminalId) {
        Assert.hasText(terminalId,  "terminalId can not be empty");

        if (!basicInfoService.isNewTerminal(terminalId)) {
            LOGGER.info("终端[{}]已授权成功，无须再次授权", terminalId);
            return true;
        }

        Integer idvLicenseNum = getTerminalLicenseNum();
        Integer idvUsedNum = getUsedNum();

        if (Objects.equals(idvLicenseNum, TERMINAL_AUTH_DEFAULT_NUM) || idvLicenseNum > idvUsedNum) {
            LOGGER.info("终端[{}]可以授权，当前licenseNum：{}，usedNum：{}", terminalId, licenseNum, usedNum);
            usedNum++;
            return true;
        }
        LOGGER.info("idv终端授权已经没有剩余，当前licenseNum：{}，usedNum：{}", licenseNum, usedNum);
        return false;
    }

}
