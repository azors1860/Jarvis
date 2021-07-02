package jarvis.functions.smart;

import jarvis.functions.TextFile;
import jarvis.BasicButton;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.main.PathProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 *
 * Класс предназначен для установки SMART на сервер.
 */
@Component
public class InstallSmart extends BasicButton {

    SshMainConnectionService sshConnection;

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    private static String script;

    public void action() {

        if (script == null) {
            TextFile textFile = new TextFile(PathProgram.installSmart);
            script = textFile.readFile();
        }

        sshConnection.setCommand(script);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
