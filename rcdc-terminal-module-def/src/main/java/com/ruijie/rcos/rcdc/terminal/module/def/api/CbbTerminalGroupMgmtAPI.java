package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbCheckGroupNameDuplicationRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbCreateTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbDeleteTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbEditTerminalGroupRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbGetTerminalGroupCompleteTreeRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.group.CbbTerminalGroupIdRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbCheckGroupNameDuplicationResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbGetTerminalGroupTreeResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbObtainGroupNamePathResponse;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.group.CbbTerminalGroupResponse;
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
    CbbGetTerminalGroupTreeResponse loadTerminalGroupCompleteTree(CbbGetTerminalGroupCompleteTreeRequest request) throws BusinessException;
    
    /**
     * 检验终端分组名称是否同级重复
     * 
     * @param request 校验请求信息
     * @return 校验结果
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbCheckGroupNameDuplicationResponse checkNameDuplication(CbbCheckGroupNameDuplicationRequest request) throws BusinessException;


    /**
     * 加载指定id终端组对象
     * 
     * @param request 请求参数id
     * @return CbbTerminalGroupResponse 终端组DTO
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbTerminalGroupResponse loadById(CbbTerminalGroupIdRequest request) throws BusinessException;

    /**
     * @description 创建终端组，产品业务组件维护，一个事务中操作不需要callback
     * @param request 页面请求创建终端组参数
     * @return Response
     * @throws BusinessException 业务异常
     */
    @Rollback(rollbackBy = "rollbackCreateTerminalGroup")
    DefaultResponse createTerminalGroup(CbbCreateTerminalGroupRequest request) throws BusinessException;

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
    DefaultResponse editTerminalGroup(CbbEditTerminalGroupRequest request) throws BusinessException;

    /**
     * @description 删除终端组
     * @param request 页面请求参数对象
     * @return Response
     * @throws BusinessException 业务异常.
     */
    @NoRollback
    DefaultResponse deleteTerminalGroup(CbbDeleteTerminalGroupRequest request) throws BusinessException;

    /**
     *  获取终端分组路径数组
     *
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @NoRollback
    CbbObtainGroupNamePathResponse obtainGroupNamePathArr(CbbTerminalGroupIdRequest request) throws BusinessException;

}
