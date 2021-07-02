package jarvis.functions.shop;

import jarvis.baseController.BaseController;
import jarvis.baseController.normalState;
import jarvis.functions.Cleanable;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

@Component
@Lazy
public class Shop {

    @Getter
    private String sap = "";

    @Getter
    private String ipVr;

    @Getter
    private String ipBo;

    private final List<Cleanable> cleanableServices = new LinkedList<>();
    private SshMainConnectionService sshConnectionService;
    private ShopInitializer shopInitializer;

    @Autowired
    public void setShopInitializer(ShopInitializer shopInitializer) {
        this.shopInitializer = shopInitializer;
    }

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnectionService) {
        this.sshConnectionService = sshConnectionService;
    }

    /**
     * Добавляет новую запись в инициализатор.
     */
    public void addEntryShopInitializer() {
        String passwordSsh = sshConnectionService.getPasswordSsh();
        if ((!sap.equals("")) && (passwordSsh != null)) {
            String user = sshConnectionService.isTrassirOrCera() ? "trassir" : "cera";
            shopInitializer.addShop(sap, user, passwordSsh);
        }
    }

    public void addCleanableServices(Cleanable el) {
        cleanableServices.add(el);
    }

    /**
     * Проверяет является ли введеный текст sap
     * @param s - текст для проверки.
     * @return - результат проверки.
     */
    public boolean isSap(String s) {
        return s.matches("[(A-Z|0-9)]{4}");
    }

    public void reconnected() {
        String newSap = sap;
        sap = "";
        newShop(newSap);
    }

    public void newShop(String sap) {
        if (!this.sap.equals(sap)) {
            addEntryShopInitializer();
            this.sap = sap;
            cleanServices();
            new Thread(this::ping).start();
        }
    }

    /**
     * Очистка и инициализация сервисов.
     */
    private void cleanServices() {
        for (Cleanable service : cleanableServices) {
            service.clean();
        }
    }

    private void ping() {
        BaseController.getBaseController().normaState(normalState.ping);
        ipBo = setIpBo(sap);
        ipVr = setIpVr(ipBo);
        setClipboard(ipVr);

        if (pingWin(getIpVr())) {
            BaseController.getBaseController().normaState(normalState.online);
            sshConnectionService.connection();
        } else {
            BaseController.getBaseController().normaState(normalState.offline);
        }
    }

    public String setIpBo(String sap) {
        String result = null;
        Process ping = null;
        try {
            ping = Runtime.getRuntime().exec("ping bo-" + sap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        assert ping != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(ping.getInputStream(), Charset.forName("CP866")));
        String line = "";

        while (true) {
            try {
                if ((line = br.readLine()) == null) break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            String[] w = line.split(" ");

            if (w[0].equals("Обмен")) {
                int ww = w[4].length();
                result = w[4].substring(1, ww - 1);
                break;
            }
        }
        return result;
    }

    public String setIpVr(String ipBo) {
        String[] s = ipBo.split("\\.");
        int w1 = Integer.parseInt(s[3]);
        return s[0] + "." + s[1] + "." + s[2] + "." + (w1 + 1);
    }

    public static boolean pingWin(String ip) {
        int VS = 0;
        int NS = 0;

        Process ping = null;
        try {
            ping = Runtime.getRuntime().exec("ping " + ip);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        assert ping != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(ping.getInputStream(), Charset.forName("CP866")));
        String line = "";
        while (true) {
            try {
                if ((line = br.readLine()) == null) break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (line.contains("Ответ")) {
                VS++;

            } else if (line.contains("Превышен")) {
                NS++;
            }
            if (VS == 2) {
                return true;
            } else if (NS == 2) {
                return false;
            }
        }
        return false;
    }

    public static void setClipboard(String str) {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }
}