package IPSO_NA;

import utils.GenerateMatrices;

public class 矩阵例子q {
	
	private static double[][] commMatrix;
    private static double[][] execMatrix;
    private static double mapping[];
    private static PSO PSOSchedularInstance;
	
	public static void main(String[] args) {
		
		// 定义etc矩阵
		double[][] etc = { 
				{ 1, 2, 3, 4 }, 
				{ 2, 3, 4, 2 }, 
				{ 1, 3, 5, 2 }, 
				{ 3, 2, 1, 4 } };
		
		
		//加etc矩阵
		new GenerateMatrices();
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
        
		//[1,3,5,4]
		//[2,3,0,4]
		// 任务分配矩阵
		int[][] task_dis = { 
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,0,1}};
		
		PSOSchedularInstance = new PSO();
        mapping = PSOSchedularInstance.run();
		//[1,3,5,4]
		/*
		 {1,0,0,0},
		 {0,1,0,0},
		 {1,0,0,0},
		 {0,0,0,1}}
		 //[2,3,0,4]
		 * 
		 * */
		//定义makespan
		double[] makespans = new double[etc[0].length];
		//计算makespan
		for(int i=0;i<task_dis.length;i++) {
			for(int j=0;j<task_dis[0].length;j++) {
				if(task_dis[i][j]==1) {
					makespans[j]+=etc[i][j];
				}
//				System.out.println("etc[i][j]"+etc[i][j]);
				
			}
		}
		
		boolean is_zdkjh = true; //是否找到可重新调度的节点
		boolean[] isceshi = new boolean[makespans.length];//在Rfrom不变的条件下,如果在某节点上没有分配成功就把该节点标记Rto为true,
		boolean[] is_vm_id_ok = new boolean[makespans.length];//在某Rfrom节点上都不能分配,就将该节点标记为true
		boolean is_vm_id = false;
		
		
		
		for(int k=0;k<etc.length;k++) {
		
		int minid = 0;
		int maxid = 0;
		double min = Double.MAX_VALUE;
		double max = 0;
		
		//找Rf,Rt的 id※
		
	   for(int i=0;i<makespans.length;i++) {
		   if(is_vm_id) {
			   if(!is_vm_id_ok[i]) { //[false,false,false]  === 1节点不能在任何一个节点分配的时候 [true,false,false]//判断记录该节点上,
					if(makespans[i]>max) {
						max = makespans[i];
						maxid = i;	//用来记录最大的id
					}
				}
		   }else {
			   if(makespans[i]>max) {//主要是在遍历的过程中,任务可交换
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
		   }else { //false 没有分配 [1,2,3] 
				if(!isceshi[i]) { //[false,false,false] [false,true,false]
					if(makespans[i]<min) { 
						min = makespans[i];
						minid = i;
					}
				}
			}
		   
		   
		   
		  if(makespans[i]<min) {
			  min=makespans[i];//RT
			  minid=i;
		  }
		  
		  if(makespans[i]>max) {
			  max=makespans[i];//RF
			  maxid=i;
		  }
	   }

	   System.out.println("min: "+ min);
	   System.out.println("minid: "+ minid);
	   System.out.println("max: "+ max);
	   System.out.println("maxid: "+ maxid);
	   
	   min = Double.MAX_VALUE;
	   int id = 0;
	   for(int i=0;i<etc.length;i++) {
		   if(task_dis[i][maxid]==1) {
			   if(etc[i][minid]<min){
				   min=etc[i][minid];
				   id=i;
			   }
		   }
	   }
	   System.out.println("min: "+ min );
	   System.out.println("id: "+ id);
	   
	   
	   if(makespans[minid]+etc[id][minid]<makespans[maxid]) {//1+2
		   makespans[minid]=makespans[minid]+etc[id][minid];
		   makespans[maxid]=makespans[maxid]+etc[id][maxid];
		   task_dis[id][minid]=1;
		   task_dis[id][maxid]=0;
		   is_zdkjh = true;//当前可以分配
		   isceshi = new boolean[makespans.length];
		   is_vm_id_ok = new boolean[makespans.length];
		   System.out.println("min: "+ min+"etc[id][minid] :"+ etc[id][minid]+"makespans[maxid]:"+makespans[maxid]);
	   }
	   
	   if(!is_zdkjh) { //min_vmid无法分配任务
			isceshi[minid] = true;
		}
	   
	   is_vm_id = true;
		is_vm_id_ok[maxid] = true;
		for(int i=0;i<isceshi.length;i++) 
		{
			if(!isceshi[i]) {//在判断的时候,可能存在没有找完的情况,暂时先变成f,等都遍历完之后都不能进行任务交换,再去找第二大.
				is_vm_id = false;
				is_vm_id_ok[maxid] = false;
				break;
			}
		}
		
		if(is_vm_id) {//如果都遍历完还是不能交换任务,就把所有的变成false,为了下一次找第二大做准备.
			isceshi = new boolean[makespans.length];
		}
	}
		
		
		//输出分配矩阵
		for(int i=0;i<task_dis.length;i++) {
			for(int j=0;j<task_dis[0].length;j++) {
				System.out.print(task_dis[i][j]+" ");
			}
			System.out.println();
		}
	}

}
