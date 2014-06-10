/**
	A Path is an equivalent of the GA gene sequence. For us, it is a valid sequence of towns that has a
	fitness according to its distance (calculated with an adjacency matrix).

	A Path can:
		1. mutate to generates diversity - essential
		2. crossOver - mate with a partner to produce a (hopefully) fitter child 
		3. calculate its distance and be converted to a useful String.

	Andrew Healy - HDipIT - 13250280 - April 2014
*/

import java.util.ArrayList;

public class Path{

	public double distance;
	public Town[] towns;
	public Town[] allTowns;
	private int size;
	private double[][]matrix; //1-indexed matrix
	public double fitness;

	//constructor used for random paths
	public Path(double[][]matrix, Town[]allTowns){
		this.matrix = matrix;
		this.allTowns = allTowns;
		towns = new Town[matrix.length];
		distance = 0.0;
		size = 0;
		fitness = 0.0;
	}

	//most commonly used constructor - for the crossover function
	public Path(double[][]matrix, Town[]allTowns, Town[]inOrder){
		this.matrix = matrix;
		this.allTowns = allTowns;
		towns = inOrder;
		distance = 0.0;
		size = 0;
		fitness = 0;
		distance = calcDistance();
		size = towns.length;
	}

	public Path(){}//used when filling the mating pool. not very important.

	//return a correctly-formatted string for submission
	public String makeString(){
		String str = "";
		
		for(int i=0; i<size; i++){
			str += towns[i].id+".";
		}

		return str;
	}

	//mutation generates diversity - essential
	//spare mutation function? - swap 2 random Towns (not the start/end though) - works w/ higher mutation factor
	public void mutate(double mutationRate){
		if(Math.random()<mutationRate){
			int a = (int)(Math.random()*(size-3));
			int b = (int)(Math.random()*(size-3));
			//do the swap
			Town townA = new Town(towns[a+1].id, towns[a+1].name, towns[a+1].latitude, towns[a+1].longitude);//towns[a+1].copy();
			Town townB = new Town(towns[b+1].id, towns[b+1].name, towns[b+1].latitude, towns[b+1].longitude);
			towns[a+1] = townB;
			towns[b+1] = townA;

			distance = calcDistance(); //it has a new distance of course!
			//System.out.println("swapping "+townA.name+" with "+townB.name);
		}
	}

	//mutation generates diversity - essential
	//better mutation function? - reverses a random subtour and puts it back in a random postion
	// - works w/ lower mutation factor
	public void mutate4(double mutationRate){
		if(Math.random() < mutationRate){
			//the start of the subtour between second and 4th last
			int a = (int)(Math.random()*(size-5)+1);
			//where to place it between second and second last
			int b = (int)(Math.random()*(size-5)+1);
			Town [] subTowns = new Town [3];
			//reverse when putting back in!
			for(int i=0; i<subTowns.length; i++){
				subTowns[i] = towns[a+i];
			}
			if(a < b){ //move everything between them down
				for(int i=a; i<b; i++){
					towns[i] = towns[i+3];
				}
			}else{ //move everything between them up
				for(int i=a; i>b; i--){
					towns[i+2] = towns[i-1];
				}
	
			}
			//put them back in reverse order
			for(int i=b, j=subTowns.length-1; i<b+subTowns.length; i++, j--){
				towns[i] = subTowns[j];
			}

			distance = calcDistance();//it has a new distance of course!

		}
	}

	//simply counts up according to the adjacency matrix
	public double calcDistance(){
		double result = 0.0;
		for(int i=0; i<towns.length-1; i++){
			result += matrix[towns[i].id][towns[i+1].id];
		}
		return result;
	}

	//at the start random paths are required. but they must be valid
	public void randomPath(){
		//keeps track of visited towns
		boolean [] visited = new boolean[allTowns.length];
		int added = 0;//haven't been to any
		while(added < allTowns.length){
			int rand = (int)(Math.random()*allTowns.length);
			//have we visited this random town
			if(!visited[rand]){
				towns[added] = allTowns[rand];
				visited[rand] = true;
				added++;
			}
		}
		//join up the start and the end and calculate the distance
		towns[added] = towns[0];
		distance = calcDistance();
		size = towns.length;
	}
	
