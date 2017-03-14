package eu.mjelen.openshifter.provider.linode;

import eu.mjelen.warden.api.cluster.Instance;
import org.slf4j.LoggerFactory;
import synapticloop.linode.LinodeApi;
import synapticloop.linode.api.response.LinodeConfigListResponse;
import synapticloop.linode.api.response.LinodeConfigResponse;
import synapticloop.linode.api.response.LinodeJobResponse;
import synapticloop.linode.api.response.LinodeResponse;
import synapticloop.linode.api.response.bean.*;
import synapticloop.linode.exception.ApiException;

import java.util.*;

public class LinodeInstance implements Instance {

    private final LinodeApi client;
    private final String datacenter;
    private final Long plan;
    private final String name;
    private final List<String> tags;
    private String id;
    private Linode linode;
    private String address;
    private String internalAddress;

    public LinodeInstance(LinodeApi client, String name, String datacenter, Long plan) {
        this(client, name, new LinkedList<>(), datacenter, plan);
    }

    public LinodeInstance(LinodeApi client, String name, List<String> tags, String datacenter, Long plan) {
        this.client = client;
        this.name = name;
        this.tags = tags;
        this.datacenter = datacenter;
        this.plan = plan;

        makeId();
    }

    private void makeId() {
        this.id = tags.stream().reduce(name, (tmp, tag) -> tmp + ";" + tag);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
        makeId();
    }

    @Override
    public boolean exists() {
        return this.linode != null;
    }

    @Override
    public void load() {
        try {
            Optional<Linode> linode = this.client.getLinodeList().getLinodes().stream().filter(item -> {
                try {
                    LinodeConfigListResponse config = this.client.getLinodeConfigList(item.getLinodeId());
                    return config.getConfigs().get(0).getLabel().equals(this.id);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                return false;
            }).findFirst();

            this.linode = linode.orElse(null);

            if(this.linode != null) {
                this.client.getLinodeIpList().getIpAddresses().forEach(item -> {
                    if(Objects.equals(item.getLinodeId(), this.linode.getLinodeId())) {
                        if(item.getIsPublic()) {
                            this.address = item.getIpAddress();
                        } else {
                            this.internalAddress = item.getIpAddress();
                        }
                    }
                });
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create() {
        if(this.exists()) return;
        try {
            Datacenter datacenter = this.client.getAvailDatacenters()
                    .getDatacenterByAbbreviation(this.datacenter);

            LinodePlan plan = this.client.getAvailLinodePlans().getLinodePlans().stream().filter(item -> {
                return Objects.equals(item.getRam(), this.plan);
            }).findFirst().get();

            Distribution distro = this.client.getAvailDistributions().getDistributions().stream().filter(item -> {
                return item.getLabel().equals("CentOS 7");
            }).findFirst().get();

            Kernel kernel = this.client.getAvailKernels().getKernels().stream().filter(item -> {
                if (item.getLabel().startsWith("Latest 64 bit")) return true;
                return false;
            }).findFirst().get();

            Long id = this.client.getLinodeCreate(datacenter.getDatacenterId(), plan.getPlanId()).getLinodeId();

            Long diskId = this.client.getLinodeDiskCreateFromDistribution(id, distro.getDistributionId(),
                    this.name + "-root",
                    5 * 1024L,
                    "admin007").getDiskId();

            Long swapDiskId = this.client.getLinodeDiskCreate(id,
                    this.name + "-docker", "ext4",
                    (plan.getDiskSize() - 5) * 1024L).getDiskId();

            LinodeConfigResponse create = this.client.getLinodeConfigCreate(id, kernel.getKernelId(),
                    this.id,
                    Long.toString(diskId) + "," + Long.toString(swapDiskId));

            LinodeJobResponse boot = this.client.getLinodeBoot(id, create.getConfigId());

            load();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        if(!this.exists()) return;
        try {
            this.client.getLinodeShutdown(this.linode.getLinodeId());
            Config config = this.client.getLinodeConfigList(this.linode.getLinodeId()).getConfigs().get(0);
            for (Long disk : config.getDiskIds()) {
                this.client.getLinodeDiskDelete(this.linode.getLinodeId(), disk);
            }
            LinodeResponse res = this.client.getLinodeDelete(this.linode.getLinodeId());
            while (res.hasErrors()) {
                res = this.client.getLinodeDelete(this.linode.getLinodeId());
                synchronized (this) {
                    try {
                        LoggerFactory.getLogger(getClass()).info("Waiting for components destruction");
                        wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public String getInternalAddress() {
        return this.internalAddress;
    }

    @Override
    public List<String> getTags() {
        return this.tags;
    }

    @Override
    public String getPassword() {
        return "admin007";
    }

}
