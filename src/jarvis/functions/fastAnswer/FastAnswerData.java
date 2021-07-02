package jarvis.functions.fastAnswer;

import jarvis.main.PathProgram;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Component
@Getter
public class FastAnswerData {
    private final ArrayList<String> arrayList = new ArrayList<>();

    @PostConstruct
    private void init() {

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (new FileInputStream(PathProgram.fastAnswer), StandardCharsets.UTF_8))) {

            StringBuilder stringBuilder = new StringBuilder();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.contains("!!!***!!!")) {
                    arrayList.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    continue;
                }
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
