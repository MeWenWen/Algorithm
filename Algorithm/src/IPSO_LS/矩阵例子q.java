package IPSO_LS;

import java.text.DecimalFormat;
import java.util.Random;

import utils.GenerateMatrices;

public class ��������q {
	
	private static double[][] commMatrix;
    private static double[][] execMatrix;
	
	public static void main(String[] args) {
		
		// ����etc����
//		double[][] etc = { 
//				{ 1, 2, 3, 4 }, 
//				{ 2, 3, 4, 2 }, 
//				{ 1, 3, 5, 2 }, 
//				{ 3, 2, 1, 4 } };
		double[][] etc = { 
				{ 5, 2, 3, 4 }, 
				{ 1, 3, 2, 7 }, 
				{ 2, 4, 1, 3 }, 
				{ 3, 2, 5, 9 }, 
				{ 3, 5, 1, 3},
				{ 4, 6, 5, 1},
				{ 1, 1, 3, 4},
				{ 9, 2, 6, 3},
				{ 2, 4, 4, 3},
				{ 4, 2, 2, 3}};
		
		//��etc����
		new GenerateMatrices();
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
        
		//[1,3,5,4]
		//[2,3,0,4]
		// ����������
		int[][] task_dis = { 
				{1,0,0,0},
				{0,1,0,0},
				{1,0,0,0},
				{0,0,0,1},
				{0,1,0,0},
				{0,1,0,0},
				{1,0,0,0},
				{0,0,1,0},
				{0,1,0,0},
				{1,0,0,0}};
		
		
//		mapping = PSOSchedularInstance.run();
		//[1,3,5,4]
		
		//����makespan
		double[] makespans = new double[etc[0].length];
		//����makespan
		for(int i=0;i<task_dis.length;i++) {
			for(int j=0;j<task_dis[0].length;j++) {
				if(task_dis[i][j]==1) {
					makespans[j]+=etc[i][j];
				}
				System.out.print("etc[i][j]"+etc[i][j]);
				
			}
			System.out.println();
		}
		
		exchangeTask(makespans, etc, task_dis);
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
		double min = Double.MAX_VALUE;
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

	   System.out.println("min: "+ min);
	   System.out.println("minid: "+ minid);
	   System.out.println("max: "+ max);
	   System.out.println("maxid: "+ maxid);
	   
	 //����������
	 		for(int i=0;i<task_dis.length;i++) {
	 			for(int j=0;j<task_dis[0].length;j++) {
	 				System.out.print(task_dis[i][j]+" ");
	 			}
	 			System.out.println();
	 		}
//	 		
//	 		//etc
//	 		//����������
			for(int i=0;i<etc.length;i++) {
				for(int j=0;j<etc[0].length;j++) {
					System.out.print(etc[i][j]+" ");
				}
				System.out.println();
			}
	   
	   min = Double.MAX_VALUE;
	   int id = 0;
	   //�����һ���±���?
	   
	   for(int i=0;i<etc.length;i++) {
		   if(task_dis[i][maxid]==1) {
			   //���
			   int index =new Random().nextInt(etc[i].length);
			   min=etc[i][index];
			   id = index;
//			   if(etc[i][minid]<min){
//				   min=etc[i][minid];
//				   id=i;
//			   }
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
		System.out.println("max: "+ max);
//		//����������
//		for(int i=0;i<task_dis.length;i++) {
//			for(int j=0;j<task_dis[0].length;j++) {
//				System.out.print(task_dis[i][j]+" ");
//			}
//			System.out.println();
//		}
//		
		
//			int taskVmnum = etc.length;
//		System.out.println("��������= "+taskVmnum);
//		for(int i=0;i<task_dis.length;i++) {
//			for(int j=0;j<task_dis[0].length;j++) {
//				if(task_dis[i][j] == 1) {
//					taskVmnum = taskVmnum - 1;
//				}
//			}
//		}
//		
//		System.out.println("ʣ������= "+taskVmnum);
//		
//		
//		//20210625��֤ ���¼���
//		double[] mkspyz = new double[etc[0].length];
//		System.out.println("task_vm.length= "+task_dis.length);
//		System.out.println("task_vm[0].length "+task_dis[0].length);
//		System.out.println("etc.length= "+etc.length);
//		System.out.println("etc[0].length= "+etc[0].length);
//		for(int i=0;i<task_dis.length;i++) {
//			for(int j=0;j<task_dis[0].length;j++) {
//				if(task_dis[i][j] == 1) {
//					mkspyz[j] = mkspyz[j] + etc[i][j];
//				}
//			}
//		}
//		System.out.print("YZX1_SH_YZX_WQYZX=");
//		for(int i=0;i<mkspyz.length;i++) {
//			System.out.print(df.format(mkspyz[i])+" ");
//
//		}
		
		
		
		return task_dis;
	}

}
