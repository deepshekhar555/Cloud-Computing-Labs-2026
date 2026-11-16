package lab;

import java.util.*;
import java.util.function.Supplier;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.*;
import org.cloudbus.cloudsim.brokers.*;
import org.cloudbus.cloudsim.cloudlets.*;
import org.cloudbus.cloudsim.hosts.*;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.*;
import org.cloudbus.cloudsim.schedulers.vm.*;
import org.cloudbus.cloudsim.utilizationmodels.*;
import org.cloudbus.cloudsim.vms.*;
import org.cloudbus.cloudsim.provisioners.*;
import org.cloudbus.cloudsim.allocationpolicies.*;

public class SchedulingExperiment {
    static void run(String title, VmScheduler vs, Supplier<CloudletScheduler> cs) {
        System.out.println("\n=== " + title + " ===");
        CloudSim sim = new CloudSim();
        List<Pe> pes = new ArrayList<>();
        for (int i = 0; i < 4; i++) pes.add(new PeSimple(1000));
        Host host = new HostSimple(8192, 100000, 1000000, pes);
        host.setRamProvisioner(new ResourceProvisionerSimple());
        host.setBwProvisioner(new ResourceProvisionerSimple());
        host.setVmScheduler(vs);
        List<Host> hosts = new ArrayList<>();
        hosts.add(host);
        new DatacenterSimple(sim, hosts, new VmAllocationPolicySimple());
        DatacenterBroker broker = new DatacenterBrokerSimple(sim);
        List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Vm vm = new VmSimple(i, 1000, 2);
            vm.setRam(2048).setBw(10000).setSize(10000);
            vm.setCloudletScheduler(cs.get());
            vms.add(vm);
        }
        List<Cloudlet> cls = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Cloudlet c = new CloudletSimple(20000, 1, new UtilizationModelFull());
            c.setSizes(300);
            cls.add(c);
        }
        broker.submitVmList(vms);
        broker.submitCloudletList(cls);
        sim.start();
        System.out.println("Cloudlet VM Status Start Finish");
        for (Cloudlet c : broker.getCloudletFinishedList())
            System.out.printf("%-9d %-3d %-8s %-8.2f %-8.2f%n", c.getId(), c.getVm().getId(), c.getStatus(), c.getExecStartTime(), c.getFinishTime());
        System.out.println("Completed: " + broker.getCloudletFinishedList().size() + " / 4");
    }

    public static void main(String[] a) {
        run("A: VM SpaceShared + Cloudlet SpaceShared", new VmSchedulerSpaceShared(), CloudletSchedulerSpaceShared::new);
        run("B: VM SpaceShared + Cloudlet TimeShared", new VmSchedulerSpaceShared(), CloudletSchedulerTimeShared::new);
        run("C: VM TimeShared + Cloudlet SpaceShared", new VmSchedulerTimeShared(), CloudletSchedulerSpaceShared::new);
        run("D: VM TimeShared + Cloudlet TimeShared", new VmSchedulerTimeShared(), CloudletSchedulerTimeShared::new);
    }
}
