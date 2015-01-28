package org.hive2hive.ml.optimize;



import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.optimization.ErrorFunction;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * Error function for robot.fcl fuzzy rule set
 *
 * @author pcingola@users.sourceforge.net
 */
public class ErrorFunctionRessources extends ErrorFunction {

	// Debug mode?
	public boolean verbose = true;
	double cpu[], memory[], network[], quality[];

	public ErrorFunctionRessources(String trainingFile) {
		load(trainingFile);
	}

	@Override
	public double evaluate(RuleBlock ruleBlock) {
		double error = 0;

		FunctionBlock fb = ruleBlock.getFunctionBlock();
		Variable varCPU = fb.getVariable("cpu");
		Variable varMemory = fb.getVariable("memory");
		Variable varNetwork = fb.getVariable("network");
		Variable varQuality = fb.getVariable("quality");
		

		if (verbose) System.out.print("Evaluate: ");
		// For all samples
		for (int sample = 0; sample < cpu.length; sample++) {
			// Set variables
			varCPU.setValue(cpu[sample]);
			varMemory.setValue(memory[sample]);
			varNetwork.setValue(network[sample]);
			

			// Evaluate FIS
			fb.evaluate();

			// Get output
			double errAv = quality[sample] - varQuality.getValue();
			//double errLa = la[sample] - varLa.getValue();

			// Accumulate error
			error += (errAv * errAv);

			if (verbose) Gpr.showMark(sample + 1, 100);
		}

		error = Math.sqrt(error);
		if (verbose) System.out.println("!\tError: " + error);

		return error;
	}

	/**
	 * Load trainig set from a file
	 * @param trainingFile
	 */
	void load(String trainingFile) {
		Gpr.debug("Loading trainig set from file: " + trainingFile);
		String lines[] = Gpr.readFile(trainingFile).split("\n");

		// Count lines (number of examples)
		int lineCount = 0;
		for (String line : lines) {
			if (!line.startsWith("#")) { // Skip comments
				lineCount++;
			}
		}
		Gpr.debug("Lines: " + lineCount);

		// Create samples
		cpu = new double[lineCount];
		memory = new double[lineCount];
		network = new double[lineCount];
		quality = new double[lineCount];
		

		// Read examples
		lineCount = 0;
		for (String line : lines) {
			if (!line.startsWith("#")) { // Skip comments
				String recs[] = line.split("\t");

				int recNum = 0;
				cpu[lineCount] = Gpr.parseDoubleSafe(recs[recNum++]);
				memory[lineCount] = Gpr.parseDoubleSafe(recs[recNum++]);
				network[lineCount] = Gpr.parseDoubleSafe(recs[recNum++]);
				quality[lineCount] = Gpr.parseDoubleSafe(recs[recNum++]);
				
				lineCount++;
			}
		}
	}
}

