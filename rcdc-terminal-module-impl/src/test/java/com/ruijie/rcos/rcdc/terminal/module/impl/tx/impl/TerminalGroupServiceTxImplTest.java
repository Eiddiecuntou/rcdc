package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import java.util.UUID;

import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalGroupEntity;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl.validate.DeleteTerminalGroupValidator;
import com.ruijie.rcos.sk.base.exception.BusinessException;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月26日
 * 
 * @author ls
 */
public class TerminalGroupServiceTxImplTest {

    @Tested
    private TerminalGroupServiceTxImpl serviceTxImpl;

    @Injectable
    private TerminalGroupService terminalGroupService;

    @Injectable
    private TerminalGroupDAO terminalGroupDAO;

    @Injectable
    private TerminalBasicInfoDAO terminalDAO;

    @Injectable
    private DeleteTerminalGroupValidator validator;

    @Injectable
    private TerminalSystemUpgradeTerminalGroupDAO systemUpgradeTerminalGroupDAO;

    /**
     * testDeleteGroup
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testDeleteGroup() throws BusinessException {
        UUID id = UUID.randomUUID();
        UUID moveGroupId = UUID.randomUUID();

        new Expectations() {
            {
                terminalGroupDAO.findByParentId(id);
                result = Lists.newArrayList();
            }
        };
        serviceTxImpl.deleteGroup(id, moveGroupId);
        new Verifications() {
            {
                validator.validate(id, moveGroupId);
                times = 1;
                terminalGroupService.checkGroupExist(id);
                times = 1;
                terminalGroupDAO.findByParentId(id);
                times = 1;
                systemUpgradeTerminalGroupDAO.deleteByTerminalGroupId(id);
                times = 1;
            }
        };
    }

    /**
     * testDeleteGroup1
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testDeleteGroup1() throws BusinessException {
        UUID id = UUID.randomUUID();
        UUID moveGroupId = UUID.randomUUID();
        TerminalGroupEntity terminalGroupEntity = new TerminalGroupEntity();
        terminalGroupEntity.setId(UUID.randomUUID());

        new Expectations() {
            {
                terminalGroupDAO.findByParentId(id);
                result = Lists.newArrayList(terminalGroupEntity);
            }
        };
        serviceTxImpl.deleteGroup(id, moveGroupId);
        new Verifications() {
            {
                validator.validate(id, moveGroupId);
                times = 1;
                terminalGroupService.checkGroupExist(id);
                times = 1;
                terminalDAO.findByGroupId(id);
                times = 1;
                terminalGroupDAO.save((TerminalGroupEntity) any);
                times = 1;
                systemUpgradeTerminalGroupDAO.deleteByTerminalGroupId(id);
                times = 1;
            }
        };
    }

    /**
     * testDeleteGroup2
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testDeleteGroup2() throws BusinessException {
        UUID id = UUID.randomUUID();
        UUID moveGroupId = Constants.DEFAULT_TERMINAL_GROUP_UUID;
        TerminalGroupEntity terminalGroupEntity = new TerminalGroupEntity();
        terminalGroupEntity.setId(UUID.randomUUID());

        new Expectations() {
            {
                terminalGroupDAO.findByParentId(id);
                result = Lists.newArrayList(terminalGroupEntity);
            }
        };

        serviceTxImpl.deleteGroup(id, moveGroupId);

        new Verifications() {
            {
                validator.validate(id, moveGroupId);
                times = 1;
                terminalGroupService.checkGroupExist(id);
                times = 1;
                terminalGroupDAO.findByParentId(id);
                times = 1;
                terminalDAO.findByGroupId((UUID) any);
                times = 2;
            }
        };
    }
}
