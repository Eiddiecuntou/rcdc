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
import com.ruijie.rcos.rcdc.terminal.module.def.enums.TerminalPlatformEnums;
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
    
    private static final String PLATFORM_SPERATOR = "&";

    @Autowired
    private ComponentUpdateListCacheManager cacheManager;

    @Override
    public void safeInit() {
        // 读取服务器上组件升级目录下的updatelist,并将其存入缓存中
        File upgradeDirectory = new File(Constants.TERMINAL_TERMINAL_COMPONET_UPGRADE_PATH);
        if (!upgradeDirectory.isDirectory()) {
            LOGGER.error("linux vdi terminal component upgrade dictory is not exist, the path is {}",
                    Constants.TERMINAL_TERMINAL_COMPONET_UPGRADE_PATH);
            return;
        }
        
        File[] subfiles = upgradeDirectory.listFiles();
        if (subfiles.length == 0) {
            LOGGER.info("no component upgrade file");
            return;
        }
        for (File subFile : subfiles) {
            if (subFile.isDirectory()) {
                LOGGER.warn("sub file is not dictory, the file path is {}", subFile.getAbsolutePath());
                continue;
            }
            File updateListFile = new File(getUpdatelistFile(subFile));
            if (!updateListFile.isFile()) {
                LOGGER.warn("updatelist file not exist or not a file, the file path is {}", updateListFile);
                continue;
            }
            try {
                String updatelistStr = FileUtils.readFileToString(updateListFile, Charset.forName("UTF-8"));
                CbbTerminalComponentUpdateListDTO updatelist =
                        JSON.parseObject(updatelistStr, CbbTerminalComponentUpdateListDTO.class);
                putInCache(updatelist);
            } catch (IOException e) {
                LOGGER.error("read updatelist file error", e);
            }
        }

    }

    private String getUpdatelistFile(File subFile) {
        return subFile.getAbsolutePath() + File.pathSeparator + Constants.TERMINAL_COMPONET_UPDATE_LIST_FILE_NAME;
    }

    private void putInCache(CbbTerminalComponentUpdateListDTO updatelist) {
        if(updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.error("updatelist is null, upgrade file is incorrect");
            return;
        }
        
        // 将终端组件升级updatelist信息按终端类型进行拆分，存入缓存中
        Map<TerminalPlatformEnums, CbbTerminalComponentUpdateListDTO> caches = cacheManager.getUpdateListCaches();
        updatelist.getComponentList().forEach(component -> {
            String[] platforms = component.getPlatform().split(PLATFORM_SPERATOR);
            for(String platformStr : platforms) {
                if(StringUtils.isBlank(platformStr) || !TerminalPlatformEnums.contains(platformStr)) {
                    LOGGER.debug("updatelist contains invalid platform[{}]", platformStr);
                    continue;
                }
                TerminalPlatformEnums platform = TerminalPlatformEnums.valueOf(platformStr);
                CbbTerminalComponentUpdateListDTO typeUpdatelist = null;
                List<CbbTerminalComponentVersionInfoDTO> componentList = null;
                if(caches.containsKey(platform)) {
                    LOGGER.debug("cache include platform updatelist, platform [{}]", platform);
                    typeUpdatelist = caches.get(platform);
                    componentList = typeUpdatelist.getComponentList();
                }else {
                    LOGGER.debug("cache not include platform updatelist, platform [{}]", platform);
                    typeUpdatelist = buildNewComponentUpdatelist(updatelist);
                    componentList = new ArrayList<>();
                }
                componentList.add(component);
                typeUpdatelist.setComponentSize(typeUpdatelist.getComponentSize() + 1);
            }
        });
        
    }

    private CbbTerminalComponentUpdateListDTO buildNewComponentUpdatelist(
            CbbTerminalComponentUpdateListDTO updatelist) {
        CbbTerminalComponentUpdateListDTO newUpdatelist = new CbbTerminalComponentUpdateListDTO();
        newUpdatelist.setVersion(updatelist.getVersion());
        newUpdatelist.setBaseVersion(updatelist.getBaseVersion());
        newUpdatelist.setLimitVersion(updatelist.getLimitVersion());
        newUpdatelist.setComponentSize(0);
        return newUpdatelist;
    }

}
