package jarvis.functions.trassirSdk.health;

import lombok.Getter;
import lombok.ToString;

/**
 * @Author Sergey Chuvashov
 */

@Getter
@ToString
public class HealthTrassirSdkModel {
    private final boolean diskOk;
    private final boolean dataBaseOk;
    private final int channelsTotal;
    private final int channelsOnline;
    private final double cpuLoad;
    private final long uptime;
    private boolean generalStatus = false;

    public HealthTrassirSdkModel(int diskOk, int dataBaseOk, int channelsTotal, int channelsOnline, double cpuLoad, long uptime) {
        this.diskOk = diskOk == 1;
        this.dataBaseOk = dataBaseOk == 1;
        this.channelsTotal = channelsTotal;
        this.channelsOnline = channelsOnline;
        this.cpuLoad = cpuLoad;
        this.uptime = uptime;
        if (diskOk == 1 && dataBaseOk == 1 && channelsTotal == channelsOnline && cpuLoad < 80) {
            generalStatus = true;
        }
    }
}
