package org.balinhui.stayAwakeSpwmod.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StayAwakeServiceTest {
    @Test
    public void test() throws Exception {
        var s = new StayAwakeService();
        s.init();
        //在 电源与电池 设置中，将屏幕超时更改为1分钟以便于测试
        s.stayAwake(true);
        Thread.sleep(1000 * 60);
        //如果一分钟后Windows还未息屏，则测试成功
        s.stayAwake(false);
        Thread.sleep(1000);
        s.stayAwake(true);
        Thread.sleep(1000 * 60);
        s.close();
        Thread.sleep(1000 * 60);//再一分钟，Windows将会息屏
    }
}