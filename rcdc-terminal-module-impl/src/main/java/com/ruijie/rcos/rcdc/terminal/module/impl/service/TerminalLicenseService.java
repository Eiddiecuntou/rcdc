package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Description: 终端授权service
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/17 5:34 下午
 *
 * @author zhouhuan
 */
public interface TerminalLicenseService {

    /**
     * 获取终端授权总数
     *
     * @return 授权总数
     */
    Integer getAllTerminalLicenseNum();

    /**
     * 根据证书码获取终端授权数量
     *
     * @param licenseCodeList 证书码列表
     * @return 授权总数
     */
    Integer getTerminalLicenseNum(List<String> licenseCodeList);

    /**
     * 更新缓存值
     * 
     * @param licenseNum 授权数量
     */
    void updateCacheLicenseNum(Integer licenseNum);

    /**
     * 获取已使用的终端授权数量
     * 
     * @return 已使用授权数量
     */
    Integer getUsedNum();

    /**
     * 锁
     * 
     * @return Object
     */
    Object getLock();

    /**
     * 获取授权类型
     * 
     * @return 授权类型
     */
    CbbTerminalLicenseTypeEnums getLicenseType();

    /**
     * 获取授权类型
     * 
     * @return 授权类型
     */
    String getLicenseConstansKey();

    /**
     * 已授权数+1
     */
    void increaseCacheLicenseUsedNum();

    /**
     * 已授权数-1
     */
    void decreaseCacheLicenseUsedNum();

    /**
     * 更新终端授权总数
     * 
     * @param licenseInfoList 终端授权数量信息
     * @throws BusinessException 业务异常
     */
    void updateTerminalLicenseNum(List<CbbTerminalLicenseInfoDTO> licenseInfoList) throws BusinessException;

    /**
     * 授权1台终端；如果授权数量为-1，或者有授权剩余，则终端已使用授权数量+1
     * 
     * @param terminalId 终端id
     * @param isNewConnection 是否是新终端接入
     * @param basicInfo shine上报的终端基本信息
     * @return true 已授权或者授权成功；false 授权数不足，无法授权
     */
    boolean auth(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo);

    /**
     * 处理从终端授权数量为-1，导入正式授权证书场景。
     * 
     * @param licenseNum 终端授权数量
     */
    void processImportOfficialLicense(Integer licenseNum);

    /**
     * 处理从终端授权数量不为-1，导入临时证书场景
     */
    void processImportTempLicense();

}
