package jarvis.functions.smart.statusHdd;

import jarvis.WindowApp;
import jarvis.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * @Author SergeyChuvashov
 */

public class StatusHddController extends WindowApp {

    public TextArea textArea;
    public TextField Number;
    public TextField Model;
    public ComboBox<String> list;
    public Label statusText;
    public AnchorPane anchorPane;
    private StatusHddService service;

    public void initialize() {
        super.initialize();
        service = Main.getContext().getBean(StatusHddService.class);
        ObservableList<String> langs = FXCollections.observableArrayList(service.getSerialNHddList());
        list.setItems(langs);
        String okOrError = service.isStatusGoodAllHdd() ? "OK" : "ERROR";
        statusText.setText("Общий статус: " + okOrError);
    }

    /**
     * Действие при выборе HDD из листа (ComboBox) = выводит всю информацию в соответствующие поля.
     */
    public void onHidden(Event event) {
        String serial = list.getValue().split(" ")[1];
        HddModel hdd = service.getHdd(serial);
        textArea.setText(hdd.getText());
        Number.setText(hdd.getSerialNumber());
        Model.setText(hdd.getDeviceModel());
    }

    @Override
    public void setWindowApp() {
        StatusHddButton button = Main.getContext().getBean(StatusHddButton.class);
        button.setWindowApp(this);
    }
}
