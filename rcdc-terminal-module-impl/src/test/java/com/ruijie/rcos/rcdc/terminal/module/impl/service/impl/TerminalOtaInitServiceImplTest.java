package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;


import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalUpgradePackageUploadDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.BtClientService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandler;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.TerminalSystemUpgradePackageHandlerFactory;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.crypto.Md5Builder;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月4日
 *
 * @author ls
 */
@RunWith(SkyEngineRunner.class)
public class TerminalOtaInitServiceImplTest {

    @Tested
    private TerminalOtaInitServiceImpl initService;

    @Injectable
    private TerminalSystemUpgradePackageHandlerFactory handlerFactory;

    @Injectable
    private TerminalSystemUpgradePackageDAO terminalSystemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Injectable
    private BtClientService btClientService;

    @Mocked
    private TerminalSystemUpgradePackageHandler handler;

    /**
     * 初始化方法，出厂包未初始化，正常流程
     *
     * @throws IOException       异常
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotInit() throws IOException, BusinessException {
        String rootPath = this.getClass().getResource("/").getPath();
        File mockInitPackage = new File(rootPath + "initPackage");
        mockInitPackage.createNewFile();
        List<File> fileList = Lists.newArrayList();
        fileList.add(mockInitPackage);

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
        };

        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.listFile(anyString);
                result = fileList;

                handlerFactory.getHandler(CbbTerminalTypeEnums.VDI_ANDROID);
                result = handler;

                FileOperateUtil.deleteFile((File) any);
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                handler.preUploadPackage();
                times = 1;
                handler.uploadUpgradePackage((CbbTerminalUpgradePackageUploadDTO) any);
                times = 1;
                handler.postUploadPackage();
                times = 1;

                List<CbbTerminalTypeEnums> typeEnumsList = Lists.newArrayList();
                handlerFactory.getHandler(this.withCapture(typeEnumsList));
                Assert.assertTrue(typeEnumsList.contains(CbbTerminalTypeEnums.VDI_ANDROID));
            }
        };

        mockInitPackage.delete();
    }

    /**
     * 初始化方法，出厂包未初始化，出厂包不存在
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotExist() throws BusinessException {
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
        };

        List<File> fileList = Lists.newArrayList();
        new Expectations(FileOperateUtil.class) {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = null;
                FileOperateUtil.listFile(anyString);
                result = fileList;
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 1;
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 0;
            }
        };
    }

    /**
     * 初始化方法，出厂包不存在
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotDirectoryAndNeedInitBt() throws BusinessException {

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }
        };

        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setFilePath("123");
        upgradePackage.setSeedPath("456");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;

                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId()
                        , (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;

                btClientService.startBtShare(upgradePackage.getFilePath(), upgradePackage.getSeedPath());
                result = new Exception("123");
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 1;
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 0;
                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId()
                        , (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                btClientService.startBtShare(upgradePackage.getFilePath(), upgradePackage.getSeedPath());
                times = 1;
            }
        };
    }

    /**
     * 初始化方法，出厂包不存在,不需要开启分享
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotDirectoryAndNotNeedInitBt() throws BusinessException {

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }

            @Mock
            public boolean isFile() {
                return false;
            }
        };

        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setFilePath("123");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 1;
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 0;

            }
        };
    }

    /**
     * 初始化方法，出厂包不存在,不需要开启分享
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotDirectoryAndNotNeedInitBt2() throws BusinessException {

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp<FileUtils>() {

            @Mock
            public String readFileToString(File file, Charset charset) {
                return "aaabb";
            }
        };

        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setFilePath("123");
        upgradePackage.setSeedPath("aaa");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 1;
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 0;

            }
        };
    }

    /**
     * 初始化方法，出厂包不存在,需要开启分享
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageNotDirectoryAndNotNeedInitBt3() throws BusinessException {

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };

        new MockUp<FileUtils>() {

            @Mock
            public String readFileToString(File file, Charset charset) {
                return "";
            }
        };

        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setFilePath("123");
        upgradePackage.setSeedPath("aaa");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                result = upgradePackage;

                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId()
                        , (List<CbbSystemUpgradeTaskStateEnums>) any);
                result = upgradingTaskList;

                btClientService.startBtShare(upgradePackage.getFilePath(), upgradePackage.getSeedPath());
                result = new Exception("123");
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                terminalSystemUpgradePackageDAO.findFirstByPackageType((CbbTerminalTypeEnums) any);
                times = 1;
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 0;

                terminalSystemUpgradeDAO.findByUpgradePackageIdAndStateInOrderByCreateTimeAsc(upgradePackage.getId()
                        , (List<CbbSystemUpgradeTaskStateEnums>) any);
                times = 1;
                btClientService.startBtShare(upgradePackage.getFilePath(), upgradePackage.getSeedPath());
                times = 1;
            }
        };
    }

    /**
     * 初始化方法，出厂包不存在
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackage() throws BusinessException {

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) {
                return "123".getBytes();
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
        };

        List<File> fileList = Lists.newArrayList();
        fileList.add(new File("/123"));
        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.listFile(anyString);
                result = fileList;
            }
        };

        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setFilePath("123");
        upgradePackage.setSeedPath("456");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                result = handler;

                handler.preUploadPackage();

                handler.uploadUpgradePackage((CbbTerminalUpgradePackageUploadDTO) any);

                handler.postUploadPackage();
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 1;

                handler.preUploadPackage();
                times = 1;

                handler.uploadUpgradePackage((CbbTerminalUpgradePackageUploadDTO) any);
                times = 1;

                handler.postUploadPackage();
                times = 1;
            }
        };
    }

    /**
     * 初始化方法，出厂包不存在
     *
     * @throws BusinessException 异常
     */
    @Test
    public void testSafeInitPackageHasException() throws BusinessException {

        new MockUp<Md5Builder>() {
            @Mock
            public byte[] computeFileMd5(File file) {
                return "123".getBytes();
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return true;
            }
        };

        List<File> fileList = Lists.newArrayList();
        fileList.add(new File("/123"));
        new Expectations(FileOperateUtil.class) {
            {
                FileOperateUtil.listFile(anyString);
                result = fileList;
            }
        };

        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setFilePath("123");
        upgradePackage.setSeedPath("456");
        List<TerminalSystemUpgradeEntity> upgradingTaskList = Lists.newArrayList();
        TerminalSystemUpgradeEntity upgradeEntity = new TerminalSystemUpgradeEntity();
        upgradingTaskList.add(upgradeEntity);
        new Expectations() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                result = handler;

                handler.preUploadPackage();

                handler.uploadUpgradePackage((CbbTerminalUpgradePackageUploadDTO) any);

                handler.postUploadPackage();
                result = new Exception("123");
            }
        };

        initService.initAndroidOta();

        new Verifications() {
            {
                handlerFactory.getHandler((CbbTerminalTypeEnums) any);
                times = 1;

                handler.preUploadPackage();
                times = 1;

                handler.uploadUpgradePackage((CbbTerminalUpgradePackageUploadDTO) any);
                times = 1;

                handler.postUploadPackage();
                times = 1;
            }
        };
    }
}
