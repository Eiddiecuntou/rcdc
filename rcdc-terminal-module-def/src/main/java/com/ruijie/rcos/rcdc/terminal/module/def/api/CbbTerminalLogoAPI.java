package com.ruijie.rcos.rcdc.terminal.module.def.api;

import com.ruijie.rcos.rcdc.terminal.module.def.api.request.logo.CbbUploadLogoRequest;
import com.ruijie.rcos.sk.base.exception.BusinessException;




/**
 * Description: 终端Logo操作接口
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年7月8日
 * 
 * @author huangsen
 */
public interface CbbTerminalLogoAPI {
    
    /**
     * 上传Logo
     *
     * @param request 请求参数
     * @return 上传Logo结果
     * @throws BusinessException 请求异常
     */
    
    void uploadLogo(CbbUploadLogoRequest request) throws BusinessException;

    /**
     * 预览Logo
     *
     * @return 获取Logo路径结果
     * @throws BusinessException 请求异常
     */

    String getLogoPath() throws BusinessException;

    /**
     * 初始化Logo
     *
     * @return 初始化Logo结果
     * @throws BusinessException 请求异常
     */
    
    void initLogo() throws BusinessException;
}

