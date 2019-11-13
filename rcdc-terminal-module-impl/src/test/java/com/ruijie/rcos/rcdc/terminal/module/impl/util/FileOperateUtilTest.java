package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFile;
import com.ruijie.rcos.sk.base.filesystem.SkyengineFileSystemEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;
import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.test.ThrowExceptionTester;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月1日
 * 
 * @author ls
 */
public class FileOperateUtilTest {

    @Mocked
    private Logger logger;

    /**
     * 初始化
     * 
     * @param <T> 泛型
     */
    @Before
    public <T> void before() {
        new MockUp<LoggerFactory>() {
            @Mock
            public Logger getLogger(Class<T> clazz) {
                //
                return logger;
            }
        };
    }


    /**
     * 测试emptyDirectory，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testEmptyDirectoryArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.emptyDirectory("", "dsd"), "directoryPath 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.emptyDirectory("sdsd", ""), "exceptFileName 不能为空");
        assertTrue(true);
    }

    /**
     * 测试emptyDirectory，directoryPath不存在并且有异常
     */
    @Test
    public void testEmptyDirectoryDirectoryPathNotExistAndHasException() {
        String directoryPath = "directoryPath";
        String exceptFileName = "exceptFileName";

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }

            @Mock
            public boolean mkdir() {
                throw new IllegalArgumentException();
            }
        };
        try {
            FileOperateUtil.emptyDirectory(directoryPath, exceptFileName);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }
    }

    /**
     * 测试emptyDirectory，directoryPath不是目录并且有异常
     */
    @Test
    public void testEmptyDirectoryDirectoryPathIsNotDirectoryAndHasException() {
        String directoryPath = "directoryPath";
        String exceptFileName = "exceptFileName";

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return false;
            }

            @Mock
            public boolean mkdir() throws Exception {
                throw new IllegalArgumentException();
            }
        };
        try {
            FileOperateUtil.emptyDirectory(directoryPath, exceptFileName);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }
    }

    /**
     * 测试emptyDirectory，childrenArr为null
     */
    @Test
    public void testEmptyDirectoryChildrenArrIsNull() {
        String directoryPath = "directoryPath";
        String exceptFileName = "exceptFileName";

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }

            @Mock
            public boolean isDirectory() {
                return false;
            }

            @Mock
            public boolean mkdir() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                // mock返回值
                return null;
            }
        };
        try {
            FileOperateUtil.emptyDirectory(directoryPath, exceptFileName);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试emptyDirectory，childrenArr长度为0
     */
    @Test
    public void testEmptyDirectoryChildrenArrLengthIsZero() {
        String directoryPath = "directoryPath";
        String exceptFileName = "exceptFileName";

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                return new File[0];
            }
        };
        try {
            FileOperateUtil.emptyDirectory(directoryPath, exceptFileName);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试emptyDirectory，子文件为排除外的文件
     */
    @Test
    public void testEmptyDirectoryIsExceptFile() {
        String directoryPath = "directoryPath";
        String exceptFileName = "exceptFileName";

        File subFile = new File(exceptFileName);
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[1];
                fileArr[0] = subFile;
                return fileArr;
            }
        };
        try {
            FileOperateUtil.emptyDirectory(directoryPath, exceptFileName);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试emptyDirectory，子文件是目录
     */
    @Test
    public void testEmptyDirectoryNotExceptFileAndIsNotFile() {
        String directoryPath = "directoryPath";
        String exceptFileName = "exceptFileName";

        File subFile = new File("sdsd");
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[1];
                fileArr[0] = subFile;
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return false;
            }
        };
        try {
            FileOperateUtil.emptyDirectory(directoryPath, exceptFileName);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试emptyDirectory，
     */
    @Test
    public void testEmptyDirectoryNotExceptFile() {
        String directoryPath = "directoryPath";
        String exceptFileName = "exceptFileName";

        File subFile = new File("E:\\data\\deleteFile");

        new MockUp<SkyengineFileSystemEnvironment>(){

            @Mock
            public List<String> getAllowOperatorFolder() {
                return Lists.newArrayList("E:\\data\\deleteFile");
            }
        };

        new MockUp<SkyengineFile>(){

            @Mock
            public boolean delete(boolean isMoveToRecy) {
                return true;
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[1];
                fileArr[0] = subFile;
                return fileArr;
            }

            @Mock
            public boolean isFile() {
                return true;
            }
        };

        try {
            FileOperateUtil.emptyDirectory(directoryPath, exceptFileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 测试emptyDirectory0，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testEmptyDirectory0ArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.emptyDirectory(""), "directoryPath 不能为空");
        assertTrue(true);
    }


    /**
     * 测试emptyDirectory0，
     */
    @Test
    public void testEmptyDirectory0() {
        String directoryPath = "directoryPath";
        new MockUp<FileOperateUtil>() {
            @Mock
            public void deleteFilesInDirectory(final String directoryPath, final String exceptFileName) {

            }
        };

        try {
            FileOperateUtil.emptyDirectory(directoryPath);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试moveFile，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testMoveFileArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.moveFile("", "sss", "sss"), "fileName 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.moveFile("sss", "", "sss"), "filePath 不能为空");
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.moveFile("sss", "sss", ""), "destPath 不能为空");
        assertTrue(true);
    }

    /**
     * 测试moveFile，文件不存在
     * 
     * @throws Exception 异常
     */
    @Test
    public void testMoveFileRcdcFileNotExist() {
        String fileName = "fileName";
        String filePath = "filePath";
        String destPath = "destPath";
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }
        };

        try {
            FileOperateUtil.moveFile(fileName, filePath, destPath);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试moveFile，重命名失败
     * 
     * @throws Exception 异常
     */
    @Test
    public void testMoveFileRenameFail() {
        String fileName = "fileName";
        String filePath = "filePath";
        String destPath = "destPath";
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public boolean renameTo(File dest) {
                return false;
            }
        };

        try {
            FileOperateUtil.moveFile(fileName, filePath, destPath);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }
    }

    /**
     * 测试moveFile，重命名出现异常
     * 
     * @throws Exception 异常
     */
    @Test
    public void testMoveFileRenameHasException() {
        String fileName = "fileName";
        String filePath = "filePath";
        String destPath = "destPath";
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public boolean renameTo(File dest) {
                throw new IllegalArgumentException();
            }
        };

        try {
            FileOperateUtil.moveFile(fileName, filePath, destPath);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_OPERATE_FAIL, e.getKey());
        }
    }

    /**
     * 测试moveFile，
     * 
     * @throws Exception 异常
     */
    @Test
    public void testMoveFileSuccess() {
        String fileName = "fileName";
        String filePath = "filePath";
        String destPath = "destPath";
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isDirectory() {
                return true;
            }

            @Mock
            public boolean renameTo(File dest) {
                return true;
            }
        };

        try {
            FileOperateUtil.moveFile(fileName, filePath, destPath);
        } catch (BusinessException e) {
            fail();
        }
    }

    /**
     * 测试copyfile，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCopyfileArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.copyfile(null, "sss"), "sourcePath can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.copyfile("sss", null), "destPath can not be null");
        assertTrue(true);
    }

    /**
     * 测试copyfile，旧文件不存在
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCopyfileOldFileNotExist() {
        String sourcePath = "sourcePath";
        String destPath = "destPath";

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }
        };
        try {
            FileOperateUtil.copyfile(sourcePath, destPath);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试copyfile，出现异常
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCopyfileHasException() {
        String sourcePath = "sourcePath";
        String destPath = "destPath";

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };
        try {
            FileOperateUtil.copyfile(sourcePath, destPath);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试copyfile
     * 
     * @throws Exception 异常
     */
    @Test
    public void testCopyfile() {
        String path = FileOperateUtilTest.class.getResource("/").getPath();
        String sourcePath = path + "testCopyFileSourcePath";
        String destPath = path + "testCopyFileDestPath";

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }
        };
        try {
            FileOperateUtil.copyfile(sourcePath, destPath);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * 测试directoryCopy，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDirectoryCopyArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.directoryCopy(null, "sss"), "sourcePath can not be null");
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.directoryCopy("sss", null), "destPath can not be null");
        assertTrue(true);
    }

    /**
     * 测试directoryCopy，sourceFile不是目录
     */
    @Test
    public void testDirectoryCopySourceFileIsNotDirectory() {
        String sourcePath = "sourcePath";
        String destPath = "destPath";
        new MockUp<File>() {
            @Mock
            public boolean isDirectory() {
                return false;
            }
        };
        try {
            FileOperateUtil.directoryCopy(sourcePath, destPath);
            fail();
        } catch (BusinessException e) {
            assertEquals(BusinessKey.RCDC_FILE_NOT_EXIST, e.getKey());
        }
    }

    /**
     * 测试directoryCopy，sourceFile的子文件不是目录
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDirectoryCopySourceFileSubFileIsNotDirectory() throws BusinessException {
        String sourcePath = "sourcePath";
        String destPath = "destPath";

        File subFile = new File("1");
        new MockUp<File>() {
            @Mock
            public boolean isDirectory(Invocation invocation) {
                Assert.notNull(invocation, "invocation can not be null");
                File file = invocation.getInvokedInstance();
                if (sourcePath.equals(file.getPath())) {
                    return true;
                }
                return false;
            }

            @Mock
            public boolean mkdir() {
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[1];
                fileArr[0] = subFile;
                return fileArr;
            }
        };

        new MockUp<FileOperateUtil>() {
            @Mock
            public void copyfile(String sourcePath, String destPath) {

            }
        };
        FileOperateUtil.directoryCopy(sourcePath, destPath);

        new Verifications() {
            {
                FileOperateUtil.copyfile(anyString, anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试directoryCopy，sourceFile的子文件是目录
     * 
     * @throws BusinessException 业务异常
     */
    @Test
    public void testDirectoryCopySourceFileSubFileIsDirectory() throws BusinessException {
        String sourcePath = "sourcePath";
        String destPath = "destPath";
        String subPath = "subPath";

        File subFile = new File(subPath);
        new MockUp<File>() {
            @Mock
            public boolean isDirectory(Invocation invocation) {
                Assert.notNull(invocation, "invocation can not be null");
                File file = invocation.getInvokedInstance();
                if (sourcePath.equals(file.getPath())) {
                    return true;
                }
                if (destPath.equals(file.getPath())) {
                    return true;
                }
                if (subPath.equals(file.getPath())) {
                    return true;
                }
                String path = sourcePath + File.separator + file.getName();
                if (path.equals(file.getPath())) {
                    return true;
                }
                return false;
            }

            @Mock
            public boolean mkdir() {
                return true;
            }

            @Mock
            public File[] listFiles(Invocation invocation) {
                Assert.notNull(invocation, "invocation can not be null");
                File file = invocation.getInvokedInstance();
                File[] fileArr = new File[1];
                fileArr[0] = subFile;
                if (sourcePath.equals(file.getPath())) {
                    return fileArr;
                }
                return new File[0];
            }
        };

        new MockUp<FileOperateUtil>() {
            @Mock
            public void copyfile(String sourcePath, String destPath) {

            }
        };
        FileOperateUtil.directoryCopy(sourcePath, destPath);

        new Verifications() {
            {
                FileOperateUtil.copyfile(anyString, anyString);
                times = 1;
            }
        };
    }

    /**
     * 测试deleteFile，参数为空
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDeleteFileArgumentIsNull() throws Exception {
        ThrowExceptionTester.throwIllegalArgumentException(() -> FileOperateUtil.deleteFile(null), "deleteFile can not be null");
        assertTrue(true);
    }

    /**
     * 测试deleteFile，删除的文件不存在
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDeleteFileNotExist() throws Exception {
        File deleteFile = new File("deleteFile");
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return false;
            }
        };
        assertFalse(FileOperateUtil.deleteFile(deleteFile));
    }

    /**
     * 测试deleteFile，删除的是文件
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDeleteFileIsFile() throws Exception {
        File deleteFile = new File("E:\\data\\deleteFile");

        new MockUp<SkyengineFileSystemEnvironment>(){

            @Mock
            public List<String> getAllowOperatorFolder() {
                return Lists.newArrayList("E:\\data\\deleteFile");
            }
        };

        new MockUp<SkyengineFile>(){

            @Mock
            public boolean delete(boolean isMoveToRecy) {
                return true;
            }
        };

        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isFile() {
                return true;
            }

        };
        assertTrue(FileOperateUtil.deleteFile(deleteFile));
    }

    /**
     * 测试deleteFile，删除的是目录
     * 
     * @throws Exception 异常
     */
    @Test
    public void testDeleteFileIsDirectory() throws Exception {
        File deleteFile = new File("E:\\data\\deleteFile");

        File subFile = new File("1");
        new MockUp<File>() {
            @Mock
            public boolean exists() {
                return true;
            }

            @Mock
            public boolean isFile(Invocation invocation) {
                Assert.notNull(invocation, "invocation can not be null");
                File file = invocation.getInvokedInstance();
                if ("deleteFile".equals(file.getPath())) {
                    return false;
                }
                return true;
            }

            @Mock
            public File[] listFiles() {
                File[] fileArr = new File[1];
                fileArr[0] = subFile;
                return fileArr;
            }
        };

        new MockUp<SkyengineFileSystemEnvironment>(){

            @Mock
            public List<String> getAllowOperatorFolder() {
                return Lists.newArrayList("E:\\data\\deleteFile");
            }
        };

        new MockUp<SkyengineFile>(){

            @Mock
            public boolean delete(boolean isMoveToRecy) {
                return true;
            }
        };
        assertTrue(FileOperateUtil.deleteFile(deleteFile));
    }


}
