package me.gb2022.htcp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HTCPFrontend {
    private final Set<HttpListenerInstance> instances = new HashSet<>();

    public HTCPFrontend(JsonObject config) {
        for (String s:config.keySet()){
            this.instances.add(new HttpListenerInstance(s,config.getAsJsonArray(s)));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (HttpListenerInstance instance:this.instances){
                instance.stop();
            }
        }));
    }

    public static void main(String[] args) {
        new HTCPFrontend(loadConfig());
    }

    private static JsonObject loadConfig() {
        File f = new File(System.getProperty("user.dir") + "/config.json");
        if (!f.exists() || f.length() == 0) {
            try (FileOutputStream stream = new FileOutputStream(f)) {
                stream.write(Objects.requireNonNull(HTCPFrontend.class.getResourceAsStream("/config.json")).readAllBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try (InputStream is = new FileInputStream(f)) {
            JsonReader reader=new JsonReader(new InputStreamReader(is));
            reader.setLenient(true);
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
