package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import java.io.File;
import java.io.FilenameFilter;
import org.springframework.util.Assert;
import com.google.common.io.PatternFilenameFilter;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * 
 * Description: 文件操作工具类
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月30日
 * 
 * @author nt
 */
public class FileOperateUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOperateUtil.class);

    /**
     * 
     * 删除目录下除了指定文件外的其他文件
     * 
     * @param directoryPath 文件夹目录
     * @param exceptFileName 不删除的文件名
     * @throws BusinessException 业务异常
     */
    public static void deleteFile(final String directoryPath, final String exceptFileName) throws BusinessException {

        Assert.hasLength(directoryPath, "directoryPath 不能为空");
        Assert.hasLength(exceptFileName, "exceptFileName 不能为空");

        deleteFilesInDirectory(directoryPath, exceptFileName);
    }

    /**
     * 
     * 删除目录下所有文件
     * 
     * @param directoryPath 文件夹目录
     * @throws BusinessException 业务异常
     */
    public static void deleteFile(final String directoryPath) throws BusinessException {

        Assert.hasLength(directoryPath, "directoryPath 不能为空");

        deleteFilesInDirectory(directoryPath, null);
    }


    /**
     * 
     * 移动文件到制定目录
     * 
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param destPath 文件路径
     * @throws BusinessException 业务异常
     */
    public static void moveFile(final String fileName, final String filePath, final String destPath)
            throws BusinessException {
        Assert.hasLength(fileName, "fileName 不能为空");
        Assert.hasLength(filePath, "filePath 不能为空");
        Assert.hasLength(destPath, "destPath 不能为空");
        
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST);
        }

        // 判断目录是否存在，不存在则创建
        checkAndGetDirectory(destPath);

        boolean isSuccess = false;
        try {
            isSuccess = file.renameTo(new File(destPath + fileName));
        } catch (Exception e) {
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
        }

        if (!isSuccess) {
            throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL);
        }
    }


    private static void deleteFilesInDirectory(final String directoryPath, final String exceptFileName)
            throws BusinessException {
        File packageDir = checkAndGetDirectory(directoryPath);

        File[] childrenArr = packageDir.listFiles();
        if (childrenArr == null || childrenArr.length == 0) {
            LOGGER.debug("path [{}] no sub file", directoryPath);
            return;
        }
        
        for (File subFile : childrenArr) {
            // 排除外的文件不删除
            String fileName = subFile.getName();
            if (fileName.equals(exceptFileName)) {
                LOGGER.debug("skip except file[{}]", exceptFileName);
                continue;
            }

            boolean isDelete = false;
            if (subFile.isFile()) {
                LOGGER.debug("delete file[{}]", fileName);
                SkyengineFile skFile = new SkyengineFile(subFile);
                isDelete = skFile.delete();
            }
            if (!isDelete) {
                throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL);
            }
        }
    }
    
    private static File checkAndGetDirectory(final String directoryPath) throws BusinessException {
        File packageDir = new File(directoryPath);
        if (!packageDir.exists() || !packageDir.isDirectory()) {
            try {
                packageDir.mkdir();
            } catch (Exception e) {
                throw new BusinessException(BusinessKey.RCDC_FILE_OPERATE_FAIL, e);
            }
        }
        return packageDir;
    }


}
