package jarvis.baseController;

import jarvis.BasicButton;
import jarvis.WindowApp;
import jarvis.functions.CloseProgram;
import jarvis.functions.ScriptTrassirHddButton;
import jarvis.functions.fastAnswer.FastAnswerService;
import jarvis.functions.mailMessage.MessageLicenseButton;
import jarvis.functions.openWindow.OpenWindow;
import jarvis.functions.ping.WindowPingIpButton;
import jarvis.functions.processes.WindowProcessesButton;
import jarvis.functions.rebootServer.RebootButton;
import jarvis.functions.setting.Setting;
import jarvis.functions.shop.Shop;
import jarvis.functions.smart.TemperatureTrassirButton;
import jarvis.functions.smart.statusHdd.StatusHddButton;
import jarvis.functions.sshConnection.mainConnection.SshMainConnectionService;
import jarvis.functions.sshConnection.mainConnection.WindowSshVRButton;
import jarvis.functions.trassirSdk.health.HealthButton;
import jarvis.functions.vnc.VncVRButton;
import jarvis.functions.vnc.connectWk.WindowConnectWKButton;
import jarvis.main.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * @Author Sergey Chuvashov
 */

public class BaseController extends WindowApp {

    public Label infoTable;
    public Label temperatureCpuTrassir;
    public ImageView iconPlus;
    public Button buttonPing;
    public Button buttonCopyPassword;
    public Button buttonOpenWindowSsh;
    public Button buttonRebootVR;
    public Button buttonScriptHdd;
    public Button buttonMailLicense;
    public Button buttonProcess;
    public Button buttonOpenWK;
    public Button buttonHddStatu;
    public Button buttonReconnectShop;
    public Button buttonOpenVncShop;
    public Button healthButton;
    public AnchorPane anchorPane;
    public ComboBox<String> comboBox;
    @Getter
    private static BaseController baseController;
    public VBox vBox;
    public Line lineDown;
    private FastAnswerService fastAnswerService;
    private AnnotationConfigApplicationContext context;
    private Shop shop;
    private int maximumTemperatureCpu;

    @Override
    public void initialize() {
        super.initialize();
        baseController = this;
        context = Main.getContext();
        Setting setting = context.getBean(Setting.class);
        addUserButton(setting.getButtonsAppUsers());
        lineDown.setLayoutY(Main.getHeight());
        shop = context.getBean(Shop.class);
        fastAnswerService = context.getBean(FastAnswerService.class);
        maximumTemperatureCpu = context.getBean(Setting.class).
                getHealthSetting().
                getMaximumCpuTemperature();

    }

    private void addUserButton(List<Button> buttons) {
        vBox.setSpacing(1);
        HBox hBox = createHBox();
        for (int i = 1; i < buttons.size() + 1; i++) {
            hBox.getChildren().add(buttons.get(i - 1));
            if (i % 5 == 0) {
                vBox.getChildren().add(hBox);
                hBox = createHBox();
            }
        }
        if ((buttons.size()) % 5 != 0) {
            vBox.getChildren().add(hBox);
        }
    }

    private HBox createHBox() {
        HBox hBox = new HBox();
        hBox.setPrefHeight(30);
        hBox.setPrefWidth(180);
        hBox.setSpacing(3);
        return hBox;
    }

    public Stage getStage() {
        return baseController.stageWindow;
    }

    /**
     * Изменить статус в строке состояния видеорегистратора.
     *
     * @param text - Новый текст.
     */
    private void setMessageStatus(String text) {
        Platform.runLater(() -> baseController.infoTable.setText(text));
    }

    /**
     * Установить температуру процессора.
     *
     * @param massageTemperature - температура процессора.
     */
    public void setMessageTemperature(String massageTemperature) {
        if (massageTemperature.equals("")) {
            Platform.runLater(() -> baseController.temperatureCpuTrassir.setText("--"));
            Platform.runLater(() -> baseController.temperatureCpuTrassir.setTextFill(Color.web("#000000")));

        } else {
            boolean textRed = false;
            try {
                int temperature = Integer.parseInt(massageTemperature);
                if (temperature > maximumTemperatureCpu) {
                    textRed = true;
                }
            } catch (Exception ignored) {
            }
            Platform.runLater(() -> baseController.temperatureCpuTrassir.setText(massageTemperature + "°с"));
            if (textRed) {
                Platform.runLater(() -> baseController.temperatureCpuTrassir.setTextFill(Color.web("#ff0000", 0.8)));
            }
        }
    }

