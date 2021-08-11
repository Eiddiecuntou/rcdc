package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLogoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbUploadLogoDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalLogoInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Description: 终端Logo实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月8日
 *
 * @author huangsen
 */
public class CbbTerminalLogoAPIImpl implements CbbTerminalLogoAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalLogoAPIImpl.class);

    private static final String TERMINAL_LOGO_NAME = "logo.png";

    @Autowired
    private ConfigFacade configFacade;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalLogoService terminalLogoService;

    @Override
    public void uploadLogo(CbbUploadLogoDTO request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        deleteLogo();

        TerminalLogoInfo terminalLogoInfo = saveLogoInfo(request);

        terminalLogoService.syncTerminalLogo(terminalLogoInfo, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);

    }

    @Override
    public String getLogoPath() {
        String logoInfo = globalParameterAPI.findParameter(TerminalLogoService.TERMINAL_LOGO);

        return getLogoPath(logoInfo);
    }

    @Override
    public void initLogo() throws BusinessException {
        String logoPath = deleteLogo();
        if (StringUtils.isNotEmpty(logoPath)) {
            globalParameterAPI.updateParameter(TerminalLogoService.TERMINAL_LOGO, null);
            terminalLogoService.syncTerminalLogo(new TerminalLogoInfo(), SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
        }
    }

    private TerminalLogoInfo saveLogoInfo(CbbUploadLogoDTO request) throws BusinessException {
        String saveLogoPath = saveLogo(request.getLogoPath());
        TerminalLogoInfo terminalLogoInfo = new TerminalLogoInfo();
        terminalLogoInfo.setLogoPath(saveLogoPath);
        terminalLogoInfo.setMd5(request.getLogoMD5());
        globalParameterAPI.updateParameter(TerminalLogoService.TERMINAL_LOGO, JSON.toJSONString(terminalLogoInfo));
        return terminalLogoInfo;

    }

    private String saveLogo(String logoPath) throws BusinessException {
        File logo = new File(logoPath);
        String saveLogoFile = configFacade.read("file.busiz.dir.terminal.logo");
        String saveLogoPath = saveLogoFile + TERMINAL_LOGO_NAME;
        File saveLogo = new File(saveLogoPath);
        createLogoFilePath(saveLogoFile);
        try {
            Files.move(logo.toPath(), saveLogo.toPath());
            boolean isSuccess = saveLogo.setReadable(true, false);
            LOGGER.info("操作结果：[{}]", isSuccess);
            isSuccess = saveLogo.setExecutable(true, false);
            LOGGER.info("操作结果：[{}]", isSuccess);

        } catch (IOException e) {
            LOGGER.error("从[{}] 移动文件到[{}]失败", logoPath, saveLogoPath);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPLOAD_LOGO_FAIL, e);
        }
        return saveLogoPath;
    }

    private String deleteLogo() {
        String logoInfo = globalParameterAPI.findParameter(TerminalLogoService.TERMINAL_LOGO);
        String logoPath = getLogoPath(logoInfo);
        File logo = new File(logoPath);
        if (logo.exists()) {
            SkyengineFile skyengineFile = new SkyengineFile(logoPath);
            skyengineFile.delete(false);
        }
        return logoPath;
    }

    private void createLogoFilePath(String logoPath) {
        File logo = new File(logoPath);
        if (!logo.exists()) {
            logo.mkdir();
            boolean isSuccess = logo.setReadable(true, false);
            LOGGER.info("操作结果：[{}]", isSuccess);
            isSuccess = logo.setExecutable(true, false);
            LOGGER.info("操作结果：[{}]", isSuccess);
        }
    }

    private String getLogoPath(String logoInfo) {
        if (StringUtils.isEmpty(logoInfo)) {
            LOGGER.info("没有保存自定义logo，返回空路径");
            return StringUtils.EMPTY;
        }
        TerminalLogoInfo terminalLogoInfo = JSONObject.parseObject(logoInfo, TerminalLogoInfo.class);
        return terminalLogoInfo.getLogoPath();

    }
}
