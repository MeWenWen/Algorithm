package ACO;

import java.util.*;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * ��Ⱥ�Ż��㷨�����������������������ﵽʱ����̵�����
 * @author Gavrila
 */
public class ACO {
	public class position{
		int vm;
		int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	}
	private List<Ant> ants;//��������Ⱥ
	private int antcount;//���ϵ�����
	private int Q = 100;
	private double[][] pheromone;//��Ϣ�ؾ���
	private double[][] Delta;//�ܵ���Ϣ������
	private int VMs;//���������
	private int tasks;//�������
	public position[] bestTour;//��ѽ�
	private double bestLength;//���Ž�ĳ��ȣ�ʱ��Ĵ�С��
	private List<? extends Cloudlet> cloudletList;
	private List<? extends Vm> vmList;
	/**
	 * ��ʼ������
	 * @param antNumΪϵͳҪ�õ����ϵ�����
	 */
	public void init(int antNum, int ts, int Vs,double[][] etc,double[][] commMatrix){
		//cloudletList = new ArrayList<? extends Cloudlet>;
		this.tasks=ts;
		this.VMs=Vs;
		antcount = antNum;
		ants = new ArrayList<Ant>(); 
		pheromone = new double[this.VMs][this.tasks];
		Delta = new double[this.VMs][this.tasks];
		bestLength = 1000000;
		//��ʼ����Ϣ�ؾ���
		for(int i=0; i<this.VMs; i++){
			for(int j=0; j<this.tasks; j++){
				pheromone[i][j] = 0.1;
			}
		}
		bestTour = new position[this.tasks];
		for(int i=0; i<this.tasks; i++){
			bestTour[i] = new position(-1, -1);
		}
		//�����������  
        for(int i=0; i<antcount; i++){  
            ants.add(new Ant(etc,commMatrix));  
            ants.get(i).RandomSelectVM(this.tasks, this.VMs);
        }  			
	}
	/**
	 * ACO�����й���
	 * @param maxgen ACO������������
	 */
	public void run(int maxgen){
		double min=0;
		for(int runTime=0; runTime<maxgen; runTime++){
			System.out.println("��"+runTime+"�Σ�");
			//ÿֻ�����ƶ��Ĺ���
			for(int i=0; i<antcount; i++){
				for(int j=1; j<tasks; j++){	
					ants.get(i).SelectNextVM(pheromone);
				}
			}
			for(int i=0; i<antcount; i++){
				System.out.println("��"+i+"ֻ����");
				ants.get(i).CalTourLength();
				System.out.println("��"+i+"ֻ���ϵ�·�̣�"+ants.get(i).tourLength);
				ants.get(i).CalDelta();
				if(ants.get(i).tourLength<bestLength){  
					//��������·��  
	                bestLength = ants.get(i).tourLength;  
	                System.out.println("=========="+ants.get(i).tourLength);
	                System.out.println("��"+runTime+"��"+"��"+i+"ֻ���Ϸ����µĽ⣺"+bestLength);   
	                for(int j=0;j<tasks;j++){  
	                	bestTour[j].vm = ants.get(i).tour.get(j).vm;
	                    bestTour[j].task = ants.get(i).tour.get(j).task;
	                } 
	               
	                System.out.println();
	                //�Է������Ž��·������Ϣ��
	                for(int k=0; k<VMs; k++){
	                	for(int j=0; j<tasks; j++){
	                		pheromone[k][j] = pheromone[k][j] + Q/bestLength;
	                	}
	                }  
				}
			}
			UpdatePheromone();//��ÿ��·������Ϣ��
				
			//���������������  
			for(int i=0;i<antcount;i++){  
				ants.get(i).RandomSelectVM(this.tasks, this.VMs);  
		    }  	
		}
	}
	/** 
     * ������Ϣ�ؾ��� 
     */  
	public void UpdatePheromone(){
		double rou=0.5;  
        for(int k=0; k<antcount; k++){
        	for(int i=0; i<VMs; i++){
        		for(int j=0; j<tasks; j++){
        			Delta[i][j] += ants.get(k).delta[i][j];
        		}
        	}
        }
        
        for(int i=0; i<VMs; i++){
        	for(int j=0; j<tasks; j++){
        		pheromone[i][j] = (1-rou)*pheromone[i][j] + Delta[i][j];
        	}
        }  
	}
	/** 
     * ����������н�� 
     */  
    public position[] ReportResult(){  
        System.out.println("����·��������"+bestLength);
        for(int j=0; j<tasks; j++)
        {
        	System.out.println(bestTour[j].task+"�������"+bestTour[j].vm);
        }
        return bestTour;
    }  	
}
