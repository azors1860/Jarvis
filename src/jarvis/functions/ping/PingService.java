package jarvis.functions.ping;

import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author Sergey Chuvashov
 */

@Service
public class PingService {

    SshMainConnectionService sshConnection;

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    /**
     * Перебор всего списка для последующего пинга по одному адресу.
     * @param ipArraySet - список всех адресов.
     * @return - новый список всех адресов, включая результаты работы
     */
    public Set<String> pingIp(HashSet<String> ipArraySet) {
        HashSet<String> result = new HashSet<>();
        for (String ip : ipArraySet) {
            sshConnection.setCommand("ping " + ip + " -c2\n");
            boolean answer = pingOneIp(ip);
            result.add(answer ? ip + "(on)" : ip + "(off)");
        }
        return result;
    }

    /**
     * Ping одного ip адреса.
     * @param ip - Адрес ip.
     * @return - результат процесса (есть ли ответ).
     */
    private boolean pingOneIp(String ip) {
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String[] result = sshConnection.getAnswer().split("\n");
            for (int j = result.length - 1; j >= 0; j--) {

                if (result[j].contains(ip + " ping stat")) {
                    String[] lines = result[j + 1].split(",");
                    for (String line : lines) {
                        if (line.contains("packet loss")) {
                            int r = Integer.parseInt(line.replaceAll("[^0-9]", ""));
                            System.out.println(r);
                            if (r == 0) {
                                return true;
                            } else if (r < 100) {
                                return true;
                                //TODO - потери при передаче
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
