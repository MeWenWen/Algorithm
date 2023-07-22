package MAXMIN;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MAMIN {
	/**
	 * MAX-MIN 算法
	 * @param etc
	 * @param vm_num
	 */
	public static int[][] MAX_MIN(double[][] etc,int vm_num){
		long startTime = System.currentTimeMillis();    //获取开始时间
		DecimalFormat    df   = new DecimalFormat("######0.00"); 
		List<String> List_remove = new ArrayList<String>();
		int[][] task_vm = new int[etc[0].length][etc.length];
		double[] delay_time = new double[vm_num]; 
		//etc.length 任务数量
		for(int n=0;n<etc.length;n++) {
			double total_min_etc = 0;
			int total_idx_h = 1000;
			int total_idx_l = 1000;
			for(int i=0;i<etc.length;i++) {
				
				if(!List_remove.contains(Integer.toString(i))) {
					double min_etc = Double.MAX_VALUE;
					//横坐标
					int idx_h = 0; 
					//列坐标
					int idx_l = 0;
					for(int j=0;j<etc[0].length;j++) {
						if(etc[i][j]+delay_time[j]<min_etc) {
							min_etc = etc[i][j]+delay_time[j];
							idx_h = i;
							idx_l = j;
						}
					}
					
					if(total_min_etc<min_etc) {
						total_min_etc = min_etc;
						total_idx_h = idx_h;
						total_idx_l = idx_l;
					}
					//输出每一个最大值
					//System.out.println("min_etc="+min_etc);
				}
				
				
			}
			//System.out.println("=============================="+total_min_etc);
			task_vm[total_idx_l][total_idx_h] = 1;
			//System.out.println("将任务 "+(total_idx_h+1)+" 分配给虚拟机 "+(total_idx_l+1));
			List_remove.add(Integer.toString(total_idx_h));
			delay_time[total_idx_l] =  total_min_etc;
			
		}
		
//		//输出分配矩阵
//		for(int j=0;j<task_vm[0].length;j++) {
//			for(int i=0;i<task_vm.length;i++) {
//				System.out.print(task_vm[i][j]+" ");
//			}
//			System.out.println();
//		}
		
		System.out.print("MAX_MIN=");
		double max = 0.0;
		for(int i=0;i<delay_time.length;i++) {
			System.out.print(df.format(delay_time[i])+" ");
			if(delay_time[i]>max) {
				max = delay_time[i];
			}
		}
		System.out.println();
		System.out.print("MAX_MIN(MAKESPAN)="+df.format(max));
		System.out.println();
		long endTime = System.currentTimeMillis();    //获取结束时间
		System.out.println("#####################"+" 运行时间"+(endTime - startTime) + "ms");
		
/*	验证结果是否正确使用可以注释掉			
		//2021025后补 验证是否所有的任务都在任务分配矩阵中
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
		return task_vm;
	}
}
