package MINMIN;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MMIN {
	/**
	 * MIN-MIN算法
	 * @param etc    ETC矩阵
	 * @param vm_num 虚拟机数量
	 * @param printAMT 是否打印分配矩阵
	 */
	public static int[][] MIN_MIN(double[][] etc,int vm_num,boolean printVMT){
		long startTime = System.currentTimeMillis();    //获取开始时间
		DecimalFormat    df   = new DecimalFormat("######0.00"); 
		List<String> List_remove = new ArrayList<String>();
		int[][] task_vm = new int[etc[0].length][etc.length];
		double[] delay_time = new double[vm_num]; 
		for(int n=0;n<etc.length;n++) {
			double total_min_etc = Double.MAX_VALUE;
			int total_idx_h = 0;
			int total_idx_l = 0;
			for(int i=0;i<etc.length;i++) {
				
				if(!List_remove.contains(Integer.toString(i))) {
					double min_etc =  Double.MAX_VALUE;
					//横坐标
					int idx_h = 0; 
					//列坐标
					int idx_l = 0;
					for(int j=0;j<etc[0].length;j++) { //找最小完成时间
						if(etc[i][j]+delay_time[j]<min_etc) {
							min_etc = etc[i][j]+delay_time[j];
							idx_h = i;
							idx_l = j;
						}
					}
					
					if(total_min_etc>min_etc) {
						total_min_etc = min_etc;
						total_idx_h = idx_h;
						total_idx_l = idx_l;
					}
					//输出每一个最小值
					//System.out.println("min_etc="+min_etc);
				}
				
				
			}
			//System.out.println("=============================="+total_min_etc);
			List_remove.add(Integer.toString(total_idx_h));
			task_vm[total_idx_l][total_idx_h] = 1;
//			System.out.println("将任务 "+(total_idx_h+1)+" 分配给虚拟机 "+(total_idx_l+1));
			delay_time[total_idx_l] =  total_min_etc;
			
		}
		
		if(printVMT) {
			System.out.println("********MIN-MIN分配矩阵********");
			//输出分配矩阵
			for(int j=0;j<task_vm[0].length;j++) {
				for(int i=0;i<task_vm.length;i++) {
					System.out.print(task_vm[i][j]+" ");
				}
				System.out.println();
			}
			System.out.println("********MIN-MIN分配矩阵********");
		}
		

		
		System.out.print("MIN_MIN=");
		double max = 0.0;
		for(int i=0;i<delay_time.length;i++) {
			System.out.print(df.format(delay_time[i])+" ");
			if(delay_time[i]>max) {
				max = delay_time[i];
			}
		}
		System.out.println();
		System.out.print("MIN_MIN(MAKESPAN)="+df.format(max));
		System.out.println();
		long endTime = System.currentTimeMillis();    //获取结束时间
		System.out.println("################################"+" 运行时间"+(endTime - startTime) + "ms");
		return task_vm;
		
/*	验证结果是否正确使用可以注释掉	
		//2021025后补 验证分配矩阵
		int taskVmnum = etc.length;
		System.out.println("任务数量= "+taskVmnum);
		for(int i=0;i<task_vm.length;i++) {
			for(int j=0;j<task_vm[0].length;j++) {
				if(task_vm[i][j] == 1) {
					taskVmnum = taskVmnum - 1;
				}
			}
		}
		
		System.out.println("剩余任务= "+taskVmnum);
		
		//20210625验证 重新计算
		double[] mkspyz = new double[vm_num];
		System.out.println("task_vm.length= "+task_vm.length);
		System.out.println("task_vm[0].length "+task_vm[0].length);
		
		for(int i=0;i<task_vm.length;i++) {
			for(int j=0;j<task_vm[0].length;j++) {
				if(task_vm[i][j] == 1) {
					mkspyz[i] = mkspyz[i] + etc[j][i];
				}
			}
		}
		
		System.out.print("MAX_MIN=");
		for(int i=0;i<delay_time.length;i++) {
			System.out.print(df.format(mkspyz[i])+" ");

		}
		System.out.println();
		System.out.println("============================================");

*/		
	}
	
}