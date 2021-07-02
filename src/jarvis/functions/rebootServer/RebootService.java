package jarvis.functions.rebootServer;

import jarvis.functions.processes.EnumProcesses;
import jarvis.functions.TextFile;
import jarvis.functions.shop.Shop;
import jarvis.functions.sshConnection.SshConnectionService;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.main.PathProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author Sergey Chuvashov
 *
 * Класс предназначен для перезапуска служб/перезагрузки регистратора.
 */

@Service
public class RebootService {
    private String rebootCera;
    private String rebootDssl;
    private SshMainConnectionService sshConnection;
    private Shop shop;

    @Autowired
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    @PostConstruct
    private void init() {
        rebootDssl = new TextFile(PathProgram.rebootDssl).readFile();
        rebootCera = new TextFile(PathProgram.rebootCera).readFile();
    }

    public void action(){
        if (sshConnection.isTrassirOrCera()){
            rebootDssl();
        } else {
            rebootCera();
        }
    }

    private void rebootDssl() {
        sshConnection.setCommand(rebootDssl);
    }

    private void rebootCera() {
        SshConnectionService ssh = new SshConnectionService
                (shop.getIpVr(), shop.getSap(), "cera", sshConnection.getPasswordSsh(), EnumProcesses.Cera_Reboot);
        ssh.setCommand(rebootCera);
        ssh.setMaxTimeOnSec(60 * 5);
        ssh.connection();
    }
}
