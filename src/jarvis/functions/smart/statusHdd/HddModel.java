package jarvis.functions.smart.statusHdd;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Sergey Chuvashov
 */

@Getter
@AllArgsConstructor
public class HddModel {
    private final String deviceModel;
    private final String serialNumber;
    private final String text;
    private final boolean isStatusGood;
}