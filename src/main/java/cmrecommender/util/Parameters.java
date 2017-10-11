package cmrecommender.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class Parameters {
  
  /* Parameters with default values */
  private String dataset = "datasets/test.csv"; // Path to dataset
  private long seed = 1267377627L; // Seed for PRNG
  private int nbFolds = 5; // Number of folds in cross validation
  private String outputDir = "output"; // Output directory for logs
  
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
    
  }
  
  public String getDataset() { return dataset; }
  public int getNbFolds() { return nbFolds; }

}
