package com.ruijie.rcos.rcdc.terminal.module.web.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruijie.rcos.rcdc.terminal.module.def.api.CbbTerminalSystemUpgradeAPI;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalSystemUpgradePackageInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.TerminalSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbBatchAddTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbRemoveTerminalSystemUpgradeTaskRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalSystemUpgradePackageListRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.CbbTerminalUpgradePackageUploadRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.CbbBaseListResponse;
import com.ruijie.rcos.rcdc.terminal.module.web.request.AddTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.BatchAddTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.ListTerminalSystemUpgradePackageRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.RemoveTerminalSystemUpgradeRequest;
import com.ruijie.rcos.rcdc.terminal.module.web.request.UploadUpgradeFileRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.util.Assert;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
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
@RestController
@RequestMapping("/terminal/sysUpgrade")
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
    @RequestMapping("uploadPackage")
    public DefaultWebResponse upload(UploadUpgradeFileRequest uploadRequest) throws BusinessException {

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
    @RequestMapping("listSystemUpgradePackage")
    public DefaultWebResponse listSystemUpgradePackage(ListTerminalSystemUpgradePackageRequest listRequest)
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
     * 批量添加终端系统升级任务
     * 
     * @param request 批量添加升级请求
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("batchAddUpgradeTask")
    public DefaultWebResponse batchAddSystemUpgradeTask(BatchAddTerminalSystemUpgradeRequest request)
            throws BusinessException {
        Assert.notNull(request, "batchAddTerminalSystemUpgradeRequest can not be null");

        CbbBatchAddTerminalSystemUpgradeTaskRequest batchAddRequest = new CbbBatchAddTerminalSystemUpgradeTaskRequest();
        batchAddRequest.setTerminalIdArr(request.getTerminalIds().split(","));
        batchAddRequest.setTerminalType(request.getTerminalType());
        DefaultResponse resp = cbbTerminalUpgradeAPI.batchAddSystemUpgradeTask(batchAddRequest);

        return DefaultWebResponse.Builder.success(resp);
    }

    /**
     * 
     * 添加终端系统升级任务
     * 
     * @param request 批量添加升级请求
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("addUpgradeTask")
    public DefaultWebResponse addTerminalSystemUpgradeTask(AddTerminalSystemUpgradeRequest request)
            throws BusinessException {
        Assert.notNull(request, "addterminalSystemUpgradeRequest 不能为空");

        CbbAddTerminalSystemUpgradeTaskRequest addRequest = new CbbAddTerminalSystemUpgradeTaskRequest();
        addRequest.setTerminalId(request.getTerminalId());
        addRequest.setTerminalType(request.getTerminalType());
        DefaultResponse resp = cbbTerminalUpgradeAPI.addSystemUpgradeTask(addRequest);

        return DefaultWebResponse.Builder.success(resp);
    }

    /**
     * 
     * 移除系统升级任务
     * 
     * @param request 移除系统升级任务请求
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("removeUpgradeTask")
    public DefaultWebResponse removeTerminalSystemUpgradeTask(RemoveTerminalSystemUpgradeRequest request)
            throws BusinessException {
        Assert.notNull(request, "removeTerminalSystemUpgradeRequest 不能为空");

        CbbRemoveTerminalSystemUpgradeTaskRequest removeRequest = new CbbRemoveTerminalSystemUpgradeTaskRequest();
        removeRequest.setTerminalId(request.getTerminalId());
        DefaultResponse resp = cbbTerminalUpgradeAPI.removeTerminalSystemUpgradeTask(removeRequest);

        return DefaultWebResponse.Builder.success(resp);
    }

    /**
     * 
     * 终端升级任务列表
     * 
     * @return 请求响应
     * @throws BusinessException 业务异常
     */
    @RequestMapping("listUpgradeTask")
    public DefaultWebResponse listTerminalSystemUpgradeTask() throws BusinessException {
        CbbBaseListResponse<TerminalSystemUpgradeTaskDTO> resp = cbbTerminalUpgradeAPI.listTerminalSystemUpgradeTask();
        return DefaultWebResponse.Builder.success(resp);
    }

}
