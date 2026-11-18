package org.balinhui.stayAwakeSpwmod.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicesManagerTest {
    @Test
    public void test() {
        ServicesManager manager = ServicesManager.getManager();
        StayAwakeService service = manager.getService(StayAwakeService.class);
        assertNotNull(service);
        assertInstanceOf(StayAwakeService.class, service);
        manager.closeAll();
    }
}