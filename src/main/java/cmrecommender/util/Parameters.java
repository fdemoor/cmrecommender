package cmrecommender.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import java.lang.StringBuilder;

public class Parameters {
  
  /* Parameters with default values */
  private String dataset = "datasets/test.csv"; // Path to dataset
  private long seed = 1267377627L; // Seed for PRNG
  private int nbFolds = 5; // Number of folds in cross validation
  private boolean useCM = false;
  private double gamma = 0.1;
  private double error = 1.0;
  private boolean pDist = false;
  private boolean runK = false;
  private boolean runEW = false;
  
  public Parameters(String[] line) throws ParseException {
  
    CommandLine commandLine;
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();
    
    Option helpOpt = Option.builder("help")
      .argName("help")
      .desc("Print this message")
      .build();
    options.addOption(helpOpt);
    
    Option datasetOpt = Option.builder("d")
      .argName("path")
      .hasArgs()
      .desc("Path to the dataset")
      .build();
    options.addOption(datasetOpt);
    
    Option nbFoldsOpt = Option.builder("N")
      .argName("value")
      .hasArgs()
      .desc("Number of folds for cross validation")
      .build();
    options.addOption(nbFoldsOpt);
    
    Option seedOpt = Option.builder("s")
      .argName("value")
      .hasArgs()
      .desc("Seed for PNRG")
      .build();
    options.addOption(seedOpt);
    
    Option outputOpt = Option.builder("o")
      .argName("path")
      .hasArgs()
      .desc("Output directory for logs")
      .build();
    options.addOption(outputOpt);
    
    Option cmOpt = Option.builder("CM")
      .desc("Use count-min sketch based cosine similarity")
      .build();
    options.addOption(cmOpt);
    
    Option gammaOpt = Option.builder("gamma")
      .argName("value")
      .hasArgs()
      .desc("Gamma deniability")
      .build();
    options.addOption(gammaOpt);
    
    Option errorOpt = Option.builder("error")
      .argName("value")
      .hasArgs()
      .desc("Error bound")
      .build();
    options.addOption(errorOpt);
    
    Option pDistOpt = Option.builder("pDist")
      .desc("Compute profile size distribution of the dataset")
      .build();
    options.addOption(pDistOpt);
    
    Option runKOpt = Option.builder("runK")
      .desc("Run evaluation with different k (as in kNN) values")
      .build();
    options.addOption(runKOpt);
    
    Option runEWOpt = Option.builder("runEW")
      .desc("Run error / width evaluation")
      .build();
    options.addOption(runEWOpt);
    
    commandLine = parser.parse(options, line);
    
    if (commandLine.hasOption("help")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("cmrecommender", options);
    }
    
    if (commandLine.hasOption("d")) {
      dataset = commandLine.getOptionValue("d");
    }
    
    if (commandLine.hasOption("N")) {
      nbFolds = Integer.parseInt(commandLine.getOptionValue("N"));
    }
    
    if (commandLine.hasOption("s")) {
      seed = Long.parseLong(commandLine.getOptionValue("s"));
    }
    
    if (commandLine.hasOption("CM")) {
      useCM = true;
    }
    
    if (commandLine.hasOption("gamma")) {
      gamma = Double.parseDouble(commandLine.getOptionValue("gamma"));
    }
    
    if (commandLine.hasOption("error")) {
      error = Double.parseDouble(commandLine.getOptionValue("error"));
    }
    
    if (commandLine.hasOption("pDist")) {
      pDist = true;
    }
    
    if (commandLine.hasOption("runK")) {
      runK = true;
    }
    
    if (commandLine.hasOption("runEW")) {
      runEW = true;
    }
    
  }
  
  public String getDataset() { return dataset; }
  public int getNbFolds() { return nbFolds; }
  public boolean useCM() { return useCM; }
  public double getGamma() { return gamma; }
  public double getError() { return error; }
  public boolean runProfileDist() { return pDist; }
  public boolean runKEvaluation() { return runK; }
  public boolean runEWEvaluation() { return runEW; }
  
  public String toString() {
    StringBuilder bld = new StringBuilder();
    String ln = System.getProperty("line.separator");
    bld.append("Dataset path: " + dataset + ln);
    bld.append("Seed: " + seed + ln);
    bld.append("Number of folds: " + nbFolds + ln);
    bld.append("Use CM: " + useCM + ln);
    bld.append("Gamma: " + gamma + ln);
    bld.append("Error bound: " + error + ln);
    bld.append("Run profile size distribution: " + pDist + ln);
    bld.append("Run k evaluation: " + runK + ln);
    bld.append("Run error / width evaluation: " + runEW + ln);
    return bld.toString();
  }

}
