package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbBatchAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.BusinessKey;
import com.ruijie.rcos.rcdc.terminal.module.web.request.CreateTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.DeleteTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradePackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.UploadUpgradeFileRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.validation.EnableCustomValidate;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.webmvc.api.annotation.OptLog;
import com.ruijie.rcos.sk.webmvc.api.response.DefaultWebResponse;
import com.ruijie.rcos.sk.webmvc.api.response.WebResponse.Status;

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

    @Autowired
    private CbbTerminalSystemUpgradeAPI cbbTerminalUpgradeAPI;

    /**
     * 上传iso文件
     * 
     * @param uploadRequest 上传文件请求
     * @return 上传响应返回
     * @throws BusinessException 业务异常
     */
    @RequestMapping("/package/upload")
    @OptLog(msgKey = BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_SUCCESS_LOG,
            msgArgs = {"request.userName", "request.terminalId"})
    @OptLog(msgKey = BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_PACKAGE_UPLOAD_FAIL_LOG,
            msgArgs = {"request.userName", "request.terminalId", "response.message"}, matchStatus = Status.ERROR)
    public DefaultWebResponse uploadPackage(UploadUpgradeFileRequest uploadRequest) throws BusinessException {
        Assert.notNull(uploadRequest, "uploadRequest 不能为空");

        CbbTerminalUpgradePackageUploadRequest request = new CbbTerminalUpgradePackageUploadRequest();
        request.setFile(uploadRequest.getFile());
        cbbTerminalUpgradeAPI.uploadUpgradeFile(request);
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
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("create")
    @OptLog(msgKey = BusinessKey.RCDC_TERMINAL_CREATE_SYSTEM_UPGRADE_TASK_SUCCESS_LOG,
    msgArgs = {"request.userName", "request.terminalId"})
    @OptLog(msgKey = BusinessKey.RCDC_TERMINAL_CREATE_SYSTEM_UPGRADE_TASK_FAIL_LOG,
    msgArgs = {"request.userName", "request.terminalId", "response.message"}, matchStatus = Status.ERROR)
    public DefaultWebResponse create(CreateTerminalSystemUpgradeRequest request) throws BusinessException {
        Assert.notNull(request, "CreateTerminalSystemUpgradeRequest can not be null");

        CbbBatchAddTerminalSystemUpgradeTaskRequest batchAddRequest = new CbbBatchAddTerminalSystemUpgradeTaskRequest();
        batchAddRequest.setTerminalIdArr(request.getTerminalIdArr());
        batchAddRequest.setTerminalType(request.getTerminalType());
        DefaultResponse resp = cbbTerminalUpgradeAPI.batchAddSystemUpgradeTask(batchAddRequest);

        return DefaultWebResponse.Builder.success(resp);
    }


    /**
     * 
     * 移除系统升级
     * 
     * @param request 移除系统升级请求
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("delete")
    public DefaultWebResponse delete(DeleteTerminalSystemUpgradeRequest request) throws BusinessException {
        Assert.notNull(request, "DeleteTerminalSystemUpgradeRequest can not be null");

        CbbRemoveTerminalSystemUpgradeTaskRequest removeRequest = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        removeRequest.setTerminalId(request.getTerminalId());
        DefaultResponse resp = cbbTerminalUpgradeAPI.removeTerminalSystemUpgradeTask(removeRequest);

        return DefaultWebResponse.Builder.success(resp);
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
        Assert.notNull(request, "ListTerminalSystemUpgradeRequest can not be null");

        CbbBaseListResponse<CbbTerminalSystemUpgradeTaskDTO> resp =
                cbbTerminalUpgradeAPI.listTerminalSystemUpgradeTask();
        return DefaultWebResponse.Builder.success(resp);
    }

}
