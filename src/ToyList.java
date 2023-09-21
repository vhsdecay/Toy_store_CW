import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class ToyList {
    HashMap<Integer,Toy> toys = new HashMap<>();
    String toyFilepath;
    FileS f = new FileS();
    protected int maxKey;


    public ToyList(String filepath) {
        this.readFromFile(filepath);
        this.maxKey = Collections.max(toys.keySet());
    }
    public ToyList() {
        this.readFromFile("src/toylist.txt");
        this.maxKey = Collections.max(toys.keySet());
    }

    void addToyList(Collection<Toy> newtoys){
        for (Toy t: newtoys){
            this.addToy(t);
        }
    }

    int addToy(Toy t){
        int finalId = t.id;
        if(toys.containsKey(t.id)){
            finalId = ++maxKey;
            t.setId(finalId);
        }
        toys.put(t.id,t);
        return finalId;
    }
    void removeToy(int idNum){
        toys.remove(idNum);
    }

    void readFromFile(String filepath){
        this.toyFilepath=filepath;
        for (String line:f.readToys(filepath)){
            String[] toyParams = line.split(" ",4);
            int toyId = Integer.parseInt(toyParams[0]);
            toys.put(toyId,new Toy(toyId,
                    Double.parseDouble(toyParams[1]),
                    toyParams[3],
                    Integer.parseInt(toyParams[2])));
        }

    }

    void saveToFile(){
        f.writeToys(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb =new StringBuilder();
        for (Toy t: toys.values()){
            sb.append(t.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
