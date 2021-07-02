package jarvis.functions;

import jarvis.BasicButton;
import javafx.application.Platform;
import jarvis.main.PathProgram;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @Author Sergey Chuvashov
 */

@Component
public class CloseProgram extends BasicButton {

    /**
     * Удаляет все временные файлы логов.
     */
    @Override
    public void action() {
        if (Files.exists(Paths.get(PathProgram.tmp))) {
            for (File myFile : Objects.requireNonNull(new File(PathProgram.tmp).listFiles())) {
                if (myFile.isFile()) myFile.delete();
            }
        }
        Platform.exit();
        System.exit(0);
    }
}