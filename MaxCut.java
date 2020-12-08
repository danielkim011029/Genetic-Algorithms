/*
This program accepts 3 parameters, input, output, and the parameter file and they all need to be stored in
the same directory as the program. It will read the input data which consists of adjacency matrix for a graph
and perform max cut algorithm by using brute force, random algorithm, and genetic algorithm
For genetic algorithm, I generate the initial population using Math.random method. Every generation after
is created by crossover, mutation, selection, and elitism. For selection of parents, I used fitness proportionate
selection, for crossover operator I used one-point crossover, for mutation I use bit flip mutation of 2 bits of
random number of random candidates, and for elitism I select top 5 candidates to move onto next generation.
*/

import java.util.*;
import java.io.*;
public class MaxCut {
    //Blum-Blum-Shub method
    public static int BBS(int P,int Q,int X0){
        int n=P*Q;
        int output=(X0*X0)%n;
        return output;
    }
    //helper function that returns true if the given object array contains given value
    public static boolean contains(Object s[],int n){
        for(int i=0;i<s.length;i++){
            if((Integer)s[i]==n) return true;
        }
        return false;
    }
    //helper function that returns the maximum weight within the weight array
    public static int getMaxweight(int s[]){
        int maxWeight=0;
        for(int i=0;i<s.length;i++){
            if(s[i]>maxWeight) maxWeight=s[i];
        }
        return maxWeight;
    }
    //calculates total weight of a set
    public static int calculateWeight(int[] set,int[][] adj){
        int weight=0;
        int intersect=0;
        //for each vertex in the set
        for(int i=0;i<set.length;i++){
            //if vertex is present
            if(set[i]==1){
                //go through the whole row of the vertex in the adj matrix
                for(int j=0;j<adj[i].length;j++){
                    //if adjacent add weight
                    if(adj[i][j]==1){
                        weight++;
                        if(set[j]==1) intersect++;//if the vertex is within the set increment intersect
                    }
                }
            }
        }
        return weight-intersect;
    }
    
    //returns the max weight of a generation
    public static int findMax(Candidate[] current_gen){
        int max=0;
        for(int i=0;i<current_gen.length;i++){
            if(current_gen[i].getWeight()>max) max=current_gen[i].getWeight();
        }
        return max;
    }
    //returns a weight of a particular vertex
    public static int getWeight(int vertex,int[][] adj){
        int weight=0;
        for(int i=0;i<adj[vertex].length;i++){
            weight=weight+adj[vertex][i];
        }
        return weight;
    }

    //returns the best solution from randomly generated solutions
    public static Candidate top1(Vector<Candidate> solutions){
        Candidate output=new Candidate();
        Iterator<Candidate> it=solutions.iterator();
        while(it.hasNext()){
            Candidate temp=it.next();
            if(temp.fitness>output.fitness) output=temp;
        }
        return output;
    }

    //returns the worst solution from randomly generated solutions
    public static Candidate bottom1(Vector<Candidate> solutions){
        Candidate output=new Candidate();
        output.setFitness(100);
        Iterator<Candidate> it=solutions.iterator();
        while(it.hasNext()){
            Candidate temp=it.next();
            if(temp.fitness<=output.fitness) output=temp;
        }
        return output;
    }
    //returns the average solution from randomly generated solutions
    public static Candidate average(Vector<Candidate> solutions){
        int avgWeight=0;
        Iterator<Candidate> it=solutions.iterator();
        while(it.hasNext()){
            avgWeight+=it.next().weight;
        }
        avgWeight=avgWeight/solutions.size();
        Iterator<Candidate> it2=solutions.iterator();
        Candidate output=it2.next();
        int diff=Math.abs(avgWeight-output.weight);
        while(it2.hasNext()){
            Candidate temp=it2.next();
            if(Math.abs(temp.weight-avgWeight)<diff){
                output=temp;
                diff=Math.abs(temp.weight-avgWeight);
            }
        }
        return output;
    }

