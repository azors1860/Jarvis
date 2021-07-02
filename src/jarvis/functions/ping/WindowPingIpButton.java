package jarvis.functions.ping;


import jarvis.BasicButton;
import jarvis.functions.openWindow.OpenWindow;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class WindowPingIpButton extends BasicButton {

    @Override
    public void action() {
        OpenWindow.builder()
                .path("/jarvis/main/fxml/ping.fxml")
                .hidePanelWindow(true)
                .windowMoves(true)
                .alwaysOnTop(true)
                .build().open();
    }
}