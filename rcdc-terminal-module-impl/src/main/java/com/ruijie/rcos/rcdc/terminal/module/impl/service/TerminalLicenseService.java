package com.ruijie.rcos.rcdc.terminal.module.impl.service;

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
     * 授权1台idv终端；如果授权数量为-1，或者有授权剩余，则终端已使用授权数量+1
     * @param terminalId 终端id
     * @return true 已授权或者授权成功；false 授权数不足，无法授权
     */
    boolean auth(String terminalId);
}
