package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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
    public static void emptyDirectory(final String directoryPath, final String exceptFileName) throws BusinessException {

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
    public static void emptyDirectory(final String directoryPath) throws BusinessException {

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
    public static void moveFile(final String fileName, final String filePath, final String destPath) throws BusinessException {
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


    private static void deleteFilesInDirectory(final String directoryPath, final String exceptFileName) throws BusinessException {
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

            if (subFile.isFile()) {
                LOGGER.debug("delete file[{}]", fileName);
                SkyengineFile skyengineFile = new SkyengineFile(subFile);
                skyengineFile.delete(false);
            }
        }
    }

    /**
     * 复制单个文件
     * 
     * @param sourcePath 要复制的文件名
     * @param destPath 目标文件名
     */
    public static void copyfile(String sourcePath, String destPath) {
        Assert.notNull(sourcePath, "sourcePath can not be null");
        Assert.notNull(destPath, "destPath can not be null");

        int hasRead = 0;
        File oldFile = new File(sourcePath);
        if (oldFile.exists()) {
            try (FileInputStream fis = new FileInputStream(oldFile); FileOutputStream fos = new FileOutputStream(destPath);) {
                byte[] bufferArr = new byte[1024];
                // 当文件没有读到结尾
                while ((hasRead = fis.read(bufferArr)) != -1) {
                    fos.write(bufferArr, 0, hasRead);// 写文件
                }
                fis.close();
            } catch (Exception e) {
                LOGGER.error("复制单个文件操作出错！", e);
            }
        }
    }

    /**
     *
     * @param sourcePath 要复制的文件夹路径
     * @param destPath 目标文件夹路径
     * @throws BusinessException 业务异常
     */
    public static void directoryCopy(String sourcePath, String destPath) throws BusinessException {
        Assert.notNull(sourcePath, "sourcePath can not be null");
        Assert.notNull(destPath, "destPath can not be null");

        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        LOGGER.info("sourcePath : {}, destPath :{}", sourcePath, destPath);
        if (!sourceFile.isDirectory()) {
            LOGGER.info("sourcePath exist : {}", sourceFile.exists());
            LOGGER.info("sourcePath is File : {}", sourceFile.isFile());
            throw new BusinessException(BusinessKey.RCDC_FILE_NOT_EXIST);
        }
        if (!destFile.isDirectory()) {
            destFile.mkdir();
        }

        // listFiles能够获取当前文件夹下的所有文件和文件夹
        File[] fileArr = sourceFile.listFiles();
        for (int i = 0; i < fileArr.length; i++) {
            if (fileArr[i].isDirectory()) {
                File dirNew = new File(destPath + File.separator + fileArr[i].getName());
                dirNew.mkdir();// 在目标文件夹中创建文件夹
                // 递归
                directoryCopy(sourcePath + File.separator + fileArr[i].getName(), destPath + File.separator + fileArr[i].getName());
            } else {
                String filePath = destPath + File.separator + fileArr[i].getName();
                copyfile(fileArr[i].getAbsolutePath(), filePath);
            }

        }
    }

    /**
     * 先根遍历序递归删除文件夹
     *
     * @param deleteFile 要被删除的文件或者目录
     * @return 删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(File deleteFile) {
        Assert.notNull(deleteFile, "deleteFile can not be null");

        // 如果dir对应的文件不存在，则退出
        if (!deleteFile.exists()) {
            return false;
        }

        if (deleteFile.isFile()) {
            SkyengineFile skyengineFile = new SkyengineFile(deleteFile);
            skyengineFile.delete(false);
            return skyengineFile.delete();
        } else {
            for (File file : deleteFile.listFiles()) {
                deleteFile(file);
            }
        }

        return new SkyengineFile(deleteFile).delete(false);
    }

    /**
     * 根据路径删除文件，只能删除文件不能删除目录
     * @param filePath 文件路径
     */
    public static void deleteFileByPath(String filePath) {
        Assert.hasText(filePath, "filePath can not be null");
        File file = new File(filePath);
        if (file.isDirectory()) {
            throw new RuntimeException("无法直接删除目录");
        }
        if (file.exists()) {
            new SkyengineFile(file).delete(false);
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

    /**
     * 获取指定目录下文件
     * 
     * @param filePath 文件目录
     * @return 返回所有文件list
     */
    public static List<File> listFile(String filePath) {
        Assert.notNull(filePath, "filePath can not be null");
        File file = new File(filePath);
        if (!file.exists()) {
            LOGGER.error("filePath is not exist");
            throw new IllegalArgumentException("filePath:{}" + filePath);
        }
        List<File> fileList = new ArrayList<File>();
        if (file.isFile()) {
            LOGGER.error("filePath is not directory");
            throw new IllegalArgumentException("filePath:{}" + filePath);
        }
        File[] fileArr = file.listFiles();
        if (fileArr != null) {
            for (int i = 0; i < fileArr.length; i++) {
                if (fileArr[i].isFile()) {
                    fileList.add(fileArr[i]);
                }
            }
        }
        return fileList;

    }


}
