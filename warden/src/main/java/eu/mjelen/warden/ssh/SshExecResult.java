package eu.mjelen.warden.ssh;

import eu.mjelen.warden.api.server.ExecResult;

public class SshExecResult implements ExecResult {

    private String command;
    private String stdout;
    private String stderr;

    private int code;

    public SshExecResult(String command, String stdout, String stderr, int code) {
        this.command = command;
        this.stdout = stdout;
        this.stderr = stderr;
        this.code = code;
    }

    @Override
    public boolean success() {
        return this.code == 0;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    @Override
    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
