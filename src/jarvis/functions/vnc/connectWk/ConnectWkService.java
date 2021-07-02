package jarvis.functions.vnc.connectWk;

import jarvis.functions.processes.EnumProcesses;
import jarvis.functions.TextFile;
import jarvis.functions.setting.Setting;
import jarvis.functions.setting.models.Passwords;
import jarvis.functions.shop.Shop;
import jarvis.functions.sshConnection.ConnectionSshException;
import jarvis.functions.sshConnection.SshConnectionService;
import jarvis.functions.vnc.VncConnection;
import jarvis.main.PathProgram;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Sergey Chuvashov
 * <p>
 * Класс предназначен для подключения по VNC к WK магазинов.
 */
@Service
@Log
public class ConnectWkService {

    private HashMap<String, String> portAndWk = null;
    private String boLogin = null;
    private String boPass = null;
    private String boCommand = null;
    private Shop shop;
    private VncConnection vncConnection;
    private Passwords passwords;

    @Autowired
    public void setPasswords(Setting setting) {
        this.passwords = setting.getPasswords();
    }

    @Autowired
    public void setVncConnection(VncConnection vncConnection) {
        this.vncConnection = vncConnection;
    }

    @Autowired
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @PostConstruct
    private void init() {
        boCommand = new TextFile(PathProgram.portAndWkCommand).readFile();
        String[] loginAndPass = passwords.getPassAndLoginBO().split(":");
        boLogin = loginAndPass[0];
        boPass = loginAndPass[1];
    }

    public HashMap<String, String> getPortAndWk() {
        return portAndWk;
    }

    public void openVnc(String wk) {
        vncConnection.openVncFile(shop.getIpBo(), portAndWk.get(wk), passwords.getPassVNCWK());
    }

    /**
     * Парсит полученный от сервера ответ и заполняет мапу portAndWk.
     */
    public void setPortAndWk() {
        try {
            String text = getAnswerServerBO();
            String[] lines = text.split("\n");
            portAndWk = new HashMap<>();
            for (String line : lines) {
                if (line.contains("wk") && (line.contains("vnc://"))) {
                    Pattern patternPort = Pattern.compile("\\d{4}");
                    Pattern patternWK = Pattern.compile("wk\\d\\d");
                    Matcher matcherPort = patternPort.matcher(line);
                    Matcher matcherWK = patternWK.matcher(line);
                    if ((matcherPort.find()) && (matcherWK.find())) {
                        String portM = matcherPort.group();
                        String wkM = matcherWK.group().replace("wk", "");
                        System.out.println(wkM + " " + portM);
                        portAndWk.put(wkM, portM);
                    }
                }
            }
        } catch (ConnectionSshException e) {
            log.warning("Ответ от сервера не получен: " + e.getMessage());
        }
    }

    /**
     * Отправляет конманду на сервер BO для получения портов WK.
     *
     * @return - ответ от сервера.
     * @throws ConnectionSshException - в случае ошибки при подключении к серверу BO.
     */
    private String getAnswerServerBO() throws ConnectionSshException {
        String result;
        SshConnectionService ssh = new SshConnectionService(shop.getIpBo(), shop.getSap(),
                boLogin, boPass, EnumProcesses.Connect_WK);
        ssh.setCommand(boCommand);
        ssh.setMaxTimeOnSec(3);
        ssh.connection();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = ssh.getAnswer();
        if (result == null) {
            throw new ConnectionSshException("Error connecting to the server BO");
        }
        return result;
    }

    public void clean() {
        portAndWk = null;
    }
}
