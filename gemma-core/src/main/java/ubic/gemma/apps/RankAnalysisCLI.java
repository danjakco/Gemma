package ubic.gemma.apps;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang.time.StopWatch;

import ubic.basecode.dataStructure.matrix.AbstractNamedMatrix;
import ubic.basecode.io.writer.MatrixWriter;
import ubic.gemma.analysis.preprocess.DedvRankService;
import ubic.gemma.analysis.preprocess.DedvRankService.Method;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Gene;

public class RankAnalysisCLI extends
		ExpressionExperimentManipulatingCLI {

	private DedvRankService dedvRankService;

	private String outFilePrefix;
	
	private DedvRankService.Method method;
	
	public RankAnalysisCLI() {
		method = Method.MAX;
	}

	@Override
    @SuppressWarnings("static-access")
    protected void buildOptions() {
		super.buildOptions();

 		Option outFilePrefixOption = OptionBuilder.isRequired().hasArg()
				.withDescription("File prefix for saved rank matrix files")
				.withArgName("File prefix").withLongOpt("outFilePrefix")
				.create('o');
		addOption(outFilePrefixOption);

		Option methodOption = OptionBuilder.hasArg().withDescription(
				"Method to use to calculate rank").withArgName("Method").withLongOpt("method")
				.create('m');
		addOption(methodOption);
	}

	@Override
    protected void processOptions() {
		super.processOptions();

		outFilePrefix = getOptionValue('o');

		dedvRankService = (DedvRankService) getBean("dedvRankService");
		
		if (hasOption('m')) {
			String methodString = getOptionValue('m');
			if (methodString.equalsIgnoreCase("MEDIAN")) {
				method = DedvRankService.Method.MEDIAN;
			} else if (methodString.equalsIgnoreCase("MEAN")) {
				method = Method.MEAN;
			} else if (methodString.equalsIgnoreCase("MAX")) {
				method = Method.MAX;
			} else if (methodString.equalsIgnoreCase("VARIANCE")) {
				method = Method.VARIANCE;
			} else if (methodString.equalsIgnoreCase("MIN")) {
				method = Method.MIN;
			}
		}
	}

	@Override
	protected Exception doWork(String[] args) {
		processCommandLine("RankAnalysisCLI", args);

		Collection<ExpressionExperiment> ees;
		try {
			ees = getExpressionExperiments(taxon);
		} catch (IOException e) {
			return e;
		}

		Collection<Gene> genes = geneService.loadGenes(taxon);
		AbstractNamedMatrix rankMatrix = dedvRankService.getRankMatrix(genes,
				ees, method);

		// gene names
		Collection<Gene> rowGenes = rankMatrix.getRowNames();
		try {
			PrintWriter out = new PrintWriter(new FileWriter(outFilePrefix
					+ ".row_names.txt"));
			for (Gene gene : rowGenes) {
				String s = gene.getOfficialSymbol();
				if (s == null)
					s = gene.getId().toString();
				out.println(s);
			}
			out.close();
		} catch (IOException exc) {
			return exc;
		}

		// expression experiment names
		Collection<ExpressionExperiment> colEes = rankMatrix.getColNames();
		try {
			PrintWriter out = new PrintWriter(new FileWriter(outFilePrefix
					+ ".col_names.txt"));
			for (ExpressionExperiment ee : colEes) {
				out.println(ee.getShortName());
			}
			out.close();
		} catch (IOException exc) {
			return exc;
		}

		DecimalFormat formatter = (DecimalFormat) NumberFormat
				.getNumberInstance();
		formatter.applyPattern("0.0000");
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setNaN("NaN");
		formatter.setDecimalFormatSymbols(symbols);
		try {
			MatrixWriter out = new MatrixWriter(outFilePrefix + ".txt",
					formatter);
			out.writeMatrix(rankMatrix, false);
			out.close();
		} catch (IOException e) {
			return e;
		}
		return null;
	}

	public static void main(String[] args) {
		RankAnalysisCLI analysis = new RankAnalysisCLI();
		StopWatch watch = new StopWatch();
		watch.start();
		Exception e = analysis.doWork(args);
		watch.stop();
		log.info("Finished analysis in " + watch);
		if (e != null)
			log.error(e.getMessage());
	}

}
