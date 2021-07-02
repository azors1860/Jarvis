package jarvis.functions.smart.statusHdd;

import jarvis.functions.InformationWindow;
import jarvis.functions.TextFile;
import jarvis.functions.smart.InstallSmart;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.main.PathProgram;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Chuvashov Sergey
 */
@Service
public class StatusHddService {

    private static String command = null;
    private ArrayList<String> serialNHddList = new ArrayList<>();
    private HashMap<String, HddModel> hashMapHdd = new HashMap<>();
    @Getter
    private boolean isStatusGoodAllHdd = true;
    private SshMainConnectionService sshConnection;
    private InstallSmart installSmart;

    public StatusHddService() {
        if (command == null) {
            TextFile textFile = new TextFile(PathProgram.hddStatusCommand);
            command = textFile.readFile();
        }
    }

    @Autowired
    public void setInstallSmart(InstallSmart installSmart) {
        this.installSmart = installSmart;
    }

    @Autowired
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    public boolean isStatusGoodAllHdd() {
        return isStatusGoodAllHdd;
    }

    public ArrayList<String> getSerialNHddList() {
        return serialNHddList;
    }

    /**
     * Отправляет команду для проверки состояния HDD на сервер. Также отправляет проверочную команду, для подтверждения
     * статуса обработки основной команды. В случае, если SMART не установлен - предложит пользователю его установить.
     *
     * @return true - если команда была обработана и успешно обработана.
     * false - в иных случаях, в том числе если на сервере не установлен SMART.
     */
    private boolean sendCommand() {
        sshConnection.setCommand(command);
        thisThreadSleep(1);
        sshConnection.setCommand("echo HddStatus command completed");

        for (int i = 0; i < 6; i++) {
            thisThreadSleep(1);
            String answerSsh = sshConnection.getAnswer();
            if (answerSsh.contains("command not found")) {
                try {
                    InformationWindow.openWindowYesOrNo(installSmart, "Smart not installed. To install?");
                } catch (Exception ignored) {}
                return false;
            }
            if (answerSsh.contains("HddStatus command completed")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Парсит ответ полученный от сервера, копируя при этом необходимые данные. Также выставляет статус здоровья HDD.
     */
    public void startService() {

        if (sendCommand()) {

            StringBuilder text = new StringBuilder();
            boolean copy = false;
            String deviceModel = null;
            String serialNumber = null;
            boolean isStatusGood = false;

            String[] lines = sshConnection.getAnswer().split("\n");

            for (String line : lines) {
                String tempLine = line;

                if (tempLine.contains("Device Model:")) {
                    deviceModel = tempLine.replace("Device Model:", "").replaceAll("[ |\t]", "");
                } else if (tempLine.contains("Serial Number")) {
                    serialNumber = tempLine.replace("Serial Number:", "").replaceAll("[ |\t]", "");
                }

                if (copy) {
                    text.append(tempLine);
                    text.append("\n");
                    if ((tempLine.contains("Reallocated_Sector")) || (tempLine.contains("Current_Pending"))) {
                        tempLine = tempLine.replace(" ", "");
                        tempLine = tempLine.replace("\t", "");
                        Pattern pattern = Pattern.compile("-\\d+");
                        Matcher matcher = pattern.matcher(tempLine);
                        if (matcher.find()) {
                            tempLine = matcher.group().replace("-", "");
                        }
                        try {
                            int q = Integer.parseInt(tempLine);
                            if (q > 9) {
                                isStatusGood = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            isStatusGood = false;
                        }
                    }
                }

                if (tempLine.contains("Vendor Specific SMART Attributes with Thresholds:")) {
                    isStatusGood = true;
                    copy = true;
                } else if (tempLine.contains("UDMA_CRC_Error")) {
                    copy = false;
                    addHdd(deviceModel, serialNumber, text.toString(), isStatusGood);
                    deviceModel = null;
                    serialNumber = null;
                    text.setLength(0);
                    isStatusGood = false;
                }
            }
        }
    }

    /**
     * Создаёт новый экземпляр класса HDD, помещает его в коллекции.
     *
     * @param deviceModel  - модель HDD
     * @param serialNumber - номер DDD
     * @param text         - показатели здоровья HDD
     * @param isStatusGood - статус здоровья HDD
     */
    private void addHdd(String deviceModel, String serialNumber, String text, boolean isStatusGood) {
        HddModel hdd = new HddModel(deviceModel, serialNumber, text, isStatusGood);
        if (!isStatusGood) {
            isStatusGoodAllHdd = false;
        }
        hashMapHdd.put(serialNumber, hdd);
        serialNHddList.add(isStatusGood ? "OK: " + serialNumber : "ERROR: " + serialNumber);
    }

    /**
     * Получить экземпляр класса HDD по serial number.
     *
     * @param serial - serial number HDD
     * @return - экземпляр класса HDD
     */
    public HddModel getHdd(String serial) {
        return hashMapHdd.get(serial);
    }

    /**
     * Усыпить текущий поток.
     *
     * @param sec - на сколько секунд отправить спать.
     */
    private void thisThreadSleep(long sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        serialNHddList = new ArrayList<>();
        hashMapHdd = new HashMap<>();
        isStatusGoodAllHdd = true;
    }
}
