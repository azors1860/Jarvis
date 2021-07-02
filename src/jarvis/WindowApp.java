package jarvis;

import jarvis.baseController.BaseController;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @Author Sergey Chuvashov
 */

public abstract class WindowApp {
    protected Stage stageWindow;
    public AnchorPane anchorPane;

    /**
     * Присвоить текущее окно конкретной кнопке.
     */
    public abstract void setWindowApp();

    /**
     * Присваивает переменной stageWindow текущее окно.
     * Присвает текущему окну действие при закрытии.
     */
    protected void setStageWindow() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stageWindow = (Stage) anchorPane.getScene().getWindow();
        stageWindow.setOnCloseRequest(windowEvent -> closeWindow());
    }

    /**
     * Закрыть текущее окно
     */
    public void closeWindow(){
        if (stageWindow.isShowing()){
            stageWindow.close();
        }
    }

    /**
     * Поднять текущее окно, в случае, если оно открыто.
     */
    public void windowUp(){
        if (stageWindow.isShowing()){
            Stage currentStage = BaseController.getBaseController().getStage();
            stageWindow.setX(currentStage.getX());
            stageWindow.setY(currentStage.getY() + currentStage.getHeight());
            stageWindow.setAlwaysOnTop(true);
            stageWindow.setAlwaysOnTop(false);
        }
    }

    public void initialize(){
        setWindowApp();
        new Thread(this::setStageWindow).start();
    }

}
