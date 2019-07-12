package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal.CheckGroupNameDuplicationRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal.CreateTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal.DeleteTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal.EditTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal.GetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal.GetTerminalGroupTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.terminal.TerminalGroupIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.terminal.CheckGroupNameDuplicationResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.terminal.GetTerminalGroupTreeResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.terminal.TerminalGroupResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.DtxBusizContext;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;
import com.ruijie.rcos.sk.modulekit.api.tx.Rollback;

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
     * 加载终端组树形结构(单层)
     * 
     * @param request 加载终端树
     * @return UserGroupTreeResponse 终端组DTO
     * @throws BusinessException 业务异常
     */
    @NoRollback
    GetTerminalGroupTreeResponse loadTerminalGroupCompleteTree(GetTerminalGroupCompleteTreeRequest request) throws BusinessException;
    
    /**
     * 检验终端分组名称是否同级重复
     * 
     * @param request 校验请求信息
     * @return 校验结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CheckGroupNameDuplicationResponse checkNameDuplication(CheckGroupNameDuplicationRequest request) throws BusinessException;


    /**
     * 加载指定id终端组对象
     * 
     * @param request 请求参数id
     * @return TerminalGroupResponse 终端组DTO
     * @throws BusinessException 业务异常
     */
    @NoRollback
    TerminalGroupResponse loadById(TerminalGroupIdRequest request) throws BusinessException;

    /**
     * @description 创建终端组，产品业务组件维护，一个事务中操作不需要callback
     * @param request 页面请求创建终端组参数
     * @return Response
     * @throws BusinessException 业务异常
     */
    @Rollback(rollbackBy = "rollbackCreateTerminalGroup")
    DefaultResponse createTerminalGroup(CreateTerminalGroupRequest request) throws BusinessException;

    /**
     * 创建终端组失败时处理需要回滚的业务
     * 
     * @param context 创建用户组请求上下问
     */
    @NoRollback
    void rollbackCreateTerminalGroup(DtxBusizContext context);

    /**
     * @description 编辑终端组
     * @param request 页面请求参数对象
     * @return Response
     * @throws BusinessException 业务异常
     */
    @NoRollback
    DefaultResponse editTerminalGroup(EditTerminalGroupRequest request) throws BusinessException;

    /**
     * @description 删除终端组
     * @param request 页面请求参数对象
     * @return Response
     * @throws BusinessException 业务异常.
     */
    @NoRollback
    DefaultResponse deleteTerminalGroup(DeleteTerminalGroupRequest request) throws BusinessException;

}
