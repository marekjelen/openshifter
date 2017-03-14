package eu.mjelen.warden.api.server;

public interface ExecResult {

    boolean success();

    String getCommand();

    String getStdout();
    String getStderr();

    int getCode();

}
