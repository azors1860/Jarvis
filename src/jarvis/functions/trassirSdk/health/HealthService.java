package jarvis.functions.trassirSdk.health;

import jarvis.baseController.BaseController;
import jarvis.functions.InformationWindow;
import jarvis.functions.rebootServer.RebootButton;
import jarvis.functions.setting.Setting;
import jarvis.functions.setting.models.HealthSetting;
import jarvis.functions.smart.TemperatureTrassirButton;
import jarvis.functions.smart.statusHdd.StatusHddButton;
import jarvis.functions.smart.statusHdd.StatusHddService;
import jarvis.functions.trassirSdk.JsonReader;
import jarvis.functions.trassirSdk.TrassirSdkService;
import jarvis.functions.trassirSdk.cameras.SdkCamerasButton;
import javafx.application.Platform;
import lombok.Getter;
import lombok.extern.java.Log;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Sergey Chuvashov
 */

@Service
@Log
public class HealthService extends TrassirSdkService {

    private TemperatureTrassirButton temperatureService;
    private StatusHddService hddService;
    private StatusHddButton statusHddButton;
    private RebootButton rebootButton;
    private HealthSetting healthSetting;
    private String temperature = null;
    private String versionSoftware = null;
    private HealthTrassirSdkModel healthTrassirSdkModel = null;
    @Getter
    private SdkCamerasButton sdkCamerasButton;
    private Thread initializer = new Thread(this::initData);

    @Autowired
    public void setSdkCamerasButton(SdkCamerasButton sdkCamerasButton) {
        this.sdkCamerasButton = sdkCamerasButton;
    }

    @Autowired
    public void setStatusHddButton(StatusHddButton statusHddButton) {
        this.statusHddButton = statusHddButton;
    }

    @Autowired
    public void setHealthSetting(Setting setting) {
        healthSetting = setting.getHealthSetting();
    }

    @Autowired
    public void setRebootButton(RebootButton rebootButton) {
        this.rebootButton = rebootButton;
    }

    @Autowired
    public void setHddService(StatusHddService hddService) {
        this.hddService = hddService;
    }

    @Autowired
    public void setTemperatureService(TemperatureTrassirButton temperatureService) {
        this.temperatureService = temperatureService;
    }

    private BaseController getBaseController(){
        return BaseController.getBaseController();
    }

    /**
     * Статус всех hdd по смарту.
     * @return статус hdd.
     */
    public boolean getAllStatusHdd() {
        if (hddService.getSerialNHddList().size() == 0) {
            hddService.startService();
        }
        boolean status = hddService.isStatusGoodAllHdd();
        log.info("getAllStatusHdd: " + status);
        if (!status){
            getBaseController().setFrameButtonStatusHdd(true);
        }
        return status;
    }

    /**
     * Получение температуры регистратора Trassir
     * @return температура.
     */
    public String getTemperature() {
        if (temperature == null) {
            temperature = temperatureService.getTemperature();
            log.info("temperature: " + temperature);
        }
        return temperature;
    }

    /**
     * Получение версии ПО на регистраторе Trassir.
     * @return версия ПО.
     */
    public String getVersionSoftware() {
        try {
            String sid = getIdSession();
            if (versionSoftware == null) {
                JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/health/servicepack_level?sid=" + sid);
                versionSoftware = json.get("value").toString();
                log.info("getVersionSoftware: " + versionSoftware);
            }
        } catch (Exception e) {
            return null;
        }
        return versionSoftware;
    }

    /**
     * Получение информации о здоровье сервера, через sdk trassir.
     * @return модель здоровья.
     */
    public HealthTrassirSdkModel getHealth() {
        if (healthTrassirSdkModel == null) {
            try {
                String sid = getIdSession();
                JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/health?sid=" + sid);
                int diskOk = json.getInt("disks");
                int dataBaseOk = json.getInt("database");
                int channelsTotal = json.getInt("channels_total");
                int channelsOnline = json.getInt("channels_online");
                double cpuLoad = json.getDouble("cpu_load");
                long uptime = json.getLong("uptime");
                healthTrassirSdkModel = new HealthTrassirSdkModel(diskOk, dataBaseOk, channelsTotal, channelsOnline, cpuLoad, uptime);
                log.info("get health: https://" + ip + ":8080/health?sid=" + sid
                        + "\n\n" + healthTrassirSdkModel.toString());
                getBaseController().setHealthButtonDisable(false);
                if (!healthTrassirSdkModel.isGeneralStatus()) {
                    getBaseController().setFrameButtonStatusHealth(true);
                }
                checkedStateCpuLoad(cpuLoad, uptime);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return healthTrassirSdkModel;
    }

    /**
     * Получить последнюю ошибку HDD сервера.
     */
    public void showMessageErrorHdd() {
        try{
            String sid = getIdSession();
            JSONObject json = JsonReader.readJsonFromUrl("https://" + ip + ":8080/settings/health/disks_last_error?sid=" + sid);
            String result = json.get("value").toString();
            if (!result.equals("")){
                InformationWindow.sendInformationMessage(2, "Ошибка HDD", result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start() {
        if (healthSetting.isAutomaticDsslStatusCheck()){
            initializer.start();
        }
    }

    private void initData() {
        getHealth();
        getVersionSoftware();
        getBaseController().setMessageTemperature(getTemperature());
        getAllStatusHdd();
    }

    public void statusHddButtonAction(){
        statusHddButton.windowActionButton();
    }

    /**
     * Проверка загруженности CPU и uptime.
     * В случае, если условия соблюдаются - предлагает перезагрузить регистратор.
     */
    private void checkedStateCpuLoad(double load, long uptime) {
        long uptimeDay = uptime / 86400;
        int cpuLoad = healthSetting.getMaximumLoadCpu();
        if (load > cpuLoad && uptime > 604800) {
            String text = "CPU: " + load + " upt: " + uptimeDay + "дн. reboot?";
            Platform.runLater(() -> InformationWindow.openWindowYesOrNo(rebootButton, text));
        }
    }

    public void clean() {
        super.clean();
        temperature = null;
        versionSoftware = null;
        healthTrassirSdkModel = null;
        initializer.interrupt();
        initializer = new Thread(this::initData);
        initializer.setDaemon(true);
        getBaseController().setHealthButtonDisable(true);
        if (getBaseController() != null){
            getBaseController().setFrameButtonStatusHdd(false);
            getBaseController().setFrameButtonStatusHealth(false);
        }
    }

    public String timeToString(long secs) {
        long month = secs / 2_592_000;
        long day = (secs % 2_592_000) / 86400;
        long hour = (secs % 86400) / 3600;
        long min = secs / 60 % 60;
        long sec = secs % 60;
        if (month > 0) {
            return month + " мес. " + hour + " дн.";
        }
        if (day > 0) {
            return day + " дн. " + hour + " ч.";
        }
        if (hour > 0) {
            return hour + " ч. " + min + " мин.";
        }
        if (min > 0) {
            return min + " мин. " + sec + " cек.";
        }
        return sec + " cек.";
    }
}
