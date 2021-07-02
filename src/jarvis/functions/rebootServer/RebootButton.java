package jarvis.functions.rebootServer;

import jarvis.BasicButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */
@Component
public class RebootButton extends BasicButton {

    private RebootService service;

    @Autowired
    public void setService(RebootService service) {
        this.service = service;
    }

    @Override
    public void action() {
        service.action();
    }
}