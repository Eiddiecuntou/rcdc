package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalOtaUpgradeScheduleService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutor;
import com.ruijie.rcos.sk.base.concurrent.ThreadExecutors;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Description: 终端刷机存在事物的操作
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年2月15日
 * 
 * @author nt
 */
@Service
public class TerminalSystemUpgradeServiceTxImpl implements TerminalSystemUpgradeServiceTx {

    private static final int PERIOD_SECOND = 15;

    private static ThreadExecutor OTA_UPGRADE_SCHEDULED_THREAD_POOL =
            ThreadExecutors.newBuilder("OTA_UPGRADE_SCHEDULED_THREAD").maxThreadNum(1).queueSize(1).build();

    private static ScheduledFuture<?> UPGRADE_TASK_FUTURE = null;

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Override
    public UUID addSystemUpgradeTask(TerminalSystemUpgradePackageEntity upgradePackage, String[] terminalIdArr) {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notEmpty(terminalIdArr, "terminalIdArr can not be empty");

        TerminalSystemUpgradeEntity entity = addSystemUpgradeTaskEntity(upgradePackage);

        UUID upgradeTaskId = entity.getId();
        for (String terminalId : terminalIdArr) {
            TerminalSystemUpgradeTerminalEntity upgradeTerminal = addSystemUpgradeTerminal(upgradeTaskId, terminalId);
            // 将终端状态设置为升级中。在终端转换为最终态时将终端设为离线（终端在线则状态不变）
            syncTerminalState(upgradeTerminal);
        }
        return upgradeTaskId;
    }

    /**
     * 添加刷机任务
     * 
     * @param upgradePackage 刷机包对象
     * @return 刷机任务对象
     */
    private TerminalSystemUpgradeEntity addSystemUpgradeTaskEntity(TerminalSystemUpgradePackageEntity upgradePackage) {
        TerminalSystemUpgradeEntity entity = new TerminalSystemUpgradeEntity();
        entity.setUpgradePackageId(upgradePackage.getId());
        entity.setPackageName(upgradePackage.getPackageName());
        entity.setPackageVersion(upgradePackage.getPackageVersion());
        entity.setPackageType(upgradePackage.getPackageType());
        entity.setCreateTime(new Date());
        entity.setState(CbbSystemUpgradeTaskStateEnums.UPGRADING);
        systemUpgradeDAO.save(entity);
        return entity;
    }

    /**
     * 添加刷机终端
     * 
     * @param upgradeTaskId 刷机任务id
     * @param terminalId 终端id
     */
    private TerminalSystemUpgradeTerminalEntity addSystemUpgradeTerminal(UUID upgradeTaskId, String terminalId) {
        TerminalSystemUpgradeTerminalEntity entity = new TerminalSystemUpgradeTerminalEntity();
        entity.setSysUpgradeId(upgradeTaskId);
        entity.setTerminalId(terminalId);
        entity.setState(CbbSystemUpgradeStateEnums.WAIT);
        entity.setCreateTime(new Date());
        systemUpgradeTerminalDAO.save(entity);
        return entity;
    }

    @Override
    public synchronized void startOtaUpgradeTask(TerminalSystemUpgradePackageEntity upgradePackage) {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        if (UPGRADE_TASK_FUTURE == null) {
            TerminalSystemUpgradeEntity entity = addSystemUpgradeTaskEntity(upgradePackage);
            UUID upgradeTaskId = entity.getId();
            //开启检查终端状态定时任务
            UPGRADE_TASK_FUTURE =  OTA_UPGRADE_SCHEDULED_THREAD_POOL.scheduleAtFixedRate(new TerminalOtaUpgradeScheduleService(upgradeTaskId,systemUpgradeTerminalDAO),
                    0, PERIOD_SECOND, TimeUnit.SECONDS);
        }
    }

    private void closeAndroidVDIUpgradeTask(TerminalSystemUpgradeEntity systemUpgradeTask) throws BusinessException {
        Assert.notNull(systemUpgradeTask, "systemUpgradeTask can not be null");
        // 将升级中的终端设置为失败
        final List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList =
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.UPGRADING);
        for (TerminalSystemUpgradeTerminalEntity upgradingTerminal : upgradingTerminalList) {
            setUpgradingTerminalToFail(upgradingTerminal);
        }
        
        //FIXME 停止bt服务
        
