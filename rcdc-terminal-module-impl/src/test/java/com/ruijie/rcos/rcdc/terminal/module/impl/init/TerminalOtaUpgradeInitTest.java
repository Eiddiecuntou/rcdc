package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.BtClientUtil;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/2/18 11:40
 *
 * @author zhangyichi
 */
@RunWith(SkyEngineRunner.class)
public class TerminalOtaUpgradeInitTest {

    @Tested
    private TerminalOtaUpgradeInit terminalOtaUpgradeInit;

    @Injectable
    private TerminalSystemUpgradePackageHandlerFactory handlerFactory;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Mocked
    private TerminalSystemUpgradePackageHandler handler;

    /**
     * 初始化方法，出厂包未初始化，正常流程
     * @throws IOException 异常
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotInit() throws IOException, BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        File mockInitPackage = new File(rootPath + "initPackage");
        mockInitPackage.createNewFile();
        List<File> fileList = Lists.newArrayList();
        fileList.add(mockInitPackage);
        new Expectations(FileOperateUtil.class) {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = null;
                FileOperateUtil.listFile(anyString);
                result = fileList;
                FileOperateUtil.deleteFile((File) any);
            }
        };

        terminalOtaUpgradeInit.safeInit();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 2;
                handler.uploadUpgradePackage((CbbTerminalUpgradePackageUploadRequest) any);
                times = 2;

                List<CbbTerminalTypeEnums> typeEnumsList = Lists.newArrayList();
                handlerFactory.getHandler(this.withCapture(typeEnumsList));
                Assert.assertTrue(typeEnumsList.contains(CbbTerminalTypeEnums.VDI_ANDROID));
                Assert.assertTrue(typeEnumsList.contains(CbbTerminalTypeEnums.IDV_LINUX));
            }
        };

        mockInitPackage.delete();
    }

    /**
     * 初始化方法，出厂包未初始化，出厂包不存在
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotExist() throws BusinessException {
        List<File> fileList = Lists.newArrayList();
        new Expectations(FileOperateUtil.class) {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = null;
                FileOperateUtil.listFile(anyString);
                result = fileList;
            }
        };

        terminalOtaUpgradeInit.safeInit();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 2;
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 0;
            }
        };
    }

    /**
     * 初始化方法，出厂包未初始化，读取文件异常
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotExistException() throws BusinessException {
        List<File> fileList = Lists.newArrayList();
        new Expectations(FileOperateUtil.class) {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = null;
                FileOperateUtil.listFile(anyString);
                result = new IOException();
            }
        };

        terminalOtaUpgradeInit.safeInit();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 2;
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 0;
            }
        };
    }

    /**
     * 初始化方法，出厂包已初始化，没有升级任务
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageHasInitNoTask() throws BusinessException {
        UUID id = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        upgradePackage.setId(id);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(id,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
            }
        };

        terminalOtaUpgradeInit.safeInit();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 2;
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 2;
            }
        };
    }

    /**
     * 初始化方法，出厂包已初始化，有升级任务
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageHasInitHasTask() throws BusinessException {
        UUID id = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(id);
        upgradePackage.setSeedPath("seedPath");
        upgradePackage.setFilePath("filePath");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity terminalSystemUpgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(terminalSystemUpgradeEntity);
        new Expectations(BtClientUtil.class) {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(id,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
                BtClientUtil.startBtShare("filePath", "seedPath");
            }
        };

        terminalOtaUpgradeInit.safeInit();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 2;
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 2;
            }
        };
    }

    /**
     * 初始化方法，出厂包已初始化，BT接口调用异常
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageHasInitBtException() throws BusinessException {
        UUID id = UUID.randomUUID();
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(id);
        upgradePackage.setSeedPath("seedPath");
        upgradePackage.setFilePath("filePath");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity terminalSystemUpgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(terminalSystemUpgradeEntity);
        new Expectations(BtClientUtil.class) {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(id,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;
                BtClientUtil.startBtShare(anyString, anyString);
                result = new Exception("xxx");
            }
        };

        terminalOtaUpgradeInit.safeInit();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 2;
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc((UUID) any,
                        (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 2;
            }
        };
    }
}