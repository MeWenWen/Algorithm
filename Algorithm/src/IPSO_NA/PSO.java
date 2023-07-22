package IPSO_NA;

import java.awt.geom.RoundRectangle2D.Double;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.Swarm;
import utils.Constants;
import PSO.*;

public class PSO {
	public static double W_MAX = 0.9;
	public static double W_MIN = 0.4;
	public static double W;
	private static Swarm swarm;
	private static SchedulerParticle particles[];
	private static SchedulerFitnessFunction ff = new SchedulerFitnessFunction();
	private Particle particle;
	private int num;

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
		int TMax = 800;
		double min = java.lang.Double.MAX_VALUE;
		HashMap<double[], java.lang.Double> globalfitness = new HashMap<double[], java.lang.Double>();
//		ArrayList<java.lang.Double> globalfitness2 = new ArrayList<java.lang.Double>();
		for (int i = 0; i < TMax; i++) {
			W = W_MAX - i * (W_MAX - W_MIN) / TMax;
			swarm.evolve();
			
//         if (i % 10 == 0) {
			if (i % 5 == 0) {
				// naive 接收规则：当新解目标值优于旧解目标值时，接受；
				// 当新解目标值比旧解目标值差，生成一个随机数p，若p》=0.5则接受新解，否则拒绝接受。
				if (min > swarm.getBestFitness()) {
					min = swarm.getBestFitness();
					num = 0;
				}
				// 都不变的情况下记录
				if (min == swarm.getBestFitness()) {
					num++;
				}
				System.out.println("\nBest makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
				// 找迭代中的那个最大的值
				if (num >= 20) {
					DecimalFormat df = new DecimalFormat("#.######");
					
					min= java.lang.Double.parseDouble(df.format(min));
//					System.out.println("min: "+min);
					Particle[] particles = swarm.getParticles();
					double min_makespan = java.lang.Double.MAX_VALUE;
					
					// 将min_makespan,跟bestfitness的值的精度保持一致
					int now_i = 0;
					for (int j = 0; j < particles.length; j++) {
						double now_makespan = SchedulerFitnessFunction.calcMakespan(particles[j].getPosition());
//						System.out.println("now_makespan: "+ df.format(now_makespan));
						now_makespan = java.lang.Double.parseDouble(df.format(now_makespan));
//						System.out.println("是这个6位: "+new DecimalFormat("0.000000").format(now_makespan));
//						System.out.println("===" + now_makespan +"jjjjj: " + j);
//						if (now_makespan < min_makespan && now_makespan != min) {
						if (now_makespan < min_makespan ) {
							min_makespan = now_makespan;
							now_i = j;
						}
//						Random rand = new Random();
//						now_makespan=particles[j].getPosition();

					}
//					System.out.println("?????: " + now_i);
//					System.out.println("min_makespan:" + min_makespan);
					// min_makespan是次优的值
					// now_i是迭代中次优的坐标

					if (min_makespan > swarm.getBestFitness()) {
						Random rand = new Random();
						double p = rand.nextDouble();// 生成一个【0，1.0】之间的小数
						if (p >= 0.5) {
							// 记录在迭代过程中的swarm.getBestFitness() 值跟这个粒子对应的任务分配的结果swarm.getBestParticle()
							globalfitness.put(swarm.getBestPosition(), swarm.getBestFitness());
//							globalfitness2.add(swarm.getBestFitness());
							swarm.setBestParticleIndex(now_i);
							swarm.setBestPosition(particles[now_i].getPosition());

						}
					}
				}
				System.out.println();
				System.out.printf("Gloabl best at iteration (%d): %f\n", i, swarm.getBestFitness());
	            System.out.println("\nBest makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
			}
		}
		globalfitness.put(swarm.getBestPosition(), swarm.getBestFitness());
		// hashmap取出来对比值,找最小值
		min = swarm.getBestFitness();
//	    double[] position = swarm.getBestPosition();
//		for(Map.Entry<double[], java.lang.Double> entry : globalfitness.entrySet()) {
//			System.out.print("value: "+ entry.getValue()+" ");
//		}
//		System.out.println();
		
		for(Map.Entry<double[], java.lang.Double> entry : globalfitness.entrySet()) {
			if(entry.getValue()<min) {
				min=entry.getValue();
				swarm.setBestPosition(entry.getKey());
			}
		}
		
//		System.out.println("min = "+min);
//		
//		System.out.println("\nThe best fitness value: " + min + "\nBest makespan: "
//				+ ff.calcMakespan(swarm.getBestParticle().getBestPosition()));

//		System.out.println("The best solution is: ");/
//		SchedulerParticle bestParticle = (SchedulerParticle) swarm.getBestParticle();
//		System.out.println(bestParticle.toString());

		return swarm.getBestPosition();
	}

	private static void initParticles() {
		particles = new SchedulerParticle[Constants.POPULATION_SIZE];
		for (int i = 0; i < Constants.POPULATION_SIZE; ++i)
			particles[i] = new SchedulerParticle();
	}

//	public void printBestFitness() {
//		System.out.println("\nBest fitness value: " + swarm.getBestFitness() + "\nBest makespan: "
//				+ ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
//	}
}
