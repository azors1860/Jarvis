package jarvis.functions.processes;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Objects;

/**
 * @Author Sergey Chuvashov
 */

@Setter
@Getter
public class ProcessModel {
   private String sap;
   private int status;
   private EnumProcesses operation;
   private File fileLog;

    public ProcessModel(String sap, int status, EnumProcesses operation) {
        this.sap = sap;
        this.status = status;
        this.operation = operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessModel that = (ProcessModel) o;
        return status == that.status && Objects.equals(sap, that.sap) && operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sap, status, operation);
    }

    @Override
    public String toString() {
        if (status == 0) {
            return sap + ":" + operation + ": в работе";
        } else if (status == 1) {
            return sap + ":" + operation + ": завершено";
        } else {
            return sap + ":" + operation + ": ошибка";
        }
    }
}
