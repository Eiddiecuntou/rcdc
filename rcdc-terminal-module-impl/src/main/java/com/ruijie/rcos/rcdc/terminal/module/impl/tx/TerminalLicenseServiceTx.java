package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

/**
 * Description: 终端授权service事务接口
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/27 10:27 上午
 *
 * @author zhouhuan
 */
public interface TerminalLicenseServiceTx {

    /**
     * 将所有IDV终端，更新授权状态为已授权。并且更新数据库中IDV终端授权数量
     * @param licenseNum 授权数量
     */
    void updateAllIDVTerminalAuthedAndUpdateLicenseNum(Integer licenseNum);

    /**
     * 将所有IDV终端，更新授权状态为未授权。并且更新数据库中IDV终端授权数量
     * @param licenseNum 授权数量
     */
    void updateAllIDVTerminalUnauthedAndUpdateLicenseNum(Integer licenseNum);
}
