package jarvis.functions.sshConnection.mainConnection;

import jarvis.WindowApp;
import jarvis.main.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * @Author Sergey Chuvashov
 */
public class SshMainViewController extends WindowApp {

    public TextArea showSshCommands;
    public TextArea sendSshCommands;
    public Button sendSshCommandsButton;
    public AnchorPane anchorPane;
    private static SshMainViewController sshMainViewController;
    private final SshMainConnectionService sshConnection = Main.getContext().getBean(SshMainConnectionService.class);


    @Override
    public void initialize() {
        super.initialize();
        sshMainViewController = this;
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
        sshMainViewController = null;
    }

    public static boolean IsNotNull() {
        return sshMainViewController != null;
    }

    /**
     * Команда для добавления текста от сервера, в основное текстовое поле. Данный метод предназначен для сервиса.
     * @param text - текст который будет добавлен.
     */
    public static void setMessage(String text) {
        Platform.runLater(() -> sshMainViewController.showSshCommands.setText(text));
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sshMainViewController.showSshCommands.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Действие при нажатии на кнопку "отправить". Отправляет комманду по SSH.
     * В случае, если отправлена команда ping без параметров, то добавляет параметр -c4
     * для ограничения количества пакетов.
     */
    public void sendSshCommandsButtonAction(ActionEvent actionEvent) {
        String command;
        if (sendSshCommands.getText().matches("ping [0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            command = sendSshCommands.getText() + " -c4";
        } else {
            command = sendSshCommands.getText();
        }
        sshConnection.setCommand(command);
        sendSshCommands.setText("");
    }

    @Override
    public void setWindowApp() {
        WindowSshVRButton sshVRButton = Main.getContext().getBean(WindowSshVRButton.class);
        sshVRButton.setWindowApp(this);
    }
}
