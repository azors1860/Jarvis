package jarvis.functions.processes;

import jarvis.WindowApp;
import jarvis.main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.IOException;

/**
 * @Author Sergey Chuvashov
 */
public class ProcessesController extends WindowApp {

    public ListView<ProcessModel> listView;
    public AnchorPane anchorPane;
    public Button button;
    public Button buttonLog;
    public Button buttonDelete;
    private ProcessModel thisModel;
    private boolean threadIsRunning = false;
    private final ProcessesService service = Main.getContext().getBean(ProcessesService.class);


    public void initialize() {
        action(null);
        super.initialize();
    }

    /**
     * Действие при нажатии на кнопку обновить. Обновляет актуальный список.
     * В случае выбора элемента - делает кнопки доступными, либо недоступными, в зависимости от условий.
     */
    public void action(ActionEvent actionEvent) {
        if (!threadIsRunning) {
            threadIsRunning = true;
            listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                thisModel = newValue;
                if (thisModel.getFileLog()!=null){
                    buttonDelete.setDisable(false);
                    buttonLog.setDisable(false);
                }else {
                    buttonLog.setDisable(true);
                    buttonDelete.setDisable(true);
                }
            });
        }
        ObservableList<ProcessModel> processes = FXCollections.observableArrayList(service.getModels());
        listView.setItems(null);
        listView.setItems(processes);
    }

    /**
     * Действие при нажатии на кнопку удалить всё. Удаляет список всех выполненных процессов.
     */
    public void clearAll(ActionEvent actionEvent) {
        service.clearList();
        action(actionEvent);
    }

    /**
     * Открывает лог файл выполненного процесса.
     */
    public void log(ActionEvent actionEvent) {

        if ((thisModel != null) &&
                (thisModel.getFileLog() != null)) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(thisModel.getFileLog());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Удалить текущий (выбранный) процесс.
     */
    public void clearThis(ActionEvent actionEvent) {
        service.removeElement(thisModel);
        action(actionEvent);
    }

    @Override
    public void setWindowApp() {
        WindowProcessesButton windowProcessesButton = Main.getContext().getBean(WindowProcessesButton.class);
        windowProcessesButton.setWindowApp(this);
    }
}
