package com.ruijie.rcos.rcdc.terminal.module.impl.util;

import com.ruijie.rcos.rcdc.terminal.module.impl.BusinessKey;
import com.ruijie.rcos.sk.base.exception.BusinessException;
import com.ruijie.rcos.sk.base.log.Logger;
import com.ruijie.rcos.sk.base.log.LoggerFactory;
import org.springframework.util.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/10/14
 *
 * @author hs
 */
public class SystemResultCheckUtil {

    /**
     * 系统抽象层返回string类型返回值为异常情况的正则表达式
     */
    private final static String RJFAIL_STRING_REGEX = "RJFAIL-\\d{5}";

    /**
     * 非数字的正则表达式
     */
    private final static String  NOT_NUMBER_REGEX = "[^-0-9]";

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemResultCheckUtil.class);

    /**
     * 判断string类型返回值是否是正确的，异常情况抛出异常
     *
     * @param result string类型返回值
     * @throws BusinessException 异常
     * @return 过滤后的结果
     */
    public static String checkResult(String result) throws BusinessException {
        Assert.hasText(result, "the result can not be blank");
        // 过滤掉无效字符
        result = result.trim();
        LOGGER.debug("The string result is:{}", result);
        if (result.matches(RJFAIL_STRING_REGEX)) {
            Pattern pattern = Pattern.compile(NOT_NUMBER_REGEX);
            Matcher matcher = pattern.matcher(result);
            //筛选出错误码
            String codeStr = matcher.replaceAll("");
            LOGGER.error("指令执行失败：code={}", codeStr);
            throw new BusinessException(BusinessKey.RCDC_TERMINAL_SYSTEM_UPGRADE_MAKE_SEED_FILE_FAIL, codeStr);
        }
        return result;
    }
}
