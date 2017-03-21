package eu.mjelen.openshifter.provider.aws;

import eu.mjelen.openshifter.api.Deployment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Terraformer {

    private final Deployment deployment;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Terraformer(Deployment deployment) {
        this.deployment = deployment;
    }

    /**
    * Loads variable.tf, replaces it with the value from
    * the Deployment (user-provided definition file)
    * and writes out the result in the templates/ directory along with
    * the main Terraform file.
    */
    public boolean initTemplates() {
      StringBuilder sb = new StringBuilder();
      File tDir = new File("templates/");
      boolean success = true;

      this.logger.info("trying to load Terraform template");

      try {
        if (!tDir.exists()) {
          tDir.mkdir();
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/variables.tf")));
        String line = buf.readLine();
        while(line != null){
          sb.append(line).append("\n");
          line = buf.readLine();
        }
        String varTF = sb.toString();

        // replace values from deployment:
        if(this.deployment.getNodes().getCount() != null){
          varTF = varTF.replaceAll("NUM_WORKER_NODES", Long.toString(this.deployment.getNodes().getCount()));
        } else {
            varTF = varTF.replaceAll("NUM_WORKER_NODES", Long.toString(this.deployment.getAws().getCount()));
        }
        if(this.deployment.getNodes().getType() != null){
          varTF = varTF.replaceAll("MACHINE_TYPE", this.deployment.getNodes().getType());
        } else {
          varTF = varTF.replaceAll("MACHINE_TYPE", this.deployment.getAws().getType());
        }
        if(this.deployment.getNodes().getZone() != null){
          varTF = varTF.replaceAll("AWS_ZONE", this.deployment.getNodes().getZone());
          this.deployment.getAws().setZone(this.deployment.getNodes().getZone());
        } else {
          varTF = varTF.replaceAll("AWS_ZONE", this.deployment.getAws().getZone());
        }
        if(this.deployment.getNodes().getRegion() != null){
          varTF = varTF.replaceAll("AWS_REGION", this.deployment.getNodes().getRegion());
        } else {
          varTF = varTF.replaceAll("AWS_REGION", this.deployment.getAws().getRegion());
        }

        // write out the resulting TF variable file into data directory
        BufferedWriter out = new BufferedWriter(new FileWriter("templates/variables.tf"));
        out.write(varTF);
        out.close();

        // copy main.tf to data directory
        copyTemplate("main.tf");
      } catch(SecurityException se){
          this.logger.error("Problem initializing Terraform template: " + se);
          success = false;
      } catch (IOException ioe) {
          this.logger.error("Problem initializing Terraform template: " + ioe);
          success = false;
     }
     return success;
    }

    public void copyTemplate(String tname) throws IOException {
      StringBuilder sb = new StringBuilder();
      BufferedReader buf = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/"+tname)));
      String line = buf.readLine();
      while(line != null){
        sb.append(line).append("\n");
        line = buf.readLine();
      }
      String tmp = sb.toString();
      BufferedWriter out = new BufferedWriter(new FileWriter("templates/"+tname));
      out.write(tmp);
      out.close();
    }

    /**
    * Executes Terraform with templates in the templates/ directory.
    */
    public void execTemplate(String mode) {
      int exitCode = 0;

      try {
        ProcessBuilder builder = new ProcessBuilder();
        StringBuilder sb = new StringBuilder();
        BufferedReader buf = null;
        builder.directory(new File("templates/"));

        Map<String, String> env = builder.environment();
        env.put("AWS_ACCESS_KEY_ID", this.deployment.getAws().getKey());
        env.put("AWS_SECRET_ACCESS_KEY", this.deployment.getAws().getSecret());

        if("plan".equals(mode)) {
          builder.command("terraform", "plan");
        }
        if("apply".equals(mode)) {
          builder.command("terraform", "apply");
        }
        if("destroy".equals(mode)) {
          builder.command("terraform", "destroy", "-force");
        }

        Process process = builder.start();

        buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = buf.readLine()) != null) {
            sb.append(line + System.getProperty("line.separator"));
        }
        this.logger.info(sb.toString());
        exitCode = process.waitFor();
        buf.close();
      } catch (IOException ioe) {
          this.logger.error("Problem: " + ioe);
      } catch (InterruptedException ie) {
          this.logger.error("Problem: " + ie);
      }
      if(exitCode > 0) {
        this.logger.error("There was a problem with executing Terraform plan, check credentials.");
        System.exit(exitCode);
      }
    }

}
