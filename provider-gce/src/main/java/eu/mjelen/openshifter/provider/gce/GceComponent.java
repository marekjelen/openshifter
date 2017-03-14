package eu.mjelen.openshifter.provider.gce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.dns.Dns;
import eu.mjelen.warden.api.Component;
import eu.mjelen.openshifter.provider.gce.components.Project;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class GceComponent implements Component {

    private Project project;
    private String url;

    @JsonIgnore
    private GceClient client;

    private List<GceComponent> dependencies = new LinkedList<>();

    private Boolean exists = null;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addDependency(GceComponent component) {
        this.dependencies.add(component);
    }

    public GceComponent setClient(GceClient client) {
        this.client = client;
        return this;
    }

    public GceClient getClient() {
        return client;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public Compute compute() {
        return this.getClient().getCompute();
    }

    public Dns dns() {
        return this.getClient().getDns();
    }

    @Override
    public boolean exists() {
        return this.getExists() == null ? false : this.getExists();
    }

    @Override
    public void load() {
        LoggerFactory.getLogger(getClass()).info("Loading state from GCE");
        try {
            waitFor(doLoad());
            setExists(true);
        } catch (GoogleJsonResponseException e) {
            if(e.getDetails().getCode() == 404) {
                setExists(false);
            } else {
                LoggerFactory.getLogger(getClass()).error("Error loading state from GCE", e);
            }
        } catch (NotFoundException e) {
            setExists(false);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Error communication with GCE", e);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Problem encountered", e);
        }
        LoggerFactory.getLogger(getClass()).info("Finished loading state from GCE");
    }

    @Override
    public void create() {
        if(exists()) return;
        LoggerFactory.getLogger(getClass()).info("Creating component on GCE");
        try {
            waitFor(doCreate());
            while(!exists()) {
                LoggerFactory.getLogger(getClass()).info("Waiting");
                synchronized (this) {
                    try {
                        this.wait(5000);
                    } catch (Exception e) {

                    }
                }
                load();
            }
        } catch (GoogleJsonResponseException e) {
            if(e.getDetails().getCode() != 404) {
                LoggerFactory.getLogger(getClass()).error("Error creating component on GCE", e);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Error communication with GCE", e);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Problem encountered", e);
        }
        LoggerFactory.getLogger(getClass()).info("Finished creating component GCE");
    }

    @Override
    public void destroy() {
        if(!exists()) return;
        LoggerFactory.getLogger(getClass()).info("Destroying component on GCE");
        try {
            waitFor(doDestroy());
            while(exists()) {
                LoggerFactory.getLogger(getClass()).info("Waiting");
                synchronized (this) {
                    try {
                        this.wait(5000);
                    } catch (Exception e) {

                    }
                }
                load();
            }
        } catch (GoogleJsonResponseException e) {
            if(e.getDetails().getCode() != 404) {
                LoggerFactory.getLogger(getClass()).error("Error destroying component on GCE", e);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Error communication with GCE", e);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Problem encountered", e);
        }
        LoggerFactory.getLogger(getClass()).info("Finished destroying component GCE");
    }

    public void waitFor(Operation operation) {
        if(operation == null) return;
        while(true) {
            Operation nop;
            try {
                if(operation.getZone() != null) {
                    String[] bits = operation.getZone().split("/");
                    nop = compute().zoneOperations().get(getProject().getName(), bits[bits.length - 1], operation.getName()).execute();
                } else if(operation.getRegion() != null) {
                    String[] bits = operation.getRegion().split("/");
                    nop = compute().regionOperations().get(getProject().getName(), bits[bits.length - 1], operation.getName()).execute();
                } else {
                    nop = compute().globalOperations().get(this.getProject().getName(), operation.getName()).execute();
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(getClass()).error("Error waiting for operation to complete", e);
                break;
            }
            if(nop != null) {
                if(nop.getStatus().equals("DONE")) {
                    break;
                } else {
                    LoggerFactory.getLogger(getClass()).info("Waiting for operation");
                    synchronized (this) {
                        try {
                            wait(5000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            }
        }
    }

    public abstract Operation doLoad() throws Exception;
    public abstract Operation doCreate() throws Exception;
    public abstract Operation doDestroy() throws Exception;

}
