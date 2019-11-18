package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.IOException;

import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBackgroundAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackgroundUpload;
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
    public DefaultResponse upload(CbbTerminalBackgroundUpload request) throws BusinessException {
        Assert.notNull(request, "request must not be null");

        deleteImageFile();

        String path = saveBackgroundImageFile(request.getImagePath());

        saveDB(request.getMd5(),path);

        terminalBackgroundService.syncTerminalBackground(path);

        return DefaultResponse.Builder.success();
    }

    private void saveDB(String md5,String imagePath) {
        TerminalSyncBackgroundRequest request = new TerminalSyncBackgroundRequest();
        request.setMd5(md5);
        request.setImagePath(imagePath);
        String requestText = JSON.toJSONString(request);
        globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, requestText);
    }

    @Override
    public DtoResponse<CbbTerminalBackgroundImageInfoDTO> getBackgroundImageInfo(DefaultRequest request) throws BusinessException {
        Assert.notNull(request, "request must not be null");

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
    public DefaultResponse initBackgroundImage(DefaultRequest request) throws BusinessException {
        Assert.notNull(request, "request must not be null");
        if (deleteImageFile()) {
            globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
            terminalBackgroundService.syncTerminalBackground(StringUtils.EMPTY);
        }
        return DefaultResponse.Builder.success();
    }

    private String saveBackgroundImageFile(String temporaryImagePath) throws BusinessException {

        File temporaryImageFile = new File(temporaryImagePath);

        File imageFile = getBackGroundImageFile();

        createBackgroundImageDir();

        try {
            Files.move(temporaryImageFile, imageFile);
        } catch (IOException e) {
            LOGGER.error("从[{}] 移动文件到[{}]失败", temporaryImagePath, imageFile.getAbsoluteFile());
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
        return imageFile.getPath();
    }

    private boolean deleteImageFile() {

        File imageFile = getBackGroundImageFile();
        if (imageFile.exists() == false) {
            return false;
        }
        SkyengineFile skyengineFile = new SkyengineFile(imageFile);
        skyengineFile.delete(false);
        return true;
    }

    private void createBackgroundImageDir() throws BusinessException {
        String backgroundPath = configFacade.read("file.busiz.dir.terminal.background");

        File file = FileOperateUtil.checkAndGetDirectory(backgroundPath);

        setReadAndExecute(file);
    }

    private String getFileNameSuffix(String name) throws BusinessException {
        if (name.lastIndexOf(".") == name.length() - 1 || name.lastIndexOf(".") == -1) {
            throw new BusinessException(BusinessKey.RCDC_FILE_INVALID_SUFFIX);
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private File getBackGroundImageFile() {
        String saveLogoFile = configFacade.read("file.busiz.dir.terminal.background");
        String saveLogoPath = saveLogoFile + TERMINAL_BACKGROUND_NAME;
        File file = new File(saveLogoPath);
        setReadAndExecute(file);
        return file;
    }

    private void setReadAndExecute(File file) {
        file.setReadable(true, false);
        file.setExecutable(true, false);
    }

}
