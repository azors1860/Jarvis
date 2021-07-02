package jarvis.functions.setting.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingAppUser {
    @JsonProperty("path img")
    private String pathImg;
    @JsonProperty("command")
    private String command;
}
