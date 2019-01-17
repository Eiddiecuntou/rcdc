package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.fail;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.callback.CbbTerminalSystemUpgradeRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSystemUpgradeInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.commkit.base.callback.RequestCallback;
import com.ruijie.rcos.sk.commkit.base.message.Message;
import com.ruijie.rcos.sk.commkit.base.sender.DefaultRequestMessageSender;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

/**
 * 
 * Description: 终端系统升级服务测试
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月29日
 * 
 * @author nt
 */
@RunWith(JMockit.class)
public class TerminalSystemUpgradeServiceImplTest {

    @Tested
    private TerminalSystemUpgradeServiceImpl terminalSystemUpgradeService;

    @Injectable
    private SessionManager sessionManager;

    @Injectable
    private DefaultRequestMessageSender sender;

    @Injectable
    private TerminalSystemUpgradePackageDAO termianlSystemUpgradePackageDAO;

    @Injectable
    private CbbTerminalSystemUpgradeRequestCallBack callback;

    /**
     * 测试修改终端升级包版本信息
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testModifyTerminalUpgradePackageVersion() throws BusinessException {
        // 构造版本信息,升级包信息
        TerminalUpgradeVersionFileInfo versionInfo = buildUpgradePackageVersion();
        TerminalSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                result = upgradePackage;

                termianlSystemUpgradePackageDAO.modifyTerminalUpgradePackageVersion(anyString, (TerminalPlatformEnums) any,
                        anyString, anyInt);
                result = 1;
            }
        };

        terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion(versionInfo);

        new Verifications() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                times = 1;

                termianlSystemUpgradePackageDAO.modifyTerminalUpgradePackageVersion(anyString, (TerminalPlatformEnums) any,
                        anyString, anyInt);
                times = 1;
            }
        };
    }


    /**
     * 测试修改终端升级包版本信息当版本信息参数为空时
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testModifyTerminalUpgradePackageVersionVersionInfoIsNull() throws BusinessException {

        try {
            terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion(null);
            fail();
        } catch (Exception e) {
            Assert.assertEquals("terminalUpgradeVersionFileInfo 不能为空", e.getMessage());
        }

    }

    /**
     * 测试修改终端升级包版本信息当数据库修改返回0
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testModifyTerminalUpgradePackageVersionDAOReturnZero() throws BusinessException {
        // 构造版本信息,升级包信息
        TerminalUpgradeVersionFileInfo versionInfo = buildUpgradePackageVersion();
        TerminalSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                result = upgradePackage;

                termianlSystemUpgradePackageDAO.modifyTerminalUpgradePackageVersion(anyString, (TerminalPlatformEnums) any,
                        anyString, anyInt);
                result = 0;
            }
        };

        try {
            terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion(versionInfo);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST, e.getKey());
        }

    }

    /**
     * 测试修改终端升级包版本信息
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testModifyTerminalUpgradePackageVersionUpgradePackageIsNull() throws BusinessException {
        // 构造版本信息,升级包信息
        TerminalUpgradeVersionFileInfo versionInfo = buildUpgradePackageVersion();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                result = null;
            }
        };

        try {
            terminalSystemUpgradeService.modifyTerminalUpgradePackageVersion(versionInfo);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_NOT_EXIST, e.getKey());
        }

    }


    /**
     * 测试添加终端升级包
     * 
     * @throws BusinessException 异常
     */
    @Test
    public void testAddTerminalUpgradePackage() throws BusinessException {
        // 构造测试数据
        TerminalUpgradeVersionFileInfo versionInfo = buildUpgradePackageVersion();
        // TermianlSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                result = null;

                termianlSystemUpgradePackageDAO.save((TerminalSystemUpgradePackageEntity) any);
            }
        };

        terminalSystemUpgradeService.addTerminalUpgradePackage(versionInfo);

        new Verifications() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                times = 1;

                termianlSystemUpgradePackageDAO.save((TerminalSystemUpgradePackageEntity) any);
                times = 1;
            }
        };
    }


    /**
     * 测试添加终端升级包当升级包已存在时
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testAddTerminalUpgradePackagePackageIsExist() throws BusinessException {
        // 构造测试数据
        TerminalUpgradeVersionFileInfo versionInfo = buildUpgradePackageVersion();
        TerminalSystemUpgradePackageEntity upgradePackage = buildUpgradePackage();
        new Expectations() {
            {
                termianlSystemUpgradePackageDAO
                        .findFirstByPackageType((TerminalPlatformEnums) any);
                result = upgradePackage;
            }
        };

        try {
            terminalSystemUpgradeService.addTerminalUpgradePackage(versionInfo);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_HAS_EXIST, e.getKey());
        }

    }

    /**
     * 测试从文件中读取系统升级状态信息
     */
    @Test
    public void testReadSystemUpgradeStateFromFile() {

        List<TerminalSystemUpgradeInfo> stateInfoList = terminalSystemUpgradeService.readSystemUpgradeStateFromFile();
        Assert.assertEquals(null, stateInfoList);

    }

    /**
     * 测试发送系统升级指令
     * 
     * 
     * @param sender 模拟的消息发送对象
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSystemUpgrade(@Mocked DefaultRequestMessageSender sender) throws BusinessException {
        String terminalId = "terminalId";
        TerminalSystemUpgradeMsg upgradeMsg = new TerminalSystemUpgradeMsg();

        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = sender;

                sender.asyncRequest((Message) any, (RequestCallback) any);

            }
        };

        terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);

        new Verifications() {
            {
                sessionManager.getRequestMessageSender(anyString);
                times = 1;

                sender.asyncRequest((Message) any, (RequestCallback) any);
                times = 1;

            }
        };
    }

    /**
     * 测试发送系统升级指令当终端id为空时
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSystemUpgradeTerminalIdIsNull() throws BusinessException {
        String terminalId = null;
        TerminalSystemUpgradeMsg upgradeMsg = new TerminalSystemUpgradeMsg();

        try {
            terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
            fail();
        } catch (Exception e) {
            Assert.assertEquals("terminalId 不能为空", e.getMessage());
        }
    }

    /**
     * 测试发送系统升级指令当发送消息为空时
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSystemUpgradeUpgradeMsgIsNull() throws BusinessException {
        String terminalId = "1111";
        TerminalSystemUpgradeMsg upgradeMsg = null;

        try {
            terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
            fail();
        } catch (Exception e) {
            Assert.assertEquals("systemUpgradeMsg 不能为空", e.getMessage());
        }
    }


    /**
     * 测试发送系统升级指令当获取到的消息发送器为空时
     * 
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testSystemUpgrade() throws BusinessException {
        String terminalId = "terminalId";
        TerminalSystemUpgradeMsg upgradeMsg = new TerminalSystemUpgradeMsg();

        new Expectations() {
            {
                sessionManager.getRequestMessageSender(anyString);
                result = null;
            }
        };

        try {
            terminalSystemUpgradeService.systemUpgrade(terminalId, upgradeMsg);
            fail();
        } catch (BusinessException e) {
            Assert.assertEquals(BusinessKey.RCDC_TERMINAL_OFFLINE, e.getKey());
        }

        new Verifications() {
            {
                sessionManager.getRequestMessageSender(anyString);
                times = 1;
            }
        };
    }



    private TerminalUpgradeVersionFileInfo buildUpgradePackageVersion() {
        TerminalUpgradeVersionFileInfo versionInfo = new TerminalUpgradeVersionFileInfo();
        versionInfo.setPackageType(TerminalPlatformEnums.VDI);
        versionInfo.setVersion("version");
        versionInfo.setImgName("imgname");
        return versionInfo;
    }


    private TerminalSystemUpgradePackageEntity buildUpgradePackage() {
        TerminalSystemUpgradePackageEntity upgradePackage = new TerminalSystemUpgradePackageEntity();
        upgradePackage.setId(UUID.randomUUID());
        upgradePackage.setPackageVersion("internalVersion");
        upgradePackage.setImgName("packageName");
        upgradePackage.setPackageType(TerminalPlatformEnums.VDI);
        upgradePackage.setUploadTime(new Date());
        return upgradePackage;
    }


}
