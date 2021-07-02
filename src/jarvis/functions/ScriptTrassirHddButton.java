package jarvis.functions;

import jarvis.BasicButton;
import jarvis.functions.processes.EnumProcesses;
import jarvis.functions.sshConnection.SshConnectionService;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.main.PathProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class ScriptTrassirHddButton extends BasicButton {

    private static String script = "";
    private SshMainConnectionService sshConnection;

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    @Override
    public void action() {
        if (script.equals("")) {
            script = new TextFile(PathProgram.scriptDsslHdd).readFile();
        }
        SshConnectionService ssh = new SshConnectionService(shop.getIpVr(), shop.getSap(),
                "trassir", sshConnection.getPasswordSsh(), EnumProcesses.Trassir_HDD);
        ssh.setCommand(script);
        ssh.setMaxTimeOnSec(60 * 8);
        ssh.connection();
    }
}
