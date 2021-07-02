package jarvis.functions;

import java.io.*;

/**
 * @Author Sergey Chuvashov
 * <p>
 * Класс для работы с текстовыми файлами. Позволяет считывать и записывать текст.
 */

public class TextFile {

    public TextFile(String path) {
        this.path = path;
    }

    private final String path;


    public String readFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder result = new StringBuilder();
            while (reader.ready()) {
                result.append(reader.readLine()).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            InformationWindow.sendInformationMessage(3, "Ошибка загрузки файла",
                    "Не удалось прочитать файл: " + path);
            return null;
        }
    }

    public void WriteFile(String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
