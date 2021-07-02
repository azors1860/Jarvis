package jarvis.functions.vnc;


import jarvis.functions.setting.Setting;
import jarvis.functions.setting.models.Passwords;
import jarvis.BasicButton;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class VncVRButton extends BasicButton {

    private VncConnection vncConnection;
    private SshMainConnectionService sshConnection;
    private Passwords passwords;

    @Autowired
    public void setSettingPass(Setting settingPass) {
        this.passwords = settingPass.getPasswords();
    }

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    @Autowired
    public void setVncConnection(VncConnection vncConnection) {
        this.vncConnection = vncConnection;
    }

    public void action() {
        String pass = null;
        if (sshConnection.getPasswordSsh() == null) {
            pass = "";
        } else if (sshConnection.isTrassirOrCera()) {
            pass = passwords.getPassVNCDSSL();
        } else if (!sshConnection.isTrassirOrCera()) {
            pass = passwords.getPassVNCCERA();
        }

        vncConnection.openVncFile(shop.getIpVr(), "5900", pass);
    }
}


