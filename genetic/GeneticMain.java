import java.util.Scanner;
import java.io.PrintWriter;
//use via "cat input.txt | java GeneticMain
public class GeneticMain
{
	static int threads = 11;
	static int iterations = 0;
	static int population = 1000;
	static int TRIALS = 3;
	static int time1 = 1000 * 60 * 1;
	static int time2 = 1000 * 60 * 1;
	static int time3 = 1000 * 60 * 1;
	static double save = 0.3;
	static double mutate = 0.3;
	static double merge = 0.3;
	public static void main(String[] args) throws Exception
	{
		Scanner scan = new Scanner(System.in);
		String[] names = scan.nextLine().split(",");
		int SIZE = names.length;
		double[][] weights = new double[SIZE][SIZE];
		for(int i = 0; i < SIZE; i++)
		{
			String[] t = scan.nextLine().split(",");
			for(int j = 0; j < SIZE; j++)
			{
				weights[i][j] = Double.parseDouble(t[j]);
			}
		}
		Data[] finalres = new Data[TRIALS];
		for(int z = 0; z < TRIALS; z++)
		{
			Thread[] thread = new Thread[threads];
			double[] res = new double[threads];
			for(int j = 0; j < threads; j++)
				res[j] = Double.MAX_VALUE;
			GeneticSimulation sim = new GeneticSimulation(weights, population);
			Data[] results = sim.pops;
			long endtime = System.currentTimeMillis() + time1;
			GeneticSimulation[] sims = new GeneticSimulation[threads];
			for(int j = 0; j < threads; j++)
			{
				int k = j;
				thread[j] = new Thread(new Runnable() {
				public void run() {
						sims[k] = new GeneticSimulation(weights, population);
						results[k] = sims[k].run(res, k, endtime, k == 0, save, mutate, merge);
					}
				});
				thread[j].start();
			}
			try
			{for(int j = 0; j < threads; j++)
				thread[j].join();
			} catch(Exception e){}
			System.out.println("_____");
			Thread.sleep(1000);
			long endtime2 = System.currentTimeMillis() + time2;
			for(int j = 0; j < threads; j++)
			{
				int k = j;
				thread[j] = new Thread(new Runnable() {
				public void run() {
						GeneticSimulation sim2 = sims[k];
						for(int i = 0; i < threads; i++)
							sim2.pops[i] = new Data(sim.pops[i]);
						results[k] = sim2.run(res, k, endtime2, k == 0, save, mutate, merge);
					}
				});
				thread[j].start();
			}
			try
			{for(int j = 0; j < threads; j++)
				thread[j].join();
			} catch(Exception e){}
			System.out.println("_____");
			long endtime3 = System.currentTimeMillis() + time3;
			finalres[z] = sim.run(new double[]{Double.MAX_VALUE}, 0, endtime3, true, save, mutate, merge);
		}
		System.out.println("_____");
		System.out.println("_____");
		int t = 0;
		for(int i = 0; i < finalres.length; i++)
		{
			System.out.println(finalres[i].getScore(weights));
			if(finalres[i].getScore(weights) < finalres[t].getScore(weights))
				t = i;
		}
		PrintWriter writer = new PrintWriter("Output.txt", "UTF-8");
		int[] best = finalres[t].pairings;
		for(int i = 0; i < best.length; i++)
			writer.println(names[i]+","+(best[i]==-1?":( Nobody":names[best[i]]));
		writer.close();
	}
}
