package jarvis.functions.smart.statusHdd;

import jarvis.functions.Cleanable;
import jarvis.BasicButton;
import jarvis.functions.openWindow.OpenWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class StatusHddButton extends BasicButton implements Cleanable {

    private StatusHddService service;

    @Autowired
    private void setService(StatusHddService service) {
        this.service = service;
    }

    /**
     * В случае, если лист пуст, отправляем заполняться и далее, если лист заполнен вызывает контроллер.
     * В случае, если лист HDD заполнен - вызывает контроллер.
     */
    @Override
    public void action() {

        ArrayList<String> hddList = service.getSerialNHddList();

        if (hddList.size() == 0) {
            service.startService();
        }
        if (hddList.size() > 0) {
            OpenWindow.builder()
                    .path("/jarvis/main/fxml/hddStatus.fxml")
                    .build().open();
        }
    }

    @Override
    public void clean() {
        service.clean();
    }
}
