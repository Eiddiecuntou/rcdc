package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLogoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbGetLogoPathRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbInitLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbUploadLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.logo.CbbGetLogoPathResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description: 终端Logo实现类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月8日
 * 
 * @author huangsen
 */
public class CbbTerminalLogoAPIImpl implements CbbTerminalLogoAPI {

    @Autowired
    private ConfigFacade configFacade;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalLogoService terminalLogoService;
    
    private static final String TERMINAL_LOGO = "terminalLogo";

    @Override
    public DefaultResponse uploadLogo(CbbUploadLogoRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        String logoName = request.getLogoName();
        String logoPath = request.getLogoPath();
        File logo = new File(logoPath);
        String srcLogoPath = globalParameterAPI.findParameter(TERMINAL_LOGO);
        if (srcLogoPath != null) {
            deleteLogo(srcLogoPath);
        }
        String saveLogoPath = moveLogo(logo, logoName);
        globalParameterAPI.updateParameter(TERMINAL_LOGO, saveLogoPath);
        terminalLogoService.syncTerminalLogo(logoName, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbGetLogoPathResponse getLogoPath(CbbGetLogoPathRequest request) throws BusinessException {
        String logoPath = globalParameterAPI.findParameter(TERMINAL_LOGO);
        CbbGetLogoPathResponse response = new CbbGetLogoPathResponse();
        response.setLogoPath(logoPath);
        return response;
    }

    @Override
    public DefaultResponse initLogo(CbbInitLogoRequest request) throws BusinessException {
        String logoPath = globalParameterAPI.findParameter(TERMINAL_LOGO);
        if (logoPath != null) {
            deleteLogo(logoPath);
            globalParameterAPI.updateParameter(TERMINAL_LOGO, null);
            terminalLogoService.syncTerminalLogo(StringUtils.EMPTY, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
        }
        return DefaultResponse.Builder.success();
    }

    private String moveLogo(File logo, String logoName) throws BusinessException {
        String saveLogoFile = configFacade.read("file.busiz.dir.terminal.logo");
        String saveLogoPath = saveLogoFile + logoName;
        File saveLogo = new File(saveLogoPath);
        createLogoFilePath(saveLogoFile);
        try {
            Files.move(logo.toPath(), saveLogo.toPath());
            saveLogo.setReadable(true, false);
            saveLogo.setExecutable(true, false);
        } catch (IOException e) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPLOAD_LOGO_FAIL, e);
        }
        return saveLogoPath;
    }

    private void deleteLogo(String logoPath) {
        Assert.notNull(logoPath, "logoPath is null");
        File logo = new File(logoPath);
        if (logo.exists()) {
            SkyengineFile skyengineFile = new SkyengineFile(logoPath);
            skyengineFile.delete(false);
        }
    }

    private void createLogoFilePath(String logoPath) {
        File logo = new File(logoPath);
        if (!logo.exists()) {
            logo.mkdir();
            logo.setReadable(true, false);
            logo.setExecutable(true, false);
        }

    }
}
