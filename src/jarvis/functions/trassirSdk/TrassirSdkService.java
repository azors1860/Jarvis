package jarvis.functions.trassirSdk;

import jarvis.functions.shop.Shop;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.functions.trassirSdk.cameras.SdkCamerasModel;
import lombok.Getter;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * @Author Sergey Chuvashov
 */

@Service
public abstract class TrassirSdkService {

    private SshMainConnectionService sshConnection;
    protected final String username = "admin";
    protected Shop shop;
    @Getter
    protected String ip;
    protected String sid;

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    @Autowired
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @PostConstruct
    private void init() throws KeyManagementException, NoSuchAlgorithmException {
        offСertificateVerification();
    }

    private void offСertificateVerification() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public String getIdSession() {
        try {
            if (sid == null) {
                ip = shop.getIpVr();
                String password = sshConnection.getPasswordSsh();
                JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/login?username=" + username + "&password=" + password);
                sid = json.get("sid").toString();
            }
            return sid;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * @param channel uid канала
     * @return Есть ли сигнал на камере
     */
    public boolean isHaveSignalPerChannel(String channel) {
        try {
            JSONObject json =
                    JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/channels/" + channel + "/flags/signal?sid=" + sid);
            return json.getInt("value") == 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void rebootAllCam() throws IOException {
        ArrayList<String> uidIpCams = getAllIpCam();
        for (String cam : uidIpCams) {
            try {
                rebootCam(cam);
            } catch (Exception e) {
                System.out.println("Не удалось перезагрузить камеру: " + e.getMessage());
            }
        }
    }

    private ArrayList<String> getAllIpCam() throws IOException {
        ArrayList<String> result = new ArrayList<>();
        JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/ip_cameras/?sid=" + sid);
        JSONArray channelsJson = json.getJSONArray("subdirs");
        for (Object ipCam : channelsJson) {
            String uidIpcam = ipCam.toString();
            if (uidIpcam.contains("ip_cam")) {
                continue;
            }
            result.add(uidIpcam);
        }
        return result;
    }







    public void disableCam(String uidIp) throws IOException {
        JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/ip_cameras/" + uidIp + "/grabber_enabled=0?sid=" + sid);
    }

    public void turnOnCam(String uidIp) throws IOException {
        JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/ip_cameras/" + uidIp + "/grabber_enabled=1?sid=" + sid);
    }

    public void rebootCam(String uidIp) throws IOException {
        JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/ip_cameras/" + uidIp + "/reboot=1?sid=" + sid);
    }



    public void clean() {
        sid = null;
        ip = null;
    }
}
