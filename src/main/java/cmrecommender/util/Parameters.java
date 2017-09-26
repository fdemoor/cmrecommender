package cmrecommender.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameters {
  
  private static final Logger log = LoggerFactory.getLogger(Parameters.class);
  
  /* Parameters with default values */
  private String dataset = "datasets/test.csv"; // Path to dataset
  private int nbFolds = 5; // Number of folds in cross validation
  
  public Parameters(String[] line) {
  
    CommandLine commandLine;
    CommandLineParser parser = new DefaultParser();
    Options options = new Options();
    
    Option helpOpt = OptionBuilder.withArgName("help")
      .withDescription("Print this message")
      .create("help");
    options.addOption(helpOpt);
    
    Option datasetOpt = OptionBuilder.withArgName("path")
      .hasArgs(1)
      .withDescription("Path to the dataset")
      .create("dataset");
    options.addOption(datasetOpt);
    
    Option nbFoldsOpt = OptionBuilder.withArgName("value")
      .hasArgs(1)
      .withDescription("Number of folds for cross validation")
      .create("N");
    options.addOption(nbFoldsOpt);
    
    try {
      commandLine = parser.parse(options, line);
      
      if (commandLine.hasOption("help")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("cmrecommender", options);
      }
      
      if (commandLine.hasOption("dataset")) {
        dataset = commandLine.getOptionValue("dataset");
      }
      
      if (commandLine.hasOption("N")) {
        nbFolds = Integer.parseInt(commandLine.getOptionValue("N"));
      }

    } catch (ParseException exception) {
      log.error("Parse error: {}", exception.getMessage());
    }
    
  }
  
  public String getDataset() { return dataset; }
  public int getNbFolds() { return nbFolds; }

}
