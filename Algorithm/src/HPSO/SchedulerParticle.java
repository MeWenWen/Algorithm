package HPSO;

import net.sourceforge.jswarm_pso.Particle;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import ONE.TEST.ACO.Ant.position;

public class SchedulerParticle extends Particle {
	private int VMs;//城市的个数（相当于虚拟机的个数）
	private int tasks;
	public List<Integer> tabu;//禁忌表
	private double[][] etc;
	private static double[][] commMatrix;
    SchedulerParticle() {
        super(Constants.NO_OF_TASKS);
        double[] position = new double[Constants.NO_OF_TASKS];
        double[] velocity = new double[Constants.NO_OF_TASKS];
        
        this.tasks=tasks;
        this.VMs=VMs;
        tabu = new ArrayList<Integer>();
        
        double[][] p;//每个节点被选中的概率
		  p = new double[VMs][tasks];
		  double alpha = 1.0;
		  double beta = 1.0;
		  double sum = 0;//分母
		
		  //随机选择粒子的位置
//			int firstVM = (int)(this.VMs*Math.random());
//			int firstExecute = (int)(this.tasks*Math.random());
//			tour.add(new position(firstVM, firstExecute));
//			tabu.add(new Integer(firstExecute));
		  
		  
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            Random randObj = new Random();
            position[i] = randObj.nextInt(Constants.NO_OF_DATA_CENTERS);
            velocity[i] = Math.random();
            
        }
        setPosition(position);
        setVelocity(velocity);
        
        int firstExecute = (int)(this.tasks*Math.random());
        tabu.add(new Integer(firstExecute));
        tabu.add(new Integer(firstExecute));
      //计算每个节点被选的概率
		  for(int m=0; m<VMs; m++){
			  for(int n=0; n<tasks; n++){
				  p[m][n] = Math.pow(Dij(m,n), alpha)*Math.pow(1/Dij(m,n),beta)/sum;
				 System.out.println("-----"+p[m][n]);
				  if(tabu.contains(new Integer(n)))p[m][n] = 0;
			  }
		  }
		  
        
    }
    public double Dij(int vm, int task){
		double d;
//		System.out.println("==="+this.tasks+"==="+this.VMs);
	    d =  this.etc[task][vm]+this.commMatrix[task][vm];
//	    d =  this.etc[task][vm];
	    //计算
	    
		return d;
	}
    
    
    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
            String tasks = "";
            int no_of_tasks = 0;
            for (int j = 0; j < Constants.NO_OF_TASKS; j++) {
                if (i == (int) getPosition()[j]) {
                    tasks += (tasks.isEmpty() ? "" : " ") + j;
                    ++no_of_tasks;
                }
            }
            if (tasks.isEmpty()) output += "There is no tasks associated to Data Center " + i + "\n";
            else
                output += "There are " + no_of_tasks + " tasks associated to Data Center " + i + " and they are " + tasks + "\n";
        }
        return output;
    }
}
