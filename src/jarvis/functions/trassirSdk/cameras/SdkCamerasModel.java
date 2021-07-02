package jarvis.functions.trassirSdk.cameras;

import lombok.Getter;

/**
 * @Author Sergey Chuvashov
 */
@Getter
public class SdkCamerasModel {
    private final String uidChannel;
    private final String uidIp;
    private final String ip;
    private final String nameChannel;
    private final boolean isAnalog;
    private final boolean online;

    public SdkCamerasModel(String uidChannel, String uidIp, String ip, String nameChannel, boolean isAnalog, boolean online) {
        this.uidChannel = uidChannel;
        this.uidIp = uidIp;
        this.ip = ip;
        this.isAnalog = isAnalog;
        this.online = online;
        if (!nameChannel.equals("")){
            this.nameChannel = nameChannel;
        } else {
            this.nameChannel = uidChannel;
        }
    }

    @Override
    public String toString() {
        return nameChannel + " : " + ip + " : " + online;
    }
}
