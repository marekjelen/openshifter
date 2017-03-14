package eu.mjelen.warden;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Index {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ScanResult index;

    public Index() throws ClassNotFoundException {
        try {
            Class clazz = Class.forName("io.github.lukehutch.fastclasspathscanner.FastClasspathScanner");
        } catch (ClassNotFoundException ex) {
            throw ex;
        }
        long time = System.nanoTime();
        this.logger.info("Scanning classes");
        this.index = new FastClasspathScanner().scan();
        this.logger.info("Scan finished in {}ms", (System.nanoTime() - time) / 1000000);
    }

    public List<Class> getClassesImplementing(Class iface) {
        return this.index.getNamesOfClassesImplementing(iface).stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public List<Class> getClassesWithAnnotation(Class annotation) {
        return this.index.getNamesOfClassesWithAnnotation(annotation).stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

}
