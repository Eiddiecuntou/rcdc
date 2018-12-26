package com.ruijie.rcos.rcdc.terminal.module.impl.init;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentUpdateListDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalComponentVersionInfoDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.Constants;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.ComponentUpdateListCacheManager;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import com.ruijie.rcos.sk.modulekit.api.bootstrap.SafetySingletonInitializer;

/**
 * 
 * Description: 终端系统升级任务初始化
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2018年12月15日
 * 
 * @author nt
 */
public class TerminalComponentUpgradeCacheInit implements SafetySingletonInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalComponentUpgradeCacheInit.class);

    @Autowired
    private ComponentUpdateListCacheManager cacheManager;

    @Override
    public void safeInit() {
        // TODO FIXME 读取服务器上组件升级目录下的updatelist,并将其存入缓存中
        File file = new File(Constants.TERMINAL_COMPONENT_UPGRADE_PACKAGE_PATH);
        if (!file.exists() || !file.isDirectory()) {
            LOGGER.error("terminal component upgrade dictory is not exist, the path is {}",
                    Constants.TERMINAL_COMPONENT_UPGRADE_PACKAGE_PATH);
            return;
        }

        File[] subfiles = file.listFiles();
        for (File subFile : subfiles) {
            if (subFile.isFile()) {
                LOGGER.warn("sub file is not dictory, the file path is {}", subFile.getAbsolutePath());
                continue;
            }
            File updateListFile = new File(
                    subFile.getAbsolutePath() + File.pathSeparator + Constants.TERMINAL_COMPONET_UPDATE_LIST_FILE_NAME);
            if (!updateListFile.exists()) {
                continue;
            }
            try {
                String updatelistStr = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
                CbbTerminalComponentUpdateListDTO updatelist =
                        JSON.parseObject(updatelistStr, CbbTerminalComponentUpdateListDTO.class);
                putInCache(updatelist);
            } catch (IOException e) {
                LOGGER.error("read updatelist file error", e);
            }
        }

    }

    private void putInCache(CbbTerminalComponentUpdateListDTO updatelist) {
        if(updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.error("updatelist is null, upgrade file is incorrect");
            return;
        }
        if(cacheManager.getCache(CbbTerminalTypeEnums.ALL) == null) {
            cacheManager.addCache(CbbTerminalTypeEnums.ALL, updatelist);
        }
        // 将终端组件升级updatelist信息按终端类型进行拆分，存入缓存中
        Map<CbbTerminalTypeEnums, CbbTerminalComponentUpdateListDTO> caches = cacheManager.getUpdateListCaches();
        updatelist.getComponentList().forEach(component -> {
            String[] terminalTypes = component.getTerminalType().split("&");
            for(String terminalTypeStr : terminalTypes) {
                if(StringUtils.isBlank(terminalTypeStr) || !CbbTerminalTypeEnums.contains(terminalTypeStr)) {
                    LOGGER.debug("updatelist contains invalid terminal type[{}]", terminalTypeStr);
                    continue;
                }
                CbbTerminalTypeEnums type = CbbTerminalTypeEnums.valueOf(terminalTypeStr.toUpperCase());
                CbbTerminalComponentUpdateListDTO typeUpdatelist = null;
                List<CbbTerminalComponentVersionInfoDTO> componentList = null;
                if(caches.containsKey(type)) {
                    typeUpdatelist = caches.get(type);
                    componentList = typeUpdatelist.getComponentList();
                }else {
                    typeUpdatelist = new CbbTerminalComponentUpdateListDTO(updatelist.getVersion(), updatelist.getBaseVersion(), updatelist.getComponentSize());
                    componentList = new ArrayList<>();
                }
                componentList.add(component);
            }
            
        });
    }

}
