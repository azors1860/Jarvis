package jarvis.functions.trassirSdk.cameras;

import jarvis.functions.trassirSdk.JsonReader;
import jarvis.functions.trassirSdk.TrassirSdkService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SdkCamerasService extends TrassirSdkService {
    public ArrayList<SdkCamerasModel> getListChannels() {
        ArrayList<SdkCamerasModel> channels = new ArrayList<>();
        try {
            String sid = getIdSession();
            System.out.println("https://" + ip + ":8080/settings/channels/?sid=" + sid);
            JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/channels/?sid=" + sid);
            JSONArray channelsJson = json.getJSONArray("subdirs");
            for (Object ch : channelsJson) {
                String uidChannel = ch.toString();

                String uidIp = getUidIp(uidChannel);
                if (uidIp == null) {
                    continue;
                }
                String ip = getIpOneCamera(uidIp);
                String nameChannel = getNameChannel(uidChannel);
                boolean online = isHaveSignalPerChannel(uidChannel);
                SdkCamerasModel camera = new SdkCamerasModel(uidChannel, uidIp, ip, nameChannel, getIsAnalog(uidIp), online);
                System.out.println(camera);
                channels.add(camera);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }

    private String getUidIp(String channel) {
        String sid = getIdSession();
        try {
            JSONObject json =
                    JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/channels/" + channel + "/info/grabber_path?sid=" + sid);
            String[] temp = json.getString("value").split("/");

            return temp[temp.length - 1];
        } catch (Exception e) {
            return null;
        }
    }

    private String getIpOneCamera(String idCamera) throws IOException {
        String sid = getIdSession();
        JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/ip_cameras/" + idCamera +
                "/connection_ip?sid=" + sid);
        return json.get("value").toString();
    }

    /**
     * @param channel uid канала
     * @return Имя канала
     * @throws IOException
     */
    private String getNameChannel(String channel) throws IOException {
        String sid = getIdSession();
        JSONObject json =
                JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/channels/" + channel + "/name?sid=" + sid);
        return json.getString("value");
    }

    private boolean getIsAnalog(String uidIp) throws IOException {
        String sid = getIdSession();
        int count = 0;
        for (int i = 0; i < 4; i++) {

            JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/ip_cameras/" + uidIp +
                    "/channel0" + i + "_guid?sid=" + sid);

            if (!json.getString("value").equals("")) {
                count++;
            }
        }
        return count > 1;
    }

    private List<SdkCamerasModel> getCamerasFilter(List<SdkCamerasModel> cameras, boolean online){
       return cameras.stream().filter(cam -> cam.isOnline() == online).collect(Collectors.toList());
    }
}
