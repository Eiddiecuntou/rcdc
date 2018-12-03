package com.ruijie.rcos.rcdc.terminal.module.def.api;

import org.springframework.data.domain.Page;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNameRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalNetworkRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalPageRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultPageResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;

/**
 * Description: 终端基本信息操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/10/30
 *
 * @author Jarman
 */
public interface CbbTerminalBasicInfoAPI {

    /**
     * 删除终端信息
     *
     * @param request 请求参数对象
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void delete(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 根据terminalId获取终端基本信息
     *
     * @param request 请求参数对象
     * @return 终端基本信息DTO
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalBasicInfoDTO findBasicInfoByTerminalId(CbbTerminalIdRequest request) throws BusinessException;

    /**
     * 修改终端名称
     *
     * @param request 请求参数对象
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void modifyTerminalName(CbbTerminalNameRequest request) throws BusinessException;

    /**
     * 修改终端网络配置
     *
     * @param request 请求参数对象
     * @throws BusinessException 业务异常
     */
    @NoRollback
    void modifyTerminalNetworkConfig(CbbTerminalNetworkRequest request) throws BusinessException;

    /**
     * 终端列表分页
     * 
     * @param pageRequest 分页请求
     * 
     * @return 列表分页信息
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultPageResponse<CbbTerminalBasicInfoDTO> listTerminal(CbbTerminalPageRequest pageRequest)
            throws BusinessException;


}
