package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;

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
    Integer getTerminalLicenseNum();

    /**
     * 获取已使用的终端授权数量
     * @return 已使用授权数量
     */
    Integer getUsedNum();

    /**
     * 更新终端授权总数
     * @param authNum 终端授权数量
     */
    void updateTerminalLicenseNum(Integer authNum);

    /**
     * 检查1台idv是否已经授权；如果未授权，并且有授权剩余，则终端已使用授权数量+1，保存终端信息到数据库
     * @param terminalId 终端id
     * @param isNewConnection 是否是新连接
     * @param basicInfo shine上报的终端基本信息
     * @return true 已授权或者授权成功；false 授权数不足，无法授权
     */
    boolean isAuthedOrAuthSuccess(String terminalId, boolean isNewConnection, CbbShineTerminalBasicInfo basicInfo);
}
