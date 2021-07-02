package jarvis.functions.processes;

import jarvis.BasicButton;
import jarvis.functions.openWindow.OpenWindow;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class WindowProcessesButton extends BasicButton {

    @Override
    public void action() {
        OpenWindow.builder()
                .path("/jarvis/main/fxml/processes.fxml")
                .build().open();
    }
}
