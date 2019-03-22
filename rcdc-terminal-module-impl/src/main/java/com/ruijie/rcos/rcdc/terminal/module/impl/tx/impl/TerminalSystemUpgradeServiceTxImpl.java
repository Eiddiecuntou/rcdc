package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradePackageEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalSystemUpgradeTerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;

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

    @Autowired
    private TerminalSystemUpgradeDAO systemUpgradeDAO;

    @Autowired
    private TerminalSystemUpgradeTerminalDAO systemUpgradeTerminalDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

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
    public void closeSystemUpgradeTask(UUID upgradeTaskId) throws BusinessException {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");

        final TerminalSystemUpgradeEntity systemUpgradeTask = getSystemUpgradeTask(upgradeTaskId);
        
        if (systemUpgradeTask.getState() != CbbSystemUpgradeTaskStateEnums.UPGRADING) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_HAS_CLOSED);
        }

        // 关闭未开始的刷机终端
        final List<TerminalSystemUpgradeTerminalEntity> waitUpgradeTerminalList = systemUpgradeTerminalDAO
                .findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.WAIT);
        for (TerminalSystemUpgradeTerminalEntity waitUpgradeTerminal : waitUpgradeTerminalList) {
            closeWaitTerminal(waitUpgradeTerminal);
        }
        // 将升级中的终端设置为失败
        final List<TerminalSystemUpgradeTerminalEntity> upgradingTerminalList = systemUpgradeTerminalDAO
                .findBySysUpgradeIdAndState(systemUpgradeTask.getId(), CbbSystemUpgradeStateEnums.UPGRADING);
        for (TerminalSystemUpgradeTerminalEntity upgradingTerminal : upgradingTerminalList) {
            setUpgradingTerminalToFail(upgradingTerminal);
        }

        systemUpgradeTask.setState(CbbSystemUpgradeTaskStateEnums.CLOSING);
        systemUpgradeDAO.save(systemUpgradeTask);
    }

    private void setUpgradingTerminalToFail(TerminalSystemUpgradeTerminalEntity upgradingTerminal) throws BusinessException {
        modifySystemUpgradeTerminalState(upgradingTerminal.getSysUpgradeId(), upgradingTerminal.getTerminalId(),
                CbbSystemUpgradeStateEnums.FAIL);
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
        modifySystemUpgradeTerminalState(waitUpgradeTerminal.getSysUpgradeId(), waitUpgradeTerminal.getTerminalId(),
                CbbSystemUpgradeStateEnums.UNDO);
    }

    @Override
    public void modifySystemUpgradeTerminalState(UUID upgradeTaskId, String terminalId,
            CbbSystemUpgradeStateEnums state) throws BusinessException {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be blank");
        Assert.hasText(terminalId, "terminalId can not be blank");
        Assert.notNull(state, "state can not be blank");

        final TerminalSystemUpgradeTerminalEntity upgradeTerminal = getUpgradeTerminalEntity(upgradeTaskId, terminalId);
        upgradeTerminal.setState(state);
        systemUpgradeTerminalDAO.save(upgradeTerminal);

        syncTerminalState(upgradeTerminal);
    }



    @Override
    public void startTerminalUpgrade(UUID upgradeTaskId, String terminalId) throws BusinessException {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        Assert.hasText(terminalId, "terminalId can not be null");

        final TerminalSystemUpgradeTerminalEntity upgradeTerminal = getUpgradeTerminalEntity(upgradeTaskId, terminalId);
        upgradeTerminal.setStartTime(new Date());
        upgradeTerminal.setState(CbbSystemUpgradeStateEnums.UPGRADING);
        systemUpgradeTerminalDAO.save(upgradeTerminal);

        syncTerminalState(upgradeTerminal);
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

        boolean isTerminalOnline = basicInfoService.isTerminalOnline(upgradeTerminal.getTerminalId());
        if (terminalState == CbbTerminalStateEnums.OFFLINE && isTerminalOnline) {
            return;
        }
        basicInfoService.modifyTerminalState(upgradeTerminal.getTerminalId(), terminalState);
    }

    private TerminalSystemUpgradeTerminalEntity getUpgradeTerminalEntity(UUID upgradeTaskId, String terminalId)
            throws BusinessException {
        final TerminalSystemUpgradeTerminalEntity upgradeTerminal =
                systemUpgradeTerminalDAO.findFirstBySysUpgradeIdAndTerminalId(upgradeTaskId, terminalId);

        if (upgradeTerminal == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TERMINAL_NOT_EXIST);
        }
        return upgradeTerminal;
    }



}
