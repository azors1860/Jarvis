package jarvis.functions.ping;

import jarvis.WindowApp;
import jarvis.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.Collection;
import java.util.HashSet;

/**
 * @Author Sergey Chuvashov
 */

public class PingController extends WindowApp {

    public TextField textIp1;
    public Button butPing;
    public ListView<String> listView;
    public AnchorPane anchorPane;
    private final HashSet<String> ipArraySet = new HashSet<>();
    private final PingService pingService = Main.getContext().getBean(PingService.class);

    /**
     * Действие при нажатии на кнопку ping.
     * Отправляет список ip в новом потоке на уровень сервиса, получает список с результатами.
     */
    public void pingAction(ActionEvent actionEvent) {
        if (textIp1.getText().matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}")) {
            ipArraySet.add(textIp1.getText());
        }
        textIp1.setText("processes...");
        listView.setItems(null);
        textIp1.setEditable(false);
        butPing.setDisable(true);

        Thread thread = new Thread(() -> {
            updateValuesListView(pingService.pingIp(ipArraySet));
            textIp1.setText("finish");
        });
        thread.start();
    }

    int countLabels = 0;

    /**
     * Добавдяет ip в список для проверки при нажатии на Enter.
     */
    public void addIp(ActionEvent actionEvent) {

        if ((countLabels < 5) && (textIp1.getText().matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"))) {
            ipArraySet.add(textIp1.getText());
            updateValuesListView(ipArraySet);
            textIp1.setText(textIp1.getText().replaceAll("[0-9]{1,3}$", ""));
            countLabels++;
        } else {
            textIp1.setText("");
        }
    }

    /**
     * Обновляет список в листе ip адресов.
     * @param collection - актуальный список, для обновления.
     */
    private void updateValuesListView(Collection<String> collection) {
        ObservableList<String> processes = FXCollections.observableArrayList(collection);
        listView.setItems(null);
        listView.setItems(processes);
    }

    @Override
    public void setWindowApp() {
        WindowPingIpButton windowPingIpButton = Main.getContext().getBean(WindowPingIpButton.class);
        windowPingIpButton.setWindowApp(this);
    }
}


