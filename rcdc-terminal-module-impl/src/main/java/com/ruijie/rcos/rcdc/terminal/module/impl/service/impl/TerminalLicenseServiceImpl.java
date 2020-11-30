package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: TerminalLicenseService实现类
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

    @Autowired
    private TerminalLicenseServiceTx terminalLicenseServiceTx;

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
                usedNum = (int) terminalBasicInfoDAO.countByPlatformAndAuthed(CbbTerminalPlatformEnums.IDV, Boolean.TRUE);
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
    public void updateIDVTerminalLicenseNum(Integer licenseNum) throws BusinessException {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        Assert.isTrue(licenseNum >= TERMINAL_AUTH_DEFAULT_NUM, "licenseNum must gt " + TERMINAL_AUTH_DEFAULT_NUM);

        synchronized (usedNumLock) {
            Integer currentNum = getIDVTerminalLicenseNum();
            if (currentNum == licenseNum) {
                LOGGER.info("当前授权数量[{}]等于准备授权的数量[{}]，无须更新授权数量", currentNum, licenseNum);
                return;
            }

            // 授权证书为-1分为两种情况：RCDC首次初始化sql时将licenseNum初始化为-1。已导入临时证书，产品调用cbb接口，设licenseNum值为-1。
            // 授权证书为-1时，不限制终端授权，可接入任意数量IDV终端。
            if (currentNum == TERMINAL_AUTH_DEFAULT_NUM) {
                processMinusOne2NotMinuxOne(currentNum, licenseNum);
                return;
            }
            if (licenseNum == TERMINAL_AUTH_DEFAULT_NUM) {
                processNotMinusOne2MinusOne(currentNum, licenseNum);
                return;
            }

            LOGGER.info("当前授权数量和准备更新的授权数量不等，且都不等于-1。当前授权数量为{}, 准备更新授权数量为{}", currentNum, licenseNum);
            if (currentNum > licenseNum) {
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_ALLOW_REDUCE_TERMINAL_LICENSE_NUM);
            }

            globalParameterAPI.updateParameter(Constants.TEMINAL_LICENSE_NUM, String.valueOf(licenseNum));
            this.licenseNum = licenseNum;
        }
    }

    // 处理终端授权数量由-1，改成非-1的场景。更新数据库中所有已授权IDV终端授权状态为未授权，已授权数量改为0
    private void processMinusOne2NotMinuxOne(Integer currentNum, Integer licenseNum) {
        LOGGER.info("授权数量-1 -> 非-1场景。当前授权数量为：{}，准备授权的数量为：{}", currentNum, licenseNum);
        // 将所有已授权IDV终端置为未授权
        terminalLicenseServiceTx.updateIDVTerminalAuthStateAndLicenseNum(licenseNum, Boolean.TRUE, Boolean.FALSE);
        this.usedNum = 0;
        this.licenseNum = licenseNum;
    }

    // 处理终端授权数量由非-1，改成-1的场景。更新t_cbb_termianl中所有未授权IDV终端授权状态为已授权
    private void processNotMinusOne2MinusOne(Integer currentNum, Integer licenseNum) {
        LOGGER.info("授权数量非-1 -> -1场景。当前授权数量为：{}，准备授权的数量为：{}", currentNum, licenseNum);
        terminalLicenseServiceTx.updateIDVTerminalAuthStateAndLicenseNum(licenseNum, Boolean.FALSE, Boolean.TRUE);
        this.licenseNum = licenseNum;
    }

    @Override
    public boolean authIDV(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {
        Assert.hasText(terminalId, "terminalId can not be empty");
        Assert.notNull(basicInfo, "basicInfo can not be null");
        synchronized (usedNumLock) {
            if (basicInfoService.isAuthed(terminalId)) {
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
