package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSONArray;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalOperatorAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbChangePasswordDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbModifyTerminalDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbOfflineLoginSettingDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBasicInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalWorkModeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalPlatformEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.enums.CbbTerminalStartMode;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.connect.SessionManager;
import com.ruijie.rcos.rcdc.terminal.module.impl.dao.TerminalBasicInfoDAO;
import com.ruijie.rcos.rcdc.terminal.module.impl.entity.TerminalEntity;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBasicInfoService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalGroupService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalOperatorService;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.impl.TerminalAuthHelper;
import com.ruijie.rcos.rcdc.terminal.module.impl.tx.TerminalBasicInfoServiceTx;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.connectkit.api.tcp.session.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


/**
 * Description: 终端操作实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018/11/5
 *
 * @author Jarman
 */
public class CbbTerminalOperatorAPIImpl implements CbbTerminalOperatorAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalOperatorAPIImpl.class);

    private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,16})$";

    @Autowired
    private TerminalOperatorService operatorService;

    @Autowired
    private TerminalBasicInfoDAO basicInfoDAO;

    @Autowired
    private TerminalBasicInfoService basicInfoService;

    @Autowired
    private TerminalBasicInfoServiceTx terminalBasicInfoServiceTx;

    @Autowired
    private TerminalGroupService terminalGroupService;

    @Autowired
    private TerminalAuthHelper terminalAuthHelper;

    @Autowired
    private SessionManager sessionManager;

    @Override
    public CbbTerminalBasicInfoDTO findBasicInfoByTerminalId(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }

        CbbTerminalBasicInfoDTO basicInfoDTO = new CbbTerminalBasicInfoDTO();
        BeanUtils.copyProperties(basicInfoEntity, basicInfoDTO, TerminalEntity.BEAN_COPY_IGNORE_NETWORK_INFO_ARR,
                TerminalEntity.BEAN_COPY_IGNORE_DISK_INFO_ARR, TerminalEntity.BEAN_COPY_IGGNORE_NET_CARD_MAC_INFO_ARR);
        basicInfoDTO.setTerminalPlatform(basicInfoEntity.getPlatform());
        basicInfoDTO.setNetworkInfoArr(basicInfoEntity.getNetworkInfoArr());
        basicInfoDTO.setDiskInfoArr(basicInfoEntity.getDiskInfoArr());
        basicInfoDTO.setNetCardMacInfoArr(basicInfoEntity.getNetCardMacInfoArr());
        if (StringUtils.isNotBlank(basicInfoEntity.getSupportWorkMode())) {
            List<CbbTerminalWorkModeEnums> supportWorkModeList = JSONArray.parseArray( //
                    basicInfoEntity.getSupportWorkMode(), //
                    CbbTerminalWorkModeEnums.class);
            basicInfoDTO.setSupportWorkModeArr(supportWorkModeList.toArray(new CbbTerminalWorkModeEnums[supportWorkModeList.size()]));
        }
        return basicInfoDTO;
    }

    @Override
    public void delete(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");
        // 在线终端不允许删除
        boolean isOnline = basicInfoService.isTerminalOnline(terminalId);
        CbbTerminalBasicInfoDTO basicInfo = findBasicInfoByTerminalId(terminalId);
        if (isOnline) {
            String terminalName = basicInfo.getTerminalName();
            String macAddr = basicInfo.getMacAddr();
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_ONLINE_CANNOT_DELETE, new String[]{terminalName, macAddr});
        }

        terminalBasicInfoServiceTx.deleteTerminal(terminalId);
        if (basicInfo.getTerminalPlatform() == CbbTerminalPlatformEnums.IDV && Objects.equals(basicInfo.getAuthed(), Boolean.TRUE)) {
            LOGGER.info("删除已授权IDV终端[{}]，IDV终端授权数量-1", terminalId);
            terminalAuthHelper.processDecreaseIdvTerminalLicense();
        }

        if (basicInfo.getTerminalPlatform() == CbbTerminalPlatformEnums.VOI && Objects.equals(basicInfo.getAuthed(), Boolean.TRUE)) {
            LOGGER.info("删除已授权VOI终端[{}]，VOI终端授权数量-1", terminalId);
            terminalAuthHelper.processDecreaseVoiTerminalLicense();
        }
    }

    @Override
    public void modifyTerminal(CbbModifyTerminalDTO request) throws BusinessException {
        Assert.notNull(request, "request不能为空");

        String terminalId = request.getCbbTerminalId();
        TerminalEntity entity = getTerminalEntity(terminalId);

        // 校验终端分组
        terminalGroupService.checkGroupExist(request.getGroupId());

        // 终端名称有变更，发送名称变更消息给终端
        if (!request.getTerminalName().equals(entity.getTerminalName())) {
            try {
                basicInfoService.modifyTerminalName(terminalId, request.getTerminalName());
            } catch (BusinessException e) {
                LOGGER.error("修改终端名称失败，terminaId:" + terminalId, e);
                throw e;
            }
        }

        entity.setGroupId(request.getGroupId());
        entity.setTerminalName(request.getTerminalName());
        basicInfoDAO.save(entity);

    }

    private TerminalEntity getTerminalEntity(String terminalId) throws BusinessException {
        TerminalEntity basicInfoEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (basicInfoEntity == null) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        return basicInfoEntity;
    }

    @Override
    public void shutdown(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");

        operatorService.shutdown(terminalId);
    }

    @Override
    public void restart(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");

        operatorService.restart(terminalId);
    }

    @Override
    public void changePassword(CbbChangePasswordDTO request) throws BusinessException {
        Assert.notNull(request, "CbbChangePasswordRequest不能为空");

        checkPwdIsLegal(request.getPassword());
        operatorService.changePassword(request.getPassword());
    }

    private void checkPwdIsLegal(String password) throws BusinessException {
        if (Pattern.matches(REGEX_PASSWORD, password)) {
            return;
        }
        throw new BusinessException(BusinessKey.RCDC_TERMINAL_ADMIN_PWD_ILLEGAL);
    }

    @Override
    public String queryPassword() throws BusinessException {
        return operatorService.getTerminalPassword();
    }

    @Override
    public void relieveFault(String terminalId, @Nullable Object content) throws BusinessException {
        Assert.hasText(terminalId, "terminalId不能为空");

        operatorService.relieveFault(terminalId, content);
    }

    @Override
    public void clearIdvTerminalDataDisk(String terminalId) throws BusinessException {
        Assert.hasText(terminalId, "terminalId can not be blank");

        operatorService.diskClear(terminalId);
    }

    /**
     * IDV终端离线登录设置
     *
     * @param request 请求参数
     * @throws BusinessException 业务异常
     */
    @Override
    public void idvOfflineLoginSetting(CbbOfflineLoginSettingDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        operatorService.offlineLoginSetting(request.getOfflineAutoLocked());
    }

    /**
     * IDV终端离线登录设置
     *
     * @return 返回成功失败
     * @throws BusinessException 业务异常
     */
    @Override
    public String queryOfflineLoginSetting() throws BusinessException {
        String offlineLoginSetting = operatorService.queryOfflineLoginSetting();
        return offlineLoginSetting;
    }

    @Override
    public void setTerminalStartMode(String terminalId, CbbTerminalStartMode startMode) throws BusinessException {
        Assert.hasText(terminalId, "terminalId cannot be empty");
        Assert.notNull(startMode, "startMode cannot be null");

        TerminalEntity terminalEntity = basicInfoDAO.findTerminalEntityByTerminalId(terminalId);
        if (terminalEntity == null) {
            LOGGER.error("终端[{}]不存在", terminalId);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_NOT_FOUND_TERMINAL);
        }
        try {
            terminalEntity.setStartMode(startMode);
            basicInfoDAO.save(terminalEntity);
        } catch (Exception e) {
            LOGGER.error("设置终端[" + terminalId + "]启动模式[" + startMode + "]失败", e);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SET_START_MODE_FAIL, e, terminalEntity.getTerminalName());
        }
    }

    @Override
    public void closeTerminalConnection(String terminalId) {
        Assert.hasText(terminalId, "terminalId can not be null");
        LOGGER.info("收到关闭终端[{}]连接请求", terminalId);

        Session session = sessionManager.getSessionByAlias(terminalId);
        sessionManager.removeSession(terminalId, session);
        session.close();
    }
}
