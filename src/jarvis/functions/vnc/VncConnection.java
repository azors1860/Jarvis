package jarvis.functions.vnc;

import jarvis.functions.TextFile;
import jarvis.main.PathProgram;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.File;

/**
 * @Author Sergey Chuvashov
 */
@Component
public class VncConnection {

    private static String configVnc;

    @PostConstruct
    public static void init() {
        if (configVnc == null) {
            configVnc = new TextFile(PathProgram.configVnc).readFile();
        }
    }

    /**
     * Формирует и открывает файл vnc, после чего удаляет его.
     *
     * @param ip - адрес, куда будет производиться подключение.
     * @param port - порт по которому будет производиться подключение.
     * @param password - пароль доступа для подключения в зашифрованном виде.
     */
    public void openVncFile(String ip, String port, String password) {
        try {
            File file = new File("CreateVnc.vnc");

            String text = configVnc.replace("[IP]", ip);
            text = text.replace("[PORT]", port);
            text = text.replace("[PASS]", password);

            TextFile textFile = new TextFile(file.getPath());
            textFile.WriteFile(text);

            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
            Thread.sleep(1000);
            System.out.println("File CreateVnc delete: " + file.delete());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


