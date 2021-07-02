package jarvis.functions.trassirSdk.cameras;

import jarvis.WindowApp;
import jarvis.main.Main;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SdkCamerasController extends WindowApp {
    public VBox vBox;
    public AnchorPane anchorPane;

    public void initialize() {
        vBox.setSpacing(1);
        SdkCamerasService camerasService = Main.getContext().getBean(SdkCamerasService.class);
        for (SdkCamerasModel cam: camerasService.getListChannels()){
            System.out.println("++");
            HBox hBox = createHBox();
            hBox.getChildren()
                    .add(new Label(cam.toString()));
            vBox.getChildren().add(hBox);
        }
    }

    private HBox createHBox() {
        HBox hBox = new HBox();
        hBox.setSpacing(3);
        return hBox;
    }

    @Override
    public void setWindowApp() {
        SdkCamerasButton button = Main.getContext().getBean(SdkCamerasButton.class);
        button.setWindowApp(this);
    }
}
