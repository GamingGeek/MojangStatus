package dev.gaminggeek.mojangstatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.modcore.api.ModCoreAPI;
import net.modcore.api.gui.Notifications;
import net.modcore.api.utils.JsonHolder;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class StatusCheck {
    public OkHttpClient client = new OkHttpClient();
    public Notifications notifications;
    public JsonObject servicesConfig = getServicesConfig();
    public Set<String> services = getServicesSet();
    public JsonObject names = servicesConfig.getAsJsonObject("names");

    public JsonObject getServiceStatus(JsonArray arr) {
        JsonObject obj = new JsonObject();
        for (JsonElement element : arr.getAsJsonArray()) {
            JsonObject e = element.getAsJsonObject();
            for (String service : services) {
                if (e.has(service)) {
                    obj.addProperty(service, e.get(service).getAsString());
                }
            }
        }
        return obj;
    }

    public JsonObject getStatus() throws Exception {
        Request request = new Request.Builder()
                .url("https://status.mojang.com/check")
                .build();

        final JsonElement[] status = new JsonElement[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    assert responseBody != null;
                    status[0] = new JsonParser().parse(responseBody.string());
                    countDownLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        countDownLatch.await();  // Ensure request has finished
        if (status[0] != null && status[0].isJsonArray()) {
            try {
                return getServiceStatus(status[0].getAsJsonArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to retrieve status");
        }
        return new JsonObject();
    }

    public void checkStatus(JsonObject lastStatus, Boolean notify) {
        if (getSize(lastStatus) == 0) lastStatus = fakeStatus();
        StatusConfig config = MojangStatus.statusConfig;
        JsonObject latestStatus;
        try {
            latestStatus = getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (notify) {
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
        }
        MojangStatus.lastStatus = latestStatus;
    }

    public void notify(String service, String status) {
        notifications = ModCoreAPI.getInstance().notifications();
        service = names.get(service).getAsString();
        String i = "is";
        if (service.endsWith("s")) i = "are";
        if (status.equalsIgnoreCase("green")) {
            notifications.push(
                    "Status Change",
                    String.format("%s " + i + " online!", service)
            );
        } else if (status.equalsIgnoreCase("yellow")) {
            notifications.push(
                    "Status Change",
                    String.format("%s " + i + " having some issues!", service)
            );
        } else if (status.equalsIgnoreCase("red")) {
            notifications.push(
                    "Status Change",
                    String.format("%s " + i + " unavailable!", service)
            );
        }
    }

    public JsonArray getChanged(JsonObject before, JsonObject after) {
        JsonArray changed = new JsonArray();
        if (getSize(before) == 0 || getSize(after) == 0) return changed;
        StatusConfig config = MojangStatus.statusConfig;
        for (String k : services) {
            if (!(before.get(k).equals(after.get(k)))) {
                JsonObject placebo = new JsonObject();
                placebo.addProperty(k, after.get(k).getAsString());
                changed.add(placebo);
            } else if (config.noChanges) {
                String status = after.get(k).getAsString();
                if (status.equals("yellow") || status.equals("red")) {
                    JsonObject placebo = new JsonObject();
                    placebo.addProperty(k, after.get(k).getAsString());
                    changed.add(placebo);
                }
            }
        }
        return changed;
    }

    public JsonObject fakeStatus() {
        StatusConfig config = MojangStatus.statusConfig;
        JsonObject statuses = new JsonObject();
        for (String service : services) {
            statuses.addProperty(service, config.debug ? "grey" : "green");
        }
        return statuses;
    }

    public Integer getSize(JsonObject obj) {
        Integer size = 0;
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            size++;
        }
        return size;
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

    public Set<String> getServicesSet() {
        JsonArray services = servicesConfig.getAsJsonArray("services");
        Set<String> keys = new HashSet<>();
        for (JsonElement s : services) {
            keys.add(s.getAsString());
        }
        return keys;
    }
}