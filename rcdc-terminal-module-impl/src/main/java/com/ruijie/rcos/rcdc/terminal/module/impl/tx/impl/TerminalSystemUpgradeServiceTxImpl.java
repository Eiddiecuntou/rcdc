package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbSystemUpgradeTaskStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalSystemUpgradeTerminalGroupDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.*;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.handler.systemupgrade.SystemUpgradeGlobal;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalSystemUpgradeServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

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

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalSystemUpgradeTerminalGroupDAO systemUpgradeTerminalGroupDAO;

    @Override
    public UUID addSystemUpgradeTask(TerminalSystemUpgradePackageEntity upgradePackage, CbbAddSystemUpgradeTaskRequest request) {
        Assert.notNull(upgradePackage, "upgradePackage can not be null");
        Assert.notNull(request, "request can not be null");

        TerminalSystemUpgradeEntity entity = addSystemUpgradeTaskEntity(upgradePackage);

        // 获取分组下的终端与选择的终端合并去重
        String[] terminalIdUnderGroupArr = obtainTerminalUnderGroup(request.getTerminalGroupIdArr(), upgradePackage.getPackageType());
        List<String> totalTerminalIdList = mergeTerminalId(request.getTerminalIdArr(), terminalIdUnderGroupArr);

        UUID upgradeTaskId = entity.getId();
        if (!CollectionUtils.isEmpty(totalTerminalIdList)) {
            totalTerminalIdList.forEach(terminalId -> {
                TerminalSystemUpgradeTerminalEntity upgradeTerminal = addSystemUpgradeTerminal(upgradeTaskId, terminalId);
                // 将终端状态设置为升级中。在终端转换为最终态时将终端设为离线（终端在线则状态不变）
                syncTerminalState(upgradeTerminal);
            });
        }

        if (ArrayUtils.isNotEmpty(request.getTerminalGroupIdArr())) {
            Arrays.stream(request.getTerminalGroupIdArr()).forEach(terminalGroupId -> {
                addSystemUpgradeGroup(upgradeTaskId, terminalGroupId);
            });
        }

        return upgradeTaskId;
    }

    private List<String> mergeTerminalId(String[] terminalIdArr, String[] terminalIdUnderGroupArr) {
        if (ArrayUtils.isEmpty(terminalIdArr)) {
            return Lists.newArrayList(terminalIdUnderGroupArr);
        }

        if (ArrayUtils.isEmpty(terminalIdUnderGroupArr)) {
            return Lists.newArrayList(terminalIdArr);
        }

        List<String> manualTerminalList = Lists.newArrayList(terminalIdArr);
        Arrays.stream(terminalIdUnderGroupArr).forEach(terminalIdUnderGroup -> {
            if (manualTerminalList.contains(terminalIdUnderGroup)) {
                // 去除重复
                return;
            }

            manualTerminalList.add(terminalIdUnderGroup);
        });

        return manualTerminalList;
    }

    private String[] obtainTerminalUnderGroup(UUID[] terminalGroupIdArr, CbbTerminalTypeEnums terminalType) {
        if (ArrayUtils.isEmpty(terminalGroupIdArr)) {
            return new String[0];
        }

        List<TerminalEntity> terminalList = Lists.newArrayList();
        Arrays.stream(terminalGroupIdArr).forEach(terminalGroupId -> {
            List<TerminalEntity> underGroupTerminalList = basicInfoDAO.findByGroupIdAndPlatformAndTerminalOsType(terminalGroupId,
                    CbbTerminalPlatformEnums.convert(terminalType.getPlatform()), terminalType.getOsType());
            if (CollectionUtils.isEmpty(underGroupTerminalList)) {
                return;
            }

            terminalList.addAll(underGroupTerminalList);
        });

        return terminalList.stream().map(terminalEntity -> terminalEntity.getTerminalId()).toArray(String[]::new);
    }

    @Override
    public void editUpgradeGroup(TerminalSystemUpgradeEntity upgradeEntity, UUID[] terminalGroupIdArr) {
        Assert.notNull(upgradeEntity, "TerminalSystemUpgradeEntity can not be null");
        Assert.notNull(terminalGroupIdArr, "terminalGroupIdArr can not be null");

        // 删除后再添加
        systemUpgradeTerminalGroupDAO.deleteBySysUpgradeId(upgradeEntity.getId());

        for (UUID groupId : terminalGroupIdArr) {
            addSystemUpgradeGroup(upgradeEntity.getId(), groupId);
        }
    }

    private void addSystemUpgradeGroup(UUID sysUpgradeId, UUID terminalGroupId) {
        TerminalSystemUpgradeTerminalGroupEntity saveUpgradeGroupEntity = new TerminalSystemUpgradeTerminalGroupEntity();
        saveUpgradeGroupEntity.setSysUpgradeId(sysUpgradeId);
        saveUpgradeGroupEntity.setTerminalGroupId(terminalGroupId);
        saveUpgradeGroupEntity.setCreateTime(new Date());

        systemUpgradeTerminalGroupDAO.save(saveUpgradeGroupEntity);
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

    @Override
    public void closeSystemUpgradeTask(UUID upgradeTaskId) throws BusinessException {
        Assert.notNull(upgradeTaskId, "upgradeTaskId can not be null");
        final TerminalSystemUpgradeEntity systemUpgradeTask = getSystemUpgradeTask(upgradeTaskId);
        if (systemUpgradeTask.getState() != CbbSystemUpgradeTaskStateEnums.UPGRADING) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_TASK_HAS_CLOSED);
        }

        doCloseTask(systemUpgradeTask);
    }

    private void doCloseTask(TerminalSystemUpgradeEntity systemUpgradeTask) throws BusinessException {

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

        systemUpgradeTask.setState(CbbSystemUpgradeTaskStateEnums.FINISH);
        systemUpgradeDAO.save(systemUpgradeTask);
    }

    private void setUpgradingTerminalToFail(TerminalSystemUpgradeTerminalEntity upgradingTerminal) throws BusinessException {
        upgradingTerminal.setState(CbbSystemUpgradeStateEnums.FAIL);
        modifySystemUpgradeTerminalState(upgradingTerminal);
        SystemUpgradeGlobal.releaseUpgradeQuota(upgradingTerminal.getTerminalId());
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
            case UNSUPPORTED:
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
