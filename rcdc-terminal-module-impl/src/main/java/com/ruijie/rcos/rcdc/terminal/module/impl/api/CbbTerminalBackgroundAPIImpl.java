package com.ruijie.rcos.rcdc.terminal.module.impl.api;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalBackgroundAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalBackgroundImageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalBackgroundUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.impl.model.TerminalBackgroundInfo;
import com.ruijie.rcos.rcdc.terminal.module.impl.service.TerminalBackgroundService;
import com.ruijie.rcos.rcdc.terminal.module.impl.util.FileOperateUtil;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.util.StringUtils;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultRequest;
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

    /**
     * 背景图名称，不带后缀
     */
    private static final String BACKGROUND_IMAGE_NAME = "background";

    /**
     * 背景图保存的目录
     */
    private static final String BACKGROUND_IMAGE_FTP_DIR = "/opt/ftp/terminal/background/";

    /**
     * 背景图保存的文件相对路径 background是不带后缀名的图片文件名
     */
    private static final String BACKGROUND_IMAGE_FTP_RELATIVE_DIR = "/background/";

    @Autowired
    private GlobalParameterAPI globalParameterAPI;

    @Autowired
    TerminalBackgroundService terminalBackgroundService;

    @Override
    public DefaultResponse upload(CbbTerminalBackgroundUploadRequest request) throws BusinessException {
        Assert.notNull(request, "request must not be null");

        deleteImageFile();

        File imageFile = saveBackgroundImageFile(request.getImagePath(), request.getImageName());

        TerminalBackgroundInfo terminalSyncBackgroundInfo = saveBackgroundImageConfig(imageFile,request.getMd5());

        terminalBackgroundService.syncTerminalBackground(terminalSyncBackgroundInfo);

        return DefaultResponse.Builder.success();
    }

    private TerminalBackgroundInfo saveBackgroundImageConfig(File imageFile,String md5) throws BusinessException {
        TerminalBackgroundInfo terminalBackgroundInfo = buildTerminalBackgroundInfo(imageFile,md5);
        String requestText = JSON.toJSONString(terminalBackgroundInfo);
        globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, requestText);
        return terminalBackgroundInfo;
    }

    @Override
    public DtoResponse<CbbTerminalBackgroundImageInfoDTO> getBackgroundImageInfo(DefaultRequest request) throws BusinessException {
        Assert.notNull(request, "request must not be null");

        CbbTerminalBackgroundImageInfoDTO dto = new CbbTerminalBackgroundImageInfoDTO();
        String parameter = globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
        if (StringUtils.isEmpty(parameter)) {
            return DtoResponse.empty();
        }

        TerminalBackgroundInfo terminalBackgroundInfo = JSON.parseObject(parameter, TerminalBackgroundInfo.class);
        TerminalBackgroundInfo.TerminalBackgroundDetailInfo detailInfo = terminalBackgroundInfo.getDetailInfo();
        File imageFile = new File(detailInfo.getFilePath());
        if(imageFile.exists() == false) {
            return DtoResponse.empty();
        }
        dto.setImagePath(imageFile.getAbsolutePath());
        dto.setImageName(imageFile.getName());
        return DtoResponse.success(dto);
    }

    @Override
    public DefaultResponse initBackgroundImage(DefaultRequest request) throws BusinessException {
        Assert.notNull(request, "request must not be null");
        String parameter = globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
        if (StringUtils.isEmpty(parameter)) {
            return DefaultResponse.Builder.success();
        }
        TerminalBackgroundInfo terminalBackgroundInfo = JSON.parseObject(parameter, TerminalBackgroundInfo.class);
        File imageFile = new File(terminalBackgroundInfo.getDetailInfo().getFilePath());
        if(imageFile.exists()) {
            SkyengineFile skyengineFile = new SkyengineFile(imageFile);
            skyengineFile.delete(false);
        }
        globalParameterAPI.updateParameter(TerminalBackgroundService.TERMINAL_BACKGROUND, null);
        TerminalBackgroundInfo terminalSyncBackgroundInfo = new TerminalBackgroundInfo();
        terminalSyncBackgroundInfo.setIsDefaultImage(true);
        terminalBackgroundService.syncTerminalBackground(terminalSyncBackgroundInfo);
        return DefaultResponse.Builder.success();
    }

    private TerminalBackgroundInfo buildTerminalBackgroundInfo(File imageFile,String md5) throws BusinessException {
        TerminalBackgroundInfo terminalSyncBackgroundInfo = new TerminalBackgroundInfo();
        TerminalBackgroundInfo.TerminalBackgroundDetailInfo backgroundDetailInfo = new TerminalBackgroundInfo.TerminalBackgroundDetailInfo();
        terminalSyncBackgroundInfo.setIsDefaultImage(false);
        backgroundDetailInfo.setFilePath(imageFile.getAbsolutePath());
        backgroundDetailInfo.setFtpPath(BACKGROUND_IMAGE_FTP_RELATIVE_DIR + imageFile.getName());
        backgroundDetailInfo.setMd5(md5);
        terminalSyncBackgroundInfo.setDetailInfo(backgroundDetailInfo);
        return terminalSyncBackgroundInfo;
    }

    private File saveBackgroundImageFile(String temporaryImagePath, String imageName) throws BusinessException {
        FileOperateUtil.checkAndGetDirectory(BACKGROUND_IMAGE_FTP_DIR);
        File imageFile = getBackGroundImageFile(imageName);
        try {
            File temporaryImageFile = new File(temporaryImagePath);
            Files.move(temporaryImageFile, imageFile);
        } catch (IOException e) {
            LOGGER.error("从[{}] 移动文件到[{}]失败", temporaryImagePath, imageFile.getAbsoluteFile());
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }
        return imageFile;
    }

    private void deleteImageFile() throws BusinessException {
        String parameter = globalParameterAPI.findParameter(TerminalBackgroundService.TERMINAL_BACKGROUND);
        if (parameter == null) {
            return;
        }
        TerminalBackgroundInfo terminalBackgroundInfo = JSON.parseObject(parameter, TerminalBackgroundInfo.class);
        File imageFile = new File(terminalBackgroundInfo.getDetailInfo().getFilePath());
        if (imageFile.exists() == false) {
            return;
        }
        SkyengineFile skyengineFile = new SkyengineFile(imageFile);
        skyengineFile.delete(false);
    }

    private File getBackGroundImageFile(String imageName) throws BusinessException {
        String fileNameSuffix = getFileNameSuffix(imageName);
        String saveBackgroundImagePath = BACKGROUND_IMAGE_FTP_DIR + BACKGROUND_IMAGE_NAME + fileNameSuffix;
        File file = new File(saveBackgroundImagePath);
        // 必须给文件加上读和可执行权限,让其他用户可读、可执行，否则会导致ftp账号没有权限下载:
        file.setReadable(true, false);
        file.setExecutable(true, false);
        return file;
    }

    private String getFileNameSuffix(String name) throws BusinessException {
        if (name.lastIndexOf(".") == name.length() - 1 || name.lastIndexOf(".") == -1) {
            throw new BusinessException(BusinessKey.RCDC_FILE_INVALID_SUFFIX);
        }
        return name.substring(name.lastIndexOf("."));
    }
}
