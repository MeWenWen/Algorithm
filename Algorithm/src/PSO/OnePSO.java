package PSO;

import java.util.Random;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.ParticleUpdate;
import net.sourceforge.jswarm_pso.Swarm;

public class OnePSO {
	// 任务个数
	static int task_num = 20;
	// 虚拟机个数
	static int vm_num = 5;
	private static Swarm swarm;
	private static Particle particles[];
	// ETC矩阵
	public static double[][] execMatrix;
	private static final double W = 0.9;
	private static final double C = 2.0;

	public static void main(String[] args) {

		OnePSO onePSO = new OnePSO();
		// 初始化粒子群
		onePSO.initParticles();
		// 运行
		onePSO.Run();
	}

	// 返回初始化一个粒子
	public Particle InitParticle() {
		this.task_num = execMatrix.length;
		this.vm_num = execMatrix[0].length;
		double[] position = new double[task_num];
		double[] velocity = new double[task_num];

		Particle p = null;
		try {
			p = new Particle(task_num) {
			};
		} catch (Exception e) {
			// TODO: handle exception
		}
		for (int i = 0; i < task_num; i++) {
			// 假设有三个节点 那么位置的选项肯定只能有 0,1,2
			Random randObj = new Random();
			position[i] = randObj.nextInt(vm_num);
			velocity[i] = Math.random();
		}
		p.setPosition(position);
		p.setVelocity(velocity);
		return p;
	}

	// 初始化一堆粒子
	public void initParticles() {
		// 群的数量设置为 10吧
		particles = new Particle[10];
		for (int i = 0; i < 10; ++i) // 25个粒子 每个粒子30个维度
			particles[i] = InitParticle();

		for (int i = 0; i < particles.length; i++) {
			for (int j = 0; j < particles[i].getBestPosition().length; j++) {
				System.out.print(particles[i].getPosition()[j] + " ");
			}
			System.out.println();
		}

	}

	// 返回一个FitnessFunction类型的函数
	public FitnessFunction InitFitnessFunction() {
		FitnessFunction f = new FitnessFunction(false) {

			@Override
			public double evaluate(double[] position) {
				// TODO Auto-generated method stub
				return calcMakespan(position);
			}
		};
		return f;
	}

	// 计算MAKESPAN
	public static double calcMakespan(double[] position) {
		double makespan = 0;
		double[] dcWorkingTime = new double[vm_num];

		for (int i = 0; i < task_num; i++) {

			int dcId = (int) position[i];
			dcWorkingTime[dcId] += execMatrix[i][dcId];
			makespan = Math.max(makespan, dcWorkingTime[dcId]);

		}
		return makespan;
	}

	// 返回更新粒子类
	public ParticleUpdate getParticleUpdate() {
		ParticleUpdate pu = new ParticleUpdate(InitParticle()) {

			@Override
			public void update(Swarm swarm, Particle particle) {
				// TODO Auto-generated method stub
				double[] v = particle.getVelocity();
				double[] x = particle.getPosition();
				double[] pbest = particle.getBestPosition();
				double[] gbest = swarm.getBestPosition();

				for (int i = 0; i < task_num; ++i) {
					v[i] = W * v[i] + C * Math.random() * (pbest[i] - x[i]) + C * Math.random() * (gbest[i] - x[i]);
					x[i] = (int) (x[i] + v[i]);
				}
			}
		};

		return pu;
	}

	public double[] Run() {

		swarm = new Swarm(10, InitParticle(), InitFitnessFunction());

		swarm.setMinPosition(0);
		swarm.setMaxPosition(vm_num - 1);
		swarm.setMaxMinVelocity(0.5);
		swarm.setParticles(particles);
		swarm.setParticleUpdate(getParticleUpdate());
		// 当前最好的次数

		double min = Double.MAX_VALUE;
		int num = 0;
		int xhcs = 100;

		// 迭代500次
		for (int i = 0; i < xhcs; i++) {
			swarm.evolve();
			if (i % 10 == 0) {
				System.out.printf("Gloabl best at iteration (%d): %f\n", i, swarm.getBestFitness());
			}
		}
		System.out.println("\nThe best fitness value: " + swarm.getBestFitness() + "\nBest makespan: "
				+ calcMakespan(swarm.getBestParticle().getBestPosition()));

		System.out.println("The best solution is: ");
		Particle bestParticle = swarm.getBestParticle();
		System.out.println(bestParticle.toString());
		System.out.println(swarm.toStringStats());
		return swarm.getBestPosition();
	}

	// 获取ETC矩阵
//	public static double[][] getEtc1() {
//
//		double[][] exec = { { 33.44, 175.91, 215.11, 479.9, 982.48 }, { 562.39, 631.5, 806.81, 67.38, 174.56 },
//				{ 51.75, 89.87, 106.38, 134.86, 677.69 }, { 80.25, 328.27, 945.49, 102.88, 484.53 },
//				{ 173.24, 249.86, 484.42, 548.07, 606.7 }, { 783.98, 699.19, 525.5, 931.36, 497.0 },
//				{ 29.34, 47.2, 296.22, 458.95, 510.03 }, { 178.67, 572.25, 282.3, 414.01, 225.93 },
//				{ 229.69, 588.08, 776.34, 800.59, 857.3 }, { 42.72, 74.04, 948.2, 213.22, 538.17 },
//				{ 249.82, 283.39, 480.55, 486.75, 793.42 }, { 982.21, 747.87, 233.92, 574.3, 691.26 },
//				{ 52.69, 136.53, 175.27, 528.97, 958.77 }, { 66.83, 184.26, 589.43, 705.56, 230.56 },
//				{ 110.03, 167.38, 192.68, 354.62, 793.28 }, { 584.51, 695.93, 548.55, 634.12, 179.76 },
//				{ 21.06, 165.79, 231.83, 269.51, 279.32 }, { 40.63, 678.13, 630.52, 395.7, 857.92 },
//				{ 261.45, 366.86, 441.36, 730.08, 946.93 }, { 169.54, 556.47, 148.4, 648.65, 118.98 },
//
//		};
//		return exec;
//	}

}
