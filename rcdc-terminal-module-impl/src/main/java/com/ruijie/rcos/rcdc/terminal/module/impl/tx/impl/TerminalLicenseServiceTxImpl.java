package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalWhiteListHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: TerminalLicenseServiceTx接口实现类
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/27 10:32 上午
 *
 * @author zhouhuan
 */
@Service
public class TerminalLicenseServiceTxImpl implements TerminalLicenseServiceTx {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalLicenseServiceTxImpl.class);

    private static final int FAIL_TRY_COUNT = 3;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Autowired
    private CbbTerminalWhiteListHandlerSPI whiteListHandlerSPI;

    @Override
    public void updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums platform, String licenseKey) {
        Assert.notNull(platform, "platform can not be empty");
        Assert.hasText(licenseKey, "licenseKey can not be empty");

        List<TerminalEntity> terminalEntityList = terminalBasicInfoDAO.findTerminalEntitiesByAuthModeAndAuthed(platform, Boolean.FALSE);

        List<TerminalEntity> needAuthTerminalList = filterLicenseFreeTerminal(terminalEntityList);

        updateTerminalAuthState(needAuthTerminalList, Boolean.TRUE);
        // 临时授权需要添加记录到授权记录表
        saveAuthRecord(platform, needAuthTerminalList);

    }

    private List<TerminalEntity> filterLicenseFreeTerminal(List<TerminalEntity> terminalEntityList) {
        return terminalEntityList.stream().filter(terminalEntity -> {
            if (StringUtils.isNotEmpty(terminalEntity.getOcsSn())) {
                LOGGER.debug("终端【{}】为OCS授权终端，无需授权，过滤掉", terminalEntity.getTerminalId());
                return false;
            }

            if (whiteListHandlerSPI.checkWhiteList(buildBasicInfo(terminalEntity))) {
                LOGGER.debug("终端【{}】为白名单终端，无需授权，过滤掉", terminalEntity.getTerminalId());
                return false;
            }

            return true;
        }).collect(Collectors.toList());
    }

    private CbbShineTerminalBasicInfo buildBasicInfo(TerminalEntity terminalEntity) {
        CbbShineTerminalBasicInfo basicInfo = new CbbShineTerminalBasicInfo();
        BeanUtils.copyProperties(terminalEntity, basicInfo);
        return basicInfo;
    }

    private synchronized void saveAuthRecord(CbbTerminalPlatformEnums platform, List<TerminalEntity> terminalEntityList) {
        terminalEntityList.stream().forEach(terminalEntity -> {
            TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalEntity.getTerminalId());
            if (authorizeEntity == null) {
                LOGGER.debug("终端不存在");
                authorizeEntity = new TerminalAuthorizeEntity();
            }
            authorizeEntity.setAuthMode(platform);
            authorizeEntity.setTerminalId(terminalEntity.getTerminalId());
            authorizeEntity.setAuthed(true);
            authorizeEntity.setLicenseType(platform.name());
            terminalAuthorizeDAO.save(authorizeEntity);
        });
    }

    @Override
    public void updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums platform, String licenseKey, Integer licenseNum) {
        Assert.notNull(platform, "platform can not be empty");
        Assert.hasText(licenseKey, "licenseKey can not be empty");
        Assert.notNull(licenseNum, "licenseNum can not null");

        List<TerminalEntity> terminalEntityList = terminalBasicInfoDAO.findTerminalEntitiesByAuthModeAndAuthed(platform, Boolean.TRUE);

        updateTerminalAuthState(terminalEntityList, Boolean.FALSE);
        // 临时授权变更为正式授权需要删除授权记录
        terminalEntityList.stream().forEach(terminalEntity -> terminalAuthorizeDAO.deleteByTerminalId(terminalEntity.getTerminalId()));


    }

    private void updateTerminalAuthState(List<TerminalEntity> terminalEntityList, Boolean authed) {
        terminalEntityList.stream().forEach(terminalEntity -> {
            if (authed.equals(terminalEntity.getAuthed())) {
                LOGGER.info("更新终端授权状态为{}时，发现终端此时的授权状态已经是{}。无须更新终端状态", authed, authed);
                return;
            }
            terminalEntity.setAuthed(authed);
            try {
                terminalBasicInfoDAO.save(terminalEntity);
            } catch (Exception e) {
                LOGGER.error("更新终端[" + terminalEntity.getTerminalId() + "]的授权状态为" + authed + "失败！", e);
                retryUpdateAuthed(terminalEntity.getTerminalId(), authed);
            }
        });
    }

    /**
     * 重试更新终端授权状态
     *
     * @param terminalId 终端id
     * @param authed 是否授权
     */
    private void retryUpdateAuthed(String terminalId, Boolean authed) {
        int retry = 0;

        while (retry++ < FAIL_TRY_COUNT) {
            LOGGER.warn("第{}次重试更新终端[{}]的授权状态为{}", retry, terminalId, authed);
            TerminalEntity entity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(terminalId);
            if (authed.equals(entity.getAuthed())) {
                LOGGER.info("重试更新终端授权状态为{}时，发现终端此时的授权状态已经是{}。无须重试更新终端状态", authed, authed);
                return;
            }
            entity.setAuthed(authed);
            try {
                terminalBasicInfoDAO.save(entity);
            } catch (Exception e) {
                LOGGER.error("第" + retry + "次重试更新终端[" + terminalId + "]的授权状态为" + authed + "失败！", e);
                continue;
            }
            LOGGER.info("成功通过重试更新终端[{}]的授权状态为：{}", terminalId, authed);
            return;
        }

        LOGGER.error("重试{}次，仍然无法成功将终端[{}]的授权状态更新为：{}", FAIL_TRY_COUNT, terminalId, authed);
    }
}
