package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBackgroundAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackGroundUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalSyncBackgroundRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.sk.base.config.ConfigFacade;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.comm.DtoResponse;
import com.ruijie.rcos.sk.modulekit.api.tool.GlobalParameterAPI;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/6
 *
 * @author songxiang
 */
public class CbbTerminalBackgroundAPIImpl implements CbbTerminalBackgroundAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbbTerminalBackgroundAPIImpl.class);

    private static final String TERMINAL_BACKGROUND_NAME = "background.png";

    @Autowired
    private ConfigFacade configFacade;

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    TerminalBackgroundService terminalBackgroundService;

    @Override
    public void upload(CbbTerminalBackGroundUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request must not be null");

        deleteImageFile();

        saveBackgroundImageFile(request.getImagePath());

        saveDB(request);

        terminalBackgroundService.syncTerminalLogo(request.getImageName());
    }

    private void saveDB(CbbTerminalBackGroundUploadRequest cbbTerminalBackGroundUploadRequest) {
        TerminalSyncBackgroundRequest request = new TerminalSyncBackgroundRequest();
        request.setMd5(cbbTerminalBackGroundUploadRequest.getMd5());
        request.setName(cbbTerminalBackGroundUploadRequest.getImageName());
        String requestText = JSON.toJSONString(request);
        globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, requestText);
    }

    @Override
    public DtoResponse<CbbTerminalBackgroundImageInfoDTO> getBackgroundImageInfo() throws BusinessException {
        CbbTerminalBackgroundImageInfoDTO dto = new CbbTerminalBackgroundImageInfoDTO();
        File imageFile = getBackGroundImageFile();
        if (imageFile.exists() == false) {
            return DtoResponse.empty();
        }
        dto.setSuffix(getFileNameSuffix(imageFile.getName()));
        dto.setImageName(Files.getNameWithoutExtension(imageFile.getName()));
        dto.setImagePath(imageFile.getAbsolutePath());

        return DtoResponse.success(dto);
    }

    @Override
    public DefaultResponse initBackgroundImage() throws BusinessException {
        if (deleteImageFile()) {
            globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
            terminalBackgroundService.syncTerminalLogo(StringUtils.EMPTY);
        }
        return DefaultResponse.Builder.success();
    }

    private void saveBackgroundImageFile(String temporaryImagePath) throws BusinessException {

        File temporaryImageFile = new File(temporaryImagePath);

        File imageFile = getBackGroundImageFile();

        createBackgroundImageDir();

        try {
            Files.move(temporaryImageFile, imageFile);
        } catch (IOException e) {
            LOGGER.error("从[{}] 移动文件到[{}]失败", temporaryImagePath, imageFile.getAbsoluteFile());
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
    }

    private boolean deleteImageFile() {

        File imageFile = getBackGroundImageFile();
        if (!imageFile.exists()) {
            return false;
        }
        SkyengineFile skyengineFile = new SkyengineFile(imageFile);
        skyengineFile.delete(false);
        return true;
    }

    private void createBackgroundImageDir() throws BusinessException {
        String backgroundPath = configFacade.read("file.busiz.dir.terminal.background");

        File file = new File(backgroundPath);
        if (file.isFile()) {
            LOGGER.error("创建终端背景的文件夹失败,已经存在同名的文件,文件路径:[{}]", backgroundPath);
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL);
        }
        if (file.exists() == false) {
            if (file.mkdir() == false) {
                LOGGER.error("创建终端背景的文件夹失败,文件路径:[{}]", backgroundPath);
                throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL);
            }
        }
        setFilePrivilege(file);
    }

    private String getFileNameSuffix(String name) throws BusinessException {
        if (name.lastIndexOf(".") == name.length() - 1 || name.lastIndexOf(".") == -1) {
            throw new BusinessException("");
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private File getBackGroundImageFile() {
        String saveLogoFile = configFacade.read("file.busiz.dir.terminal.background");
        String saveLogoPath = saveLogoFile + TERMINAL_BACKGROUND_NAME;
        File file = new File(saveLogoPath);
        setFilePrivilege(file);
        return file;
    }

    private void setFilePrivilege(File file) {
        file.setReadable(true, false);
        file.setExecutable(true, false);
    }

}