    //Mutation Operator method
    public static void mutationOperator(Candidate[] new_gen,int randomCandidate){
        //flip 2 random bits
        for(int i=0;i<2;i++){
            int randomBit=(int)(Math.random()*new_gen[randomCandidate].set.length);
            if(new_gen[randomCandidate].set[randomBit]==0)
                new_gen[randomCandidate].set[randomBit]=1;
            else
                new_gen[randomCandidate].set[randomBit]=0;
        }
    }
    //One point crossover method
    public static Candidate crossoverMethod(Candidate parent1,Candidate parent2){
        Candidate output=new Candidate();
        int[] childSet=new int[parent1.set.length];
        int crossOverPoint=(int)((Math.random()*childSet.length)+1);
        for(int i=0;i<childSet.length;i++){
            if(i<=crossOverPoint) childSet[i]=parent1.set[i];
            else childSet[i]=parent2.set[i];
        }
        output.setSet(childSet);
        return output;
    }
    //Fitness proportionate selection method
    public static Candidate selectionMethod(Candidate[] current_gen){
        Candidate output=new Candidate();
        int sum=0;
        for(int i=0;i<current_gen.length;i++) sum+=current_gen[i].weight;
        int breakPoint=(int)(Math.random()*(sum));
        int partial_sum=0;
        for(int j=0;j<current_gen.length;j++){
            partial_sum+=current_gen[j].weight;
            if(partial_sum>breakPoint){
                output=current_gen[j];
                break;
            }
        }
        return output;
    }
    //returns the nth top candidate
    public static Candidate nth_candidate(Candidate[] current_gen,int n){
        Candidate output=new Candidate();
        int weight=getMaxWeight(current_gen);
        if(n==1){
            for(int i=0;i<current_gen.length;i++){
                if(current_gen[i].weight==weight) output=current_gen[i];
            }
        }
        else{
            for(int k=0;k<n-1;k++) weight=nextWeight(current_gen,weight);
            for(int i=0;i<current_gen.length;i++){
                if(current_gen[i].weight==weight) output=current_gen[i];
            }
        }
        return output;
    }
    //this returns the max weight within a generaiton
    public static int getMaxWeight(Candidate[] current_gen){
        int maxWeight=0;
        for(int i=0;i<current_gen.length;i++){
            if(current_gen[i].weight>maxWeight) maxWeight=current_gen[i].weight;
        }
        return maxWeight;
    }
    //this returns the next max weight after the given weight
    public static int nextWeight(Candidate[] current_gen,int weight){
        int output=weight;
        int difference=weight;
        for(int i=0;i<current_gen.length;i++){
            if(weight>current_gen[i].weight){
                if(current_gen[i].weight!=weight){
                    if(weight-current_gen[i].weight<difference){
                        output=current_gen[i].weight;
                        difference=weight-current_gen[i].weight;
                    }
                }
            }
        }
        return output;
    }
    //This program accepts 3 parameters. Input file, output file, and the parameter file. The input file
    //contains adjacency matrix of graphs and max cut algorithm is performed on each graph using 
    //Brute force, Math.random, BBS, and GA. GA algorithm starts on line 453.
    public static void main(String[]args){
        String input=args[0];   //name of input file
        String output=args[1];  //name of output file
        String parameterFile=args[2];    //name of parameter file
        int size=0; //number of vertices
        int counter=0;//counter that keeps track of the rows of adjacency matrix
        Vector<Integer> n=new Vector<>();   //contains all integers of the adjacency matrix
        ArrayList<graph> graphs=new ArrayList<>(); //arraylist that holds graph object
        int max_gen=0;  //maximum generation
        int max_pop=0;  //maximum population

        //read the input and save the graphs into an object and store that in a arraylist
        try{
            BufferedReader reader=new BufferedReader(new FileReader("./"+input));
            String line=reader.readLine();
            while(line!=null){
                //if the line is describing how many vertices there are
                if(line.charAt(0)=='#'){
                    size=Integer.parseInt(line.substring(line.indexOf('=')+1));
                }
                //if the line is part of the adjacency matrix
                else{
                    for(int i=0;i<line.length();i++){
                        if(line.charAt(i)!=',') n.add(Character.getNumericValue(line.charAt(i)));
                    }
                    //increment row
                    counter++;
                    //if finished reading the graph create a 2d array containing the information
                    if(counter==size){
                        int adj[][]=new int[size][size];
                        int r=0;
                        int c=0;
                        Iterator<Integer> it=n.iterator();
                        while(it.hasNext()){
                            adj[r][c]=it.next();
                            c++;
                            if(c==size){
                                c=0;
                                r++;
                            }
                        }
                        //create a object graph and save the 2d array 
                        graph g=new graph(adj);
                        graphs.add(g);
                        counter=0;  //row reset
                        n.clear();  //integer storage reset
                    }
                }
                line=reader.readLine();
            }
            reader.close();
            //parameterfile reading
            BufferedReader param_reader=new BufferedReader(new FileReader("./"+parameterFile));
            String param_line=param_reader.readLine();
            while(param_line!=null){
                if(param_line.indexOf("Generation")!=-1){
                    max_gen=Integer.parseInt(param_line.substring(param_line.indexOf("=")+1));
                }
                else if(param_line.indexOf("Population")!=-1){
                    max_pop=Integer.parseInt(param_line.substring(param_line.indexOf("=")+1));
                }
                param_line=param_reader.readLine();
            }
            param_reader.close();

        }
        catch(Exception e){
            System.out.println(e);
        }

        //printing the graph
        try{
            FileWriter writer=new FileWriter("./"+output);  //writer to output  
            Iterator<graph> g_it=graphs.iterator();
            while(g_it.hasNext()){//while there is no more graphs left
                int[][] adj=g_it.next().adj;
                for(int i=0;i<adj.length;i++){
                    writer.write("{");
                    for(int j=0;j<adj[i].length;j++){
                        if(j!=adj[i].length-1){
                            writer.write(adj[i][j]+",");
                        }
                        else{
                            writer.write(adj[i][j]+"}");
                        }
                    }
                    writer.write("\n"); 
                }
                writer.write("\n"); 

                //Brute Force
                Vector<Set<Object>> partitions=new Vector<Set<Object>>();   //vector that contains sets
                //Iterates through all of the vertices in the graph
                for(int i=0;i<adj.length;i++){
                    //if there are sets present inside partitions then append the new vertex into every single sets
                    //as a new set
                    if(!partitions.isEmpty()){
                        Vector<Set<Object>> partitions2=new Vector<Set<Object>>();  //temporary vector to hold newly create sets
                        Iterator<Set<Object>> it=partitions.iterator(); //iterator for partitions
                        while(it.hasNext()){
                            Set<Object> temp=new HashSet<Object>();
                            Object[] array=it.next().toArray(); //get an array object of the sets to copy the contents of the sets
                            for(int j=0;j<array.length;j++){
                                temp.add(array[j]);
                            }
                            temp.add(i);    //append the vertex into the set
                            partitions2.add(temp);  //add the new set into temporary partitions vector
                        }
                        //add every sets inside partitions2 into partitions
                        Iterator<Set<Object>> it2=partitions2.iterator();   
                        while(it2.hasNext()){
                            partitions.add(it2.next());
                        }
                    }
                    //add the vertex itself as a set and add it to partitions set
                    Set<Object> temp=new HashSet<Object>();
                    temp.add(i);
                    partitions.add(temp);
                }
                
                int[] weight=new int[partitions.size()];    //array to hold the weights of each set
                int index=0;    //index for weight
                //count all of the weights of each set minus all shared edges between vertices in the set to eliminate double couting
                Iterator<Set<Object>> it=partitions.iterator();
                while(it.hasNext()){
                    int counter2=0;  //counter for total weights 
                    int intersect=0;    //counter for shared edges between vertices within the same set
                    Object[] temp=it.next().toArray();
                    for(int i=0;i<temp.length;i++){
                        for(int j=0;j<adj[i].length;j++){
                            if(adj[(Integer)temp[i]][j]==1){
                                counter2++;
                                if(contains(temp,j)) intersect++;
                            }
                        }
                    }//end of double for loop
                    weight[index]=counter2-intersect;
                    index++;
                } //end of iterating while loop

                //writing the maximum sets and its corresponding weight to the output file
                writer.write("BruteForce Algorithm\n");
                writer.write("Graph with Vertices "+adj.length+"\n");
                int index3=0;
                int maxWeight=getMaxweight(weight);
                Iterator<Set<Object>> it3=partitions.iterator();
                while(it3.hasNext()){
                     if(weight[index3]==maxWeight){
                        writer.write(it3.next()+"  Weight: "+weight[index3]+"\n");
                    }
                    else{
                        it3.next();
                    }
                    index3++;
                }
                writer.write("\n");
                
                //RANDOM ALGORITHM
                int num_vertices=adj.length;
                Vector<Candidate> RA_Solutions=new Vector<>();

                //iteration until max gen
                //Math.random algorithm
                for(int i=0;i<max_gen;i++){
                    Candidate[] current_gen=new Candidate[max_pop]; //array that holds candidates for a current generation
                    //create current population by creating random candidates and put it into current gen
                    for(int j=0;j<max_pop;j++){
                        //generate random number for how many vertices will be in the set 
                        int num=(int)(Math.random()*(num_vertices-1)+1);
                        int[] set=new int[num_vertices];    //binary encoding of the sets stored in an array
                        for(int k=0;k<num;k++){
                            set[(int)(Math.random()*(num_vertices-1))]=1;   //1=vertex present  0=not present
                        }
                        Candidate temp=new Candidate(set);
                        current_gen[j]=temp;
                    }
                    //have all the population for current generation stored in current_gen
                    //calculate weight and fitness score
                    //fitess score is the ratio from its weight to fitness threshold
                    for(int j=0;j<current_gen.length;j++){
                        int cur_weight=calculateWeight(current_gen[j].set, adj);
                        current_gen[j].setWeight(cur_weight);
                        current_gen[j].setFitness(((double)cur_weight/maxWeight)*100);
                    }
                    //have all the weight for each candidate of this generation
                    //store max weights into solutions
                    int max=findMax(current_gen);
                    for(int j=0;j<current_gen.length;j++){
                        if(current_gen[j].getWeight()==max) RA_Solutions.add(current_gen[j]);
                    }
                }
                //write to output file
                writer.write("Math.Random Algorithm\n");
                writer.write("Graph with Vertices "+adj.length+"\n");
                Candidate top1=top1(RA_Solutions);
                Candidate bottom1=bottom1(RA_Solutions);
                Candidate average=average(RA_Solutions);
                writer.write("TOP SOLUTION:  "+top1.print()+"\n");
                writer.write("WORST SOLUTION:  "+bottom1.print()+"\n");
                writer.write("AVERAGE SOLUTION:  "+average.print()+"\n");
                writer.write("\n");
                
                //iteration until max gen
                //BBS algorithm
                Vector<Candidate> BBS_Solutions=new Vector<>();
                int var=127;
                for(int i=0;i<max_gen;i++){
                    Candidate[] current_gen=new Candidate[max_pop]; //array that holds candidates for a current generation
                    //create current population by creating random candidates and put it into current gen
                    for(int j=0;j<max_pop;j++){
                        //generate random number for how many vertices will be in the set 
                        var=BBS(67,79,var);
                        String decimal="."+var;
                        double rand=Double.parseDouble(decimal);
                        int num=(int)(rand*(num_vertices-1)+1);
                        int[] set=new int[num_vertices];    //binary encoding of the sets stored in an array
                        for(int k=0;k<num;k++){
                            var=BBS(67,79,var);
                            decimal="."+var;
                            rand=Double.parseDouble(decimal);
                            int random_index=(int)(rand*(num_vertices-1)+1);
                            set[random_index]=1;   //1=vertex present  0=not present
                        }
                        Candidate temp=new Candidate(set);
                        current_gen[j]=temp;
                    }
                    //have all the population for current generation stored in current_gen
                    //calculate weight and fitness score
                    //fitess score is the ratio from its weight to fitness threshold
                    for(int j=0;j<current_gen.length;j++){
                        int cur_weight=calculateWeight(current_gen[j].set, adj);
                        current_gen[j].setWeight(cur_weight);
                        current_gen[j].setFitness(((double)cur_weight/maxWeight)*100);
                    }
                    //have all the weight for each candidate of this generation
                    //store max weights into solutions
                    int max=findMax(current_gen);
                    for(int j=0;j<current_gen.length;j++){
                        if(current_gen[j].getWeight()==max) BBS_Solutions.add(current_gen[j]);
                    }
                }

                 //write to output file
                 writer.write("BBS Algorithm\n");
                 writer.write("Graph with Vertices "+adj.length+"\n");
                 Candidate BBS_top1=top1(BBS_Solutions);
                 Candidate BBS_bottom1=bottom1(BBS_Solutions);
                 Candidate BBS_average=average(BBS_Solutions);
                 writer.write("TOP SOLUTION:  "+BBS_top1.print()+"\n");
                 writer.write("WORST SOLUTION:  "+BBS_bottom1.print()+"\n");
                 writer.write("AVERAGE SOLUTION:  "+BBS_average.print()+"\n");
                 writer.write("\n");

                //GENETIC ALGORITHM PHASE 3
                Vector<Candidate> GA_Solutions=new Vector<>();
                Candidate[] current_gen=new Candidate[max_pop]; //array that holds candidates for a current generation
                for(int i=0;i<max_gen;i++){
                    //initial population
                    if(i==0){
                        //create current population by creating random candidates and put it into current gen
                        for(int j=0;j<max_pop;j++){
                            //generate random number for how many vertices will be in the set 
                            int num=(int)(Math.random()*(num_vertices-1)+1);
                            int[] set=new int[num_vertices];    //binary encoding of the sets stored in an array
                            for(int k=0;k<num;k++){
                                set[(int)(Math.random()*(num_vertices-1))]=1;   //1=vertex present  0=not present
                            }
                            Candidate temp=new Candidate(set);
                            current_gen[j]=temp;
                        }
                        //have all the population for current generation stored in current_gen
                        //calculate weight and fitness score
                        //fitess score is the ratio from its weight to fitness threshold
                        for(int j=0;j<current_gen.length;j++){
                            int cur_weight=calculateWeight(current_gen[j].set, adj);
                            current_gen[j].setWeight(cur_weight);
                            current_gen[j].setFitness(((double)cur_weight/maxWeight)*100);
                        }
                        //have all the weight for each candidate of this generation
                        //store max weights into solutions
                        int max=findMax(current_gen);
                        for(int j=0;j<current_gen.length;j++){
                            if(current_gen[j].getWeight()==max) GA_Solutions.add(current_gen[j]);
                        }
                    }
                    //not initial population
                    else{
                        Candidate[] new_gen=new Candidate[max_pop];
                        //ELITISM: select 5 top candidates to the new generation
                        new_gen[0]=nth_candidate(current_gen,1);
                        new_gen[1]=nth_candidate(current_gen,2);
                        new_gen[2]=nth_candidate(current_gen,3);
                        new_gen[3]=nth_candidate(current_gen,4);
                        new_gen[4]=nth_candidate(current_gen,5);
                        //crossover to create rest of the population
                        for(int j=5;j<max_pop;j++){
                            Candidate parent1=selectionMethod(current_gen);
                            Candidate parent2=selectionMethod(current_gen);
                            Candidate child=crossoverMethod(parent1,parent2);
                            new_gen[j]=child;
                        }
                        //mutation crossover
                        int numberOfMutations=(int)(Math.random()*new_gen.length);
                        for(int j=0;j<numberOfMutations;j++){
                            int randomCandidate=(int)(Math.random()*new_gen.length);
                            mutationOperator(new_gen,randomCandidate);
                        }

                        //have all the population for current generation stored in current_gen
                        //calculate weight and fitness score
                        //fitess score is the ratio from its weight to fitness threshold
                        for(int j=0;j<new_gen.length;j++){
                            int cur_weight=calculateWeight(new_gen[j].set, adj);
                            new_gen[j].setWeight(cur_weight);
                            new_gen[j].setFitness(((double)cur_weight/maxWeight)*100);
                        }
                        //have all the weight for each candidate of this generation
                        //store max weights into solutions
                        int max=findMax(new_gen);
                        for(int j=0;j<new_gen.length;j++){
                            if(new_gen[j].getWeight()==max) GA_Solutions.add(new_gen[j]);
                        }
                        //update current gen to new gen
                        for(int j=0;j<new_gen.length;j++){
                            current_gen[j]=new_gen[j];
                        }
                    }
                }
                 //write to output file
                 writer.write("GA Algorithm\n");
                 writer.write("Graph with Vertices "+adj.length+"\n");
                 Candidate GA_top1=top1(GA_Solutions);
                 Candidate GA_bottom1=bottom1(GA_Solutions);
                 Candidate GA_average=average(GA_Solutions);
                 writer.write("TOP SOLUTION:  "+GA_top1.print()+"\n");
                 writer.write("WORST SOLUTION:  "+GA_bottom1.print()+"\n");
                 writer.write("AVERAGE SOLUTION:  "+GA_average.print()+"\n");
                 writer.write("\n");
            }
            writer.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }//end of main
}