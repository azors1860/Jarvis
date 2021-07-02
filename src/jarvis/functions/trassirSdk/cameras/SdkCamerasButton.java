package jarvis.functions.trassirSdk.cameras;

import jarvis.BasicButton;
import jarvis.functions.openWindow.OpenWindow;
import org.springframework.stereotype.Component;

@Component
public class SdkCamerasButton extends BasicButton {

    @Override
    public void action() {
        OpenWindow.builder()
                .path("/jarvis/main/fxml/sdkCameras.fxml")
                .alwaysOnTop(true)
                .windowMoves(true)
                .build().open();
    }
}
