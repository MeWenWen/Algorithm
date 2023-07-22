package IPSO;

import net.sourceforge.jswarm_pso.Swarm;
import utils.Constants;

public class PSO {
	public static double W_MAX = 0.9;
	public static double W_MIN = 0.4;
	public static double W;
    private static Swarm swarm;
    private static SchedulerParticle particles[];
    private static SchedulerFitnessFunction ff = new SchedulerFitnessFunction();

    public PSO() {
        initParticles();
    }


    public double[] run() {
        swarm = new Swarm(Constants.POPULATION_SIZE, new SchedulerParticle(), ff);

        swarm.setMinPosition(0);
        swarm.setMaxPosition(Constants.NO_OF_DATA_CENTERS - 1);
        swarm.setMaxMinVelocity(0.5);
        swarm.setParticles(particles);
        swarm.setParticleUpdate(new SchedulerParticleUpdate(new SchedulerParticle()));
        int TMax = 1000;
        for (int i = 0; i < TMax; i++) {
			W = W_MAX - i * (W_MAX - W_MIN) / TMax;
            swarm.evolve();
            if (i % 10 == 0) {
            System.out.printf("Gloabl best at iteration (%d): %f\n", i, swarm.getBestFitness());
            System.out.println("\nBest makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
            }
        }

//        System.out.println("\nThe best fitness value: " + swarm.getBestFitness() + "\nBest makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));

        System.out.println("The best solution is: ");
        SchedulerParticle bestParticle = (SchedulerParticle) swarm.getBestParticle();
        System.out.println(bestParticle.toString());

        return swarm.getBestPosition();
    }

    private static void initParticles() {
        particles = new SchedulerParticle[Constants.POPULATION_SIZE];
        for (int i = 0; i < Constants.POPULATION_SIZE; ++i)
            particles[i] = new SchedulerParticle();
    }

    public void printBestFitness() {
        System.out.println("\nBest fitness value: " + swarm.getBestFitness() +
                "\nBest makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
    }
}
