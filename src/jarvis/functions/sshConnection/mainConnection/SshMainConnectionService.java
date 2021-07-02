package jarvis.functions.sshConnection.mainConnection;

import jarvis.baseController.BaseController;
import jarvis.baseController.normalState;
import jarvis.functions.setting.Setting;
import jarvis.functions.setting.models.Passwords;
import jarvis.functions.shop.Shop;
import jarvis.functions.shop.ShopInitializer;
import jarvis.functions.shop.ShopInitializerModel;
import jarvis.functions.sshConnection.ConnectionSshException;
import jarvis.functions.sshConnection.SshConnectionService;
import jarvis.functions.trassirSdk.health.HealthService;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * @Author Sergey Chuvashov
 * <p>
 * Класс предназначен для работы с соединением текущего сервера, взаимодействует с контроллером.
 */
@Log
@Service
public class SshMainConnectionService extends SshConnectionService {

    private String answerServer = "";
    private ArrayList<String> authorization;
    private Shop shop;
    private ShopInitializer shopInitializer;
    private HealthService healthService;
    private Passwords passwords;

    @Autowired
    public void setPasswords(Setting setting) {
        this.passwords = setting.getPasswords();
    }

    @Getter
    private String loginSsh;
    @Getter
    private String passwordSsh;
    @Getter
    private boolean trassirOrCera;

    @Autowired
    public void setHealthService(HealthService healthService) {
        this.healthService = healthService;
    }

    @Autowired
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Autowired
    public void setShopInitializer(ShopInitializer shopInitializer) {
        this.shopInitializer = shopInitializer;
    }

    @PostConstruct
    private void init() {
        authorization = new ArrayList<>();
        String[] loginAndPass = passwords.getPassVR();
        for (String lAndP : loginAndPass) {
            if (lAndP.contains(":")) {
                authorization.add(lAndP);
            }
        }
    }

    protected void sendCommand() {
        super.sendCommand();
        printInfoSshController();
    }

    public void runThread() {
        String thisSap = shop.getSap();
        log.info(Thread.currentThread().getName() + ": run. SSH - main thread");
        String ipVr = shop.getIpVr();

        try {
            ShopInitializerModel node = shopInitializer.getModel(shop.getSap());
            String name = node.getUser();
            String pass = node.getPassword();
            if (!connectionMonitoring(ipVr, pass, name)) {
                throw new Exception("Ошибка: подключение при быстром подключении");
            }
        } catch (Exception e) {
            int i;
            boolean resultConnection = false;
            for (i = 0; i < authorization.size(); i++) {
                String[] loginAndPass = authorization.get(i).split(":");
                resultConnection = connectionMonitoring(ipVr, loginAndPass[1], loginAndPass[0]);
                if (resultConnection) {
                    break;
                }
            }
        }
        if (thisSap.equals(shop.getSap())){
            BaseController.getBaseController().normaState(normalState.notSSH);
        }
        disconnect();

    }

    protected void connect(String host, String pass, String user) throws ConnectionSshException {
        try {
            super.connect(host, pass, user);
            printInfoAboutServerBaseController(pass, user);
        } catch (ConnectionSshException e) {
            throw new ConnectionSshException(e);
        }
    }

    private void printInfoSshController() {
        if (SshMainViewController.IsNotNull()) {
            if (!answerServer.equals(getAnswer())) {
                answerServer = getAnswer();
                SshMainViewController.setMessage(answerServer);
            }
        } else {
            answerServer = "";
        }
    }

    private void printInfoAboutServerBaseController(String pass, String user) {
        loginSsh = user;
        passwordSsh = pass;
        if (user.equals("trassir")) {
            BaseController.getBaseController().normaState(normalState.trassir);
            trassirOrCera = true;
            healthService.start();
        } else {
            BaseController.getBaseController().normaState(normalState.cera);
            trassirOrCera = false;
        }
    }

    public void disconnect() {
        close();
        log.info(Thread.currentThread().getName() + ":SSH stop - main thread");
    }

    public void clean() {
        passwordSsh = null;
        disconnect();
        initThread();
    }
}
