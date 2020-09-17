package com.ruijie.rcos.rcdc.terminal.module.def.api.enums;

import java.io.IOException;
import java.lang.reflect.Type;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.util.Assert;

/**
 * Description: 终端模式序列化与反序列化处理（fastJson）
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/11/6
 *
 * @author nt
 */
public class CbbTerminalWirelessAuthModeEnumsSerializer implements ObjectSerializer, ObjectDeserializer {

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object o) {
        Assert.notNull(parser, "parser can not be null");

        Object value = parser.parse();
        return value == null ? null : (T) CbbTerminalWirelessAuthModeEnums.convert(TypeUtils.castToString(value));
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.UNDEFINED;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        Assert.notNull(serializer, "serializer can not be null");
        String writeStr = object == null ? null : ((CbbTerminalWirelessAuthModeEnums) object).getName();
        serializer.write(writeStr);
    }
}
