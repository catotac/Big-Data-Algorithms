import java.util.HashSet;



/*This class is the vertex class that holds info about a vertex*/
    public class Vertex {
        String name;
        int inDegree =0 , outDegree=0, index;
        HashSet<Vertex> neighborsSet = new HashSet<Vertex>(); //set of all neighborsSet having edges from this
        public Vertex(String name,int index){
            this.name = name;
            this.index = index;
        }

        @Override
        public boolean equals(Object object){
            if(object instanceof Vertex && this.index == ((Vertex)object).index){
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.index);
        }
    }
