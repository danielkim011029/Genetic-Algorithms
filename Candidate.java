//Individual candidates for a population that contains the binary encoding, the weight, and the fitness
//For binary encoding, set[i]=0 represents that vertex i does not exist in the set and 
//set[i]=1 represents that vertex i exist in the set.
//For example: [0,1,0,0,1] would represent a set {1,4}
public class Candidate {
    int[] set;
    int weight;
    double fitness;
    public Candidate(){
        set=null;
        weight=0;
        fitness=0;
    }
    public Candidate(int[] s){
        set=s;
    }

    public void setSet(int[] set){
        this.set=set;
    }
    public void setWeight(int weight){
        this.weight=weight;
    }

    public void setFitness(double fitness){
        this.fitness=fitness;
    }

    public int getWeight(){
        return weight;
    }

    public double getFitness(){
        return fitness;
    }


    public String print(){
        String output="";
        output=output+"{";
        for(int i=0;i<set.length;i++){
            if(i!=set.length-1){
                output=output+set[i]+",";
            }
            else{
                output=output+set[i];
            }
        }
        output=output+"}    Weight: "+weight+"   Fitness: "+fitness+"%";
        return output;
    }
}
