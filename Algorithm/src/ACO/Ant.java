package ACO;

import java.util.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 *������
 *@author Gavrila
 */
 public class Ant{
	public class position{
		public int vm;
		public int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	} 
	public Ant(double[][] etc,double[][] commMatrix) {
		this.etc=etc;
		this.commMatrix=commMatrix;
	}
	public double[][] delta;//ÿ���ڵ����ӵ���Ϣ��
	public int Q = 100;
	public List<position> tour;//���ϻ�õ�·�����⣬��������������ķַ���
	public double tourLength;//���ϻ�õ�·�����ȣ�����ú��ܵĻ���ʱ�䣩
	public long[] TL_task;//ÿ�����������������
	public List<Integer> tabu;//���ɱ�
	private int VMs;//���еĸ������൱��������ĸ�����
	private int tasks;//�������
//	private List<? extends Cloudlet> cloudletList;	//�������б�
//	private List<? extends Vm> vmList;				//������б�
	private double[][] etc;
	private static double[][] commMatrix;
	/**
	 *����������ϵ�ĳ���ڵ��У�ͬʱ������ϰ����ֶεĳ��Ի�����
	 *@param list1 �����б�
	 *@param list2 ������б�
	 */
	public void RandomSelectVM(int ts, int Vs){
		this.tasks=ts;
		this.VMs=Vs;
		delta = new double[this.VMs][this.tasks]; //���������
		TL_task = new long[this.VMs];
		for(int i=0; i<this.VMs; i++)TL_task[i] = 0;
		tabu = new ArrayList<Integer>();
		tour=new ArrayList<position>();
		
		//���ѡ�����ϵ�λ��
		int firstVM = (int)(this.VMs*Math.random());
		int firstExecute = (int)(this.tasks*Math.random());
		tour.add(new position(firstVM, firstExecute));
		tabu.add(new Integer(firstExecute));
		//��������
//		TL_task[firstVM] += cloudletList.get(firstExecute).getCloudletLength();
	}
	/**
	  * calculate the expected execution time and transfer time of the task on vm
	  * @param vm ��������
	  * @param task �������
	  */
	public double Dij(int vm, int task){
		double d;
//		System.out.println("==="+this.tasks+"==="+this.VMs);
	    d =  this.etc[task][vm]+this.commMatrix[task][vm];
//	    d =  this.etc[task][vm];
	    //����
	    
		return d;
	}
	 /**
	  * ѡ����һ���ڵ�
	  * @param pheromone ȫ�ֵ���Ϣ����Ϣ
	  */
	  public void SelectNextVM(double[][] pheromone){
		  double[][] p;//ÿ���ڵ㱻ѡ�еĸ���
		  p = new double[VMs][tasks];
		  double alpha = 1.0;
		  double beta = 1.0;
		  double sum = 0;//��ĸ
		  //���㹫ʽ�еķ�ĸ����  
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  if(tabu.contains(new Integer(j))) continue;
				  sum += Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta);
			  }
		  }
		  //����ÿ���ڵ㱻ѡ�ĸ���
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  p[i][j] = Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta)/sum;
				  if(tabu.contains(new Integer(j)))p[i][j] = 0;
			  }
		  }
		double selectp = Math.random();
        //���̶�ѡ��һ��VM
        double sumselect = 0;
        int selectVM = -1;
        int selectTask = -1;
        boolean flag=true;
        for(int i=0; i<VMs&&flag==true; i++){
        	for(int j=0; j<tasks; j++){
        		sumselect += p[i][j];
        		if(sumselect>=selectp){
        			selectVM = i;
        			selectTask = j;
        			flag=false;
        			break;
        		}
        	}
        }
        if (selectVM==-1 | selectTask == -1)  
            System.out.println("ѡ����һ�������û�гɹ���");
    		tabu.add(new Integer(selectTask));
		tour.add(new position(selectVM, selectTask));
//		TL_task[selectVM] += cloudletList.get(selectTask).getCloudletLength();  		
	  }
	  
	  
	  //makespan
	public void CalTourLength(){
		System.out.println();
		double[] max;
		max = new double[this.VMs];
		for(int i=0; i<tour.size(); i++){
			max[tour.get(i).vm] += this.etc[tour.get(i).task][tour.get(i).vm]+this.commMatrix[tour.get(i).task][tour.get(i).vm]; //?
			//����makespan,�����������
//			max[tour.get(i).vm] += this.etc[tour.get(i).task][tour.get(i).vm];
//			max[tour.get(i).vm] += cloudletList.get(tour.get(i).task).getCloudletLength()/vmList.get(tour.get(i).vm).getMips(); 
		}		
		tourLength = max[0];
		for(int i=0; i<this.VMs; i++){
			if(max[i]>tourLength)tourLength = max[i];
			System.out.println("��"+i+"̨�������ִ��ʱ�䣺"+max[i]);
		}
		return;
	}
	/**
	 * ������Ϣ����������
	 */
    public void CalDelta(){
    	for(int i=0; i<this.VMs; i++){
    		for(int j=0; j<tasks; j++){
    			if(i==tour.get(j).vm&&tour.get(j).task==j)delta[i][j] = Q/tourLength;
    			else delta[i][j] = 0;
    		}
    	}
    }
 }
