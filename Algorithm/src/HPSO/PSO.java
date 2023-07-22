package HPSO;

import java.awt.geom.RoundRectangle2D.Double;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import IPSO_NA.SchedulerFitnessFunction;

import java.util.Random;

import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.Swarm;
import utils.Constants;
import utils.GenerateMatrices;
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
	private static double[][] etc1 = GenerateMatrices.getCommMatrix();
	private static double[][] etc2 = GenerateMatrices.getExecMatrix();
	private static double[][] etc=new double[etc1.length][etc1[0].length];
	private static  double ex_max = 0;

	
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
		HashMap<java.lang.Double,double[]> globalfitness = new HashMap<java.lang.Double,double[]>();
//		ArrayList<java.lang.Double> globalfitness2 = new ArrayList<java.lang.Double>();
		
		
		for(int k=0;k<etc1.length;k++) {
			for(int l=0;l<etc1[0].length;l++) {
				etc[k][l]=etc1[k][l]+etc2[k][l];
			}
		}
		
		for (int i = 0; i < TMax; i++) {
			//IPSO
			W = W_MAX - i * (W_MAX - W_MIN) / TMax;
			swarm.evolve();
		
//         if (i % 10 == 0) {
			//����ε���ֵ
			if (i % 5 == 0) {
				
				
				if (min > swarm.getBestFitness()) {
					min = swarm.getBestFitness();
					num = 0;
				}
				// �����������¼�¼
				if (min == swarm.getBestFitness()) {
					num++;
				}
				
				
				/**
				 * 1.��ѡ��ǰ���ٴ�ʹ�ý��ܹ���,����ٴ�ʹ�����񽻻�
				 * 2.
				 * **/
				//����
				if (num >= 20) {
					DecimalFormat df = new DecimalFormat("#.######");
					
					min= java.lang.Double.parseDouble(df.format(min));
//					System.out.println("min: "+min);
					Particle[] particles = swarm.getParticles();
					double min_makespan = java.lang.Double.MAX_VALUE;
					
					// ��min_makespan,��bestfitness��ֵ�ľ��ȱ���һ��
					int now_i = 0;
					for (int j = 0; j < particles.length; j++) {
						double now_makespan = SchedulerFitnessFunction.calcMakespan(particles[j].getPosition());//����calcMakespan�������ǰ��makespanֵ
						now_makespan = java.lang.Double.parseDouble(df.format(now_makespan));//���־���
						if (now_makespan < min_makespan && now_makespan != min) {//�ж��Ǹ��ȵ���������С��MS��ֵ,���Ҳ����뵱ǰMS���
							min_makespan = now_makespan;
							now_i = j;//��¼��id
						}

					}
//					System.out.println("?????: " + now_i);
//					System.out.println("min_makespan:" + min_makespan);
					// min_makespan�Ǵ��ŵ�ֵ
					// now_i�ǵ����д��ŵ�����
					// naive ���չ��򣺵��½�Ŀ��ֵ���ھɽ�Ŀ��ֵʱ�����ܣ�
					// ���½�Ŀ��ֵ�Ⱦɽ�Ŀ��ֵ�����һ�������p����p��=0.5������½⣬����ܾ����ܡ�
					if (min_makespan > swarm.getBestFitness()) {
						Random rand = new Random();
						double p = rand.nextDouble();// ����һ����0��1.0��֮���С��
						if (p >= 0.5) {
							// ��¼�ڵ��������е�swarm.getBestFitness() ֵ��������Ӷ�Ӧ���������Ľ��swarm.getBestParticle()
							double[] Ruleresult= new double[swarm.getBestPosition().length];
							for(int m=0;m<Ruleresult.length;m++) {
								Ruleresult[m]=swarm.getBestPosition()[m];
							}
							globalfitness.put(swarm.getBestFitness(), Ruleresult);
//							globalfitness2.add(swarm.getBestFitness());
							swarm.setBestParticleIndex(now_i);
							swarm.setBestPosition(particles[now_i].getPosition());

						}
					}
				}
				//���񽻻�����5��
				//����������---ETC---makespans
				if (num >= 5) {
					double[] r = swarm.getBestPosition();//��¼���,����ά�����һά
					int[][] task_vm=new int[etc.length][etc[0].length];
					for(int j=0;j<task_vm.length;j++) {
						int vmId=(int)r[j];
						task_vm[j][vmId]=1;
					}
					//��¼makespan
					double[] makespans = new double[etc[0].length];
					for(int m=0;m<task_vm.length;m++) {
						for(int n=0;n<task_vm[0].length;n++) {
							if(task_vm[m][n]==1) {
								makespans[n]+=etc[m][n];
								
							}
							
							
						}
					}
					//makespan
//					for(int w=0;w<makespans.length;w++) {
//						System.out.print(makespans[w]+" ");
//					}
					
					System.out.println();
					//�����񽻺�Ľ��������ǰ���ŵ�λ�ü��������Ľ��
					task_vm = exchangeTask(makespans, etc, task_vm);
//					(int[][] task_vm, double[] nodeOkTime, double[][] etc)
//					task_vm = exchangeTask1(task_vm, makespans, etc);
					//1.���õ�ǰ�������ӵ�ID ע��!!!!�����ӵ�ID
					//1.1��ǰ�ǽ����񽻻���Ľ�������������ӵ�λ��,���ǵ�ǰ��
					//������û��������ŷ������,�����޷����������������ID
					//1.2��N��������,�ҵ�һ������,Ȼ���������,������������,��Ȼ
					//��Ҫ��������ӵ�λ�����óɵ�ǰ���ŵ���������񽻻���ķ�����
					double[] result = swarm.getBestPosition();
					for(int q=0;q<task_vm.length;q++) {
						for(int z=0;z<task_vm[0].length;z++) {
							if(task_vm[q][z]==1) {
								result[q]=z;
							}
						}
					}
					
					//��������λ��
//					System.out.println("==="+result.length);
					Particle bestParticle = swarm.getBestParticle();
					bestParticle.setBestPosition(result);
					swarm.setBestParticleIndex(swarm.getBestParticleIndex());
					
					double[] nowResult = new double[etc.length];
					for(int n=0;n<nowResult.length;n++) {//result�Ǹ���������,������Ҫ�õ�nowResult�洢һ��
						nowResult[n] = result[n];
					}
					swarm.setBestPosition(result);
					globalfitness.put(ex_max,nowResult);
//					System.out.println("��˭!!  "+ ff.calcMakespan(result)+ " "+ex_max);
					for(int n=0;n<result.length;n++) {
//						System.out.print(result[n]+" ");
					}
//					System.out.println();
					
				}
				System.out.println();
				System.out.printf("Gloabl best at iteration (%d): %f\n", i, swarm.getBestFitness());
				System.out.println("\nBest makespan: " + ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
			}
		}
//		globalfitness.put( swarm.getBestFitness(),swarm.getBestPosition());
		// hashmapȡ�����Ա�ֵ,����Сֵ
		min = java.lang.Double.MAX_VALUE;
//	    double[] position = swarm.getBestPosition();
//		for(Map.Entry<double[], java.lang.Double> entry : globalfitness.entrySet()) {
//			System.out.print("value: "+ entry.getValue()+" ");
//		}
//		System.out.println();
		
		for(Map.Entry<java.lang.Double,double[]> entry : globalfitness.entrySet()) {
//			System.out.println("entry.getValue():"+entry.getValue());
			if(entry.getKey()<min) {
				min=entry.getKey();
				swarm.setBestPosition(entry.getValue());
//				System.out.println("!!!!!!!!!min: "+min+"????entry.getKey(): "+ff.calcMakespan(entry.getValue()));
				for(int n=0;n<entry.getValue().length;n++) {
					System.out.print(entry.getValue()[n]+" ");
				}
				System.out.println();
			}
			
		}
		
//		System.out.println("min = "+min);
		
		System.out.println("\nThe best fitness value: " + min + "\nBest makespan: "
				+ ff.calcMakespan(swarm.getBestPosition()));

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

	public void printBestFitness() {
		System.out.println(swarm.getBestParticle().getBestPosition().length);
		System.out.println("\nBest fitness value: " + swarm.getBestFitness() + "\nBest makespan: "
				+ ff.calcMakespan(swarm.getBestParticle().getBestPosition()));
	}
	
	
	
public static int[][] exchangeTask(double[] makespans, double[][] etc, int[][] task_dis){
	
		boolean is_zdkjh = true; //�Ƿ��ҵ������µ��ȵĽڵ�
		boolean[] isceshi = new boolean[makespans.length];//��Rfrom�����������,�����ĳ�ڵ���û�з���ɹ��ͰѸýڵ���RtoΪtrue,
		boolean[] is_vm_id_ok = new boolean[makespans.length];//��ĳRfrom�ڵ��϶����ܷ���,�ͽ��ýڵ���Ϊtrue
		boolean is_vm_id = false;
		DecimalFormat    df   = new DecimalFormat("######0.00"); 
		
		
		for(int k=0;k<etc.length;k++) {
//			System.out.println(etc.length);
//		System.out.println("=============");
		int minid = 0;
		int maxid = 0;
		double min = java.lang.Double.MAX_VALUE;
		double max = 0;
		
		//��Rf,Rt�� id��
		
	   for(int i=0;i<makespans.length;i++) {
		   if(is_vm_id) {
			   if(!is_vm_id_ok[i]) { //[false,false,false]  === 1�ڵ㲻�����κ�һ���ڵ�����ʱ�� [true,false,false]//�жϼ�¼�ýڵ���,
					if(makespans[i]>max) {
						max = makespans[i];
						maxid = i;	//������¼����id
					}
				}
		   }else {
			   if(makespans[i]>max) {//��Ҫ���ڱ����Ĺ�����,����ɽ���
					if(!is_vm_id_ok[i]) {
						max = makespans[i];
						maxid = i;
					}
					
				}
		   }
		   
		   if(is_zdkjh) {
			   if(makespans[i]<min) {
					min = makespans[i];
					minid = i;
				}
		   }else { //false û�з��� [1,2,3] 
				if(!isceshi[i]) { //[false,false,false] [false,true,false]
					if(makespans[i]<min) { 
						min = makespans[i];
						minid = i;
					}
				}
			}
		   
		   
		   
//		  if(makespans[i]<min) {
//			  min=makespans[i];//RT
//			  minid=i;
//		  }
//		  
//		  if(makespans[i]>max) {
//			  max=makespans[i];//RF
//			  maxid=i;
//		  }
	   }

//	   System.out.println("min: "+ min);
//	   System.out.println("minid: "+ minid);
//	   System.out.println("max: "+ max);
//	   System.out.println("maxid: "+ maxid);
	   
	 //����������
//	 		for(int i=0;i<task_dis.length;i++) {
//	 			for(int j=0;j<task_dis[0].length;j++) {
//	 				System.out.print(task_dis[i][j]+" ");
//	 			}
//	 			System.out.println();
//	 		}
//	 		
//	 		//etc
//	 		//����������
//			for(int i=0;i<etc.length;i++) {
//				for(int j=0;j<etc[0].length;j++) {
//					System.out.print(etc[i][j]+" ");
//				}
//				System.out.println();
//			}
	   
	   min = java.lang.Double.MAX_VALUE;
	   int id = 0;
	   for(int i=0;i<etc.length;i++) {
		   if(task_dis[i][maxid]==1) {
			   if(etc[i][minid]<min){
				   min=etc[i][minid];
				   id=i;
				   is_zdkjh = false;
			   }
		   }
	   }
//	   System.out.println("min: "+ min );
//	   System.out.println("id: "+ id);
	   
	   
	   if(makespans[minid]+etc[id][minid]<makespans[maxid]) {//1+2
		   makespans[minid]=makespans[minid]+etc[id][minid];
		   makespans[maxid]=makespans[maxid]-etc[id][maxid];
		   task_dis[id][minid]=1;
		   task_dis[id][maxid]=0;
		   is_zdkjh = true;//��ǰ���Է���
		   isceshi = new boolean[makespans.length];
		   is_vm_id_ok = new boolean[makespans.length];
//		   System.out.println("min: "+ min+"etc[id][minid] :"+ etc[id][minid]+"makespans[maxid]:"+makespans[maxid]);
	   }
	   
	   if(!is_zdkjh) { //min_vmid�޷���������
			isceshi[minid] = true;
		}
	   
	   is_vm_id = true;
		is_vm_id_ok[maxid] = true;
		for(int i=0;i<isceshi.length;i++) 
		{
			if(!isceshi[i]) {//���жϵ�ʱ��,���ܴ���û����������,��ʱ�ȱ��f,�ȶ�������֮�󶼲��ܽ������񽻻�,��ȥ�ҵڶ���.
				is_vm_id = false;
				is_vm_id_ok[maxid] = false;
				break;
			}
		}
		
		if(is_vm_id) {//����������껹�ǲ��ܽ�������,�Ͱ����еı��false,Ϊ����һ���ҵڶ�����׼��.
			isceshi = new boolean[makespans.length];
		}
		
	}
		double max=0;
		for(int i=0;i<makespans.length;i++) {
			if(makespans[i]>max) {
				max=makespans[i];
			}
			
		}
//		System.out.println("max: "+ max);
		ex_max=max;
//		//����������
//		for(int i=0;i<task_dis.length;i++) {
//			for(int j=0;j<task_dis[0].length;j++) {
//				System.out.print(task_dis[i][j]+" ");
//			}
//			System.out.println();
//		}
//		
		
			int taskVmnum = etc.length;
//		System.out.println("��������= "+taskVmnum);
		for(int i=0;i<task_dis.length;i++) {
			for(int j=0;j<task_dis[0].length;j++) {
				if(task_dis[i][j] == 1) {
					taskVmnum = taskVmnum - 1;
				}
			}
		}
		
//		System.out.println("ʣ������= "+taskVmnum);
		
		
		//20210625��֤ ���¼���
		double[] mkspyz = new double[etc[0].length];
//		System.out.println("task_vm.length= "+task_dis.length);
//		System.out.println("task_vm[0].length "+task_dis[0].length);
//		System.out.println("etc.length= "+etc.length);
//		System.out.println("etc[0].length= "+etc[0].length);
		for(int i=0;i<task_dis.length;i++) {
			for(int j=0;j<task_dis[0].length;j++) {
				if(task_dis[i][j] == 1) {
					mkspyz[j] = mkspyz[j] + etc[i][j];
				}
			}
		}
//		System.out.print("YZX1_SH_YZX_WQYZX=");
//		for(int i=0;i<mkspyz.length;i++) {
//			System.out.print(df.format(mkspyz[i])+" ");
//		}
		
		return task_dis;
	}




public int[][] exchangeTask1(int[][] task_vm, double[] nodeOkTime, double[][] etc){
	
	long startTime = System.currentTimeMillis();    //��ȡ��ʼʱ��
	DecimalFormat    df   = new DecimalFormat("######0.00"); 
	
	boolean is_zdkjh = true; //�Ƿ��ҵ������µ��ȵĽڵ�
	boolean[] isceshi = new boolean[nodeOkTime.length];
	boolean[] is_vm_id_ok = new boolean[nodeOkTime.length];
	boolean is_vm_id = false; //�����ж��Ƿ�������ֵ�� �Ѿ��޷������κ�����
	
	//ÿ�ε���N��
	for(int k=0;k<etc.length;k++) {
		
		//
		/**
		 * 1.�ҵ�����makespan �� ��Сmakespan
		 * 2.�ҵ�makespan�з�������� �Լ� ��Щ������ ��Сmakespan����С������
		 * 3�����¼�����Сmakespan
		 * 4���Ƚ���Сmakespan��ԭʼ���makespan�Ĵ�С
		 * 5.�������ԭʼ���makespan ��������� 
		 * 4�����·���������makespan ���½��ж��ε���
		 * ####
		 *  δ���а��շ���ֵ��������
		 * ####
		 * 
		 */
		double max = 0.0;
		double min = java.lang.Double.MAX_VALUE;
		//Rfrom
		int max_vmid = 0;
		//Rto
		int min_vmid = 0;
		
		for(int i=0;i<nodeOkTime.length;i++) {
			
			//Ѱ����Ҫ�������������
			if(is_vm_id) //���Ϊtrue ����� ���MAX �޷����κ�һ���������������
			{
				if(!is_vm_id_ok[i]) { //�ų�֮ǰ�Թ��Ľ��
					if(nodeOkTime[i]>max) { //�ҵ���ǰ���� ����
						max = nodeOkTime[i];
						max_vmid = i;
					}
				}
			}else //Ѱ�������Ľ��
			{
				if(nodeOkTime[i]>max) { //Ѱ�����Ľ��
					if(!is_vm_id_ok[i]) {
						max = nodeOkTime[i];
						max_vmid = i;
					}
					
				}
			}
			
			
			
			//���Ϊtrue ������ϴο��������Ľ��з��� ��ô���Ѱ����С����Ѱ�����е�id
			if(is_zdkjh) 
			{
				if(nodeOkTime[i]<min) {
					min = nodeOkTime[i];
					min_vmid = i;
				}
			}else {  
				if(!isceshi[i]) {
					if(nodeOkTime[i]<min) {
						min = nodeOkTime[i];
						min_vmid = i;
					}
				}
			}
		}//~for Ѱ�ҽ���
		
		//��ʼѵ����Сִ��ʱ�������
		min = java.lang.Double.MAX_VALUE;
		int min_taskid = 0;
		//��¼���makespan�����е�����
		for(int i=0;i<task_vm.length;i++) {
			if(task_vm[i][max_vmid] == 1) {
				if(etc[i][min_vmid] < min) {
					min = etc[i][min_vmid];
					min_taskid = i;
					is_zdkjh = false;
				}
			}
	    }//~�ҵ���Сִ��ʱ������� ����
		
		//��ʼ�ж��Ƿ��ܹ����е���
		if(nodeOkTime[min_vmid] +etc[min_taskid][min_vmid]  < nodeOkTime[max_vmid]) {
			nodeOkTime[min_vmid] = nodeOkTime[min_vmid] + etc[min_taskid][min_vmid];
			nodeOkTime[max_vmid] = nodeOkTime[max_vmid] - etc[min_taskid][max_vmid];
			task_vm[min_taskid][min_vmid] = 1;
			task_vm[min_taskid][max_vmid] = 0;
			is_zdkjh = true;
			isceshi = new boolean[nodeOkTime.length];
			is_vm_id_ok = new boolean[nodeOkTime.length];
//			System.out.println("����� "+(max_vmid+1)+" �е����� "+(min_taskid+1)+" ������ "+"����� "+(min_vmid+1));
		}//~��������
		
		if(!is_zdkjh) { //min_vmid�޷���������
			isceshi[min_vmid] = true;
		}
		
		is_vm_id = true; //�����ж��ǲ��ǵ�ǰ���ID �Ѿ� ����һ����
		//����������ڼ�¼��ǰ�����ID ����Ҫ���� Ѱ�Ҳ���
		is_vm_id_ok[max_vmid] = true;
		for(int i=0;i<isceshi.length;i++) 
		{
			//ֻҪ��һ����ǰ����СID���� Ϊ false ˵���ڵ�ǰ�����ID���øı�
			if(isceshi[i] != true) { 
				//����ʹ�õ�ǰ�����ID
				is_vm_id = false;
				//���ID���ɿ���ʹ��
				is_vm_id_ok[max_vmid] = false;
				break;
			}
		}
		
		if(is_vm_id) {
			isceshi = new boolean[nodeOkTime.length];
		}
		
		
	}//~�涨�ĵ�������
	
	
	double max_ = 0.0;
	System.out.print("YZX1_SH_YZX_WQYZX=");
	for(int i=0;i<nodeOkTime.length;i++) { //m
		System.out.print(df.format(nodeOkTime[i])+" ");
		if(nodeOkTime[i]>max_) {
			max_ = nodeOkTime[i];
		}
	}
	System.out.println();
//	System.out.println("YZX1_SH_YZX_WQYZX(MAKESPAN)="+df.format(max_));
	long endTime = System.currentTimeMillis();    //��ȡ����ʱ��
	System.out.println("#####################"+" ����ʱ��"+(endTime - startTime) + "ms");
	this.ex_max = max_;
	return task_vm;
}




	public static void main(String[] args) {
	     double[][] etc9 = {
					      {1,2,3},
					      {2,4,6},
					      {15,30,45},
					      {22.5,45,67.5},
					      {50,100,150},
					      {100,200,300},
					    };
	}

	
}