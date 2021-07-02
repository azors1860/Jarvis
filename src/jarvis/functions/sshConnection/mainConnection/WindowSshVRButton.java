package jarvis.functions.sshConnection.mainConnection;

import jarvis.BasicButton;
import jarvis.functions.Cleanable;
import jarvis.functions.openWindow.OpenWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */
@Component
public class WindowSshVRButton extends BasicButton implements Cleanable {

    SshMainConnectionService service;

    @Autowired
    public void setService(SshMainConnectionService service) {
        this.service = service;
    }

    @Override
    public void action() {
        OpenWindow.builder()
                .path("/jarvis/main/fxml/sshView.fxml")
                .resizableWindow(true)
                .build().open();
    }

    @Override
    public void clean() {
        service.clean();
    }
}
