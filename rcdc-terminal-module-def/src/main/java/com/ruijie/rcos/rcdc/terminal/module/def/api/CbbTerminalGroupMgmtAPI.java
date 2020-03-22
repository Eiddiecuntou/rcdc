package com.ruijie.rcos.rcdc.terminal.module.def.api;

import java.util.List;
import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalGroupNameDuplicationRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbDeleteTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbEditTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbGetTerminalGroupTreeResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbObtainGroupNamePathResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbTerminalGroupResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbCheckGroupNameDuplicationResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.IdRequest;


/**
 * 
 * Description: 终端组API接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年07月08日
 * 
 * @author nt
 */
public interface CbbTerminalGroupMgmtAPI {

    /**
     * 默认终端分组id
     */
    UUID DEFAULT_TERMINAL_GROUP_ID = UUID.fromString("7769c0c6-473c-4d4c-9f47-5a62bdeb30ba");

    /**
     * 获取所有树节点信息
     *
     * @param request 默认请求
     * @return UserGroupTreeResponse 响应
     */
    DtoResponse<List<TerminalGroupDTO>> getAllTerminalGroup(DefaultRequest request);
    
    
    /**
     * 加载终端组树形结构(单层)
     *
     * @param request 加载终端树
     * @return UserGroupTreeResponse 终端组DTO
     * @throws BusinessException 业务异常
     */
    
    CbbGetTerminalGroupTreeResponse loadTerminalGroupCompleteTree(CbbGetTerminalGroupCompleteTreeRequest request) throws BusinessException;
    
    /**
     * 根据分组名称及父分组id获取分组
     * 
     * @param request 请求信息
     * @return 校验结果
     * @throws BusinessException 业务异常
     */
    
    CbbTerminalGroupResponse getByName(CbbTerminalGroupRequest request) throws BusinessException;

    /**
     * 加载指定id终端组对象
     * 
     * @param request 请求参数id
     * @return CbbTerminalGroupResponse 终端组DTO
     * @throws BusinessException 业务异常
     */
    
    CbbTerminalGroupResponse loadById(IdRequest request) throws BusinessException;

    /**
     * @description 创建终端组
     * @param request 页面请求创建终端组参数
     * @return 创建终端组详细信息
     * @throws BusinessException 业务异常
     */
    DtoResponse<TerminalGroupDTO> createTerminalGroup(CbbTerminalGroupRequest request) throws BusinessException;

    /**
     * @description 编辑终端组
     * @param request 页面请求参数对象
     * @return Response
     * @throws BusinessException 业务异常
     */
    
    DefaultResponse editTerminalGroup(CbbEditTerminalGroupRequest request) throws BusinessException;

    /**
     * @description 删除终端组
     * @param request 页面请求参数对象
     * @return Response
     * @throws BusinessException 业务异常.
     */
    
    DefaultResponse deleteTerminalGroup(CbbDeleteTerminalGroupRequest request) throws BusinessException;

    /**
     *  获取终端分组路径数组
     *
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    
    CbbObtainGroupNamePathResponse obtainGroupNamePathArr(IdRequest request) throws BusinessException;

    /**
     * 判断分组名称是否存在
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    
    CbbCheckGroupNameDuplicationResponse checkUseGroupNameDuplication(CbbTerminalGroupNameDuplicationRequest request) throws BusinessException;
}
