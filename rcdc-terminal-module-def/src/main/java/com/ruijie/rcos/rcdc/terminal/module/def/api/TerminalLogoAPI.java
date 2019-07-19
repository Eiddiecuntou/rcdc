package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.InitLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.PreviewLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.UploadLogoRequest;
import com.ruijie.rcos.rcdc.terminal.module.def.api.response.logo.PreviewLogoResponse;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.modulekit.api.comm.DefaultResponse;
import com.ruijie.rcos.sk.modulekit.api.tx.NoRollback;



/**
 * Description:
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/7/8
 *
 * @author hs
 */
public interface TerminalLogoAPI {

    /**
     * 上传Logo
     *
     * @param request 请求参数
     * @return 上传Logo结果
     * @throws BusinessException 请求异常
     */
    @NoRollback
    DefaultResponse uploadLogo(UploadLogoRequest request) throws BusinessException;

    /**
     * 预览Logo
     *
     * @param request 请求参数
     * @return 预览Logo结果
     * @throws BusinessException 请求异常
     */
    @NoRollback
    PreviewLogoResponse previewLogo(PreviewLogoRequest request) throws BusinessException;

    /**
     * 初始化Logo
     *
     * @param request 请求参数
     * @return 初始化Logo结果
     * @throws BusinessException 请求异常
     */
    @NoRollback
    DefaultResponse initLogo(InitLogoRequest request) throws BusinessException;
}

