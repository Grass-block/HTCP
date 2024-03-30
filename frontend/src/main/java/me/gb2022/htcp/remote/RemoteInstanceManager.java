package me.gb2022.htcp.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface RemoteInstanceManager {

    Map<String, RemoteRequestClient> INSTANCES = new HashMap<>();

    static RemoteRequestClient getClientInstance(String host) {
        try {
            if (!INSTANCES.containsKey(host)) {
                RemoteRequestClient instance = new RemoteRequestClient(host);
                instance.start();
                INSTANCES.put(host, instance);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return INSTANCES.get(host);
    }

    static void stopAll(){
        for (RemoteRequestClient client:INSTANCES.values()){
            client.stop();
        }
    }
}
