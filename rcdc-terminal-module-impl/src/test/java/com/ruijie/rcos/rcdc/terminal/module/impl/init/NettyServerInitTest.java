//package com.ruijie.rcos.rcdc.terminal.module.impl.init;
//
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.Test;
//import org.springframework.util.Assert;
//import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
//import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalEventNoticeSPI;
//import com.ruijie.rcos.rcdc.terminal.module.def.spi.request.CbbNoticeRequest;
//import com.ruijie.rcos.rcdc.terminal.module.impl.connect.ConnectEventHandler;
//import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
//import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
//import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
//import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
//import com.ruijie.rcos.sk.commkit.server.TcpServer;
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Mock;
//import mockit.MockUp;
//import mockit.Mocked;
//import mockit.Tested;
//import mockit.Verifications;
//
///**
// *
// * Description: Function Description
// * Copyright: Copyright (c) 2019
// * Company: Ruijie Co., Ltd.
// * Create Time: 2019年3月4日
// *
// * @author ls
// */
//public class NettyServerInitTest {
//
//    @Tested
//    private NettyServerInit init;
//
//    @Injectable
//    private TerminalBasicInfoDAO terminalBasicInfoDAO;
//
//    @Injectable
//    private TerminalBasicInfoService terminalBasicInfoService;
//
//    @Injectable
//    private ConnectEventHandler connectEventHandler;
//
//    @Injectable
//    private CbbTerminalEventNoticeSPI terminalEventNoticeSPI;
//
//    /**
//     * 测试safeInit，没有需要初始化的终端状态
//     *
//     * @param tcpServer mock对象
//     * @throws InterruptedException ex
//     */
//    @Test
//    public void testSafeInitNoInitTerminal(@Mocked TcpServer tcpServer) throws InterruptedException {
//        new Expectations() {
//            {
//                terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
//                result = new ArrayList<>();
//            }
//        };
//        init.safeInit();
//
//        Thread.sleep(1000);
//
//        new Verifications() {
//            {
//                terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
//                times = 1;
//                terminalBasicInfoService.modifyTerminalStateToOffline(anyString);
//                times = 0;
//                tcpServer.start();
//                times = 1;
//            }
//        };
//    }
//
//    /**
//     * 测试safeInit，有需要初始化的终端状态
//     *
//     * @param tcpServer mock对象
//     * @throws InterruptedException ex
//     */
//    @Test
//    public void testSafeInitHasInitTerminal(@Mocked TcpServer tcpServer) throws InterruptedException {
//        new Expectations() {
//            {
//                terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
//                List<TerminalEntity> terminalList = new ArrayList<>();
//                TerminalEntity terminalEntity = new TerminalEntity();
//                terminalEntity.setTerminalId("1");
//                terminalList.add(terminalEntity);
//                result = terminalList;
//            }
//        };
//        init.safeInit();
//
//        Thread.sleep(1000);
//
//        new Verifications() {
//            {
//                terminalBasicInfoDAO.findTerminalEntitiesByState(CbbTerminalStateEnums.ONLINE);
//                times = 1;
//                terminalBasicInfoService.modifyTerminalStateToOffline("1");
//                times = 1;
//                tcpServer.start();
//                times = 1;
//                terminalEventNoticeSPI.notify((CbbNoticeRequest) any);
//                times = 1;
//            }
//        };
//    }
//
//}
