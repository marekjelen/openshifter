package eu.mjelen.openshifter.api;

public class Nodes {

    private Long count = 0L;
    private Boolean infra = false;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Boolean getInfra() {
        return infra;
    }

    public void setInfra(Boolean infra) {
        this.infra = infra;
    }

}
