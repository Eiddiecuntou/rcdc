package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseCommonService;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLicenseService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Autowired
    private TerminalLicenseCommonService terminalLicenseCommonService;

    protected final Object usedNumLock = new Object();

    protected Integer usedNum = null;

    @Override
    public Integer getUsedNum() {

        synchronized (this.getLock()) {
            // 如果usedNum值为null，表示usedNum还没有从数据库同步数据;licenseNum为-1时，代表临时授权不会维护已授权数目，所以需要从数据库同步数据
            final Integer terminalLicenseNum = this.getAllTerminalLicenseNum();
            final boolean isTempLicense = isTempLicense(terminalLicenseNum);
            if (usedNum == null || isTempLicense) {
                long count = terminalAuthorizeDAO.countByLicenseTypeContaining(getLicenseType().name());
                usedNum = (int) count;
                LOGGER.info("从数据库同步[{}]授权已用数为：{},授权数为：{}", getLicenseType(), usedNum, terminalLicenseNum);
            }
        }

        return usedNum;
    }

    @Override
    public void increaseCacheLicenseUsedNum() {
        synchronized (usedNumLock) {
            if (usedNum == null) {
                usedNum = getUsedNum();
            }

            usedNum++;
        }
    }

    @Override
    public void decreaseCacheLicenseUsedNum() {
        synchronized (usedNumLock) {
            if (usedNum == null) {
                usedNum = getUsedNum();
            }

            if (usedNum > 0) {
                usedNum--;
            }
        }
    }

    @Override
    public Integer getAllTerminalLicenseNum() {
        return countLicenseNum(null);
    }

    @Override
    public Integer getTerminalLicenseNum(List<String> licenseCodeList) {
        Assert.notNull(licenseCodeList, "licenseCodeList can not be null");
        Assert.isTrue(licenseCodeList.size() > 0, "licenseCodeList is empty");

        return countLicenseNum(licenseCodeList);
    }

    @Override
    public List<CbbTerminalLicenseInfoDTO> getTerminalLicenseInfo(List<String> licenseCodeList) {
        Assert.notNull(licenseCodeList, "licenseCodeList can not be null");

        List<CbbTerminalLicenseInfoDTO> licenseInfoList = LICENSE_MAP.get(getLicenseType());
        if (CollectionUtils.isEmpty(licenseInfoList)) {
            return Lists.newArrayList();
        }

        if (CollectionUtils.isEmpty(licenseCodeList)) {
            return licenseInfoList;
        }

        return licenseInfoList.stream()
                .filter(licenseInfo -> licenseCodeList.stream().anyMatch(licenseCode -> licenseCode.equals(licenseInfo.getLicenseCode())))
                .collect(Collectors.toList());
    }

    private Integer countLicenseNum(List<String> licenseCodeList) {
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


    @Override
    public void updateTerminalLicenseNum(List<CbbTerminalLicenseInfoDTO> licenseInfoList) throws BusinessException {
        Assert.notNull(licenseInfoList, "licenseInfoList can not be null");

        synchronized (getLock()) {
            updateLicenseNum(licenseInfoList);
        }
    }

    private void updateLicenseNum(List<CbbTerminalLicenseInfoDTO> licenseInfoList) {

        int licenseNum = licenseInfoList.stream().mapToInt(licenseInfo -> licenseInfo.getTotalNum()).sum();
        if (licenseNum < Constants.TERMINAL_AUTH_DEFAULT_NUM) {
            licenseNum = Constants.TERMINAL_AUTH_DEFAULT_NUM;
        }
        Integer currentNum = getAllTerminalLicenseNum();

        if (Objects.equals(currentNum, licenseNum)) {
            LOGGER.info("当前授权数量[{}]等于准备授权的数量[{}]，只更新缓存及数据库", currentNum, licenseNum);
            updateCacheAndDbLicenseNum(licenseInfoList, licenseNum, currentNum);
            return;
        }

        // 授权证书为-1分为两种情况：RCDC首次初始化sql时将licenseNum初始化为-1。已导入临时证书，产品调用cbb接口，设licenseNum值为-1。
        // 授权证书为-1时，不限制终端授权，可接入任意数量IDV终端。
        if (Objects.equals(currentNum, Constants.TERMINAL_AUTH_DEFAULT_NUM)) {
            LOGGER.info("从终端授权数量为-1，导入正式授权证书场景。当前授权数量为：{}，准备授权的数量为：{}", currentNum, licenseNum);
            // fixMe 此处需考虑通知产品，断开shine连接
            processImportOfficialLicense(licenseNum);
        } else if (licenseNum == Constants.TERMINAL_AUTH_DEFAULT_NUM) {
            LOGGER.info("从终端授权数量不是-1，导入临时授权证书场景。当前授权数量为：{}，准备授权的数量为：{}", currentNum, licenseNum);
            processImportTempLicense();
        } else if (currentNum > licenseNum) {
            LOGGER.info("当前授权数量为{}，准备更新授权数量为{}，当前授权数小于准备更新授权数，回收授权", currentNum, licenseNum);
            // fixMe 此处需考虑通知产品，断开shine连接
            processImportOfficialLicense(licenseNum);
        }

        updateCacheAndDbLicenseNum(licenseInfoList, licenseNum, currentNum);

    }

    private void updateCacheAndDbLicenseNum(List<CbbTerminalLicenseInfoDTO> licenseInfoList, int licenseNum, Integer currentNum) {
        LOGGER.info("当前授权数量为{}, 准备更新授权数量为{}，当前授权数大于准备更新授权数，更新授权数量", currentNum, licenseNum);

        globalParameterAPI.updateParameter(getLicenseConstansKey(), JSON.toJSONString(licenseInfoList));
        LICENSE_MAP.put(getLicenseType(), licenseInfoList);
    }

    @Override
    public boolean auth(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo) {
        Assert.hasText(terminalId, "terminalId can not be empty");
        Assert.notNull(basicInfo, "basicInfo can not be null");
        synchronized (getLock()) {
            if (terminalLicenseCommonService.isTerminalAuthed(terminalId)) {
                LOGGER.info("终端[{}]已授权成功，无须再次授权", terminalId);
                return true;
            }
            Integer licenseNum = getAllTerminalLicenseNum();
            if (licenseNum < Constants.TERMINAL_AUTH_DEFAULT_NUM) {
                LOGGER.info("获取到的总授权数小于临时授权数，说明有多个临时授权，设置数量为临时授权数量");
                licenseNum = Constants.TERMINAL_AUTH_DEFAULT_NUM;
            }
            Integer usedNum = getUsedNum();
            if (!Objects.equals(licenseNum, Constants.TERMINAL_AUTH_DEFAULT_NUM) && usedNum >= licenseNum) {
                LOGGER.info("{}类型终端授权已经没有剩余，当前licenseNum：{}，usedNum：{}", getLicenseType(), licenseNum, usedNum);
                return false;
            }
            LOGGER.info("终端[{}]可以授权，当前licenseNum：{}，usedNum：{}", terminalId, licenseNum, usedNum);

            this.increaseCacheLicenseUsedNum();
            return true;
        }
    }

    @Override
    public boolean checkEnableAuth(CbbTerminalPlatformEnums authMode) {
        Assert.notNull(authMode, "authMode can not be null");

        Integer usedNum = getUsedNum();
        Integer licenseNum = getAllTerminalLicenseNum();

        if (!Objects.equals(licenseNum, Constants.TERMINAL_AUTH_DEFAULT_NUM) && usedNum >= licenseNum) {
            LOGGER.info("{}类型终端授权已经没有剩余，当前licenseNum：{}，usedNum：{}", getLicenseType(), usedNum, licenseNum);
            return false;
        }

        LOGGER.info("[{}]可以授权，当前licenseNum：{}，usedNum：{}", authMode, licenseNum, usedNum);
        return true;
    }

    boolean isTempLicense(Integer licenseNum) {
        return Objects.equals(licenseNum, Constants.TERMINAL_AUTH_DEFAULT_NUM);
    }

}
