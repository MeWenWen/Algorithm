package RoundRobin;

import java.util.List;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class RoundRobinDatacenterBroker extends DatacenterBroker {

	RoundRobinDatacenterBroker(String name) throws Exception {
		super(name);
	}

	@Override
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
			distributeRequestsForNewVmsAcrossDatacentersUsingTheRoundRobinApproach();
		}
	}

	/**
	 * Distributes the VMs across the data centers using the round-robin approach. A
	 * VM is allocated to a data center only if there isn't a VM in the data center
	 * with the same id.采用轮循方式将虚拟机分布到多个数据中心。只有当数据中心中没有虚拟机的id时，才会将虚拟机分配给该数据中心。
	 */
	protected void distributeRequestsForNewVmsAcrossDatacentersUsingTheRoundRobinApproach() {
		int numberOfVmsAllocated = 0;
		int i = 0;

		final List<Integer> availableDatacenters = getDatacenterIdsList();

		for (Vm vm : getVmList()) {
			int datacenterId = availableDatacenters.get(i++ % availableDatacenters.size());
			String datacenterName = CloudSim.getEntityName(datacenterId);

			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in "
						+ datacenterName);
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				numberOfVmsAllocated++;
			}
		}

		setVmsRequested(numberOfVmsAllocated);
		setVmsAcks(0);
	}
}