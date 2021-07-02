package jarvis.functions.processes;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Sergey Chuvashov
 */
@Service
public class ProcessesService {

    @Getter
    private final List<ProcessModel> models = new ArrayList<>();

    public void clearList() {
        models.removeIf(processModel -> processModel.getStatus() == 1);
    }

    public void addProcessModel(ProcessModel model) {
      if (!isStreamExists(model)){
          models.add(model);
      }
    }

    public void removeElement(ProcessModel s) {
        models.remove(s);
    }

    public boolean isStreamExists(ProcessModel model) {
        return models.contains(model);
    }
}
