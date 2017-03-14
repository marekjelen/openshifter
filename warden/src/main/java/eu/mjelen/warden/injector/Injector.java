package eu.mjelen.warden.injector;

import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Injector {

    private Map<Class, Class> providers = new HashMap<>();
    private Map<Class, AnalyzedClass> classes = new HashMap<>();

    public Injector() {
    }

    public <T> void analyze(Class<T> clazz) {
        if(!this.classes.containsKey(clazz)) {
            LoggerFactory.getLogger(getClass()).debug("Analyzing class {}", clazz);
            this.classes.put(clazz, new AnalyzedClass<>(this, clazz));
            analyzeProviders(clazz, this.providers);
        }
    }

    public <T> void bind(Class<T> clazz, T value) {
        this.classes.get(clazz).bind(value);
    }

    public <T> void unbind(Class<T> clazz) {
        this.classes.get(clazz).unbind();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, Class... subtypes) {
        if(clazz == Provider.class) {
            LoggerFactory.getLogger(getClass()).debug("Producing provider {}", subtypes);
            Class subtype = subtypes[0];
            Class provider = this.providers.get(subtype);
            return (T) this.classes.get(provider).get();
        } else {
            LoggerFactory.getLogger(getClass()).debug("Producing {}", clazz);
            if(this.providers.containsKey(clazz)) {
                clazz = this.providers.get(clazz);
                LoggerFactory.getLogger(getClass()).debug("Well know provider is {}", clazz);
                return ((Provider<T>) this.classes.get(clazz).get()).get();
            }
            return (T) this.classes.get(clazz).get();
        }
    }

    private void analyzeProviders(Class clazz, Map<Class, Class> providers) {
        Class parentClass = clazz.getSuperclass();
        if(parentClass != null) {
            analyzeProviders(parentClass, providers);
        }
        Arrays.asList(clazz.getGenericInterfaces()).forEach(iface -> {
            String type = iface.getTypeName();
            if(type.startsWith("javax.inject.Provider")) {
                try {
                    Class provides = Class.forName(type.substring(type.indexOf('<') + 1, type.indexOf('>')));
                    LoggerFactory.getLogger(getClass()).debug("Registering provider {} for {}", clazz, provides);
                    providers.put(provides, clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Injector{" +
                "providers=" + providers +
                ", classes=" + classes +
                '}';
    }
}
