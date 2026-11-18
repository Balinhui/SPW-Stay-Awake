package org.balinhui.stayAwakeSpwmod.services;

import java.util.List;

public class ServicesManager {
    private final List<Service> services;

    private static final ServicesManager manager = new ServicesManager();

    private ServicesManager() {
        services = List.of(
                new StayAwakeService()
        );
    }

    public static ServicesManager getManager() {
        return manager;
    }

    public void initAll() {
        services.forEach(Service::init);
    }

    public void closeAll() {
        services.forEach(Service::close);
    }

    public <T> T getService(Class<T> clazz) {
        for (Service service : services) {
            if (clazz.isInstance(service)) {
                return clazz.cast(service);
            }
        }
        return null;
    }
}
