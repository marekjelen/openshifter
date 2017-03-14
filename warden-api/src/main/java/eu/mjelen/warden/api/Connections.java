package eu.mjelen.warden.api;

import eu.mjelen.warden.api.server.Connection;

import java.util.List;

public interface Connections {

    List<Connection> getConnections(String labels);

}
