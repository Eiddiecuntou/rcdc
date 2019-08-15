package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLogoAPI;
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

    private static final String TERMINAL_LOGO_NAME = "logo.png";

    @Autowired
    private ConfigFacade configFacade;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalLogoService terminalLogoService;

    @Override
    public DefaultResponse uploadLogo(CbbUploadLogoRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        deleteLogo();

        saveLogo(request.getLogoPath());

        terminalLogoService.syncTerminalLogo(TERMINAL_LOGO_NAME, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);

        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbGetLogoPathResponse getLogoPath(DefaultRequest request) {
        String logoPath = globalParameterAPI.findParameter(TerminalLogoService.TERMINAL_LOGO);
        CbbGetLogoPathResponse response = new CbbGetLogoPathResponse();
        response.setLogoPath(logoPath);
        return response;
    }

    @Override
    public DefaultResponse initLogo(DefaultRequest request) throws BusinessException {
        String logoPath = deleteLogo();
        if (logoPath != null) {
            globalParameterAPI.updateParameter(TerminalLogoService.TERMINAL_LOGO, null);
            terminalLogoService.syncTerminalLogo(StringUtils.EMPTY, SendTerminalEventEnums.CHANGE_TERMINAL_LOGO);
        }
        return DefaultResponse.Builder.success();
    }

    private void saveLogo(String logoPath) throws BusinessException {
        File logo = new File(logoPath);
        String saveLogoFile = configFacade.read("file.busiz.dir.terminal.logo");
        String saveLogoPath = saveLogoFile + TERMINAL_LOGO_NAME;
        File saveLogo = new File(saveLogoPath);
        createLogoFilePath(saveLogoFile);
        try {
            Files.move(logo.toPath(), saveLogo.toPath());
            saveLogo.setReadable(true, false);
            saveLogo.setExecutable(true, false);

            globalParameterAPI.updateParameter(TerminalLogoService.TERMINAL_LOGO, saveLogoPath);
        } catch (IOException e) {
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_UPLOAD_LOGO_FAIL, e);
        }

    }

    private String deleteLogo() {
        String logoPath = globalParameterAPI.findParameter(TerminalLogoService.TERMINAL_LOGO);
        if (logoPath != null) {
            File logo = new File(logoPath);
            if (logo.exists()) {
                SkyengineFile skyengineFile = new SkyengineFile(logoPath);
                skyengineFile.delete(false);
            }
        }
        return logoPath;
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
