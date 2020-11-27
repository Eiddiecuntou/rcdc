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
     * 找出所有授权状态为expectAuthState的IDV终端，更新它们的授权状态，并且更新数据库中IDV终端授权数量
     * @param licenseNum 授权数量
     * @param expectAuthState 期望终端的授权状态
     * @param updateAuthState 要更新成的终端授权状态
     */
    void updateIDVTerminalAuthStateAndLicenseNum(Integer licenseNum, Boolean expectAuthState, Boolean updateAuthState);
}
