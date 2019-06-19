package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.MatchEqual;
import com.ruijie.rcos.rcdc.terminal.module.def.api.enums.CbbTerminalStateEnums;
import com.ruijie.rcos.sk.webmvc.api.request.PageWebRequest;
import com.ruijie.rcos.sk.webmvc.api.vo.ExactMatch;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Description: Function Description
 * Copyright: Copyright (c) 2018
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019/6/17
 *
 * @author nt
 */
public class CbbUpgradeableTerminalPageSearchRequest extends PageSearchRequest {

    public CbbUpgradeableTerminalPageSearchRequest(PageWebRequest webRequest) {
        super(webRequest);
    }

    @Override
    protected MatchEqual[] exactMatchConvert(ExactMatch[] exactMatchArr) {
        Assert.notNull(exactMatchArr , "exactMatchArr must not be null");
        MatchEqual[] matchEqualArr = new MatchEqual[exactMatchArr.length];

        for (int i = 0; i < exactMatchArr.length; i++) {
            ExactMatch exactMatch = exactMatchArr[i];
            MatchEqual matchEqual = null;
            switch (exactMatch.getName()) {
                case "groupId":
                    String[] valueArr = exactMatch.getValueArr();
                    UUID[] idArr = new UUID[valueArr.length];
                    for (int j = 0; j < valueArr.length; j++) {
                        idArr[j] = UUID.fromString(valueArr[j]);
                    }
                    matchEqual = new MatchEqual(exactMatch.getName(), idArr);
                    break;
                default :
                    matchEqual = new MatchEqual(exactMatch.getName(), exactMatch.getValueArr());
                    break;
            }
            matchEqualArr[i] = matchEqual;
        }

        return matchEqualArr;
    }
}
