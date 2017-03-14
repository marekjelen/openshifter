package eu.mjelen.warden.api.cluster;

import eu.mjelen.warden.api.Component;

import java.util.List;

public interface Instance extends Component {

    String getAddress();

    String getInternalAddress();

    List<String> getTags();

    default String getPassword() { return null; }

    default String getUsername() { return "root"; }

}
