package com.ruijie.rcos.rcdc.terminal.module.impl.spi.maintenance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbCpuArchType;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradePackageDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalSystemUpgradeServiceImpl;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;

/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/4/17
 *
 * @author nt
 */
@RunWith(SkyEngineRunner.class)
public class BeforeEnteringMaintenanceHandlerTest {

    @Tested
    private BeforeEnteringMaintenanceHandler handler;

    @Injectable
    private TerminalSystemUpgradePackageDAO systemUpgradePackageDAO;

    @Injectable
    private TerminalSystemUpgradeServiceImpl terminalSystemUpgradeService;

    /**
     * testHandleNotVDIPackage
     *
     * @throws BusinessException BusinessException
     */
    @Test
    public void testHandleNotVDIPackage() throws BusinessException {

        new Expectations() {
            {
                systemUpgradePackageDAO.findByPackageTypeAndCpuArchAndIsDelete(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64, false);
                result = null;
            }
        };

        handler.handle();

        new Verifications() {
            {
                systemUpgradePackageDAO.findByPackageTypeAndCpuArchAndIsDelete(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64, false);
                times = 1;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress((UUID) any);
                times = 0;
            }
        };
    }

    /**
     * testHandleHasUpgradeTask
     *
     * @throws BusinessException BusinessException
     */
    @Test
    public void testHandleHasUpgradeTask() throws BusinessException {

        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setId(UUID.randomUUID());
        packageEntity.setIsDelete(false);
        new Expectations() {
            {
                systemUpgradePackageDAO.findByPackageTypeAndCpuArchAndIsDelete(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64, false);
                result = packageEntity;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageEntity.getId());
                result = true;
            }
        };

        try {
            handler.handle();
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_TERMINAL_MAINTENANCE_PRE_VALIDATE_FAIL_FOR_LINUX_VDI_UPGRADING_TASK, e.getKey());
        }

        new Verifications() {
            {
                systemUpgradePackageDAO.findByPackageTypeAndCpuArchAndIsDelete(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64, false);
                times = 1;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress((UUID) any);
                times = 1;
            }
        };
    }

    /**
     * testHandle
     *
     * @throws BusinessException BusinessException
     */
    @Test
    public void testHandle() throws BusinessException {

        TerminalSystemUpgradePackageEntity packageEntity = new TerminalSystemUpgradePackageEntity();
        packageEntity.setId(UUID.randomUUID());
        packageEntity.setIsDelete(false);
        new Expectations() {
            {
                systemUpgradePackageDAO.findByPackageTypeAndCpuArchAndIsDelete(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64, false);
                result = packageEntity;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress(packageEntity.getId());
                result = false;
            }
        };

        handler.handle();

        new Verifications() {
            {
                systemUpgradePackageDAO.findByPackageTypeAndCpuArchAndIsDelete(CbbTerminalTypeEnums.VDI_LINUX, CbbCpuArchType.X86_64, false);
                times = 1;

                terminalSystemUpgradeService.hasSystemUpgradeInProgress((UUID) any);
                times = 1;
            }
        };
    }
}
