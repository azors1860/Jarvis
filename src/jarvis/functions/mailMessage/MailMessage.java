package jarvis.functions.mailMessage;

import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @Author Sergey Chuvashov
 *
 * Класс предназначен для формирования письма.
 */

@Setter
@Component
public class MailMessage {

    private String to = "";
    private String cc = "";
    private String subject = "";
    private String body = "";

    /**
     * Сформировать и открыть письмо.
     */
    public void create() {
        String newRecipient;
        try {
            newRecipient = transformation(to);
            String newCC = "&cc=" + transformation(cc);
            String newTopic = "&subject=" + transformation(subject);
            String newBody = "&body=" + transformation(body);
            Desktop desktop = Desktop.getDesktop();
            URI u = new URI("mailto:" + newRecipient + newCC + newTopic + newBody);
            desktop.mail(u);
            System.out.println(u);
            Thread.sleep(5 * 1000);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Переводит текст в кодировку UTF8 и подготовливает текст в читаемое состояние.
     *
     * @param text - текст который необходимо перевести.
     * @return - закодированный текст
     */
    private static String transformation(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, StandardCharsets.UTF_8.toString()).replace("+", "%20")
                .replace("%0D%0D%0A", "%0D%0A");
    }
}
