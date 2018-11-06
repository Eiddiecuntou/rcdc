package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.TerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.TerminalNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.TerminalNetworkRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

/**
 * Description: 终端基本信息操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface TerminalBasicInfoAPI {


    /**
     * 删除终端信息
     *
     * @param request
     */
    @NoRollback
    void delete(TerminalIdRequest request) throws BusinessException;

    /**
     * 根据terminalId获取终端基本信息
     *
     * @param request
     * @return
     */
    @NoRollback
    TerminalBasicInfoDTO findBasicInfoByTerminalId(TerminalIdRequest request) throws BusinessException;

    /**
     * 修改终端名称
     *
     * @param request
     */
    @NoRollback
    void modifyTerminalName(TerminalNameRequest request) throws BusinessException;

    /**
     * 修改终端网络配置
     *
     * @param request
     */
    @NoRollback
    void modifyTerminalNetworkConfig(TerminalNetworkRequest request) throws BusinessException;


}
