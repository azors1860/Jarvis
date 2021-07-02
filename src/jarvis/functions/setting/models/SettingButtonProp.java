package jarvis.functions.setting.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingButtonProp {
    @JsonProperty("class name")
    private String className;
    @JsonProperty("confirm action button")
    private boolean confirmActionButton;
    @JsonProperty("auto close")
    private boolean autoClose;
}
