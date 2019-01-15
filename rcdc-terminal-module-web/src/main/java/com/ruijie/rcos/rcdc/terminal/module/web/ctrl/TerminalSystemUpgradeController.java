package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.request.CreateTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.DeleteTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradePackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradeRequest;
import com.ruijie.rcos.sk.base.batch.BatchTaskBuilder;
import com.ruijie.rcos.sk.base.batch.BatchTaskFinishResult;
import com.ruijie.rcos.sk.base.batch.BatchTaskHandler;
import com.ruijie.rcos.sk.base.batch.BatchTaskItem;
import com.ruijie.rcos.sk.base.batch.BatchTaskItemResult;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.webmvc.api.optlog.ProgrammaticOptLogRecorder;
import com.ruijie.rcos.sk.webmvc.api.request.ChunkUploadFile;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;

/**
 * 
 * Description: 终端系统升级
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年11月19日
 * 
 * @author nt
 */
@Controller
@RequestMapping("/terminal/system/upgrade")
@EnableCustomValidate(enable = false)
public class TerminalSystemUpgradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalSystemUpgradeController.class);

    @Autowired
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    /**
     * 上传系统升级文件
     * 
     * @param uploadRequest 上传文件请求
     * @param optLogRecorder 日志记录
     * @return 上传响应返回
     * @throws BusinessException 业务异常
     */
    @RequestMapping("/package/upload")
    public DefaultWebResponse uploadPackage(ChunkUploadFile file, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        Assert.notNull(file, "file 不能为空");

        CbbTerminalUpgradePackageUploadRequest request =
                new CbbTerminalUpgradePackageUploadRequest(file.getFilePath(), file.getFileName(), file.getFileMD5());
        try {
            cbbTerminalUpgradeAPI.uploadUpgradeFile(request);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG,
                    file.getFileName());
        } catch (Exception e) {
            // TODO 精简一下
            if (e instanceof BusinessException) {
                BusinessException ex = (BusinessException) e;
                // 上传文件处理失败
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG,
                        file.getFileName(), ex.getI18nMessage());
            } else {
                throw e;
            }
        }
        return DefaultWebResponse.Builder.success();
    }

    /**
     * 
     * 系统终端升级包信息列表
     * 
     * @param listRequest 分页请求信息
     * @return 分页列表信息
     * @throws BusinessException 业务异常
     */
    @RequestMapping("/package/list")
    public DefaultWebResponse listPackage(ListTerminalSystemUpgradePackageRequest listRequest)
            throws BusinessException {
        Assert.notNull(listRequest, "ListTerminalSystemUpgradePackageRequest can not be null");

        CbbTerminalSystemUpgradePackageListRequest request = new CbbTerminalSystemUpgradePackageListRequest();
        request.setTerminalType(listRequest.getTerminalType());
        CbbBaseListResponse<CbbTerminalSystemUpgradePackageInfoDTO> resp =
                cbbTerminalUpgradeAPI.listSystemUpgradePackage(request);
        return DefaultWebResponse.Builder.success(resp);
    }

    /**
     * 
     * 添加终端系统升级任务
     * 
     * @param request 添加升级请求
     * @param optLogRecorder 日志记录
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("create")
    public DefaultWebResponse create(CreateTerminalSystemUpgradeRequest request,
            ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        Assert.notNull(request, "CreateTerminalSystemUpgradeRequest can not be null");

        
        // TODO 批处理框架
        CbbTerminalTypeEnums terminalType = request.getTerminalType();
        for (String terminalId : request.getTerminalIdArr()) {
            addUpgradeTaskAddOptLog(terminalId, terminalType, optLogRecorder);
        }

        return DefaultWebResponse.Builder.success();
    }


    private void addUpgradeTaskAddOptLog(String terminalId, CbbTerminalTypeEnums terminalType,
            ProgrammaticOptLogRecorder optLogRecorder) throws BusinessException {
        CbbAddTerminalSystemUpgradeTaskRequest addRequest = new CbbAddTerminalSystemUpgradeTaskRequest();
        addRequest.setTerminalId(terminalId);
        addRequest.setTerminalType(terminalType);
        try {
            cbbTerminalUpgradeAPI.addSystemUpgradeTask(addRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CREATE_SYSTEM_UPGRADE_TASK_SUCCESS_LOG, terminalId);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException ex = (BusinessException) e;
                // 添加升级失败
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_CREATE_SYSTEM_UPGRADE_TASK_FAIL_LOG, terminalId,
                        ex.getI18nMessage());
            } else {
                throw e;
            }
        }

    }

    /**
     * 
     * 移除系统升级
     * 
     * @param request 移除系统升级请求
     * @param optLogRecorder 日志记录
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("delete")
    public DefaultWebResponse delete(DeleteTerminalSystemUpgradeRequest request,
            ProgrammaticOptLogRecorder optLogRecorder, BatchTaskBuilder builder) throws BusinessException {
        Assert.notNull(request, "request can not be null");
        Assert.notNull(optLogRecorder, "optLogRecorder can not be null");
        Assert.notNull(builder, "builder can not be null");

        LOGGER.warn("start remove system upgrade task...");
        final String[] idArr = request.getIdArr();
//        builder.setTaskName("批量删除系统升级任务")
//               .setTaskDesc("批量删除系统升级任务")
//               .enableParallel()
//               .registerHandler(new BatchTaskHandler<BatchTaskItem>() {
//                   final Iterator<DefaultBatchTaskItem> iterator = Stream.of(idArr)
//                           .map(id -> DefaultBatchTaskItem.builder().itemId(id).itemName("删除云桌面").build()).iterator();
//
//                @Override
//                public boolean hasNext() {
//                    // TODO Auto-generated method stub
//                    return false;
//                }
//
//                @Override
//                public BatchTaskItem next() {
//                    // TODO Auto-generated method stub
//                    return null;
//                }
//
//                @Override
//                public void afterException(BatchTaskItem arg0, Exception arg1) {
//                    // TODO Auto-generated method stub
//                    
//                }
//
//                @Override
//                public BatchTaskFinishResult onFinish(int arg0, int arg1) {
//                    // TODO Auto-generated method stub
//                    return null;
//                }
//
//                @Override
//                public BatchTaskItemResult processItem(BatchTaskItem arg0) throws BusinessException {
//                    // TODO Auto-generated method stub
//                    return null;
//                }
//                   
//               }).start();
        for (String terminalId : request.getIdArr()) {
            deleteAddOptLog(terminalId, optLogRecorder);
        }
        LOGGER.warn("finish remove system upgrade task");
        return DefaultWebResponse.Builder.success();
    }

    private void deleteAddOptLog(String terminalId, ProgrammaticOptLogRecorder optLogRecorder)
            throws BusinessException {
        CbbRemoveTerminalSystemUpgradeTaskRequest removeRequest = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        removeRequest.setTerminalId(terminalId);
        try {
            cbbTerminalUpgradeAPI.removeTerminalSystemUpgradeTask(removeRequest);
            optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_SYSTEM_UPGRADE_SUCCESS_LOG, terminalId);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException ex = (BusinessException) e;
                // 批量删除升级失败
                optLogRecorder.saveOptLog(BusinessKey.RCDC_TERMINAL_DELETE_SYSTEM_UPGRADE_FAIL_LOG, terminalId,
                        ex.getI18nMessage());
            } else {
                throw e;
            }
        }
    }

    /**
     * 
     * 终端升级任务列表
     * 
     * @param request 请求参数
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("list")
    public DefaultWebResponse list(ListTerminalSystemUpgradeRequest request) throws BusinessException {
        Assert.notNull(request, "request can not be null");

        CbbBaseListResponse<CbbTerminalSystemUpgradeTaskDTO> resp =
                cbbTerminalUpgradeAPI.listTerminalSystemUpgradeTask();
        return DefaultWebResponse.Builder.success(resp);
    }

}
