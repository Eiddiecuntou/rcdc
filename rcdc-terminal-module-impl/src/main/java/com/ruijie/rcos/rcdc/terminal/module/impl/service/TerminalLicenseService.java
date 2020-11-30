package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;

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
     * @return 授权总数
     */
    Integer getIDVTerminalLicenseNum();

    /**
     * 获取已使用的终端授权数量
     * @return 已使用授权数量
     */
    Integer getIDVUsedNum();

    /**
     * 已授权数-1
     */
    void decreaseIDVTerminalLicenseUsedNum();

    /**
     * 更新终端授权总数
     * @param licenseNum 终端授权数量
     * @throws BusinessException 业务异常
     */
    void updateIDVTerminalLicenseNum(Integer licenseNum) throws BusinessException;

    /**
     * 授权1台idv终端；如果授权数量为-1，或者有授权剩余，则终端已使用授权数量+1
     * @param terminalId 终端id
     * @param isNewConnection 是否是新终端接入
     * @param basicInfo shine上报的终端基本信息
     * @return true 已授权或者授权成功；false 授权数不足，无法授权
     */
    boolean authIDV(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo);

}
