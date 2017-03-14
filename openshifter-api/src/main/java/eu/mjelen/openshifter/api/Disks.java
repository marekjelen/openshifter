package eu.mjelen.openshifter.api;

public class Disks {

    private String image;

    private Long root = 100L;
    private Long docker = 100L;
    private Long pvs = 100L;

    public Long getRoot() {
        return root;
    }

    public void setRoot(Long root) {
        this.root = root;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getDocker() {
        return docker;
    }

    public void setDocker(Long docker) {
        this.docker = docker;
    }

    public Long getPvs() {
        return pvs;
    }

    public void setPvs(Long pvs) {
        this.pvs = pvs;
    }

}
