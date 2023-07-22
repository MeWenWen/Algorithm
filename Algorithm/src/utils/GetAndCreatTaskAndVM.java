package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * �������񳤶��Լ��������������
 * @author Administrator
 *
 */
public class GetAndCreatTaskAndVM {
	
	static Random random = new Random();
	
	/**
	 * ��������ָ��������Χ�ڵ����������� === ʵ����������������ÿһ������������ʹ��
	 * @param min ����/����� ����Сֵ
	 * @param max ����/����� �����ֵ
	 * @param taskAndVmNum ����/����� �ĸ���
	 * @param fileName �ļ��� �ļ�������ʽ TASK/VM/BW-��Сֵ-���ֵ-�������.txt
	 * @return
	 */
	public static void createTaskAndVM(int min, int max, int taskAndVmNum, String fileName) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0;i<taskAndVmNum;i++) {
			try {
				writer.write((random.nextInt(max)%(max-min+1) + min) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	/**
	 * ��ָ���ļ��ж�ȡ������������ === ʵ����������������ÿһ������������ʹ��
	 * @param fileName �ļ�������ʽ TASK/VM/BW-��Сֵ-���ֵ-�������.txt
	 * @return
	 */
	public static double[] getTaskAndVM(String fileName) {
		ArrayList<String> arrayList = new ArrayList<String>();
		File fin = new File(fileName);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fin));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				arrayList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
        double[] tasksAndVms = new double[arrayList.size()];
        for(int i=0;i<arrayList.size();i++) {
        	tasksAndVms[i] = Double.parseDouble(arrayList.get(i));
        }   
		return tasksAndVms;
	}
	
	
	public static void main(String[] args) {
//		//TASK-1000-3000-256.txt	//�����С
//		createTaskAndVM(1000, 3000, 256, "TASK-1000-3000-256.txt");
//		//TASK-1000-3000-512.txt
//		createTaskAndVM(1000, 3000, 512, "TASK-1000-3000-512.txt");
//		//TASK-1000-3000-1024.txt
		createTaskAndVM(10000, 50000, 1024, "TASK-1000-3000-1024.txt");
//		createTaskAndVM(1000, 3000, 2048, "TASK-1000-3000-2048.txt");
//		//VM-100-300-8.txt	//���������
//		createTaskAndVM(100, 300, 8, "VM-100-300-8.txt");
//		//VM-100-300-16.txt
		createTaskAndVM(100, 300, 16, "VM-100-300-16.txt");
//		//VM-100-300-32.txt
//		createTaskAndVM(100, 300, 32, "VM-100-300-32.txt");
//		createTaskAndVM(10, 100, 8, "VM-10-100-8.txt");
//		//VM-100-300-16.txt
//		createTaskAndVM(10, 100, 16, "VM-10-100-16.txt");
//		//VM-100-300-32.txt
//		createTaskAndVM(10, 100, 32, "VM-10-100-32.txt");
	}

}
