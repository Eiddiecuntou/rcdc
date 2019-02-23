package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import static org.junit.Assert.fail;
import java.util.Date;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.callback.CbbTerminalSystemUpgradeRequestCallBack;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.message.TerminalSystemUpgradeMsg;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalUpgradeVersionFileInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalSystemUpgradePackageService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.SystemUpgradeFileClearHandler;
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
    private TerminalSystemUpgradeDAO terminalSystemUpgradeDAO;

    @Injectable
    private CbbTerminalSystemUpgradeRequestCallBack callback;

    @Injectable
    private SystemUpgradeFileClearHandler upgradeFileClearHandler;


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
