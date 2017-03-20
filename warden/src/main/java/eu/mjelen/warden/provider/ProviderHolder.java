package eu.mjelen.warden.provider;

import eu.mjelen.warden.api.annotation.Provider;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.ClusterProvider;
import eu.mjelen.warden.api.cluster.map.ClusterMap;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ProviderHolder<A> implements ClusterProvider<A> {

    private URLClassLoader loader;
    private ScanResult index;
    private Class<? extends ClusterProvider<A>> providerClass;
    private ClusterProvider<A> provider;

    @SuppressWarnings("unchecked")
    public ProviderHolder(String name) {
        File file = new File("../provider-" + name + ".jar");
        if(!file.exists()) {
            LoggerFactory.getLogger(getClass()).error("ProviderHolder {} does not exist.", name);
            System.exit(1);
        }

        try {
            this.loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ProviderHolder.class.getClassLoader());

            FastClasspathScanner scanner = new FastClasspathScanner();
            scanner.overrideClassLoaders(this.loader);
            this.index = scanner.scan();

            String className = this.index.getNamesOfClassesWithAnnotation(Provider.class).get(0);

            this.providerClass = (Class<? extends ClusterProvider<A>>) this.loader.loadClass(className);
            this.provider = this.providerClass.newInstance();
        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ProviderHolder(ClusterProvider<A> clusterProvider) {
        this.provider = clusterProvider;
    }

    @Override
    public Cluster analyze(A descriptor) {
        return this.provider.analyze(descriptor);
    }
}
