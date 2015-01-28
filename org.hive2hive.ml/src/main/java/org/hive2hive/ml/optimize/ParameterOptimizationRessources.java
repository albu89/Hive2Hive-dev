package org.hive2hive.ml.optimize;


import java.util.ArrayList;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.Gpr;
import net.sourceforge.jFuzzyLogic.optimization.OptimizationDeltaJump;
import net.sourceforge.jFuzzyLogic.optimization.OptimizationGradient;
import net.sourceforge.jFuzzyLogic.optimization.Parameter;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;

/**
 * Fuzzy rule set parameter optimization for robot.fcl
 * 
 * @author pcingola@users.sourceforge.net
 */
public class ParameterOptimizationRessources {

	public static final String trainingFile = System.getProperty("user.home") + "/data/train.txt";
	public static final String fclFile = System.getProperty("user.home") + "/data/ressources.fcl";

	public static int NUM_ITERATIONS = 10;

	public static void main(String[] args) throws Exception {
		System.out.println("ParameterOptimizationDemo: Begin");

		//---
		// Load FIS (Fuzzy Inference System)
		//---
		FIS fis = FIS.load(fclFile);
		FunctionBlock functionBlock = fis.getFunctionBlock(null);
		JFuzzyChart.get().chart(functionBlock);
		RuleBlock ruleBlock = functionBlock.getFuzzyRuleBlock(null);

		//---
		// Create a list of parameter to optimize
		//---
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();

		// Add variables to be optimized
		parameterList.addAll(Parameter.parametersMembershipFunction(ruleBlock.getVariable("cpu")));
		parameterList.addAll(Parameter.parametersMembershipFunction(ruleBlock.getVariable("memory")));
		parameterList.addAll(Parameter.parametersMembershipFunction(ruleBlock.getVariable("network")));
		parameterList.addAll(Parameter.parametersMembershipFunction(ruleBlock.getVariable("quality")));

		//		// Add every rule's weight
		//		for (Rule rule : ruleBlock.getRules())
		//			parameterList.addAll(Parameter.parametersRuleWeight(rule));

		//---
		// Create an error function to be optimized (i.e. minimized)
		//---
		ErrorFunctionRessources errorFunction = new ErrorFunctionRessources(trainingFile);

		//---
		// Optimize (using 'Delta jump optimization')
		//---
		
		OptimizationGradient optimizationGradient = new OptimizationGradient(ruleBlock, errorFunction, parameterList);
		
		optimizationGradient.alphaLineIterations = 0.5;
		optimizationGradient.setMaxIterations(NUM_ITERATIONS); 
		optimizationGradient.optimize();
		//OptimizationDeltaJump optimizationDeltaJump = new OptimizationDeltaJump(ruleBlock, errorFunction, parameterList);
		//optimizationDeltaJump.setMaxIterations(NUM_ITERATIONS); // Number optimization of iterations
		//optimizationDeltaJump.setVerbose(true);
		//optimizationDeltaJump.optimize();

		//---
		// Save optimized fuzzyRuleSet to file
		//---
		System.out.println(functionBlock);
		Gpr.toFile(System.getProperty("user.home") + "/data/optimized.fcl", functionBlock.toString());

		// Show 
		functionBlock.reset(false);
		JFuzzyChart.get().chart(functionBlock);

		System.out.println("ParameterOptimizationDemo: End");
	}
}

