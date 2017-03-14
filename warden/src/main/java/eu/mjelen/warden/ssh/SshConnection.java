package eu.mjelen.warden.ssh;

import com.jcraft.jsch.*;
import eu.mjelen.warden.api.server.Connection;
import org.slf4j.*;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;

public class SshConnection implements Connection, UserInfo {

    private final JSch jsch;

    private String user;
    private String host;
    private String password;
    private Session session;
    private Logger logger;

    public SshConnection() {
        this.jsch = new JSch();
        try {
            this.jsch.addIdentity(Paths.get("private.key").toAbsolutePath().toFile().toString());
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public boolean connect(String user, String host) {
        return connect(user, host, null);
    }

    @Override
    public boolean connect(String user, String host, String password) {
        this.logger = LoggerFactory.getLogger("SSH [" + host + "]");
        this.user = user;
        this.host = host;
        this.password = password;
        while(true) {
            try {
                this.logger.info("Connecting to server as {}", this.user);
                this.session = this.jsch.getSession(this.user, this.host, 22);
                if(this.password != null) {
                    this.session.setPassword(this.password);
                } else {
                    this.session.setUserInfo(this);
                }
                this.session.setConfig("StrictHostKeyChecking", "no");
                this.logger.info("Opening connection");
                this.session.connect(10000);
                this.logger.info("Connection open");
                return true;
            } catch (JSchException e) {
                if (e.getMessage().startsWith("timeout")) {
                    this.logger.info("Waiting for connection");
                    synchronized (this) {
                        try {
                            this.wait(10000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    e.printStackTrace();
                    return false;
                }
            }
        }
    }

    @Override
    public boolean connect(String user) {
        return connect(user, this.host);
    }

    @Override
    public boolean connect() {
        return connect(this.user, this.host);
    }

    @Override
    public SshExecResult sudo(String command) {
        command = "sudo sh -c '" + command.replace("'", "\\'") + "';";
        return exec(command);
    }

    @Override
    public SshExecResult exec(String command) {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        try {
            this.logger.info("Executing command on server: {} ", command);
            ChannelExec channel = (ChannelExec) this.session.openChannel("exec");

            channel.setPty(true);
            channel.setPtyType("VT100");
            channel.setCommand(command);
            channel.setOutputStream(stdout);
            channel.setErrStream(stderr);

            this.logger.info("... executing!");
            channel.connect();

            while(true) {
                if(channel.isClosed()) break;
                this.logger.info("... waiting for command to finish!");
                synchronized (this) {try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}}
            }

            if(channel.getExitStatus() == 0) {
                this.logger.info("Execution completed successfully");
            } else {
                this.logger.info("Execution completed with error");
            }

            return new SshExecResult(command, stdout.toString(), stderr.toString(), channel.getExitStatus());
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return new SshExecResult(command, stdout.toString(), stderr.toString(), -1);
    }

    @Override
    public boolean upload(String file, String content) {
        return upload(file, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public boolean upload(String file, InputStream content) {

        try {
            this.logger.info("Uploading file: {}", file);
            ChannelSftp channel = (ChannelSftp) this.session.openChannel("sftp");
            channel.connect();
            channel.put(content, file);
            channel.disconnect();
            this.logger.info("Uploading completed: {}", file);
            return true;
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String download(String file) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            this.logger.info("Downloading file: {}", file);
            ChannelSftp channel = (ChannelSftp) this.session.openChannel("sftp");
            channel.connect();
            channel.get(file, out);
            channel.disconnect();
            return out.toString();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean forwardPort(int local, int remote) {
        try {
            this.session.setPortForwardingL(local, "localhost", remote);
            return true;
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return false;
    }

    @Override
    public boolean promptYesNo(String s) {
        return false;
    }

    @Override
    public void showMessage(String s) {
        this.logger.info(s);
    }

    public void disconnect() {
        this.session.disconnect();
    }

}
