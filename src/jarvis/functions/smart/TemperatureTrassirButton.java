package jarvis.functions.smart;

import jarvis.functions.InformationWindow;
import jarvis.BasicButton;
import jarvis.baseController.BaseController;
import jarvis.functions.Cleanable;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author SergeyChuvashov
 * <p>
 * Класс предназначен для проверки температуры CPU сервера, через SMART.
 */

@Component
public class TemperatureTrassirButton extends BasicButton implements Cleanable {

    private InstallSmart installSmart;
    private SshMainConnectionService sshConnection;
    private String temperature;

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    @Autowired
    public void setInstallSmart(InstallSmart installSmart) {
        this.installSmart = installSmart;
    }

    @Override
    public void action() {
        if (sshConnection.isTrassirOrCera()){
            BaseController.getBaseController().setMessageTemperature(getTemperature());
        }
    }

    public String getTemperature() {
        String oldAnswerSsh = sshConnection.getAnswer();
        sshConnection.setCommand("sudo /root/crap/monitoring");
        String result = "--";

        for (int i = 0; i < 5; i++) {
            sleepSecThisThread();
            String resultCommand = sshConnection.getAnswer().replace(oldAnswerSsh, "");
            if (resultCommand.contains("command not found")) {
                try {
                    InformationWindow.openWindowYesOrNo(installSmart, "Smart not installed. To install?");
                } catch (Exception ignored) {
                }
                return result;
            } else {
                String[] resultCommandArray = resultCommand.split("\n");

                for (String s : resultCommandArray) {
                    if (s.contains("CPU Temper")) {
                        result = s.replaceAll("[^0-9]+", "");
                        return result;
                    }
                }
            }
        }
        return result;
    }

    private void sleepSecThisThread() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clean() {
        temperature = null;
    }
}