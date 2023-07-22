package RoundRobin;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;

public class RoundRobinScheduler {

	private static List<Cloudlet> cloudletList;
	private static List<Vm> vmList;
	private static Datacenter[] datacenter;
	private static double[][] commMatrix;
	private static double[][] execMatrix;

	private static List<Vm> createVM(int userId, int vms) {
		// Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		// VM Parameters
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		int mips = 250;
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VMs
		Vm[] vm = new Vm[vms];

		for (int i = 0; i < vms; i++) {
			vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm,
					new CloudletSchedulerSpaceShared());
			list.add(vm[i]);
		}

		return list;
	}

	private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		// cloudlet parameters
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for (int i = 0; i < cloudlets; i++) {
			int dcId = (int) (Math.random() * Constants.NO_OF_DATA_CENTERS);
			long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
			cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel,
					utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			cloudlet[i].setVmId(dcId + 2);
			list.add(cloudlet[i]);
		}
		return list;
	}

	public static void main(String[] args) {
		Log.printLine("Starting Round Robin Scheduler...");

		new GenerateMatrices();
		execMatrix = GenerateMatrices.getExecMatrix();
		commMatrix = GenerateMatrices.getCommMatrix();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置时间格式
		String startDate = ft.format(new Date());
		System.out.println("开始时间: "+startDate);
		// 开始时间
        long stime = System.currentTimeMillis();
        System.out.printf("开始时间： "+stime );

		try {
			int num_user = 1; // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
			for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
				datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
			}

			// Third step: Create Broker
			RoundRobinDatacenterBroker broker = createBroker("Broker_0"); // 创建容器数据中心代理Broke。
			int brokerId = broker.getId();

			// Fourth step: Create VMs and Cloudlets and send them to broker
			vmList = createVM(brokerId, Constants.NO_OF_DATA_CENTERS);
			cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

			broker.submitVmList(vmList); // 将虚拟机表、云任务表提交给broker。
			broker.submitCloudletList(cloudletList); //

			// Fifth step: Starts the simulation
			CloudSim.startSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			// newList.addAll(globalBroker.getBroker().getCloudletReceivedList());

			CloudSim.stopSimulation();

			printCloudletList(newList);

			Log.printLine(RoundRobinScheduler.class.getName() + " finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
		// 结束时间
        long etime = System.currentTimeMillis();
     // 计算执行时间
        System.out.printf("执行时长：%d 毫秒.", etime-stime ,"ms");
	}

	private static RoundRobinDatacenterBroker createBroker(String name) throws Exception {
		return new RoundRobinDatacenterBroker(name);
	}

	/**
	 * Prints the Cloudlet objects
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + indent
				+ "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		dft.setMinimumIntegerDigits(2); // 设置数字的整数部分中允许的最小位数
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + dft.format(cloudlet.getCloudletId()) + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
//				Log.print("SUCCESS");
//
//				Log.printLine(indent + indent + dft.format(cloudlet.getResourceId()) + indent + indent + indent
//						+ dft.format(cloudlet.getVmId()) + indent + indent + dft.format(cloudlet.getActualCPUTime())
//						+ indent + indent + dft.format(cloudlet.getExecStartTime()) + indent + indent + indent
//						+ dft.format(cloudlet.getFinishTime()));
			}
		}
		double makespan = calcMakespan(list);
		Log.printLine("Makespan using RR: " + makespan);
		SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置时间格式
		String endDate = ft1.format(new Date());
	 System.out.println("结束时间: "+ endDate);
	// 结束时间
//     long etime = System.currentTimeMillis();
//  // 计算执行时间
//     System.out.printf("执行时长：%d 毫秒.", etime );
	}

	private static double calcMakespan(List<Cloudlet> list) {
		double makespan = 0;
		double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

		for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
			int dcId = list.get(i).getVmId() % Constants.NO_OF_DATA_CENTERS;
			if (dcWorkingTime[dcId] != 0)
				--dcWorkingTime[dcId];
			dcWorkingTime[dcId] += execMatrix[i][dcId] + commMatrix[i][dcId];
			makespan = Math.max(makespan, dcWorkingTime[dcId]);
		}
		return makespan;
	}
}