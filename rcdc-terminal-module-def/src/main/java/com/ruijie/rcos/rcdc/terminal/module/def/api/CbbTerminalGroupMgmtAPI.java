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
     * @api {POST} CbbTerminalGroupMgmtAPI.getAllTerminalGroup 获取所有树节点信息
     * @apiName getAllTerminalGroup
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 获取所有树节点信息
     * @apiParam (请求体字段说明) {void} request 无请求参数
     *
     * @apiSuccess (响应字段说明) {TerminalGroupDTO[]}  itemArr 终端分组树形结构数组
     * @apiSuccess (响应字段说明) {UUID} itemArr.id 终端分组id
     * @apiSuccess (响应字段说明) {String} itemArr.groupName 终端分组名称
     * @apiSuccess (响应字段说明) {UUID} itemArr.parentGroupId 父级分组id
     * @apiSuccess (响应字段说明) {String} itemArr.parentGroupName 父级分组名称
     * @apiSuccess (响应字段说明) {Boolean} itemArr.enableDefault
     */
    /**
     * 获取所有树节点信息
     *
     * @return UserGroupTreeResponse 响应
     */
    List<TerminalGroupDTO> getAllTerminalGroup();


    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.loadTerminalGroupCompleteTree 加载终端组树形结构(单层)
     * @apiName loadTerminalGroupCompleteTree
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 加载终端组树形结构(单层)
     * @apiParam (请求体字段说明) {CbbGetTerminalGroupCompleteTreeRequest} request CbbGetTerminalGroupCompleteTreeRequest
     * @apiParam (请求体字段说明) {Boolean} request.enableFilterDefaultGroup enableFilterDefaultGroup
     * @apiParam (请求体字段说明) {UUID} [request.filterGroupId] filterGroupId
     *
     * @apiSuccess (响应字段说明) {TerminalGroupTreeNodeDTO[]}  itemArr 终端分组树形结构数组
     * @apiSuccess (响应字段说明) {UUID} itemArr.id id
     * @apiSuccess (响应字段说明) {String} itemArr.label label
     * @apiSuccess (响应字段说明) {Boolean} itemArr.enableDefault enableDefault
     * @apiSuccess (响应字段说明) {UUID} itemArr.parentId 父级节点id，用于组装树形结构，序列化时忽略该属性
     */
    /**
     * 加载终端组树形结构(单层)
     *
     * @param request 加载终端树
     * @return UserGroupTreeResponse 终端组DTO
     * @throws BusinessException 业务异常
     */

    TerminalGroupTreeNodeDTO[] loadTerminalGroupCompleteTree(CbbGetTerminalGroupCompleteTreeRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.getByName 根据分组名称及父分组id获取分组
     * @apiName getByName
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 根据分组名称及父分组id获取分组
     * @apiParam (请求体字段说明) {CbbTerminalGroupRequest} request CbbTerminalGroupRequest
     * @apiParam (请求体字段说明) {String} request.groupName 终端分组名称
     * @apiParam (请求体字段说明) {UUID} [request.parentGroupId] 父级分组id
     *
     * @apiSuccess (响应字段说明) {TerminalGroupDTO}  terminalGroupDTO 终端分组dto
     * @apiSuccess (响应字段说明) {UUID} terminalGroupDTO.id 终端分组id
     * @apiSuccess (响应字段说明) {String} terminalGroupDTO.groupName 终端分组名称
     * @apiSuccess (响应字段说明) {UUID} terminalGroupDTO.parentGroupId 父级分组id
     * @apiSuccess (响应字段说明) {String} terminalGroupDTO.parentGroupName 父级分组名称
     * @apiSuccess (响应字段说明) {Boolean} terminalGroupDTO.enableDefault
     */
    /**
     * 根据分组名称及父分组id获取分组
     *
     * @param request 请求信息
     * @return 校验结果
     * @throws BusinessException 业务异常
     */

    TerminalGroupDTO getByName(CbbTerminalGroupRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.loadById 加载指定id终端组对象
     * @apiName loadById
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 加载指定id终端组对象
     * @apiParam (请求体字段说明) {UUID} groupId 终端组id
     *
     * @apiSuccess (响应字段说明) {TerminalGroupDTO}  terminalGroupDTO 终端分组dto
     * @apiSuccess (响应字段说明) {UUID} terminalGroupDTO.id 终端分组id
     * @apiSuccess (响应字段说明) {String} terminalGroupDTO.groupName 终端分组名称
     * @apiSuccess (响应字段说明) {UUID} terminalGroupDTO.parentGroupId 父级分组id
     * @apiSuccess (响应字段说明) {String} terminalGroupDTO.parentGroupName 父级分组名称
     * @apiSuccess (响应字段说明) {Boolean} terminalGroupDTO.enableDefault
     */
    /**
     * 加载指定id终端组对象
     *
     * @param groupId 请求参数id
     * @return TerminalGroupDTO 终端组DTO
     * @throws BusinessException 业务异常
     */

    TerminalGroupDTO loadById(UUID groupId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.createTerminalGroup 创建终端组
     * @apiName createTerminalGroup
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 创建终端组
     * @apiParam (请求体字段说明) {CbbTerminalGroupRequest} request CbbTerminalGroupRequest
     * @apiParam (请求体字段说明) {String} request.groupName 终端分组名称
     * @apiParam (请求体字段说明) {UUID} [request.parentGroupId] 父级分组id
     *
     * @apiSuccess (响应字段说明) {TerminalGroupDTO}  terminalGroupDTO 终端分组dto
     * @apiSuccess (响应字段说明) {UUID} terminalGroupDTO.id 终端分组id
     * @apiSuccess (响应字段说明) {String} terminalGroupDTO.groupName 终端分组名称
     * @apiSuccess (响应字段说明) {UUID} terminalGroupDTO.parentGroupId 父级分组id
     * @apiSuccess (响应字段说明) {String} terminalGroupDTO.parentGroupName 父级分组名称
     * @apiSuccess (响应字段说明) {Boolean} terminalGroupDTO.enableDefault
     */
    /**
     * @description 创建终端组
     * @param request 页面请求创建终端组参数
     * @return 创建终端组详细信息
     * @throws BusinessException 业务异常
     */
    TerminalGroupDTO createTerminalGroup(CbbTerminalGroupRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.editTerminalGroup 编辑终端组
     * @apiName editTerminalGroup
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 编辑终端组
     * @apiParam (请求体字段说明) {CbbEditTerminalGroupRequest} request CbbEditTerminalGroupRequest
     * @apiParam (请求体字段说明) {UUID} request.id 终端分组id
     * @apiParam (请求体字段说明) {String} request.groupName 分组名称
     * @apiParam (请求体字段说明) {UUID} [request.parentGroupId] 父级分组id
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     * @description 编辑终端组
     * @param request 页面请求参数对象
     * @throws BusinessException 业务异常
     */
    void editTerminalGroup(CbbEditTerminalGroupRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.deleteTerminalGroup 删除终端组
     * @apiName deleteTerminalGroup
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 删除终端组
     * @apiParam (请求体字段说明) {CbbDeleteTerminalGroupRequest} request CbbDeleteTerminalGroupRequest
     * @apiParam (请求体字段说明) {UUID} request.id 分组id
     * @apiParam (请求体字段说明) {String} [request.moveGroupId] 删除分组id
     *
     * @apiSuccess (响应字段说明) {void} void 无返回值参数
     */
    /**
     * @description 删除终端组
     * @param request 页面请求参数对象
     * @throws BusinessException 业务异常.
     */
    void deleteTerminalGroup(CbbDeleteTerminalGroupRequest request) throws BusinessException;

    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.obtainGroupNamePathArr 获取终端分组路径数组
     * @apiName obtainGroupNamePathArr
     * @apiGroup 获取终端分组路径数组
     * @apiDescription 删除终端组
     * @apiParam (请求体字段说明) {UUID} groupId 终端组id
     *
     * @apiSuccess (响应字段说明) {String[]} groupNameArr 分组名称数组
     */
    /**
     *  获取终端分组路径数组
     *
     * @param groupId 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */

    String[] obtainGroupNamePathArr(UUID groupId) throws BusinessException;

    /**
     * @api {POST} CbbTerminalGroupMgmtAPI.checkUseGroupNameDuplication 判断分组名称是否存在
     * @apiName checkUseGroupNameDuplication
     * @apiGroup CbbTerminalGroupMgmtAPI
     * @apiDescription 判断分组名称是否存在
     * @apiParam (请求体字段说明) {CbbTerminalGroupNameDuplicationRequest} request CbbTerminalGroupNameDuplicationRequest
     * @apiParam (请求体字段说明) {UUID} [request.id] 分组id
     * @apiParam (请求体字段说明) {UUID} [request.parentId] 父id
     * @apiParam (请求体字段说明) {String} request.groupName 分组名称
     *
     * @apiSuccess (响应字段说明) {boolean} hasDuplication 是否存在
     */
    /**
     * 判断分组名称是否存在
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */

    boolean checkUseGroupNameDuplication(CbbTerminalGroupNameDuplicationRequest request) throws BusinessException;
}
