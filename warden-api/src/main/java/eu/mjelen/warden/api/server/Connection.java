package eu.mjelen.warden.api.server;

import java.io.InputStream;

public interface Connection {

    boolean connect(String user, String host, String password);
    boolean connect(String user, String host);
    boolean connect(String user);
    boolean connect();

    void disconnect();

    String getHost();
    String getUser();

    ExecResult exec(String command);
    ExecResult sudo(String command);

    boolean upload(String file, String content);
    boolean upload(String file, InputStream content);

    String download(String file);

    boolean forwardPort(int local, int remote);

}
