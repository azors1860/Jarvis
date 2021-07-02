package jarvis.functions.sshConnection;

/**
 * @Author Sergey Chuvashov
 *
 * Свидетельствует о том, что в ходе подключения по SSH возникли ошибки.
 */
public class ConnectionSshException extends Throwable{
    public ConnectionSshException(String message) {
        super(message);
    }

    public ConnectionSshException(Throwable cause) {
        super(cause);
    }

    public ConnectionSshException(String message, Throwable cause) {
        super(message, cause);
    }

}
