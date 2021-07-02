package jarvis.functions.trassirSdk.health;

import jarvis.BasicButton;
import jarvis.functions.Cleanable;
import jarvis.functions.openWindow.OpenWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class HealthButton extends BasicButton implements Cleanable {

   private HealthService service;

    @Autowired
    public void setService(HealthService service) {
        this.service = service;
    }

    @Override
    public void action() {
        OpenWindow.builder()
                .path("/jarvis/main/fxml/health.fxml")
                .alwaysOnTop(true)
                .windowMoves(true)
                .build().open();
    }

    @Override
    public void clean() {
        service.clean();
    }
}
