/**
	TRAVELLING SALESMAN GENETIC ALGORITHM

	The main class loads the indices, names, latitudes and longitudes from a .csv file. 
	It creates an array of Town objects using these values.
	it creates a (1-indexed) adjacency matrix of the distance between the towns. 
	For each run of the genetic algorithm, it creates a Population object using these arrays.
	The Population is also initialised with a given size and mutation rate.
	The Population starts with random (but valid) Paths of Towns.
	As long as the the best distance found by the Population has changed recently enough,
		1. Keep track of the best Path of each generation and print new ones
		2. Add Paths to the mating pool accoring to their relative fitness
		3. Pick 2 Paths at random from the mating pool and combine with the crossover function
		4. (Possibly) mutate this path before re-populating with the generated child Path
		5. Get the fitness (distance) of each Path in the Popluation using a fitness function. 
	The best Path from each Population is stored from each run.
	The best Path from these is returned as the answer.

	Andrew Healy - HDipIT - 13250280 - April 2014

*/

import java.util.ArrayList;



public class TSP{

	public static double [][] adjacencyMatrix; //find towns according to their 1-indexed ids
	public static Town [] towns; //0-indexed!

	public static void main(String[] args) {
		

		double start = System.currentTimeMillis();

		// load the town information from csv file using the given FileIO class
		FileIO reader = new FileIO(); 
 		String[] twns = reader.load("towns.csv");

 		// into an array
		towns = new Town[twns.length]; 

 		for(int i=0; i<towns.length; i++){
 			String[]parts = twns[i].split(",");
 			towns[i] = new Town (Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
 		}

 		// and make the adjacency matrix of distances

 		adjacencyMatrix = new double[towns.length+1][towns.length+1];
 		//want the indices to correspond to the ids
 		for(int i=1; i<=towns.length;i++){
 			for(int j=1; j<=towns.length;j++){
 				adjacencyMatrix[i][j] = towns[i-1].distanceTo(towns[j-1]);
 			}
 		}

 		//it's a random process so let's run it this many times and take the best answer
 		int runs = 12;

 		//save the best path from each run
 		Path [] bests = new Path[runs];

 		for(int i = 0; i<runs; i++){

 			//a population needs to know the towns and the matrix
 			//mutation rate, population size
 			Population population = new Population(adjacencyMatrix, towns, 0.05, 2000);
 					
 			//haven't found a better route in 100 generations, probably won't
	 		while(population.since_change < 100){

	 			// Generate mating pool
	  			population.naturalSelection();
	  			//Create next generation
	  			population.generate();
	  			// Calculate fitness
	  			population.calcFitness();
	  		}

	  		bests[i] = population.all_time;//add the run's best path to the array of best paths
 		}


 		//find the best of the best
 		Path best = bests[0];
 		System.out.println("\nbests [0]: "+best.distance);

 		for(int i=1; i<runs; i++){
 			System.out.println("bests ["+i+"]: "+bests[i].distance);
 			if(bests[i].distance < best.distance){
 				best = bests[i];
 			}
 		}

 		System.out.println("\ntotal best: \n"+best.makeString()+"\nwith a distance of: "+best.distance);

 		double secs_taken = (System.currentTimeMillis() - start)/1000;
 		System.out.println("\nfinished in "+(int)(secs_taken/60)+" mins "+(int)(secs_taken%60)+" secs");

 		
	}

}