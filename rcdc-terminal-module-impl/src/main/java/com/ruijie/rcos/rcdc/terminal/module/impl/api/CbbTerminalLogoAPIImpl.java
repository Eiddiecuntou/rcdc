package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalLogoAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbInitLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbPreviewLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbUploadLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.logo.CbbPreviewLogoResponse;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.SendTerminalEventEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalLogoService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
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

    @Autowired
    private ConfigFacade configFacade;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    private TerminalLogoService terminalLogoService;

    @Override
    public DefaultResponse uploadLogo(CbbUploadLogoRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        String logoName = request.getLogoName();
        String logoPath = request.getLogoPath();
        File logo = new File(logoPath);
        String srcLogoPath = globalParameterAPI.findParameter("terminalLogo");
        if (srcLogoPath != null) {
            deleteLogo(srcLogoPath);
        }
        String saveLogoPath = moveLogo(logo, logoName);
        globalParameterAPI.updateParameter("terminalLogo", saveLogoPath);
        terminalLogoService.syncTerminalLogo(logoName, SendTerminalEventEnums.UPDATE_TERMINAL_LOGO);
        return DefaultResponse.Builder.success();
    }

    @Override
    public CbbPreviewLogoResponse previewLogo(CbbPreviewLogoRequest request) throws BusinessException {
        String logoPath = globalParameterAPI.findParameter("terminalLogo");
        CbbPreviewLogoResponse response = new CbbPreviewLogoResponse();
        response.setLogoPath(logoPath);
        return response;
    }

    @Override
    public DefaultResponse initLogo(CbbInitLogoRequest request) throws BusinessException {
        String logoPath = globalParameterAPI.findParameter("terminalLogo");
        if (logoPath != null) {
            File logo = new File(logoPath);
            String logoName = logo.getName();
            deleteLogo(logoPath);
            globalParameterAPI.updateParameter("terminalLogo", null);
            terminalLogoService.syncTerminalLogo(logoName, SendTerminalEventEnums.INIT_TERMINAL_LOGO);
        }
        return DefaultResponse.Builder.success();
    }

    private String moveLogo(File logo, String logoName) throws BusinessException {
        String saveLogoFile = configFacade.read("file.busiz.dir.terminal.logo");
        String saveLogoPath = configFacade.read("file.busiz.dir.terminal.logo") + logoName;
        File saveLogo = new File(saveLogoPath);
        createLogoFile(saveLogoFile);
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

    private void createLogoFile(String logoPath) {
        File logo = new File(logoPath);
        if (!logo.exists()) {
            logo.mkdir();
            logo.setReadable(true, false);
            logo.setExecutable(true, false);
        }

    }
}