    /**
     * Действие при вводе информации.
     * В случае, если это SAP - отправляет эту информацию в shop для обновления подключения текущего сервера.
     */
    public void input(KeyEvent keyEvent) {

        String inp = comboBox.getEditor().getText();
        if (shop.isSap(inp)) {
            shop.newShop(inp);
            BasicButton.closeWindows();

        } else if (inp.length() > 2) {
            ObservableList<String> list = FXCollections.observableArrayList();
            comboBox.setItems(list);
            List<String> listTemp = fastAnswerService.search(inp);
            list.addAll(listTemp);
        }
    }


    public void actionComboBox(ActionEvent actionEvent) {
        String textAnswer = comboBox.getEditor().getText();
        if (textAnswer.length() > 20) {
            comboBox.getEditor().setText("");
            Shop.setClipboard(textAnswer.replace("|", "\n"));
        }
    }

    /**
     * Меняет статус базового контроллера. В зависимости от статуса кнопки могут быть не активны.
     *
     * @param n - normalState, в зависимости от необходимого состояния.
     */
    public void normaState(normalState n) {

        if (n == normalState.offline) {
            setMessageStatus(baseController.shop.getSap() + " не в сети.");
            setMessageTemperature("");
            Button[] vrOffline = {baseController.buttonPing, baseController.buttonOpenVncShop,
                    baseController.buttonCopyPassword, baseController.buttonOpenWindowSsh,
                    baseController.buttonRebootVR, baseController.buttonScriptHdd,
                    baseController.buttonMailLicense, baseController.buttonHddStatu, baseController.healthButton};
            baseController.buttonSetVisible(false, vrOffline);
        } else if (n == normalState.online) {
            setMessageStatus(baseController.shop.getSap() + " в сети.");
            Button[] vrOnline = {baseController.buttonOpenVncShop, baseController.buttonProcess,
                    baseController.buttonOpenWK, baseController.buttonReconnectShop};
            baseController.buttonSetVisible(true, vrOnline);
        } else if (n == normalState.cera) {
            setMessageStatus(baseController.shop.getSap() + " в сети. cera");
            Button[] cera = {baseController.buttonPing, baseController.buttonOpenVncShop, baseController.buttonCopyPassword,
                    baseController.buttonOpenWindowSsh, baseController.buttonRebootVR, baseController.buttonProcess,
                    baseController.buttonOpenWK, baseController.buttonReconnectShop};
            baseController.buttonSetVisible(true, cera);
        } else if (n == normalState.trassir) {
            setMessageStatus(baseController.shop.getSap() + " в сети. trassir");
            Button[] trassir = {baseController.buttonPing,
                    baseController.buttonCopyPassword, baseController.buttonOpenVncShop, baseController.buttonOpenWindowSsh,
                    baseController.buttonRebootVR, baseController.buttonScriptHdd, baseController.buttonMailLicense,
                    baseController.buttonProcess, baseController.buttonOpenWK,
                    baseController.buttonHddStatu, baseController.buttonReconnectShop, baseController.healthButton};
            baseController.buttonSetVisible(true, trassir);
        } else if (n == normalState.ping) {
            normaState(normalState.offline);
            setMessageStatus("ping " + baseController.shop.getSap());
        } else if (n == normalState.notSSH) {
            normaState(normalState.offline);
            normaState(normalState.online);
            setMessageStatus(baseController.shop.getSap() + " в сети. not ssh");
        }
    }

    /**
     * В случае нажатия на надпись с температурой, надпись становится жирной.
     */
    public void pressedLabelTemperature(MouseEvent mouseEvent) {
        temperatureCpuTrassir.setStyle("-fx-font-weight: bold");
    }

