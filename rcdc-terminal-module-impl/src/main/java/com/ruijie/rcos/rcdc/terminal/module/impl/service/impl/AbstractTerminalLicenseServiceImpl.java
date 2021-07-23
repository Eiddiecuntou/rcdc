package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.springframework.util.CollectionUtils;

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

    protected static final Map<CbbTerminalLicenseTypeEnums, List<CbbTerminalLicenseInfoDTO>> LICENSE_MAP = Maps.newConcurrentMap();

    @Autowired
    protected GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    protected final Object usedNumLock = new Object();

    @Override
    public Integer getTerminalLicenseNum(@Nullable List<String> licenseCodeList) {
        List<CbbTerminalLicenseInfoDTO> licenseInfoList = LICENSE_MAP.get(getLicenseType());

        if (CollectionUtils.isEmpty(licenseInfoList)) {
            licenseInfoList = loadByDB();
        }

        int licenseNum = 0;
        for (CbbTerminalLicenseInfoDTO infoDTO : licenseInfoList) {
            if (CollectionUtils.isEmpty(licenseCodeList)) {
                licenseNum += infoDTO.getTotalNum();
                continue;
            }

            for (String licenseCode : licenseCodeList) {
                if (licenseCode.equals(infoDTO.getLicenseCode())) {
                    licenseNum += infoDTO.getTotalNum();
                }
            }
        }

        return licenseNum;
    }

    private List<CbbTerminalLicenseInfoDTO> loadByDB() {
        String terminalLicenseNum = globalParameterAPI.findParameter(getLicenseConstansKey());
        Assert.hasText(terminalLicenseNum, "terminalLicenseNum can not be empty");
        // [{"licenseCode": 123}, {"licenseCode": 123}]
        List<CbbTerminalLicenseInfoDTO> licenseInfoList = JSON.parseArray(terminalLicenseNum, CbbTerminalLicenseInfoDTO.class);
        LOGGER.info("从数据库同步[{}]licenseNum的值为:{}", getLicenseType(), terminalLicenseNum);
        LICENSE_MAP.put(getLicenseType(), licenseInfoList);

        return licenseInfoList;
    }

    abstract protected Integer getCacheLicenseNum();

    @Override
    public void updateTerminalLicenseNum(Integer licenseNum) throws BusinessException {
        Assert.notNull(licenseNum, "licenseNum can not be null");
        Assert.isTrue(licenseNum >= Constants.TERMINAL_AUTH_DEFAULT_NUM, "licenseNum must gt " + Constants.TERMINAL_AUTH_DEFAULT_NUM);

        synchronized (getLock()) {
            Integer currentNum = getTerminalLicenseNum(null);
            if (Objects.equals(currentNum, licenseNum)) {
                LOGGER.info("当前授权数量[{}]等于准备授权的数量[{}]，无须更新授权数量", currentNum, licenseNum);
                return;
            }

            // 授权证书为-1分为两种情况：RCDC首次初始化sql时将licenseNum初始化为-1。已导入临时证书，产品调用cbb接口，设licenseNum值为-1。
            // 授权证书为-1时，不限制终端授权，可接入任意数量IDV终端。
            if (currentNum == Constants.TERMINAL_AUTH_DEFAULT_NUM) {
                LOGGER.info("从终端授权数量为-1，导入正式授权证书场景。当前授权数量为：{}，准备授权的数量为：{}", currentNum, licenseNum);
                // fixMe 此处需考虑通知产品，断开shine连接
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
                LOGGER.info("当前授权数量为{}，准备更新授权数量为{}，当前授权数小于准备更新授权数，回收授权", currentNum, licenseNum);
                // fixMe 此处需考虑通知产品，断开shine连接
                processImportOfficialLicense(licenseNum);
                return;
            }

            LOGGER.info("当前授权数量为{}, 准备更新授权数量为{}，当前授权数大于准备更新授权数，更新授权数量", currentNum, licenseNum);
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
            Integer licenseNum = getTerminalLicenseNum(null);
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
