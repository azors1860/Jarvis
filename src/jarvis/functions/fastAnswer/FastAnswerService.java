package jarvis.functions.fastAnswer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Chuvashov Sergey
 */

@Service
public class FastAnswerService {

    private FastAnswerData data;

    @Autowired
    public void setData(FastAnswerData data) {
        this.data = data;
    }

    public List<String> search(String str) {
        return data.getArrayList().stream()
                .filter(el -> el.toLowerCase().contains(str.toLowerCase()))
                .collect(Collectors.toList());
    }
}
