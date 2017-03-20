package eu.mjelen.openshifter.provider.aws;

import eu.mjelen.openshifter.api.Deployment;
import eu.mjelen.warden.api.cluster.Cluster;
import eu.mjelen.warden.api.cluster.Instance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsCluster implements Cluster {

    private final Deployment deployment;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AwsCluster(Deployment deployment) {
        this.deployment = deployment;
    }

    @Override
    public Instance instance(String label) {
        return null;
    }

    @Override
    public List<? extends Instance> instances() {
        return null;
    }

    @Override
    public List<? extends Instance> instances(String label) {
        return null;
    }

    @Override
    public void validate() {
      StringBuilder sb = new StringBuilder();
      BufferedReader br = null;
      int exitCode = 0;

      this.logger.info("terraform plan");
      try {
        BufferedReader buf = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/variables.tf")));
        String line = buf.readLine();
        while(line != null){
          sb.append(line).append("\n");
          line = buf.readLine();
        }
        String varTF = sb.toString();
        this.logger.info("node count: " + Long.toString(this.deployment.getNodes().getCount()));
        varTF = varTF.replaceAll("NUM_WORKER_NODES", Long.toString(this.deployment.getNodes().getCount()));
        System.out.println(varTF);
      } catch (IOException ioe) {
           this.logger.error("Problem: " + ioe);
     }

      // try {
      //   ProcessBuilder builder = new ProcessBuilder();
      //   // this needs to go in a relative sub-dir to data and copied in the Docker file:
      //   builder.directory(new File("../../provider-aws/"));
      //   builder.command("/usr/local/bin/terraform", "plan");
      //
      //   Process process = builder.start();
      //
      //   br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      //   String line = null;
      //   while ((line = br.readLine()) != null) {
      //       sb.append(line + System.getProperty("line.separator"));
      //   }
      //   this.logger.info(sb.toString());
      //   exitCode = process.waitFor();
      //   br.close();
      // } catch (IOException ioignore) {
      //     this.logger.error("Problem: " + ioignore);
      // }
      //   catch (InterruptedException ieignore) {
      //     this.logger.error("Problem: " + ieignore);
      // }
      // if(exitCode > 0) {
      //   this.logger.error("There was a problem with the Terraform plan, check credentials.");
      //   System.exit(1);
      // }

    }

    @Override
    public void create() {
      StringBuilder sb = new StringBuilder();
      BufferedReader br = null;
      int exitCode = 0;

      this.logger.info("terraform apply");

      try {
        ProcessBuilder builder = new ProcessBuilder();
        // this needs to go in a relative sub-dir to data and copied in the Docker file:
        builder.directory(new File("../../provider-aws/"));
        builder.command("/usr/local/bin/terraform", "apply");

        Process process = builder.start();

        br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + System.getProperty("line.separator"));
        }
        this.logger.info(sb.toString());
        exitCode = process.waitFor();
        br.close();
      } catch (IOException ioignore) {
          this.logger.error("Problem: " + ioignore);
      }
        catch (InterruptedException ieignore) {
          this.logger.error("Problem: " + ieignore);
      }
      System.exit(0);
    }

    @Override
    public void destroy() {
      StringBuilder sb = new StringBuilder();
      BufferedReader br = null;
      int exitCode = 0;

      this.logger.info("terraform destroy");

      try {
        ProcessBuilder builder = new ProcessBuilder();
        // this needs to go in a relative sub-dir to data and copied in the Docker file:
        builder.directory(new File("../../provider-aws/"));
        builder.command("/usr/local/bin/terraform", "destroy", "-force");

        Process process = builder.start();

        br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + System.getProperty("line.separator"));
        }
        this.logger.info(sb.toString());
        exitCode = process.waitFor();
        br.close();
      } catch (IOException ioignore) {
          this.logger.error("Problem: " + ioignore);
      }
        catch (InterruptedException ieignore) {
          this.logger.error("Problem: " + ieignore);
      }
      System.exit(0);

    }

}
