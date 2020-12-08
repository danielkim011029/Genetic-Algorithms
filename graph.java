//Object that holds 2d array
public class graph {
    int[][] adj;

    public graph(int[][] s){
        adj=s;
    }

    public void print(){
        for(int i=0;i<adj.length;i++){
            for(int j=0;j<adj[i].length;j++){
                System.out.print(adj[i][j]+",");
            }
            System.out.println();
        }
    }
}

