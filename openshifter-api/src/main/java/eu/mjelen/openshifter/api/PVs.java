package eu.mjelen.openshifter.api;

public class PVs {

    private String type = "host";
    private Integer count = 0;
    private Long size;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PVs{" +
                "count='" + count + '\'' +
                ", size=" + size +
                '}';
    }
}
