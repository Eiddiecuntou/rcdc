package com.ruijie.rcos.rcdc.terminal.module.impl.tx;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;

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
     * 将所有IDV终端，更新授权状态为已授权。并且更新数据库中IDV终端授权数量为-1，-1表示不限制idv终端授权数量
     * 
     * @param platform 终端类型
     * @param licenseKey 授权key
     * 
     */
    void updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums platform, String licenseKey);

    /**
     * 将所有IDV终端，根据证书类型更新授权状态为已授权。并且更新数据库中IDV终端相应证书类型的授权数量为-1，-1表示不限制idv终端授权数量
     * @param platform 终端类型
     * @param licenseKey 授权key
     * @param licenseType 证书类型
     */
    void updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums platform, String licenseKey, CbbTerminalLicenseTypeEnums licenseType);


    /**
     * 将所有IDV终端，更新授权状态为未授权。并且更新数据库中IDV终端授权数量
     * 
     * @param licenseNum 授权数量
     * @param platform 终端类型
     * @param licenseKey 授权key
     */
    void updateTerminalUnAuthedAndUpdateLicenseNum(CbbTerminalPlatformEnums platform, String licenseKey, Integer licenseNum);

    /**
     * 将所有IDV终端，根据证书类型，更新授权状态为未授权。并且更新数据库中IDV终端相应证书类型的授权数量
     * @param platform 终端类型
     * @param licenseKey 授权key
     * @param licenseNum 授权数量
     * @param licenseType 证书类型
     */
    void updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums platform, String licenseKey,
                                                   Integer licenseNum, CbbTerminalLicenseTypeEnums licenseType);
}
