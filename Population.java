/**
	A Poplulation is made up of Paths. These Paths breed with eachother and their children make up the next
	generation's Population.  

	A Population has methods to:
		1. caluculate the relative fitness of its Paths
		2. fill the mating pool according to these fitness values
		3. pick 2 paths from the mating pool, crossover, mutate and re-populate the next generation

	Andrew Healy - HDipIT - 13250280 - April 2014 
*/

import java.util.ArrayList;
import java.util.Arrays;

public class Population{
	
	private double mutationRate;
	public Path [] population;
	private ArrayList <Path> matingPool;
	public int generations;
	public int since_change;
	public Path all_time;
	public double [][] adjacencyMatrix;
	public Town [] towns;

	//This is called by the main method
	public Population(double [][] adjacencyMatrix, Town [] towns, double mutationRate, int num){
		this.mutationRate = mutationRate;
		this.adjacencyMatrix = adjacencyMatrix;
		this.towns = towns;
		population = new Path [num]; //array - data structure to store the paths
		for(int i=0; i<num; i++){
			//populate with random paths
			Path temp = new Path(adjacencyMatrix, towns);
			temp.randomPath();
			population[i] = temp;
		}

		since_change = 0;
		calcFitness(); //called here rather than the main method on initialisation

		matingPool = new ArrayList <Path> ();
		all_time = new Path();
		all_time.distance = 100000; //abnormally high number

	}

	//very important for the makeup of the mating pool
	public void calcFitness(){
		
		//get unique distances of Paths in the population
		ArrayList dists = new ArrayList();
		for(int i=0; i<population.length; i++){
			Double dist = new Double(1/population[i].distance);
    		if(!dists.contains(dist)){
    			dists.add(dist);
    		}	
		}

		//convert arraylist to array
    	double [] ascending_dists = new double[dists.size()];
    	for(int i=0; i<ascending_dists.length; i++){
    		Double temp = (Double)dists.get(i);
    		ascending_dists[i] = temp.doubleValue();
    	}

    	Arrays.sort(ascending_dists);//sort to ascending order
    	
		for(int i=0; i<population.length; i++){

			for(int j=0; j<ascending_dists.length; j++){
				if((1/population[i].distance) == ascending_dists[j]){
					//fitness is successive integers raised to the power of 4.
					//need to keep distance between better Paths to reward difference
					//at the higher end.
					population[i].fitness = Math.pow(j,4);
					break;
				}
			}
			
		}
	}

	
	//Fill the mating pool according to each Path's relative fitness.
	//The all-time best Path is also added to make sure the mating pool has at least one instance of it.
	public void naturalSelection(){

		// Clear the ArrayList
    	matingPool.clear();

    	//always choose the best from the previous generation
    	Path generation_best = new Path();

    	double maxFitness = 0;
    	double totalFitness = 0.0;

    	double gen_best = 100000; //some big number

    	//getting the relative fitness of each path to another
    	for (int i = 0; i < population.length; i++) {

    		totalFitness += population[i].fitness;

    		//update the generation best path while we're at it
    		if(gen_best > population[i].distance){
    			generation_best = population[i];
    			gen_best = generation_best.distance;
    		}

    		//the biggest fitness value of any generation must be kept
      		if (population[i].fitness > maxFitness) {
        		maxFitness = population[i].fitness;
      		}
    	}


    	//we've found a new best from all generations, so print it
    	if(generation_best.distance < all_time.distance){
    		all_time = generation_best;
    		System.out.println("new best: "+all_time.distance+" after "+generations);
    		since_change = 0;
    	}else{
    		since_change++;
    	}

	    // Based on fitness, each member will get added to the mating pool a certain number of times
	    // a higher fitness = more entries to mating pool = more likely to be picked as a parent
	    // a lower fitness = fewer entries to mating pool = less likely to be picked as a parent
	    for (int i = 0; i < population.length; i++) {

	    	double fitness = population[i].fitness/maxFitness; 	

	      	int n = (int)(fitness * 1000);  		// Arbitrary multiplier
	      	for (int j = 0; j < n; j++) {         // and pick two random numbers
	        	matingPool.add(population[i]);
	      	}
	    }

	} 

	public void generate(){

		//making sure the generation doesn't get any worse
		population[0] = all_time;

		// Refill the population with children from the mating pool
	    for (int i = 1; i < population.length; i++) {

	    	int a = (int)(Math.random()*matingPool.size());
	    	int b = (int)(Math.random()*matingPool.size());
	    	Path partnerA = (Path) matingPool.get(a);
	    	Path partnerB = (Path) matingPool.get(b);
	    	Path child = new Path(adjacencyMatrix, towns, partnerA.crossOver(partnerB));
	    	//System.out.println("crossing "+partnerA.distance+" with "+partnerB.distance);
	    	//if(Math.random()<0.5){
	    		child.mutate4(mutationRate);
	    	//}else{
	    	//	child.mutate(mutationRate);
	    	//}
	    	population[i] = child;	
	
	    }
	    generations++;

	}
}