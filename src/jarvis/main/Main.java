package jarvis.main;

import jarvis.functions.setting.Setting;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

@ComponentScan(value = "jarvis")
public class Main extends Application {

    private static AnnotationConfigApplicationContext mainContext;

    public static AnnotationConfigApplicationContext getContext() {
        return mainContext;
    }

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static int height = 129;
    private static int width = 181;

    public static int getHeight() {
        return height;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        height = getSizeHeight(129, 5, 30);
        setSettingEncoding();

        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("fxml/sample.fxml"));

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        primaryStage.setTitle("cash");
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
    }

    private int getSizeHeight(int basicSize, int numberOfButtonsInRow, int heightHBox) {
        Setting setting = getContext().getBean(Setting.class);
        int sizeListButton = setting.getButtonsAppUsers().size();
        if (sizeListButton == 0) {
            return basicSize;
        }

        int quantityLineButton = 0;
        if (sizeListButton % numberOfButtonsInRow == 0) {
            quantityLineButton = sizeListButton / numberOfButtonsInRow;
        } else {
            quantityLineButton = (sizeListButton / numberOfButtonsInRow) + 1;
        }
        return basicSize + (quantityLineButton * heightHBox);
    }

    private void setSettingEncoding() throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("file.encoding", "UTF-8");
        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null, null);
    }

    public static void main(String[] args) {
        mainContext = new AnnotationConfigApplicationContext();
        mainContext.scan("jarvis");
        mainContext.refresh();
        launch(Main.class, args);
    }
}
