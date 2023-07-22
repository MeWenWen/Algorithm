package Tabu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import utils.Constants;
import utils.GenerateMatrices;

public class TABU {

	private double[][] ct;

//	double[][] ct = { 
//			{ 1, 2, 3 }, 
//			{ 2, 3, 4}, 
//			{ 1, 3, 5}, 
//			{ 3, 2, 1} };
	private int cloudletNum;
	private int vmNum;
	private int[][] plan;
	private double mipss;
	private double sum;
	private double vl_rg = 0;

	public TABU(double[][] ct) {
		this.ct = ct;
	}

	public static void main(String[] args) {
		double[][] ct = { { 1, 2, 3 }, { 2, 3, 4 }, { 1, 3, 5 }, { 3, 2, 1 } };
		TABU ta = new TABU(ct);
		ta.bindUseTabu();
	}

	
	public double[] RandomTabu() {
		cloudletNum = ct.length;
		vmNum = ct[0].length;
		// �������
		plan = new int[cloudletNum][vmNum];
		Random rand = new Random();
		for(int i=0;i<plan.length;i++) {
			plan[i][rand.nextInt(plan[0].length)]=1;
		}
		return null;
		
	}
	
	// BP-Tabu�㷨
	public double[] bindUseTabu() {
		cloudletNum = ct.length;
		vmNum = ct[0].length;
		// ��ʼ�����䷽��ȫΪ0
		plan = new int[cloudletNum][vmNum];
		for (int i = 0; i < cloudletNum; i++) {
			for (int j = 0; j < vmNum; j++) {
				sum += ct[i][j];
			}

		}
		vl_rg = sum / (cloudletNum * vmNum);// ƽ��ÿ�������ִ��ʱ��=�����ܳ���/�������mips
//		timeAwared();// 1.��ʼ��etc����,2.��̰���㷨��ʼ���������
		RandomTabu();//�����ʼ
		tabu();
		double[] makespan = new double[ct[0].length];

		for (int i = 0; i < plan.length; i++) {
			for (int j = 0; j < plan[0].length; j++) {
				if (plan[i][j] == 1) {
					makespan[j] += ct[i][j];
				}
			}
			
		}
		for(int i=0;i<makespan.length;i++) {
			System.out.println("makespan["+i+"]:"+makespan[i]);
		}
//		for(int i=0;i<plan.length;i++) {
//			for(int j=0;j<plan[0].length;j++) {
//				System.out.print(plan[i][j]);
//			}System.out.println();
//		}

		double[] results = new double[ct.length];
		for (int i = 0; i < plan.length; i++) {
			for (int j = 0; j < ct[0].length; j++) {
				if (plan[i][j] == 1) {
					results[i] = j;
				}
			}
		}
		return results;

//		for(int p = 0 ; p < cloudletNum;p++){
//			for(int k = 0;k < vmNum;k++){
//				if(plan[p][k] == 1){//���plan������ֵΪ1,��¼��id
//					cloudletList.get(p).setVmId(vmList.get(k).getId());
//				}
//			}
//		}
	}

	public void timeAwared() {
		// ��ĳ����������������ִ��ʱ��
		double[] vmLoad = new double[vmNum];
		// ��ĳ��Vm�����е���������
		int[] vmTasks = new int[vmNum];
		// ��¼��ǰ������䷽ʽ������ֵ
		double minLoad = 0;
		// ��¼��ǰ�������ŷ��䷽ʽ��Ӧ��������к�
		int idx = 0;
		// ��һ��cloudlet���������vm
		vmLoad[vmNum - 1] = ct[0][vmNum - 1];
		vmTasks[vmNum - 1] = 1;
//		cloudletList.get(0).setVmId(vmList.get(vmNum-1).getId());
		plan[0][vmNum - 1] = 1;// ��һ�������
		for (int h = 1; h < cloudletNum; h++) {
			minLoad = vmLoad[vmNum - 1] + ct[h][vmNum - 1];
			idx = vmNum - 1;
			for (int j = vmNum - 2; j >= 0; j--) {
				// �����ǰ�����δ����������Ƚ��굱ǰ����������������Ƿ�����
				if (vmLoad[j] == 0) {
					if (minLoad >= ct[h][j])
						idx = j;
					break;
				}
				if (minLoad > vmLoad[j] + ct[h][j]) {
					minLoad = vmLoad[j] + ct[h][j];
					idx = j;
				}
				// �򵥵ĸ��ؾ���
				else if (minLoad == vmLoad[j] + ct[h][j] && vmTasks[j] < vmTasks[idx])
					idx = j;
			}
			vmLoad[idx] += ct[h][idx];
			vmTasks[idx]++;
//			cloudletList.get(i).setVmId(vmList.get(idx).getId());
			plan[h][idx] = 1;
		}
	}



