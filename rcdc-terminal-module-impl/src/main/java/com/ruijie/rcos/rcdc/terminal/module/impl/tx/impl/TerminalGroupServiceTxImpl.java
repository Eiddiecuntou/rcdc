package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Objects;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalGroupServiceTx;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate.DeleteTerminalGroupValidator;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 终端分组存在事物操作的实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年1月9日
 * 
 * @author nt
 */
@Service
public class TerminalGroupServiceTxImpl implements TerminalGroupServiceTx {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalGroupServiceTxImpl.class);

    @Autowired
    private TerminalGroupService terminalGroupService;

    @Autowired
    private TerminalGroupDAO terminalGroupDAO;

    @Autowired
    private TerminalBasicInfoDAO terminalDAO;

    @Autowired
    private DeleteTerminalGroupValidator validator;

    @Override
    public void deleteGroup(UUID id, @Nullable UUID moveGroupId) throws BusinessException {
        Assert.notNull(id, "terminal group id can not be null");

        LOGGER.warn("delete terminal group, id[{}]", id);

        validator.validate(id, moveGroupId);
        TerminalGroupEntity groupEntity = terminalGroupService.checkGroupExist(id);
        deleteAndMoveGroup(id, moveGroupId);
    }

    private void deleteAndMoveGroup(UUID id, UUID moveGroupId) {
        doDeleteGroup(id, moveGroupId);
        // 选择的移动分组不是未分组，则将删除分组的子分组移动至选择分组下
        List<TerminalGroupEntity> subGroupList = terminalGroupDAO.findByParentId(id);
        if (CollectionUtils.isEmpty(subGroupList)) {
            return;
        }
        if (!Objects.equal(moveGroupId, Constants.DEFAULT_TERMINAL_GROUP_UUID)) {
            updateSubGroupParent(subGroupList, moveGroupId);
            return;
        }

        for (TerminalGroupEntity groupEntity : subGroupList) {
            deleteAndMoveGroup(groupEntity.getId(), moveGroupId);
        }
    }

    private void doDeleteGroup(UUID id, UUID moveGroupId) {

        terminalGroupDAO.deleteById(id);

        // 将删除分组下的终端的分组设置为选择的分组
        List<TerminalEntity> terminalList = terminalDAO.findByGroupId(id);
        if (CollectionUtils.isEmpty(terminalList)) {
            LOGGER.debug("no terminal in the deleted group");
            return;
        }

        UUID updateGroupId = moveGroupId == null ? Constants.DEFAULT_TERMINAL_GROUP_UUID : moveGroupId;
        for (TerminalEntity terminal : terminalList) {
            terminal.setGroupId(updateGroupId);
            terminalDAO.save(terminal);
        }
    }

    private void updateSubGroupParent(List<TerminalGroupEntity> subGroupList, UUID moveGroupId) {
        for (TerminalGroupEntity groupEntity : subGroupList) {
            groupEntity.setParentId(moveGroupId);
            terminalGroupDAO.save(groupEntity);
        }
    }

}
