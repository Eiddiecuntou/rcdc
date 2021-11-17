package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;

/**
 * Description:
 * Copyright: Copyright (c) 2021
 * Company: Ruijie Co., Ltd.
 * Create Time: 2021/9/16
 *
 * @author zhangsiming
 */
public interface TerminalAuthorizationWhitelistService {
    /**
     * @param terminalId 终端id
     * @return 返回该终端是否为OCS默认授权
     */
    Boolean isOCSFreeAuthorization(String terminalId);

    /**
     * 检验终端是否在白名单中
     *
     * @param terminalBasicInfo 终端信息
     * @return boolean 是否在终端白名单中
     */
    boolean checkWhiteList(CbbShineTerminalBasicInfo terminalBasicInfo);


    /**
     * @param terminalEntity 需要为该对象填充ocsSn字段
     * @param diskInfo shine上报的磁盘信息
     */
    void fillOcsSnAndRecycleIfAuthed(TerminalEntity terminalEntity, String diskInfo);

}