	// bp-tabu
	int k;
	int pt;
	// vmmax��vmmin������
	int idxmax = 0, idxmin = 0;
	double vmmax = 0, vmmin = 0;
	int[] tabuList;
	ArrayList<Integer> vmmaxIdx = new ArrayList<Integer>();
	ArrayList<Integer> vmminIdx = new ArrayList<Integer>();

	private void tabu() {
		k = 10;
		pt = 0;
		tabuList = new int[cloudletNum];
		for (int i = 0; i < cloudletNum; i++) {
			tabuList[i] = 1;
		}
		do {
			getVMmaxVMmin();
			if(pt >= k){
				//�ͽ����
				publishment();
			}
			//����������ֵ�������н���
			if(idxmin != idxmax){
				changeByBenefit();
			}
		}while (isFinishSchedualing());
	}

	private void getVMmaxVMmin() {
		for (int j = 0; j < vmNum; j++) {
			double vmtemp = 0;
			for (int k = 0; k < cloudletNum; k++) {
				vmtemp += plan[k][j] * ct[k][j];
			}
			if (vmtemp > vmmax) {
				vmmax = vmtemp;
				idxmax = j;
			}
			if (vmmin == 0) {
				vmmin = vmtemp;
			}
			if (vmtemp < vmmin) {
				vmmin = vmtemp;
				idxmin = j;
			}
		}
		for (int l = 0; l < cloudletNum; l++) {
			if (plan[l][idxmax] == 1) {
				vmmaxIdx.add(l);
			}
			if (plan[l][idxmin] == 1) {
				vmminIdx.add(l);
			}
		}
	}

	private void publishment() {
		vmmin -= 1;
	}

	private boolean isFinishSchedualing() {
		boolean tag = true;
		for (int i : vmmaxIdx) {
			if (tabuList[i] == 1) {
				tag = false;
			}
		}
		for (int j : vmminIdx) {
			if (tabuList[j] == 1) {
				tag = false;
			}
		}
		return tag;
	}

	private double getLB() {
		double lb = 0;
		lb = getVL() / vmNum;
		return lb;
	}

	private double getVL() {
		double vl = 0;
		for (int j = 0; j < vmNum; j++) {
			double vlj = 0;
			for (int i = 0; i < cloudletNum; i++) {
				vlj += plan[i][j] * ct[i][j];
			}
			vl += (vlj - vl_rg) * (vlj - vl_rg);
		}
		return vl;
	}

	private void changeByBenefit() {
		// ���彻�����ս���������
		int changeMaxIndex, changeMinIndex;
		// ������ʱ��ŵĽ�������
		int tempIndex, tempMaxIndex = -1, tempMinIndex = -1;
		// ���ڽ���
		int temp;
		// ��ʱ�������ֵ�Լ�������ֵ
		double maxBenefit, tempBenefit;
		maxBenefit = getLB();
		// Ѱ�ҽ��������ŵ����������
		for (int maxIndex : vmmaxIdx) {
			if (tabuList[maxIndex] == 1) {
				// tempMaxIndex = maxIndex;
				for (int minIndex : vmminIdx) {
					if (tabuList[minIndex] == 1) {
						// tempMinIndex = minIndex;
//						temp = plan[minIndex][idxmin];
						plan[minIndex][idxmin] = plan[maxIndex][idxmax] = 0;
						plan[minIndex][idxmax] = plan[maxIndex][idxmin] = 1;
						tempBenefit = getLB();
						if (tempBenefit < maxBenefit) {
							maxBenefit = tempBenefit;
							tempMaxIndex = maxIndex;
							tempMinIndex = minIndex;
//							changeMaxIndex = tempMaxIndex;
//							changeMinIndex = tempMinIndex;
						}
						plan[minIndex][idxmin] = plan[maxIndex][idxmax] = 1;
						plan[minIndex][idxmax] = plan[maxIndex][idxmin] = 0;
					}
				}
			}
		}
		if (tempMinIndex != -1) {
			tabuList[tempMaxIndex] = 0;
			tabuList[tempMinIndex] = 0;

			plan[tempMinIndex][idxmin] = plan[tempMaxIndex][idxmax] = 0;
			plan[tempMinIndex][idxmax] = plan[tempMaxIndex][idxmin] = 1;
		} else {
			pt++;
		}
	}
}