	//crossover is the way to get better Paths from a previous generation. Also maintains diversity. 
	//using the edge recombinator technique - return an array of towns in order
	//the algorithm is described here: http://en.wikipedia.org/wiki/Edge_recombination_operator
	public Town [] crossOver(Path partner){

		try{

			//get the adjacency matrices
			ArrayList [] neighbours = getNeighbours();
			ArrayList [] p_neighbours = partner.getNeighbours();

			//take the union of these matrices
			ArrayList [] union = new ArrayList[towns.length];

			for(int i=1; i<neighbours.length; i++){
				ArrayList temp = new ArrayList (4);

				for(int j=0; j<2; j++){
					Integer candidate = (Integer) neighbours[i].get(j);
					temp.add(candidate);
				}

				for(int j=0; j<2; j++){
					Integer candidate = (Integer) p_neighbours[i].get(j);
					if(!temp.contains(candidate)){
						temp.add(candidate);
					}
				}
				union[i] = temp;
			}

	    	ArrayList <Integer> list = new ArrayList <Integer> (); //to store the combined path
	    	//pick the first at random from the firsts
	    	Integer n = (Math.random()<0.5) ? new Integer(towns[0].id) : new Integer(partner.towns[0].id);
	    	//until each town is in once
	    	while(list.size() < towns.length-1){
	    		list.add(n);
	    		//remove n from all neighbour lists
	    		for(int i=1; i<union.length; i++){
					boolean removed = union[i].remove(n);
				}
				int n_int = n.intValue();

				ArrayList find_n = union[n_int];

				if(find_n.size() > 0){//n's neighbour list is non-empty
					//set initial values
					Integer leastI = (Integer)find_n.get(0);
					int least_size = union[leastI.intValue()].size();
					for(int i=1; i<find_n.size(); i++){
						Integer testI = (Integer)find_n.get(i);
						if(union[testI.intValue()].size() < least_size){
							//found new lowest, update both
							least_size = union[testI.intValue()].size();
							leastI = (Integer)find_n.get(i);
						}else if(union[testI.intValue()].size() == least_size){
							//found equal lowest, choose randomly
							if(Math.random() < 0.5){
								leastI = (Integer)find_n.get(i);	
							}
						}
					}

					n = leastI;

				}else{//neighbour list is empty so choose closest from the closest unvisited


					double least = 100000;
					Integer choose = new Integer(1);

					for(int i=1; i<towns.length; i++){
						Integer temp = new Integer(i);
						if(!list.contains(temp) && matrix[towns[i].id][towns[n_int].id] < least){
							least = matrix[towns[i].id][towns[n_int].id];
							choose = temp;
						}
					}

					n = choose;
				}

	    	}//end while - we've found enough Towns

	    	Integer first = (Integer)list.get(0);
	    	list.add(first); //join it up!

	    	Town [] newTowns = new Town[list.size()];//the array we will return
	    	//convert those Integers to Towns
	    	for (int i=0; i<list.size(); i++) {
	    		Integer temp = (Integer)list.get(i);
	    		newTowns[i] = allTowns[temp.intValue()-1];
	    	}

	    	return newTowns;

	    //slim possibility of a NullPointerException though
	    }catch(Exception e){
	    	System.out.println(e+" caught while towns = \n"+makeString());
	    	return towns;
	    }

	}
	
	//convience function used by the crossover function. 
	//returns a kind of "neighbour matrix" of Integers. 
	private ArrayList [] getNeighbours(){

		ArrayList [] lists = new ArrayList [towns.length]; //1-indexed

		//the start/end is a special case
		ArrayList temp = new ArrayList(2);
		temp.add(new Integer(towns[towns.length-2].id));
		temp.add(new Integer(towns[1].id));
		lists[towns[0].id] = temp;

		for(int i=1; i<towns.length-1; i++){
			ArrayList temp2 = new ArrayList(2);
			temp2.add(new Integer(towns[i-1].id));
			temp2.add(new Integer(towns[i+1].id));
			lists[towns[i].id] = temp2;
		}
		return lists;
	}


}