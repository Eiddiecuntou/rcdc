package com.ruijie.rcos.rcdc.terminal.module.def.api.request;

import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbEditTerminalGroupDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbSystemUpgradeTaskDTO;
import com.ruijie.rcos.rcdc.terminal.module.def.api.dto.CbbTerminalGroupDetailDTO;
import com.ruijie.rcos.sk.base.junit.SkyEngineRunner;
import com.ruijie.rcos.sk.base.test.GetSetTester;
import mockit.Expectations;
import mockit.Verifications;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SkyEngineRunner.class)
public class PageSearchRequestTest {

    /**
     * 测试getAndSet方法
     */
    @Test
    public void testSetAndGet() {
        GetSetTester getSetTester = new GetSetTester(PageSearchRequest.class);
        getSetTester.runTest();
        Assert.assertTrue(true);
    }
    /**
     * 测试自定义添加精确匹配查询条件
     */
     @Test
     public void testappendCustomMatchEqual(){
         MatchEqual matchEqual=new MatchEqual();
         MatchEqual[] sourceArr=new MatchEqual[] {matchEqual};
         PageSearchRequest pageSearchRequest=new PageSearchRequest();
         new Expectations() {
             {
                 pageSearchRequest.setMatchEqualArr(new MatchEqual[] {matchEqual});
                 result=pageSearchRequest;
             }
         };

         pageSearchRequest.appendCustomMatchEqual(matchEqual);

         new Verifications() {
             {
                 MatchEqual[] targetArr = new MatchEqual[sourceArr.length + 1];
                 times=0;
             }
         };

     }
    /**
     * 测试写MatchEqual，UUID类型
     */
    @Test
    public void testcoverMatchEqualForUUID(){
        String name="wangwu";
        MatchEqual matchEqual=new MatchEqual();
        MatchEqual[] sourceArr=new MatchEqual[] {matchEqual};
        PageSearchRequest pageSearchRequest=new PageSearchRequest();


        new Expectations() {
            {
               result=pageSearchRequest;
            }
        };



        new Verifications() {
            {
                ArrayUtils.isEmpty(sourceArr);
                times=0;
            }
        };

    }



}