package jarvis.functions.mailMessage;

import jarvis.functions.TextFile;
import jarvis.functions.shop.Shop;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.main.PathProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author SergeyChuvashov
 */
@Service
public class MessageLicenseService {

    private SshMainConnectionService sshConnection;
    private Shop shop;
    private MailMessage mail;

    @Autowired
    public void setMail(MailMessage mail) {
        this.mail = mail;
    }

    @Autowired
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    /**
     * Формирует письмо для запроса лицензии.
     */
    public void action() {
        String license = readLicense();
        String dongleID = readDongleID(license);
        TextFile textFile = new TextFile(PathProgram.mailLicenseMessage);
        String[] emails = textFile.readFile().split("\n");
        mail.setTo(emails[0]);
        mail.setCc(emails[1]);
        mail.setSubject("Лицензия SAP: " + shop.getSap() + " Dongle ID: " + dongleID);
        mail.setBody("Добрый день.\nПросьба отправить стандартную лицензию.\n\nТекст лицензии:\n\n" + license);
        mail.create();
    }

    /**
     * @return текст лицензии с сервера.
     */
    private String readLicense() {
        String license = "";
        try {
            sshConnection.setCommand("sudo cat ' Trassir 3 License.txt'\n");
            Thread.sleep(1500);
            String text = sshConnection.getAnswer();
            license = text.substring(text.indexOf("By this"), text.indexOf("Signature"));


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return license;
    }

    /**
     * Ищет в тексте лицензии DongleID.
     * @param license - текст лицензии.
     * @return - DongleID.
     */
    private String readDongleID(String license) {
        String[] strings = license.split("\n");
        for (String string : strings) {
            if (string.contains("USB Dongle ID:")) {
                return string.replace("USB Dongle ID:  ", "");
            }
        }
        return null;
    }
}
