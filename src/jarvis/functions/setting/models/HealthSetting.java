package jarvis.functions.setting.models;

import lombok.Data;

@Data
public class HealthSetting {
    private boolean automaticDsslStatusCheck;
    private int maximumCpuTemperature;
    private int maximumLoadCpu;
    private boolean redHealthStatusButtonFrame;
    private boolean redStatusHddButtonFrame;
}
