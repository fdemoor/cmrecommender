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
  private double q = 1.0;
  private boolean runDist = false;
  private boolean runK = false;
  
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
    
    Option qOpt = Option.builder("q")
      .argName("value")
      .hasArgs()
      .desc("Privacy/accuracy trade-off parameter")
      .build();
    options.addOption(qOpt);
    
    Option pDistOpt = Option.builder("runDist")
      .desc("Compute several distributions of the dataset")
      .build();
    options.addOption(pDistOpt);
    
    Option runKOpt = Option.builder("runK")
      .desc("Run evaluation with different k (as in kNN) values")
      .build();
    options.addOption(runKOpt);
    
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
    
    if (commandLine.hasOption("q")) {
      q = Double.parseDouble(commandLine.getOptionValue("q"));
    }
    
    if (commandLine.hasOption("runDist")) {
      runDist = true;
    }
    
    if (commandLine.hasOption("runK")) {
      runK = true;
    }
    
  }
  
  public String getDataset() { return dataset; }
  public int getNbFolds() { return nbFolds; }
  public boolean useCM() { return useCM; }
  public long getSeed() { return seed; }
  public double getQ() { return q; }
  public boolean runDistEvaluation() { return runDist; }
  public boolean runKEvaluation() { return runK; }
  
  public String toString() {
    StringBuilder bld = new StringBuilder();
    String ln = System.getProperty("line.separator");
    bld.append("Dataset path: " + dataset + ln);
    bld.append("Seed: " + seed + ln);
    bld.append("Number of folds: " + nbFolds + ln);
    bld.append("Use CM: " + useCM + ln);
    bld.append("Value of q: " + q + ln);
    bld.append("Run several distributions: " + runDist + ln);
    bld.append("Run k evaluation: " + runK + ln);
    return bld.toString();
  }

}
