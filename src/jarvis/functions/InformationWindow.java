package jarvis.functions;

import jarvis.BasicButton;
import jarvis.functions.openWindow.OpenWindow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.swing.*;

public class InformationWindow {

    public Label lable;
    private static BasicButton basicButton;
    private static String text;

    public void yesAction(ActionEvent actionEvent) {
        noAction(actionEvent);
        Platform.runLater(() ->  basicButton.action());
        basicButton.setWindowItsAction(false);
    }

    public void noAction(ActionEvent actionEvent) {
        Stage stage = (Stage) lable.getScene().getWindow();
        stage.close();
    }

    /**
     * Вывести сообщение на экран
     *
     * @param typeMessage
     * 1 = Ошибка
     * 2 = Информационное сообщение
     * 3 = предупреждение
     * @param title - Заголовок сообщения.
     * @param message - Текст сообщения.
     */
    public static void sendInformationMessage(int typeMessage, String title, String message){
        JOptionPane.showMessageDialog(null, message, title, typeMessage);
    }

    public static void openWindowYesOrNo(BasicButton button) {
        basicButton = button;
        OpenWindow.builder()
                .path("/jarvis/main/fxml/YesNo.fxml")
                .alwaysOnTop(true)
                .resizableWindow(false)
                .build().open();
    }

    public static void openWindowYesOrNo(BasicButton button, String textMessage) {
        text = textMessage;
        openWindowYesOrNo(button);
    }
    public void initialize(){
        if (text != null){
            lable.setText(text);
        }
        text = null;
    }
}
