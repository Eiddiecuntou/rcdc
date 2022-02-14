package com.ruijie.rcos.rcdc.terminal.module.impl.tx.impl;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.spi.CbbTerminalWhiteListHandlerSPI;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalLicenseServiceTx;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

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
    public void updateTerminalAuthedAndUnlimitTerminalAuth(CbbTerminalPlatformEnums platform, String licenseKey,
                                                           CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(platform, "platform can not be empty");
        Assert.hasText(licenseKey, "licenseKey can not be empty");
        Assert.notNull(licenseType, "licenseType can not be empty");

        List<String> productTypeWhiteList = whiteListHandlerSPI.getProductTypeWhiteList();
        LOGGER.info("productTypeWhiteList ==>{}", JSON.toJSONString(productTypeWhiteList));

        List<TerminalEntity> needAuthTerminalList = terminalBasicInfoDAO.findNoAuthedTerminalEntitiesByAuthModeAndLicenseType
                (platform.name(), licenseType.name(), productTypeWhiteList);
        LOGGER.info("needAuthTerminalList ==>{}", needAuthTerminalList.size());

        terminalBasicInfoDAO.updateTerminalsByAuthModeAndAuthed(platform, Boolean.FALSE, Boolean.TRUE, productTypeWhiteList);
        // 临时授权需要添加记录到授权记录表
        saveAuthRecord(platform, licenseType, needAuthTerminalList, productTypeWhiteList);
    }

    private void saveAuthRecord(CbbTerminalPlatformEnums platform, CbbTerminalLicenseTypeEnums licenseType,
                                List<TerminalEntity> needAuthTerminalList, List<String> productTypeWhiteList) {
        LOGGER.info("update or save terminalAuthorize==>{}", platform, needAuthTerminalList.size());
        terminalAuthorizeDAO.updateTerminalAuthorizesByPlatformAndAuthed(platform.name(), Boolean.FALSE, Boolean.TRUE, productTypeWhiteList);
        needAuthTerminalList.forEach(terminalEntity -> {
            TerminalAuthorizeEntity authorizeEntity = new TerminalAuthorizeEntity();
            authorizeEntity.setAuthMode(platform);
            authorizeEntity.setTerminalId(terminalEntity.getTerminalId());
            authorizeEntity.setAuthed(true);
            authorizeEntity.setLicenseType(licenseType.name());
            terminalAuthorizeDAO.save(authorizeEntity);
        });
        LOGGER.info("update or save terminalAuthorize end");
    }

    @Override
    public void updateTerminalUnauthedAndUpdateLicenseNum(CbbTerminalPlatformEnums platform, String licenseKey,
                                                          Integer licenseNum, CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(platform, "platform can not be empty");
        Assert.hasText(licenseKey, "licenseKey can not be empty");
        Assert.notNull(licenseNum, "licenseNum can not null");
        Assert.notNull(licenseType, "licenseType can not be null");

        List<String> productTypeWhiteList = whiteListHandlerSPI.getProductTypeWhiteList();
        LOGGER.info("开始更新授权[{}]信息", licenseType);
        terminalBasicInfoDAO.updateTerminalsByAuthModeAndAuthedJudgeByLicenseType(licenseType.name(), platform.name(), Boolean.TRUE, Boolean.FALSE,
                productTypeWhiteList);
        LOGGER.info("结束更新授权[{}]信息", licenseType);
        // 临时授权变更为正式授权需要删除授权记录
        LOGGER.info("开始删除授权信息[{}]", licenseType);
        terminalAuthorizeDAO.deleteByLicenseTypeContains(licenseType.name());
        LOGGER.info("结束删除授权信息[{}]", licenseType);
    }
}
