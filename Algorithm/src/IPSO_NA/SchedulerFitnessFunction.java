package IPSO_NA;
import java.util.Random;

import net.sourceforge.jswarm_pso.FitnessFunction;
import utils.Constants;
import utils.GenerateMatrices;

public class SchedulerFitnessFunction extends FitnessFunction {
    private static double[][] execMatrix, commMatrix;

    public SchedulerFitnessFunction() {
        super(false);
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
    }

    @Override
    public double evaluate(double[] position) {
        double alpha = 0.3;
//        return alpha * calcTotalTime(position) + (1 - alpha) * calcMakespan(position);
        return calcMakespan(position);
    }

    private double calcTotalTime(double[] position) {
        double totalCost = 0;
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            totalCost += execMatrix[i][dcId] + commMatrix[i][dcId];
        }
        return totalCost;
    }

    public static double calcMakespan(double[] position) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            if(dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] += execMatrix[i][dcId] + commMatrix[i][dcId];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
            
          
        }
        return makespan;
    }
    
    private void initMatrices() {
        System.out.println("Initializing input matrices (e.g. exec time & communication time matrices");
//        execMatrix = new double[Constant.NO_OF_TASKS][Constant.NO_OF_DATA_CENTERS];
//        commMatrix = new double[Constant.NO_OF_TASKS][Constant.NO_OF_DATA_CENTERS];

        commMatrix = GenerateMatrices.getCommMatrix();
		execMatrix = GenerateMatrices.getExecMatrix();
		
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            for (int j = 0; j < Constants.NO_OF_DATA_CENTERS; j++) {
            	execMatrix[i][j] = Math.random() * 500;
                commMatrix[i][j] = Math.random() * 500 + 20;
            }
        }
    }
}
