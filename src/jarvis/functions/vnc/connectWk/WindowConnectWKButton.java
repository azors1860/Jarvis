package jarvis.functions.vnc.connectWk;

import jarvis.BasicButton;
import jarvis.functions.Cleanable;
import jarvis.functions.openWindow.OpenWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class WindowConnectWKButton extends BasicButton implements Cleanable {

    ConnectWkService service;

    @Autowired
    public void setService(ConnectWkService service) {
        this.service = service;
    }

    @Override
    public void action() {
        if (service.getPortAndWk() == null) {
            service.setPortAndWk();
        }

        if (service.getPortAndWk() != null) {
            OpenWindow.builder().path("/jarvis/main/fxml/connectWK.fxml")
                    .alwaysOnTop(true)
                    .hidePanelWindow(true).
                    build().open();
        }
    }

    @Override
    public void clean() {
        service.clean();
    }
}
