package jarvis.functions.openWindow;

import jarvis.baseController.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jarvis.main.Main;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.IOException;

/**
 * @Author Sergey Chuvashov
 *
 * Класс предназначен для открытия окон с задаными параметрами.
 */

@AllArgsConstructor
@Builder
public class OpenWindow {

    private final String path;
    private final boolean windowMoves;
    private final boolean hidePanelWindow;
    private final boolean alwaysOnTop;
    private final boolean resizableWindow;

    private static double xOffset = 0;
    private static double yOffset = 0;

    /**
     * Открывает окно с заданными параметрами.
     */
    public void open() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(path));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();

            if (windowMoves) {
                page.setOnMousePressed(event -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                page.setOnMouseDragged(event -> {
                    dialogStage.setX(event.getScreenX() - xOffset);
                    dialogStage.setY(event.getScreenY() - yOffset);
                });
            }

            if (hidePanelWindow) {
                dialogStage.initStyle(StageStyle.UNDECORATED);
            }

            Stage currentStage = BaseController.getBaseController().getStage();
            dialogStage.setX(currentStage.getX());
            if (alwaysOnTop) {
                dialogStage.setAlwaysOnTop(true);
                dialogStage.setY(currentStage.getY());
            } else {
                dialogStage.setY(currentStage.getY() + currentStage.getHeight());
            }

            if (!resizableWindow) {
                dialogStage.setResizable(false);
            }

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(page));
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
