package com.ruijie.rcos.rcdc.terminal.module.impl.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.Nullable;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupDetailDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.sk.base.exception.BusinessException;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年10月30日
 * 
 * @author chenzj
 */
public interface TerminalGroupService {
    
    /**
     * 根据终端类型查询所有分组
     * @return 分组列表
     */
    List<TerminalGroupEntity> findAll();
    
    /**
     * @description 保存终端组
     * @param terminalGroup 需要保存的终端组对象
     * @throws BusinessException 业务异常
     * @return 用户组信息实体类
     */
    TerminalGroupEntity saveTerminalGroup(CbbTerminalGroupDetailDTO terminalGroup) throws BusinessException;

    /**
     * @description 根据UUID获取指定终端组
     * @param uuid 终端组uuid
     * @return UserGroupEntity 终端组容实体
     * @throws BusinessException 业务异常
     */
    TerminalGroupEntity getTerminalGroup(UUID uuid) throws BusinessException;


    /**
     * 判断分组名称是否同级唯一
     * 
     * @param terminalGroup 终端分组对象
     * @return 是否同级唯一结果
     * @throws BusinessException 业务异常
     */
    boolean checkGroupNameUnique(CbbTerminalGroupDetailDTO terminalGroup) throws BusinessException;

    /**
     * 修改终端分组信息
     * 
     * @param terminalGroup 终端分组对象
     * @throws BusinessException 业务异常
     */
    void modifyGroupById(CbbTerminalGroupDetailDTO terminalGroup) throws BusinessException;
    
    /**
     * 检验分组是否存在
     * 
     * @param id 分组id
     * @return 分组信息
     * @throws BusinessException 业务异常
     */
    TerminalGroupEntity checkGroupExist(UUID id) throws BusinessException;

    /**
     * 获取终端分组层级名称数组
     * @param groupId 分组id
     * @return 分组层级名称数组
     * @throws BusinessException 业务异常
     */
    String[] getTerminalGroupNameArr(UUID groupId) throws BusinessException;

    /**
     *  根据名称获取分组下的子分组
     * @param parentGroupId 父分组id
     * @param groupName 分组名称
     * @return 分组对象
     */
    List<TerminalGroupEntity> getByName(@Nullable UUID parentGroupId, String groupName);
}
