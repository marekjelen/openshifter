package eu.mjelen.warden.api.cluster.map;

public class SshKey {

    private String username;
    private String type;
    private String key;
    private String comment;

    public SshKey() {
    }

    public SshKey(String key) {
        String[] segments = key.split(" ");

        for(String segment : segments) {
            detect(segment.trim());
        }
    }

    private void detect(String segment) {
        if(segment.startsWith("ssh-")) {
            setType(segment);
        } else if(segment.startsWith("AAAA")) {
            setKey(segment);
        } else {
            setComment(segment);
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getComment() {
        return comment;
    }
}
