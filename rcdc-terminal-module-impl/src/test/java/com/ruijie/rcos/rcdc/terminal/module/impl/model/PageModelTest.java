package com.ruijie.rcos.rcdc.terminal.module.impl.model;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * 
 * Description: Function Description
 * Copyright: Copyright (c) 2019
 * Company: Ruijie Co., Ltd.
 * Create Time: 2019年3月5日
 * 
 * @author ls
 */
public class PageModelTest {

    /**
     * 
     */
    @Test
    public void testConstructor() {
        PageModel pageModel = new PageModel(1, 1);
        assertEquals(1, pageModel.getPage());
        assertEquals(1, pageModel.getLimit());
        
        pageModel.setLimit(2);
        pageModel.setPage(3);
        assertEquals(3, pageModel.getPage());
        assertEquals(2, pageModel.getLimit());
    }

}
