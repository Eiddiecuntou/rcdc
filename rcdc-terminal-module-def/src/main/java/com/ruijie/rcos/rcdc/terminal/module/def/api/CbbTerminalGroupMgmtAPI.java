package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.terminal.TerminalGroupTreeNodeDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalGroupNameDuplicationRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbDeleteTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbEditTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbTerminalGroupRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;

import java.util.List;
import java.util.UUID;


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
     * @return UserGroupTreeResponse 响应
     */
    List<TerminalGroupDTO> getAllTerminalGroup();
    
    
    /**
     * 加载终端组树形结构(单层)
     *
     * @param request 加载终端树
     * @return UserGroupTreeResponse 终端组DTO
     * @throws BusinessException 业务异常
     */

    TerminalGroupTreeNodeDTO[] loadTerminalGroupCompleteTree(CbbGetTerminalGroupCompleteTreeRequest request) throws BusinessException;
    
    /**
     * 根据分组名称及父分组id获取分组
     * 
     * @param request 请求信息
     * @return 校验结果
     * @throws BusinessException 业务异常
     */

    TerminalGroupDTO getByName(CbbTerminalGroupRequest request) throws BusinessException;

    /**
     * 加载指定id终端组对象
     * 
     * @param groupId 请求参数id
     * @return TerminalGroupDTO 终端组DTO
     * @throws BusinessException 业务异常
     */

    TerminalGroupDTO loadById(UUID groupId) throws BusinessException;

    /**
     * @description 创建终端组
     * @param request 页面请求创建终端组参数
     * @return 创建终端组详细信息
     * @throws BusinessException 业务异常
     */
    TerminalGroupDTO createTerminalGroup(CbbTerminalGroupRequest request) throws BusinessException;

    /**
     * @description 编辑终端组
     * @param request 页面请求参数对象
     * @throws BusinessException 业务异常
     */
    
    void editTerminalGroup(CbbEditTerminalGroupRequest request) throws BusinessException;

    /**
     * @description 删除终端组
     * @param request 页面请求参数对象
     * @throws BusinessException 业务异常.
     */
    
    void deleteTerminalGroup(CbbDeleteTerminalGroupRequest request) throws BusinessException;

    /**
     *  获取终端分组路径数组
     *
     * @param groupId 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */

    String[] obtainGroupNamePathArr(UUID groupId) throws BusinessException;

    /**
     * 判断分组名称是否存在
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */

    boolean checkUseGroupNameDuplication(CbbTerminalGroupNameDuplicationRequest request) throws BusinessException;
}
