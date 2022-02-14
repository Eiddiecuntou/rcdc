package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLicenseMgmtAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalLicenseNumDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalLicenseTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.TerminalLicenseAuthService;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.dao.TerminalAuthorizeDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.auth.entity.TerminalAuthorizeEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.factory.CbbTerminalLicenseFactoryProvider;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Description: 终端授权管理apiImpl
 * Copyright: Copyright (c) 2020
 * Company: Ruijie Co., Ltd.
 * Create Time: 2020/9/17 9:51 下午
 *
 * @author zhouhuan
 */
public class CbbTerminalLicenseMgmtAPIImpl implements CbbTerminalLicenseMgmtAPI {

    public static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalLicenseMgmtAPIImpl.class);

    @Autowired
    CbbTerminalLicenseFactoryProvider licenseFactoryProvider;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalAuthHelper terminalAuthHelper;

    @Autowired
    private TerminalLicenseAuthService terminalLicenseAuthService;

    @Autowired
    private TerminalAuthorizeDAO terminalAuthorizeDAO;

    @Override
    public void setTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, List<CbbTerminalLicenseInfoDTO> licenseInfoList)
            throws BusinessException {
        Assert.notNull(licenseType, "licenseType can not be null");
        Assert.notNull(licenseInfoList, "licenseInfoList can not be null");
        licenseFactoryProvider.getService(licenseType).updateTerminalLicenseNum(licenseInfoList);

        // 更新所有类型证书的已用授权数
        refreshAllLicenseUsedNum();
    }

    private void refreshAllLicenseUsedNum() {
        for (CbbTerminalLicenseTypeEnums licenseType : CbbTerminalLicenseTypeEnums.values()) {
            LOGGER.info("更新证书类型【{}】的证书使用数量信息", licenseType.name());
            licenseFactoryProvider.getService(licenseType).refreshLicenseUsedNum();
        }
    }

    @Override
    public boolean checkTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(licenseType, "licenseType can not be null");
        CbbTerminalLicenseNumDTO terminalLicenseNum = getTerminalLicenseNum(licenseType, null);
        return Objects.equals(terminalLicenseNum.getLicenseNum(), Constants.TERMINAL_AUTH_DEFAULT_NUM)
                || terminalLicenseNum.getUsedNum() < terminalLicenseNum.getLicenseNum();
    }

    @Override
    public CbbTerminalLicenseNumDTO getTerminalLicenseNum(CbbTerminalLicenseTypeEnums licenseType, @Nullable List<String> licenseCodeList) {
        Assert.notNull(licenseType, "licenseType can not be null");

        Integer licenseNum;
        if (CollectionUtils.isEmpty(licenseCodeList)) {
            licenseNum = licenseFactoryProvider.getService(licenseType).getAllTerminalLicenseNum();
        } else {
            licenseNum = licenseFactoryProvider.getService(licenseType).getTerminalLicenseNum(licenseCodeList);
        }

        Integer usedNum = licenseFactoryProvider.getService(licenseType).getUsedNum();

        List<CbbTerminalLicenseInfoDTO> licenseInfoList = licenseFactoryProvider.getService(licenseType)
                .getTerminalLicenseInfo(CollectionUtils.isEmpty(licenseCodeList) ? Lists.newArrayList() : licenseCodeList);
        setLicenseUsedNum(licenseInfoList, usedNum);

        CbbTerminalLicenseNumDTO licenseNumDTO = new CbbTerminalLicenseNumDTO();
        licenseNumDTO.setLicenseType(licenseType);
        licenseNumDTO.setLicenseNum(licenseNum);
        licenseNumDTO.setUsedNum(usedNum);
        licenseNumDTO.setLicenseInfoList(licenseInfoList);

        LOGGER.info("终端授权数量：{}", JSON.toJSONString(licenseNumDTO, SerializerFeature.PrettyFormat));
        return licenseNumDTO;
    }

    private void setLicenseUsedNum(List<CbbTerminalLicenseInfoDTO> licenseInfoList, Integer usedNum) {
        if (CollectionUtils.isEmpty(licenseInfoList)) {
            LOGGER.info("证书信息为空，无需处理");
            return;
        }

        int leftNum = usedNum;
        for (CbbTerminalLicenseInfoDTO licenseInfoDTO : licenseInfoList) {
            // 总数为-1则表示临时授权
            if (licenseInfoDTO.getTotalNum() == Constants.TERMINAL_AUTH_DEFAULT_NUM) {
                LOGGER.info("证书为临时证书,{}", JSON.toJSONString(licenseInfoDTO));
                licenseInfoDTO.setUsedNum(leftNum);
                break;
            }

            if (licenseInfoDTO.getTotalNum() == 0) {
                licenseInfoDTO.setUsedNum(0);
                continue;
            }

            if (licenseInfoDTO.getTotalNum() >= leftNum) {
                LOGGER.info("证书数量大于等于使用数量,{}", JSON.toJSONString(licenseInfoDTO));
                licenseInfoDTO.setUsedNum(leftNum);
                break;
            }

            LOGGER.info("证书数量小于等于使用数量,{}", JSON.toJSONString(licenseInfoDTO));
            licenseInfoDTO.setUsedNum(licenseInfoDTO.getTotalNum());
            leftNum = leftNum - licenseInfoDTO.getTotalNum();
        }

    }

    @Override
    public Boolean checkEnableAuthTerminal(CbbTerminalPlatformEnums authMode) throws BusinessException {
        Assert.notNull(authMode, "authMode can not be null");

        return terminalLicenseAuthService.checkEnableAuth(authMode);
    }

    @Override
    public void cancelTerminalAuth(String terminalId) throws BusinessException {
        Assert.notNull(terminalId, "terminalId can not be null");

        LOGGER.info("收到取消终端:{}授权请求", terminalId);
        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (terminalEntity == null) {
            LOGGER.error("不存在终端:{}信息，无需取消授权", terminalId);
            return;
        }

        if (Boolean.FALSE.equals(terminalEntity.getAuthed())) {
            LOGGER.info("终端:{}未授权，无需取消授权", terminalId);
            return;
        }

        cancelAuth(terminalEntity);
    }

    private void cancelAuth(TerminalEntity terminalEntity) throws BusinessException {
        try {
            //更新数据库
            terminalEntity.setAuthed(Boolean.FALSE);
            basicInfoDAO.save(terminalEntity);
        } catch (Exception e) {
            LOGGER.error("保存终端信息失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_CANCEL_AUTH_FAIL, e);
        }

        //处理终端授权扣除逻辑
        terminalAuthHelper.processDecreaseTerminalLicense(terminalEntity.getTerminalId(), terminalEntity.getAuthMode(), Boolean.TRUE);
    }

    @Override
    public boolean checkTerminalCurrentLicenseType(String terminalId, CbbTerminalLicenseTypeEnums licenseType) {
        Assert.notNull(terminalId, "terminalId can not be null");
        Assert.notNull(licenseType, "licenseType can not be null");
        TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
        if (authorizeEntity == null) {
            LOGGER.error("不存在终端:{}信息，无需添加授权", terminalId);
            return false;
        }
        String licenseTypeByEntity = authorizeEntity.getLicenseType();
        if (StringUtils.isBlank(licenseTypeByEntity)) {
            return false;
        }
        switch (licenseType) {
            case IDV:
                return licenseTypeByEntity.equals(CbbTerminalLicenseTypeEnums.IDV.name());
            case CVA:
                return licenseTypeByEntity.equals(CbbTerminalLicenseTypeEnums.CVA_IDV.name())
                        || licenseTypeByEntity.equals(CbbTerminalLicenseTypeEnums.CVA.name());
            case VOI:
                return licenseTypeByEntity.equals(CbbTerminalLicenseTypeEnums.VOI.name());
            case VOI_PLUS_UPGRADED:
                return licenseTypeByEntity.equals(CbbTerminalLicenseTypeEnums.VOI_PLUS_UPGRADED.name());
            case IDV_PLUS_UPGRADED:
                return licenseTypeByEntity.equals(CbbTerminalLicenseTypeEnums.IDV_PLUS_UPGRADED.name());
            default:
                return false;
        }
    }

    @Override
    public void addTerminalCvaAuth(String terminalId, CbbTerminalLicenseTypeEnums terminalLicenseType) {

        Assert.notNull(terminalId, "terminalId can not be null");
        Assert.notNull(terminalLicenseType, "terminalLicenseType can not be null");

        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (terminalEntity == null) {
            LOGGER.error("不存在终端:{}信息，无需添加授权", terminalId);
            return;
        }

        TerminalAuthorizeEntity authorizeEntity = terminalAuthorizeDAO.findByTerminalId(terminalId);
        if (authorizeEntity.getAuthed().equals(Boolean.FALSE)) {
            LOGGER.info("应用虚拟化终端:{}，转换IDV为CVA证书类型", terminalId);
            authorizeEntity.setLicenseType(terminalLicenseType.name());
            authorizeEntity.setAuthed(Boolean.TRUE);
            terminalAuthorizeDAO.save(authorizeEntity);
        }
        if (terminalLicenseType == CbbTerminalLicenseTypeEnums.CVA_IDV) {
            licenseFactoryProvider.getService(CbbTerminalLicenseTypeEnums.CVA_IDV).increaseCacheLicenseUsedNum();
        }
        if (terminalLicenseType == CbbTerminalLicenseTypeEnums.CVA) {
            licenseFactoryProvider.getService(CbbTerminalLicenseTypeEnums.CVA).increaseCacheLicenseUsedNum();
        }
    }
}
