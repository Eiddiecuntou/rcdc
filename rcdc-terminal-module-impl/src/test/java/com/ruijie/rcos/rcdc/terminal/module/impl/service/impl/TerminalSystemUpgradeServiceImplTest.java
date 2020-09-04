package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.codec.adapter.base.sender.DefaultRequestMessageSender;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalGroupEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.quartz.handler.TerminalOffLineException;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.LinuxVDISystemUpgradeFileClearHandler;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020年1月8日
 * 
 * @author nt
 */
public class TerminalSystemUpgradeServiceImplTest {

    @Tested
    private TerminalSystemUpgradeServiceImpl upgradeService;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Injectable
    private LinuxVDISystemUpgradeFileClearHandler upgradeFileClearHandler;

    @Injectable
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Injectable
    private TerminalSystemUpgradeTerminalGroupDAO systemUpgradeTerminalGroupDAO;

    /**
     * 测试编辑升级任务状态 - 任务记录为null
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeStateUpgradeTaskIsNull() throws Exception {
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                result = Optional.empty();
            }
        };

        try {
            upgradeService.modifySystemUpgradeState(upgradeEntity);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST, e.getKey());
        }

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                times = 1;

                terminalSystemUpgradeDAO.save((TerminalSystemUpgradeEntity) any);
                times = 0;
            }
        };
    }

    /**
     * 测试编辑升级任务状态 - 任务记录为null
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeState() throws Exception {
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                result = Optional.of(upgradeEntity);
            }
        };

        upgradeService.modifySystemUpgradeState(upgradeEntity);

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                times = 1;

                terminalSystemUpgradeDAO.save(upgradeEntity);
                times = 1;

                upgradeFileClearHandler.clear();
                times = 0;
            }
        };
    }

    /**
     * 测试编辑升级任务状态 - linux vdi任务关闭
     *
     * @throws Exception 异常
     */
    @Test
    public void testModifySystemUpgradeStateIsLinuxVDIAndStateIsFinish() throws Exception {
        TerminalSystemUpgradeEntity upgradeEntity = buildUpgradeEntity();
        upgradeEntity.setPackageType(CbbTerminalTypeEnums.VDI_LINUX);
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.FINISH);

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                result = Optional.of(upgradeEntity);
            }
        };

        upgradeService.modifySystemUpgradeState(upgradeEntity);

        new Verifications() {
            {
                terminalSystemUpgradeDAO.findById(upgradeEntity.getId());
                times = 1;

                terminalSystemUpgradeDAO.save(upgradeEntity);
                times = 1;

                upgradeFileClearHandler.clear();
                times = 1;
            }
        };
    }

    /**
     * 发送升级消息，正常流程
     * @param sender sender
     * @throws BusinessException 异常
     */
    @Test
    public void testSystemUpgrade(@Mocked DefaultRequestMessageSender sender) throws BusinessException {
        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
            }
        };

        upgradeService.systemUpgrade("terminalId", new Object());

        new Verifications() {
            {
                sender.request((Message) any);
                times = 1;
            }
        };
    }

    /**
     * 发送升级消息，获取sender异常
     * @throws BusinessException 异常
     */
    @Test(expected = TerminalOffLineException.class)
    public void testSystemUpgradeGetSenderException() throws BusinessException {
        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = new Exception("xxx");
            }
        };

        upgradeService.systemUpgrade("terminalId", new Object());

        Assert.fail();
    }

    /**
     * 发送升级消息，发送异常
     * @param sender sender
     * @throws BusinessException 异常
     */
    @Test
    public void testSystemUpgradeSendException(@Mocked DefaultRequestMessageSender sender) throws BusinessException {
        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;
                sender.request((Message) any);
                result = new Exception("xxx");
            }
        };

        try {
            upgradeService.systemUpgrade("terminalId", new Object());
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_UPGRADE_MESSAGE_SEND_FAIL, e.getKey());
        }

    }

    /**
     * 获取是否存在升级任务
     */
    @Test
    public void testHasSystemUpgradeInProgressTrue() {
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        upgradingTaskList.add(buildUpgradeEntity());

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };

        boolean hasSystemUpgradeInProgress = upgradeService.hasSystemUpgradeInProgress(UUID.randomUUID());
        Assert.assertTrue(hasSystemUpgradeInProgress);
    }

    /**
     * 获取是否存在升级任务
     */
    @Test
    public void testHasSystemUpgradeInProgressFalse() {
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };

        boolean hasSystemUpgradeInProgress = upgradeService.hasSystemUpgradeInProgress(UUID.randomUUID());
        Assert.assertFalse(hasSystemUpgradeInProgress);
    }

    /**
     * 获取升级任务
     */
    @Test
    public void testGetUpgradingSystemUpgradeTaskByPackageId() {
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity entity1 = buildUpgradeEntity();
        TerminalSystemUpgradeEntity entity2 = buildUpgradeEntity();
        upgradingTaskList.add(entity1);
        upgradingTaskList.add(entity2);

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };

        TerminalSystemUpgradeEntity task = upgradeService.getUpgradingSystemUpgradeTaskByPackageId(UUID.randomUUID());
        Assert.assertEquals(entity1, task);
    }
    /**
     * 获取升级任务
     */
    @Test
    public void testGetUpgradingSystemUpgradeTaskByPackageIdNull() {
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();

        new Expectations() {
            {
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any, (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };

        TerminalSystemUpgradeEntity task = upgradeService.getUpgradingSystemUpgradeTaskByPackageId(UUID.randomUUID());
        Assert.assertNull(task);
    }

    /**
     * 获取升级终端
     */
    @Test
    public void testGetSystemUpgradeTerminalByTaskId() {
        UUID taskId = UUID.randomUUID();
        String terminalId = "terminalId";

        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();

        new Expectations() {
            {
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(taskId, terminalId);
                result = entity;
            }
        };

        TerminalSystemUpgradeTerminalEntity taskEntity = upgradeService.getSystemUpgradeTerminalByTaskId(terminalId, taskId);
        Assert.assertEquals(entity, taskEntity);
    }

    /**
     * 终端组是否在任务中
     */
    @Test
    public void testIsGroupInUpgradeTask() {
        UUID upgradeTaskId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        TerminalSystemUpgradeTerminalGroupEntity upgradeGroup = new TerminalSystemUpgradeTerminalGroupEntity();

        new Expectations() {
            {
                systemUpgradeTerminalGroupDAO.findBySysUpgradeIdAndTerminalGroupId(upgradeTaskId, groupId);
                result = upgradeGroup;
            }
        };

        boolean isInTask = upgradeService.isGroupInUpgradeTask(upgradeTaskId, groupId);
        Assert.assertTrue(isInTask);
    }

    private TerminalSystemUpgradeEntity buildUpgradeEntity() {
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradeEntity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        upgradeEntity.setPackageType(CbbTerminalTypeEnums.VDI_ANDROID);
        upgradeEntity.setId(UUID.randomUUID());

        return upgradeEntity;
    }

}
