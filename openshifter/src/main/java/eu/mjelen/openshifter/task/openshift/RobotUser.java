package eu.mjelen.openshifter.task.openshift;

import eu.mjelen.openshifter.clients.OpenShifterKubernetesClient;
import eu.mjelen.openshifter.clients.OpenShifterOpenShiftClient;
import eu.mjelen.openshifter.task.installer.InstallerTask;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.Context;
import eu.mjelen.warden.api.annotation.Dependency;
import eu.mjelen.warden.api.annotation.Target;
import eu.mjelen.warden.api.annotation.Task;
import eu.mjelen.warden.api.server.Connection;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.openshift.client.OpenShiftClient;

import javax.inject.Inject;

@Task
@Target("master")
@Dependency(InstallerTask.class)
public class RobotUser {

    @Inject
    private Context context;

    @Inject
    private Connection remote;

    @Inject
    private Cluster cluster;

    public void execute() {
        this.remote.exec("/usr/local/bin/oc create serviceaccount robot --namespace=default");

        this.remote.exec("/usr/local/bin/oc adm policy analyze-cluster-role-to-user cluster-admin " +
                "system:serviceaccount:default:robot");

        String token = this.remote.exec("/usr/local/bin/oc sa get-token robot --namespace=default").getStdout().trim();

        this.context.setInjection(OpenShiftClient.class, new OpenShifterOpenShiftClient(this.cluster, token).getClient());
        this.context.setInjection(KubernetesClient.class, new OpenShifterKubernetesClient(this.cluster, token).getClient());
    }

}
