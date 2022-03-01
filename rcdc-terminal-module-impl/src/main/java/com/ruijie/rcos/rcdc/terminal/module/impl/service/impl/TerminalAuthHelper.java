package com.ruijie.rcos.rcdc.terminal.module.impl.service.impl;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbShineTerminalBasicInfo;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalWhiteListHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseAuthService;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseCommonService;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalAuthResultEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalAuthResult;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalAuthorizationWhitelistService;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Description: TerminalAuthHelper
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/11/20
 *
 * @author nting
 */
@Service
public class TerminalAuthHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalAuthHelper.class);

    @Autowired
    private CbbTerminalWhiteListHandlerSPI whiteListHandlerSPI;

    @Autowired
    private TerminalLicenseAuthService terminalLicenseAuthService;

    @Autowired
    private TerminalLicenseCommonService terminalLicenseCommonService;

    @Autowired
    private TerminalAuthorizationWhitelistService terminalAuthorizationWhitelistService;

    @Autowired
    private TerminalBasicInfoDAO terminalBasicInfoDAO;

    /**
     * 终端进行授权
     *
     * @param isInUpgradeProcess 是否处于升级进程中
     * @param basicInfo          终端基本信息
     * @return TerminalAuthResult 授权结果
     */
    public TerminalAuthResult processTerminalAuth(boolean isInUpgradeProcess, CbbShineTerminalBasicInfo basicInfo) {
        Assert.notNull(basicInfo, "basicInfo can not be null");

        LOGGER.info("终端[{}]{}接入", basicInfo.getTerminalId(), basicInfo.getTerminalName());
        if (isInUpgradeProcess) {
            LOGGER.info("终端处于升级过程中");
            // 终端需要升级，或者异常升级结果（不属于需要升级、不需要升级范畴，如：服务器准备中），不保存终端信息
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        TerminalEntity terminalEntity = terminalBasicInfoDAO.findTerminalEntityByTerminalId(basicInfo.getTerminalId());
        // TCI OCS授权优先
        if (terminalAuthorizationWhitelistService.checkWhiteList(basicInfo, terminalEntity)) {
            LOGGER.info("终端[{}]在CBB白名单中，无需认证", basicInfo.getTerminalId());
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        if (whiteListHandlerSPI.checkWhiteList(basicInfo)) {
            LOGGER.info("终端[{}]在白名单中，无需认证", basicInfo.getTerminalId());
            // 查找终端授权表是否有该终端信息，有的话则处理终端授权扣除逻辑
            if (terminalLicenseCommonService.isTerminalAuthed(basicInfo.getTerminalId())) {
                LOGGER.info("终端【{}】已授权且在白名单中，需要进行回收", basicInfo.getTerminalId());
                try {
                    terminalLicenseAuthService.recycle(basicInfo.getTerminalId(), basicInfo.getAuthMode());
                } catch (BusinessException e) {
                    LOGGER.error("white list auth recycle error: ", e);
                }
            }

            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        if (terminalLicenseCommonService.isTerminalAuthed(basicInfo.getTerminalId())) {
            LOGGER.info("终端[{}]{}已授权，需要更新终端信息", basicInfo.getTerminalId(), basicInfo.getTerminalName());
            return new TerminalAuthResult(true, TerminalAuthResultEnums.SKIP);
        }

        if (ArrayUtils.isEmpty(basicInfo.getTerminalWorkSupportModeArr())) {
            LOGGER.error("终端工作模式为空，跳过授权");
            return new TerminalAuthResult(false, TerminalAuthResultEnums.SKIP);
        }

        //TODO yanlin 临时授权导入正式授权，终端置为未授权时，终端重新接入，
        // 应该要判断下是否CVA授权，再进行云应用的授权分配，决定auth方法是传true还是false
        try {
            return terminalLicenseAuthService.auth(basicInfo.getTerminalId(), basicInfo.getAuthMode(), Boolean.FALSE);
        } catch (BusinessException e) {
            LOGGER.error("终端[" + basicInfo.getTerminalId() + "]授权异常", e);
            return new TerminalAuthResult(false, TerminalAuthResultEnums.FAIL);
        }

    }

    /**
     * 处理终端授权扣除逻辑
     *
     * @param terminalId 终端id
     * @param authMode   平台类型
     * @param authed     是否授权
     * @throws BusinessException 业务异常
     */
    public void processDecreaseTerminalLicense(String terminalId, CbbTerminalPlatformEnums authMode, Boolean authed) throws BusinessException {
        Assert.notNull(terminalId, "terminalId can not be null");
        Assert.notNull(authMode, "authMode can not be null");
        Assert.notNull(authed, "authed can not be null");

        if (Objects.equals(authed, Boolean.TRUE)) {
            LOGGER.info("删除已授权终端[{}]，终端授权数量-1", terminalId);
            terminalLicenseAuthService.recycle(terminalId, authMode);
            return;
        }

        LOGGER.info("删除未授权终端[{}]，无需处理", terminalId);

    }


}
