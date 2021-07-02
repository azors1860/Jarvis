package jarvis.functions.vnc.connectWk;

import jarvis.WindowApp;
import jarvis.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Sergey Chuvashov
 */
public class ConnectWkController extends WindowApp {

    public ComboBox<String> list;
    public Button button;
    public AnchorPane anchorPane;
    private final ConnectWkService service = Main.getContext().getBean(ConnectWkService.class);

    /**
     * Действие при выборе wk из списка.
     * Открывает окно vnc и закрывает текущее окно.
     */
    public void action(ActionEvent actionEvent) {
        if (list.getValue() != null){
            service.openVnc(list.getValue());
        }
        closeWindow();
    }

    public void initialize() {

        super.initialize();

        HashMap<String, String> ports = service.getPortAndWk();
        List<String> arrayList = new ArrayList<>();

        for (Map.Entry<String, String> pair : ports.entrySet()) {
            arrayList.add(pair.getKey() + "");
        }

        ObservableList<String> langs = FXCollections.observableArrayList(arrayList);
        list.setItems(langs);
    }

    @Override
    public void setWindowApp() {
        WindowConnectWKButton windowConnectWKButton = Main.getContext().getBean(WindowConnectWKButton.class);
        windowConnectWKButton.setWindowApp(this);
    }
}
