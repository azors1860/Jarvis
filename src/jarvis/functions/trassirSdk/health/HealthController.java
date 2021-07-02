package jarvis.functions.trassirSdk.health;

import jarvis.WindowApp;
import jarvis.functions.setting.Setting;
import jarvis.main.Main;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author Sergey Chuvashov
 */

public class HealthController extends WindowApp {
    public Label disk;
    public Label db;
    public Label cam;
    public Label cpuLoad;
    public Label uptime;
    public Label temperature;
    public Label hddSmart;
    public Label versionSoft;
    public AnchorPane anchorPane;
    private HealthService service;
    private int maximumTemperatureCpu;
    private int maximumCpuLoad;

    public void initialize() {
        AnnotationConfigApplicationContext context = Main.getContext();
        service = context.getBean(HealthService.class);

        maximumTemperatureCpu = context.getBean(Setting.class).
                getHealthSetting().
                getMaximumCpuTemperature();

        maximumCpuLoad = context.getBean(Setting.class).
                getHealthSetting().
                getMaximumLoadCpu();


        fillData(service.getHealth());
        super.initialize();
    }

    private void fillData(HealthTrassirSdkModel model) {
        try {
            if (model != null) {

                setLabelOkOrError(disk, model.isDiskOk());
                setLabelOkOrError(db, model.isDataBaseOk());


                cam.setText(model.getChannelsOnline() + " / " + model.getChannelsTotal());
                if (model.getChannelsOnline() == model.getChannelsTotal()) {
                    cam.setTextFill(Color.web("#000000"));
                } else {
                    cam.setTextFill(Color.web("#ff0000", 0.8));
                }

                uptime.setText(service.timeToString(model.getUptime()));
                double cpuLoadService = model.getCpuLoad();
                cpuLoad.setText(Double.toString(cpuLoadService));
                cpuLoad.setTextFill(Color.web("#000000"));
                if (cpuLoadService > maximumCpuLoad){
                    cpuLoad.setTextFill(Color.web("#ff0000", 0.8));
                }
            }

            setLabelOkOrError(hddSmart, service.getAllStatusHdd());

            String temperatureService = service.getTemperature();
            temperature.setText(temperatureService);
            temperature.setTextFill(Color.web("#000000"));
            try {
                int temperatureServiceInt = Integer.parseInt(temperatureService);
                if (temperatureServiceInt > maximumTemperatureCpu) {
                    temperature.setTextFill(Color.web("#ff0000", 0.8));
                }
            } catch (Exception ignore) {
            }


            versionSoft.setText(service.getVersionSoftware());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLabelOkOrError(Label label, boolean isOk){
        if (isOk){
            label.setText("ok");
            label.setTextFill(Color.web("#000000"));
        } else {
            label.setText("error");
            label.setTextFill(Color.web("#ff0000", 0.8));
        }
    }

    @Override
    public void setWindowApp() {
        HealthButton button = Main.getContext().getBean(HealthButton.class);
        button.setWindowApp(this);
    }



    public void pressedLabelDisc(MouseEvent mouseEvent) {
        disk.setStyle("-fx-font-weight: bold");
    }

    public void releasedLabelDisc(MouseEvent mouseEvent) {
       disk.setStyle("");
    }

    public void clickedLabelDisc(MouseEvent mouseEvent) {
        service.showMessageErrorHdd();
    }

    public void clickedLabelSmartDisk(MouseEvent mouseEvent) {
        service.statusHddButtonAction();
    }

    public void pressedLabelSmartDisc(MouseEvent mouseEvent) {
        hddSmart.setStyle("-fx-font-weight: bold");
    }

    public void releasedLabelSmartDisc(MouseEvent mouseEvent) {
        hddSmart.setStyle("");
    }

    public void camClicked(MouseEvent mouseEvent) {
        service
                .getSdkCamerasButton().action();
    }

    public void camPressed(MouseEvent mouseEvent) {
        cam.setStyle("-fx-font-weight: bold");
    }

    public void camReleased(MouseEvent mouseEvent) {
        cam.setStyle("");
    }
}