    /**
     * В случае отпускания нажатия на надпись с температурой, возвращаются стандартные настройки.
     */
    public void releasedLabelTemperature(MouseEvent mouseEvent) {
        temperatureCpuTrassir.setStyle("");
    }

    /**
     * Обводит кнопку "StatusHdd" красной рамкой.
     *
     * @param setFrame - значение для установки изменений
     *                 true: обвести красной рамкой.
     *                 false: стандартные настройки.
     */
    public void setFrameButtonStatusHdd(boolean setFrame) {
        if (setFrame) {
            Platform.runLater(() -> buttonHddStatu.setStyle("-fx-border-color: #ff0000; -fx-border-width: 1px;"));
        } else {
            Platform.runLater(() -> buttonHddStatu.setStyle(""));
        }
    }

    /**
     * Обводит кнопку "StatusHealth" красной рамкой.
     *
     * @param setFrame - значение для установки изменений
     *                 true: обвести красной рамкой.
     *                 false: стандартные настройки.
     */
    public void setFrameButtonStatusHealth(boolean setFrame) {
        if (setFrame) {
            Platform.runLater(() -> healthButton.setStyle("-fx-border-color: #ff0000; -fx-border-width: 1px;"));
        } else {
            Platform.runLater(() -> healthButton.setStyle(""));
        }
    }

    /**
     * Делает кнопку заблокированной для нажатий.
     *
     * @param setDisable - значение для установки изменений
     *                   true: заблокировать кнопку.
     *                   false: разблокировать кнопку.
     */
    public void setHealthButtonDisable(boolean setDisable) {
        Platform.runLater(() -> healthButton.setDisable(setDisable));
    }

    private void buttonSetVisible(boolean setVisible, Button... buttons) {
        for (Button b : buttons) {
            b.setVisible(setVisible);
        }
    }

    public void openVncShopAction(ActionEvent actionEvent) {
        context.getBean(VncVRButton.class)
                .windowActionButton();
    }

    public void rebootVRAction(ActionEvent actionEvent) {
        context.getBean(RebootButton.class)
                .windowActionButton();
    }

    public void closeAppAction(ActionEvent actionEvent) {
        context.getBean(CloseProgram.class)
                .windowActionButton();
    }

    public void openWindowSshAction(ActionEvent actionEvent) {
        context.getBean(WindowSshVRButton.class)
                .windowActionButton();
    }

    public void openWindowPing(ActionEvent actionEvent) {
        context.getBean(WindowPingIpButton.class)
                .windowActionButton();
    }

    public void copyPasswordAction(ActionEvent actionEvent) {
        String passwordSsh = context.getBean(SshMainConnectionService.class).getPasswordSsh();
        Shop.setClipboard(passwordSsh);
    }

    public void buttonScriptHddAction(ActionEvent actionEvent) {
        context.getBean(ScriptTrassirHddButton.class)
                .windowActionButton();
    }

    public void mailLicenseAction(ActionEvent actionEvent) {
        context.getBean(MessageLicenseButton.class)
                .windowActionButton();
    }

    public void viewProcess(ActionEvent actionEvent) {
        context.getBean(WindowProcessesButton.class)
                .windowActionButton();
    }

    public void openWK(ActionEvent actionEvent) {
        context.getBean(WindowConnectWKButton.class)
                .windowActionButton();
    }

    public void hddStatusAction(ActionEvent actionEvent) {
        context.getBean(StatusHddButton.class)
                .windowActionButton();
    }

    public void plusIpPressed(MouseEvent mouseEvent) {
        iconPlus.setOpacity(1);
        Shop.setClipboard(shop.getIpVr());
    }

    public void plusIpReleased(MouseEvent mouseEvent) {
        iconPlus.setOpacity(0.7);
    }

    public void reconnectShop(ActionEvent actionEvent) {
        shop.reconnected();
    }

    @Override
    public void setWindowApp() {
    }

    public void healthButtonAction(ActionEvent actionEvent) {
        context.getBean(HealthButton.class)
                .windowActionButton();
    }

    public void clickTemperature(MouseEvent mouseEvent) {
        context.getBean(TemperatureTrassirButton.class)
                .windowActionButton();
    }
}