package dev.gaminggeek.mojangstatus;

import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.util.JsonHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StatusCheck {
    public OkHttpClient client = new OkHttpClient();
    public static final Notifications notifications = Notifications.INSTANCE;
    public JsonObject servicesConfig = getServicesConfig();
    public JsonArray services = (JsonArray) servicesConfig.get("services");
    public JsonObject names = (JsonObject) servicesConfig.get("names");

    public JsonArray getStatus() throws Exception {
        Request request = new Request.Builder()
                .url("https://status.mojang.com/check")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            JsonElement status = new JsonParser()
                    .parse(response.body().string());
            if (status.isJsonArray()) {
                try {
                    return (JsonArray) status;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        throw new Exception("Failed to retrieve status");
    }

    public void checkStatus(JsonArray lastStatus) {
        StatusConfig config = MojangStatus.statusConfig;
        JsonArray latestStatus = new JsonArray();
        try {
            latestStatus = getStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (config.debug) lastStatus = fakeStatus();
        JsonArray changes = getChanged(lastStatus, latestStatus);
        for (JsonElement service : changes) {
            JsonObject change = service.getAsJsonObject();
            String s = "minecraft.net";
            if (change.has(s) && config.minecraftWebsite) {
                notify(s, change.get(s).getAsString());
            }
            s = "session.minecraft.net";
            if (change.has(s) && config.minecraftSessions) {
                notify(s, change.get(s).getAsString());
            }
            s = "textures.minecraft.net";
            if (change.has(s) && config.minecraftTextures) {
                notify(s, change.get(s).getAsString());
            }
            s = "mojang.com";
            if (change.has(s) && config.mojangWebsite) {
                notify(s, change.get(s).getAsString());
            }
            s = "account.mojang.com";
            if (change.has(s) && config.mojangAccounts) {
                notify(s, change.get(s).getAsString());
            }
            s = "authserver.mojang.com";
            if (change.has(s) && config.mojangAuth) {
                notify(s, change.get(s).getAsString());
            }
            s = "sessionserver.mojang.com";
            if (change.has(s) && config.mojangSessions) {
                notify(s, change.get(s).getAsString());
            }
            s = "api.mojang.com";
            if (change.has(s) && config.mojangAPI) {
                notify(s, change.get(s).getAsString());
            }
        }
        MojangStatus.lastStatus = latestStatus;
    }

    public void notify(String service, String status) {
        service = names.get(service).getAsString();
        String i = "is";
        if (service.endsWith("s")) i = "are";
        if (status.equalsIgnoreCase("green")) {
            notifications.pushNotification(
                    "Status Change",
                    String.format("%s " + i + " online!", service)
            );
        } else if (status.equalsIgnoreCase("yellow")) {
            notifications.pushNotification(
                    "Status Change",
                    String.format("%s " + i + " having some issues!", service)
            );
        } else if (status.equalsIgnoreCase("red")) {
            notifications.pushNotification(
                    "Status Change",
                    String.format("%s " + i + " unavailable!", service)
            );
        }
    }

    public JsonArray getChanged(JsonArray before, JsonArray after) {
        JsonArray changed = new JsonArray();
        for (int i = 0; i < before.size(); ++i) {
            if (!(before.get(i).equals(after.get(i)))) {
                changed.add(after.get(i));
            }
        }
        return changed;
    }

    public JsonArray initStatus() {
        JsonArray statuses = new JsonArray();
        for (JsonElement service : services) {
            JsonObject o = new JsonObject();
            o.addProperty(service.getAsString(), "green");
            statuses.add(o);
        }
        return statuses;
    }

    public JsonArray fakeStatus() {
        JsonArray statuses = new JsonArray();
        for (JsonElement service : services) {
            JsonObject o = new JsonObject();
            o.addProperty(service.getAsString(), "grey");
            statuses.add(o);
        }
        return statuses;
    }

    public JsonObject getServicesConfig() {
        final InputStream inputStream = StatusCheck.class.getResourceAsStream("/services.json");
        final StringBuilder sb = new StringBuilder();
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            inputStream.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String data = sb.toString();
        return new JsonHolder(data).getObject();
    }
}