package com.ruijie.rcos.rcdc.terminal.module.impl.init.updatelist;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.updatelist.CbbCommonUpdatelistDTO;
import com.ruijie.rcos.rcdc.terminal.module.impl.enums.TerminalTypeEnums;
import com.ruijie.rcos.rcdc.terminal.module.impl.cache.TerminalUpdateListCacheManager;
import com.ruijie.rcos.sk.base.filesystem.common.FileUtils;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/8/1
 * 
 * @param <T>
 *
 * @author nt
 */
public abstract class AbstractUpdatelistCacheInitTemplate<T extends CbbCommonUpdatelistDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUpdatelistCacheInitTemplate.class);

    /**
     * updatelist缓存初始化
     */
    public final void init() {
        // 开始初始化，将缓存就绪状态设为未就绪
        LOGGER.info("start init updatelist...");
        TerminalTypeEnums terminalType = getTerminalType();
        TerminalUpdateListCacheManager.setUpdatelistCacheNotReady(terminalType);

        String filePath = getUpdateListPath();
        File updateListFile = new File(filePath);
        if (!updateListFile.isFile()) {
            LOGGER.error("updatelist file not exist or not a file, the file path is {}", filePath);
            return;
        }

        String updateListContent = readUpdateListContent(updateListFile);
        if (StringUtils.isBlank(updateListContent)) {
            LOGGER.error("获取updatelist信息失败，请检查updatelist文件是否正确，updatelist文件路径：{}", filePath);
            return;
        }

        T updatelist = JSON.parseObject(updateListContent, getTClass());
        putInCache(updatelist);

        // 完成初始化后将updatelist缓存状态更新为false
        TerminalUpdateListCacheManager.setUpdatelistCacheReady(terminalType);
        LOGGER.info("finish init updatelist...");


    }

    /**
     * 读取updatelist的内容
     *
     * @param updateListFile updatelist文件
     * @return
     */
    private String readUpdateListContent(File updateListFile) {
        String updatelistStr = "";
        try {
            updatelistStr = FileUtils.readFileToString(updateListFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            LOGGER.error("read updatelist file error", e);
        }

        return updatelistStr;
    }

    /**
     * 获取泛型对相应的Class对象
     *
     * @return Class
     */
    public Class<T> getTClass() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class) type.getActualTypeArguments()[0];
    }

    /**
     * 将组件信息存入缓存
     *
     * @param updatelist 组件信息
     */
    protected void putInCache(T updatelist) {
        if (updatelist == null || CollectionUtils.isEmpty(updatelist.getComponentList())) {
            LOGGER.error("updatelist is null, upgrade file is incorrect");
            return;
        }

        fillUpdateList(updatelist);
        updatelist.setComponentSize(updatelist.getComponentList().size());

        // 将组件升级updatelist按终端类型，存入缓存中
        TerminalUpdateListCacheManager.add(getTerminalType(), updatelist);
    }

    /**
     * 获取updatelist文件路径
     *
     * @return updatelist文件路径
     */
    protected abstract String getUpdateListPath();

    /**
     * 补充updatelist信息
     * 
     * @param updatelist updatelist信息
     */
    protected abstract void fillUpdateList(T updatelist);

    /**
     * 获取终端类型
     * 
     * @return 终端类型
     */
    protected abstract TerminalTypeEnums getTerminalType();

}
