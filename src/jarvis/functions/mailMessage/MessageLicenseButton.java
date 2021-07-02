package jarvis.functions.mailMessage;

import jarvis.BasicButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class MessageLicenseButton extends BasicButton {

    MessageLicenseService service;

    @Autowired
    public void setService(MessageLicenseService service) {
        this.service = service;
    }

    @Override
    public void action() {
        service.action();
    }
}