        systemUpgradeTask.setState(CbbSystemUpgradeTaskStateEnums.FINISH);
        systemUpgradeDAO.save(systemUpgradeTask);
        //关闭定时任务
        cancelScheduleTask();
    }

    private void cancelScheduleTask() {
        if (UPGRADE_TASK_FUTURE != null) {
            UPGRADE_TASK_FUTURE.cancel(true);
            UPGRADE_TASK_FUTURE = null;
        }
    }

    @Override
    public void closeSystemUpgradeTask(UUID upgradeTaskId) throws BusinessException {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        final TerminalSystemUpgradeEntity systemUpgradeTask = getSystemUpgradeTask(upgradeTaskId);
        if (systemUpgradeTask.getPackageType() == CbbTerminalTypeEnums.VDI_ANDROID) {
            closeAndroidVDIUpgradeTask(systemUpgradeTask);

        } else if (systemUpgradeTask.getPackageType() == CbbTerminalTypeEnums.VDI_LINUX) {
            closeLinuxVDIUpgradeTask(systemUpgradeTask);
        }

    }

    private void closeLinuxVDIUpgradeTask(TerminalSystemUpgradeEntity systemUpgradeTask) throws BusinessException {
        Assert.notNull(systemUpgradeTask, "systemUpgradeTask can not be null");
        if (systemUpgradeTask.getState() != CbbSystemUpgradeTaskStateEnums.UPGRADING) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_HAS_CLOSED);
        }

        // 关闭未开始的刷机终端
        final List<TerminalSystemUpgradeTerminalEntity> waitUpgradeTerminalList =
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
        for (TerminalSystemUpgradeTerminalEntity waitUpgradeTerminal : waitUpgradeTerminalList) {
            closeWaitTerminal(waitUpgradeTerminal);
        }
        // 将升级中的终端设置为失败
        final List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList =
                systemUpgradeTerminalDAO.findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.UPGRADING);
        for (TerminalSystemUpgradeTerminalEntity upgradingTerminal : upgradingTerminalList) {
            setUpgradingTerminalToFail(upgradingTerminal);
        }

        systemUpgradeTask.setState(CbbSystemUpgradeTaskStateEnums.CLOSING);
        systemUpgradeDAO.save(systemUpgradeTask);
    }

    private void setUpgradingTerminalToFail(TerminalSystemUpgradeTerminalEntity upgradingTerminal) throws BusinessException {
        upgradingTerminal.setState(CbbSystemUpgradeStateEnums.FAIL);
        modifySystemUpgradeTerminalState(upgradingTerminal);
    }

    /**
     * 获取刷机任务记录
     * 
     * @param upgradeTaskId 刷机任务id
     * @return 刷机任务id
     * @throws BusinessException 业务异常
     */
    private TerminalSystemUpgradeEntity getSystemUpgradeTask(UUID upgradeTaskId) throws BusinessException {
        Optional<TerminalSystemUpgradeEntity> systemUpgradeOpt = systemUpgradeDAO.findById(upgradeTaskId);
        final TerminalSystemUpgradeEntity upgradeTask = systemUpgradeOpt.orElse(null);
        if (upgradeTask == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_NOT_EXIST);
        }
        return upgradeTask;
    }

    /**
     * 关闭未开始的刷机终端
     * 
     * @param waitUpgradeTerminal 等待中的刷机终端
     * @throws BusinessException 业务异常
     */
    private void closeWaitTerminal(TerminalSystemUpgradeTerminalEntity waitUpgradeTerminal) throws BusinessException {
        waitUpgradeTerminal.setState(CbbSystemUpgradeStateEnums.UNDO);
        modifySystemUpgradeTerminalState(waitUpgradeTerminal);
    }

    @Override
    public void modifySystemUpgradeTerminalState(TerminalSystemUpgradeTerminalEntity upgradeTerminal) throws BusinessException {
        Assert.notNull(upgradeTerminal, "upgradeTerminal can not be null");
        final UUID upgradeTaskId = upgradeTerminal.getSysUpgradeId();
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        final String terminalId = upgradeTerminal.getTerminalId();
        Assert.hasText(terminalId, "terminalId can not be blank");
        final CbbSystemUpgradeStateEnums state = upgradeTerminal.getState();
        Assert.notNull(state, "state can not be null");

        final TerminalSystemUpgradeTerminalEntity saveEntity = getUpgradeTerminalEntity(upgradeTaskId, terminalId);
        saveEntity.setState(state);
        systemUpgradeTerminalDAO.save(saveEntity);

        syncTerminalState(saveEntity);
    }



    @Override
    public void startTerminalUpgrade(TerminalSystemUpgradeTerminalEntity upgradeTerminal) throws BusinessException {
        Assert.notNull(upgradeTerminal, "upgradeTerminal can not be null");
        final UUID upgradeTaskId = upgradeTerminal.getSysUpgradeId();
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        final String terminalId = upgradeTerminal.getTerminalId();
        Assert.hasText(terminalId, "terminalId can not be null");

        final TerminalSystemUpgradeTerminalEntity saveEntity = getUpgradeTerminalEntity(upgradeTaskId, terminalId);
        saveEntity.setStartTime(new Date());
        saveEntity.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        systemUpgradeTerminalDAO.save(saveEntity);

        syncTerminalState(saveEntity);
    }

    /**
     * 根据刷机终端的状态变化改变终端状态
     * 
     * @param upgradeTerminal 刷机终端对象
     */
    private void syncTerminalState(TerminalSystemUpgradeTerminalEntity upgradeTerminal) {
        final CbbSystemUpgradeStateEnums state = upgradeTerminal.getState();
        CbbTerminalStateEnums terminalState = null;
        switch (state) {
            case UPGRADING:
                terminalState = CbbTerminalStateEnums.UPGRADING;
                break;
            case SUCCESS:
            case FAIL:
            case UNDO:
                terminalState = CbbTerminalStateEnums.OFFLINE;
                break;
            default:
                break;
        }

        if (terminalState == null) {
            return;
        }

        String terminalId = upgradeTerminal.getTerminalId();
        boolean isTerminalOnline = basicInfoService.isTerminalOnline(terminalId);
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (terminalState == CbbTerminalStateEnums.OFFLINE && isTerminalOnline) {
            // 终端在线，则判断终端数据库状态是否为升级中，是则更新为在线
            if (terminalEntity.getState() != CbbTerminalStateEnums.UPGRADING) {
                return;
            }
            terminalState = CbbTerminalStateEnums.ONLINE;
        }
        basicInfoService.modifyTerminalState(terminalId, terminalState);
    }

    private TerminalSystemUpgradeTerminalEntity getUpgradeTerminalEntity(UUID upgradeTaskId, String terminalId) throws BusinessException {
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);

        if (upgradeTerminal == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NOT_EXIST);
        }
        return upgradeTerminal;
    }



}
