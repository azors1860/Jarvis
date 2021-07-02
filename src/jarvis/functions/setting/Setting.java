package jarvis.functions.setting;

import com.fasterxml.jackson.databind.ObjectMapper;
import jarvis.functions.InformationWindow;
import jarvis.functions.setting.models.HealthSetting;
import jarvis.functions.setting.models.Passwords;
import jarvis.functions.setting.models.SettingAppUser;
import jarvis.functions.setting.models.SettingButtonProp;
import jarvis.functions.shop.Shop;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.functions.trassirSdk.TrassirSdkService;
import jarvis.main.PathProgram;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.scene.control.Button;

@Component
@Log
public class Setting {

    private Shop shop;
    private SshMainConnectionService sshConnection;
    private TrassirSdkService sdkService;
    private final HashMap<String, SettingButtonProp> settingMap = new HashMap<>();
    private final List<Button> buttonsAppUsers = new ArrayList<>();
    @Getter
    private Passwords passwords;
    @Getter
    private HealthSetting healthSetting;


    @Autowired
    @Lazy
    public void setSdkService(TrassirSdkService sdkService) {
        this.sdkService = sdkService;
    }

    @Autowired
    @Lazy
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Autowired
    @Lazy
    public void setSshConnection(SshMainConnectionService sshConnection) {
        this.sshConnection = sshConnection;
    }

    public List<Button> getButtonsAppUsers() {
        return buttonsAppUsers;
    }

    public SettingButtonProp getSetting(String className) {
        return settingMap.get(className);
    }

    @PostConstruct
    private void init() {
        loadSettingButton();
        loadSettingAppUser();
        loadPasswords();
        loadSettingHealthStatus();
    }

    /**
     * Обрабатывает информацию из json файла (с информацией о паролях) и инициализирует файл
     */
    private void loadPasswords() {
        try {
            passwords = new ObjectMapper().
                    readValue(new File(PathProgram.passwords), Passwords.class);
        } catch (Exception e) {
            log.warning("FAILED TO LOAD PASSWORDS");
            e.printStackTrace();
            InformationWindow.sendInformationMessage(1, "Загрузка паролей"
                    , "Не удалось загрузить пароли из файла из файла \"passwords.json\"" +
                            " - программа не может быть запущена");
            System.exit(1);
        }
    }

    /**
     * Обрабатывает информацию из json файла (с информацией о настройках функции проверки здоровья)
     * и инициализирует файл.
     */
    private void loadSettingHealthStatus() {
        try {
            healthSetting = new ObjectMapper().readValue(new File(PathProgram.healthSetting), HealthSetting.class);
        } catch (Exception e) {
            log.warning("FAILED TO LOAD HEALTH STATUS");
            e.printStackTrace();
            InformationWindow.sendInformationMessage(3, "Загрузка настроек проверки здоровья"
                    , "Не удалось инициализировать настройки из файла \"healthSetting.json\"" +
                            " - будут применены стандартные настройки");
            defaultSettingsHealthStatus();
        }
    }

    /**
     * Дефолтные настройки SettingsHealthStatus.
     */
    private void defaultSettingsHealthStatus() {
        healthSetting = new HealthSetting();
        healthSetting.setAutomaticDsslStatusCheck(false);
        healthSetting.setMaximumCpuTemperature(100);
        healthSetting.setMaximumLoadCpu(100);
        healthSetting.setRedHealthStatusButtonFrame(false);
        healthSetting.setRedStatusHddButtonFrame(false);
    }

    /**
     * Обрабатывает информацию из json файла (с информацией о настройках кнопок),
     * заполняет мапу.
     */
    private void loadSettingButton() {
        try {
            SettingButtonProp[] buttons = new ObjectMapper().
                    readValue(new File(PathProgram.settingButton), SettingButtonProp[].class);
            for (SettingButtonProp button : buttons) {
                settingMap.put(button.getClassName(), button);
            }
        } catch (Exception e) {
            log.warning("FAILED TO LOAD SETTING BUTTON");
            e.printStackTrace();
            InformationWindow.sendInformationMessage(3, "Загрузка настроек кнопок"
                    , "Не удалось инициализировать настройки из файла \"setting_button.json\"" +
                            " - будут применены стандартные настройки");
        }
    }

    /**
     * Обрабатывает информацию из json файла (с информацией о дополнительных, пользовательских приложениях),
     * заполняет спикок с кнопками
     */
    private void loadSettingAppUser() {
        try {
            SettingAppUser[] settings = new ObjectMapper().
                    readValue(new File("app_user/sett.json"), SettingAppUser[].class);

            for (SettingAppUser sett : settings) {
                String uriImg = new File(sett.getPathImg())
                        .toURI()
                        .toString();
                Image image = new Image(uriImg, 16, 16, false, false);
                Button button = new Button("", new ImageView(image));
                button.setOnAction(actionEvent -> {
                    try {
                        getRunCommand(sett.getCommand());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                buttonsAppUsers.add(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
            InformationWindow.sendInformationMessage(3, "Загрузка настроек пользовательских приложений"
                    , "Не удалось инициализировать настройки из файла \"app_user/sett.json\"" +
                            " - будут применены стандартные настройки");
        }
    }

    /**
     * Заполняет актуальные данные для запуска пользовательского приложения с параметрами.
     *
     * @param command - команда для запуска пользовательского приложения.
     */
    private void getRunCommand(String command) throws IOException {
        command = replaceStrIpVr(command);
        command = replaceStrPasswordSsh(command);
        command = replaceStrUidSdk(command);
        System.out.println(command);
        Runtime.getRuntime().exec(command);
    }

    private String replaceStrIpVr(String input) {
        if (input.contains("[ip_vr]")) {
            try {
                String ipVr = shop.getIpVr();
                return input.replace("[ip_vr]", ipVr);
            } catch (Exception e) {
                return input.replace("[ip_vr]", "");
            }
        }
        return input;
    }

    private String replaceStrPasswordSsh(String input) {
        if (input.contains("[pass_ssh]")) {
            try {
                String ipVr = sshConnection.getPasswordSsh();
                return input.replace("[pass_ssh]", ipVr);
            } catch (Exception e) {
                return input.replace("[pass_ssh]", "");
            }
        }
        return input;
    }

    private String replaceStrUidSdk(String input) {
        if (input.contains("[uid_sdk]")) {
            try {
                String ipVr = sdkService.getIdSession();
                return input.replace("[uid_sdk]", ipVr);
            } catch (Exception e) {
                return input.replace("[uid_sdk]", "");
            }
        }
        return input;
    }
}
