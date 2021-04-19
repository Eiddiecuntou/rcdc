package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description: TerminalLicenseService实现类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/17 5:35 下午
 *
 * @author zhouhuan
 */
@Service
public abstract class AbstractTerminalLicenseServiceImpl implements TerminalLicenseService {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractTerminalLicenseServiceImpl.class);

    @Autowired
    protected GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    protected final Object usedNumLock = new Object();

    @Override
    public Integer getTerminalLicenseNum() {
        // licenseNum如果为null，表示licenseNum还没有从数据库同步数据。
        Integer licenseNum = getCacheLicenseNum();
        if (licenseNum == null) {
            String terminalLicenseNum = globalParameterAPI.findParameter(getLicenseConstansKey());
            Assert.hasText(terminalLicenseNum, "terminalLicenseNum can not be empty");
            licenseNum = Integer.valueOf(terminalLicenseNum);
            LOGGER.info("从数据库同步[{}]licenseNum的值为:{}", getLicenseType(), licenseNum);
        }
        return licenseNum;
    }

    abstract protected Integer getCacheLicenseNum();

    @Override
    public void updateTerminalLicenseNum(Integer licenseNum) throws BusinessException {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        Assert.isTrue(licenseNum >= Constants.TERMINAL_AUTH_DEFAULT_NUM, "licenseNum must gt " + Constants.TERMINAL_AUTH_DEFAULT_NUM);

        synchronized (getLock()) {
            Integer currentNum = getTerminalLicenseNum();
            if (Objects.equals(currentNum, licenseNum)) {
                LOGGER.info("当前授权数量[{}]等于准备授权的数量[{}]，无须更新授权数量", currentNum, licenseNum);
                return;
            }

            // 授权证书为-1分为两种情况：RCDC首次初始化sql时将licenseNum初始化为-1。已导入临时证书，产品调用cbb接口，设licenseNum值为-1。
            // 授权证书为-1时，不限制终端授权，可接入任意数量IDV终端。
            if (currentNum == Constants.TERMINAL_AUTH_DEFAULT_NUM) {
                LOGGER.info("从终端授权数量为-1，导入正式授权证书场景。当前授权数量为：{}，准备授权的数量为：{}", currentNum, licenseNum);
                processImportOfficialLicense(licenseNum);
                return;
            }
            if (licenseNum == Constants.TERMINAL_AUTH_DEFAULT_NUM) {
                LOGGER.info("从终端授权数量不是-1，导入临时授权证书场景。当前授权数量为：{}，准备授权的数量为：{}", currentNum, licenseNum);
                processImportTempLicense();
                return;
            }

            LOGGER.info("当前授权数量和准备更新的授权数量不等，且都不等于-1。当前授权数量为{}, 准备更新授权数量为{}", currentNum, licenseNum);
            if (currentNum > licenseNum) {
                throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_ALLOW_REDUCE_TERMINAL_LICENSE_NUM);
            }

            globalParameterAPI.updateParameter(getLicenseConstansKey(), String.valueOf(licenseNum));
            updateCacheLicenseNum(licenseNum);
        }
    }

    @Override
    public boolean auth(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {
        Assert.hasText(terminalId, "terminalId can not be empty");
        Assert.notNull(basicInfo, "basicInfo can not be null");
        synchronized (getLock()) {
            if (basicInfoService.isAuthed(terminalId)) {
                LOGGER.info("终端[{}]已授权成功，无须再次授权", terminalId);
                return true;
            }
            Integer licenseNum = getTerminalLicenseNum();
            Integer usedNum = getUsedNum();
            if (!Objects.equals(licenseNum, Constants.TERMINAL_AUTH_DEFAULT_NUM) && usedNum >= licenseNum) {
                LOGGER.info("{}类型终端授权已经没有剩余，当前licenseNum：{}，usedNum：{}", getLicenseType(), usedNum, licenseNum);
                return false;
            }
            LOGGER.info("终端[{}]可以授权，当前licenseNum：{}，usedNum：{}", terminalId, licenseNum, usedNum);
            basicInfoService.saveBasicInfo(terminalId, isNewConnection, basicInfo, Boolean.TRUE);
            this.increaseCacheLicenseUsedNum();
            return true;
        }
    }

    boolean isTempLicense(Integer licenseNum) {
        return Objects.equals(licenseNum, Constants.TERMINAL_AUTH_DEFAULT_NUM);
    }

}
