package com.ruijie.rcos.rcdc.terminal.module.impl.auth;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dto.TempLicCreateDTO;

import java.util.List;

/**
 * Description: 授权策略服务接口
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/7/15 17:26
 *
 * @author TING
 */
public interface StrategyService {

    /**
     *  初始化临时证书
     *
     * @param tempLicCreateDTO 临时证书初始化对象
     */
    void init(TempLicCreateDTO tempLicCreateDTO);


    /**
     *  授权分配
     *
     * @param licenseTypeList 证书类型列表
     * @param authMode 授权模式
     * @return 是否分配成功
     */
    boolean checkAllocate(List<CbbTerminalLicenseTypeEnums> licenseTypeList, CbbTerminalPlatformEnums authMode);

    /**
     *  授权分配
     *
     * @param licenseTypeList 证书类型列表
     * @param authMode 授权类型
     * @param terminalId 终端id
     * @return 是否分配成功
     */
    boolean allocate(String terminalId, CbbTerminalPlatformEnums authMode, List<CbbTerminalLicenseTypeEnums> licenseTypeList);

    /**
     *  授权回收
     *
     * @param licenseTypeList 证书类型列表
     * @param authMode 授权类型
     * @param terminalId 终端id
     * @param isCvaAuthed 是否云应用授权
     * @return 是否回收成功
     */
    boolean recycle(String terminalId, CbbTerminalPlatformEnums authMode, List<CbbTerminalLicenseTypeEnums> licenseTypeList,
                    Boolean isCvaAuthed);

}
