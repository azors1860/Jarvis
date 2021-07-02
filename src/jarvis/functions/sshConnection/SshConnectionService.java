package jarvis.functions.sshConnection;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import jarvis.functions.TextFile;
import jarvis.functions.processes.EnumProcesses;
import jarvis.functions.processes.ProcessModel;
import jarvis.functions.processes.ProcessesService;
import jarvis.main.Main;
import jarvis.main.PathProgram;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author Sergey Chuvashov
 * <p>
 * Класс предназчен для создания подключения к серверам по SSH, отправки команд, просмотра ответов.
 */

@Log
public class SshConnectionService {

    private Session session;
    protected Channel channel;
    private PrintStream commander;
    private ByteArrayOutputStream streamAnswer;
    @Setter
    private String command = "";

    private String host;
    private String user;
    private String pass;
    private int timeOn;
    private ProcessesService processesService;
    private ProcessModel processModel;
    private Thread thisThread;

    public SshConnectionService() {
        initThread();
    }

    public SshConnectionService(String host, String sap, String user, String pass, EnumProcesses operation) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        processesService = Main.getContext().getBean(ProcessesService.class);
        processModel = new ProcessModel(sap, 0, operation);
        initThread();
    }

    protected void initThread() {
        thisThread = new Thread(this::runThread);
        thisThread.setDaemon(true);
    }

    public void runThread() {
        if (processesService.isStreamExists(processModel)) {
            log.warning("Thread already exists");
        } else {
            processesService.addProcessModel(processModel);
            log.info(Thread.currentThread().getName() + ": run. SSH - child thread");
            connectionMonitoring(host, pass, user);
            processModel.setStatus(1);
            disconnect();
            log.info(Thread.currentThread().getName() + ": stop. SSH - child thread");
        }
    }

    public void connection() {
        thisThread.start();
    }

    public void setMaxTimeOnSec(int timeOn) {
        this.timeOn = timeOn;
    }

    public String getAnswer() {
        if (streamAnswer==null){
            return null; //TODO
        }
        return streamAnswer.toString();
    }

    /**
     * Отправляет спать текущий поток. Инкрементирует и возвращает число.
     */
    private int sleepThisThreadOneSecAndIncrementCount(int count) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ++count;
    }

    /**
     * Производит подключение к серверу по протоколу SSH.
     *
     * @param host - ip сервера.
     * @param pass - пароль для авторизации на сервере.
     * @param user - логин ждя авторизации на сервере.
     * @throws ConnectionSshException - В случае, если возникли ошибки в ходе подключения и подключиться не удалось.
     */
    protected void connect(String host, String pass, String user) throws ConnectionSshException {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, 22);
            session.setPassword(pass);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = session.openChannel("shell");
            OutputStream inputStreamForTheChannel = null;

            inputStreamForTheChannel = channel.getOutputStream();
            commander = new PrintStream(inputStreamForTheChannel, true);
            streamAnswer = new ByteArrayOutputStream();
            channel.setOutputStream(streamAnswer);
            channel.connect();
            log.info("CONNECTED: " + host + ":" + user + ":" + pass);
        } catch (JSchException | IOException e) {
            throw new ConnectionSshException(host + ":" + user + " not connected", e);
        }
    }

    /**
     * В цикле проверяет: таймер, активно ли подключение, вызывает метод отправки команды на сервер.
     * <p>
     * Таймер - в случае, если время по таймеру вышло - соединение закрывается.
     * При этом, если время для таймера не задано т.е. = 0 - в этом случае таймер не будет иметь значения.
     *
     * @param host - ip сервера.
     * @param pass - пароль для авторизации на сервере.
     * @param user - логин ждя авторизации на сервере.
     * @return true - в случае, если подключение выполнено и завершено;
     * false - в сдучае, если в ходе подключения произощли ошибки.
     */
    protected boolean connectionMonitoring(String host, String pass, String user) {
        try {
            connect(host, pass, user);
            int countTime = 0;
            while (true) {
                countTime = sleepThisThreadOneSecAndIncrementCount(countTime);
                if ((timeOn != 0) && (countTime >= timeOn)) {
                    break;
                }
                sendCommand();
                if (!channel.isConnected()) {
                    break;
                }
            }
            return true;

        } catch (ConnectionSshException e) {
            if (processModel != null) {
                processModel.setStatus(-1);
            }
            log.warning(e.getMessage());
            return false;
        }
    }

    /**
     * Отправить команду на сервер.
     */
    protected void sendCommand() {
        if (!command.equals("")) {
            commander.println(command);
            command = "";
        }
    }

    /**
     * Вызывает необходимые методы для завершения работы с потоком.
     */
    public void disconnect() {
        saveLogFile();
        close();
    }

    /**
     * Закрывает потоки и соединения.
     */
    protected void close() {
        try {
            if (streamAnswer != null) {
                streamAnswer.close();
            }
            if (commander != null) {
                commander.close();
            }

            if ((channel != null) && (channel.isConnected())) {
                channel.disconnect();
            }
            if ((session != null) && (session.isConnected())) {
                session.disconnect();
            }
            log.info("fun close: " + thisThread.getName());
            thisThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Сохраняет ответ от сервера в отдельный файл.
     */
    private void saveLogFile() {
        if (!Files.exists(Paths.get(PathProgram.tmp))) {
            new File(PathProgram.tmp).mkdirs();
        }
        if (processModel.getFileLog() == null) {
            File fileLog = new File("tmp/" + this.hashCode() + ".txt");
            TextFile textFile = new TextFile(fileLog.toPath().toString());
            textFile.WriteFile(getAnswer());
            processModel.setFileLog(fileLog);
        }
    }
}


