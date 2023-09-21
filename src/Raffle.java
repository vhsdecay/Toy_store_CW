import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
/**
 *  Класс, который смотрит веса игрушек, назначает им относительный шанс в текущем розыгрыше, и разыгрывает игрушки по списку участников.
 *  Шансы считаются по простой формуле (вес игрушки/общий вес очереди), возможность проигрыша закладывается переменной lossWeight,
 *  которая задает вес вероятного проигрыша.
 */
public class Raffle {
    ToyList currentToys;
    ParticipantQueue currentParticipants;
    double lossWeight = 0; //0 для соответствия заданию, где веса разбиваются на полную вероятность в 100%
    int lossId;

    ChanceCalc cc = new ChanceCalc();
    Raffle.QuantityCalc qc = new Raffle.QuantityCalc();

    public Raffle(ParticipantQueue kids, ToyList tl) {

        this.currentToys = cc.assignChance(tl);
        this.currentParticipants = kids;
    }

    /**
     * основной метод перебора розыгрыша
     */
    public void runRaffle() {
        ParticipantQueue kids = this.currentParticipants;
        ToyList tl = this.currentToys;
        PriorityQueue<Toy> prizes = new PriorityQueue<>(tl.toys.values());
        try {
            BufferedWriter log = FileS.raffleLog();

            while(kids.iterator().hasNext()){
                double winRoll = cc.doRoll();
                Participant k = kids.iterator().next();
                try {
                    Toy win = cc.checkPrize(prizes, winRoll);
                    //showRoll(k,win,winRoll);
                    prizes = qc.adjustQuantityLeft(win,tl,prizes);
                    log.write(showWin(k, win) + "\n");
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }

            }
            log.close();

        } catch(IOException e) {
            e.printStackTrace();
        }

    }


    String showWin(Participant kid, Toy prize) {
        String winLine;
        if(prize.name.equals("ничего")){
            winLine = kid.toString() + " не выиграл ничего";
        } else {
            winLine = kid.toString() + " выиграл " + prize.name;
        }
        System.out.println(winLine);
        return winLine;
    }

    void showRoll(Participant kid, Toy prize, double roll) {
        System.out.printf("%s бросает на %.3f , это ниже шанса %.2f у %s%n", kid.name, roll, prize.getChance(), prize.name);
    }

    /**
     * Вручную задайте вес проигрыша. Относительные вероятности в розыгрыше пересчитаются соответственно.
     *
     * @param lossWeight вес по той же шкале, что и веса игрушек.
     */
    public void setLossWeight(double lossWeight) {
        this.lossWeight = lossWeight;
        this.lossId = this.currentToys.addToy(new Toy(lossWeight, "ничего", -1));
        cc.assignChance(currentToys);
    }

// Здесь собраны методы, отвечающие за учет остатков призов

    class QuantityCalc {
        PriorityQueue<Toy> adjustQuantityLeft(Toy t, ToyList tl,PriorityQueue<Toy> currentQueue) {
            if(t.quantity > 0){
                t.quantity -= 1;
            }
            if(t.quantity == 0){
                removeStock(t.id, tl);
                Raffle.this.cc.assignChance(tl);
                currentQueue = new PriorityQueue<>(tl.toys.values());
            }
            return currentQueue;
        }

        void removeStock(int idNum, ToyList toys) {
            toys.removeToy(idNum);
        }


    }

}
/**
 * здесь собраны методы расчета вероятности, служебный класс для Raffle
 */
class ChanceCalc {
    Random r = new Random();
    double maxChance;
    double totalWeight;

    double doRoll(){
        return r.nextDouble()*maxChance;
    }

    Toy checkPrize(PriorityQueue<Toy> prizes, double roll) throws Exception {
        PriorityQueue<Toy> onePoll = new PriorityQueue<>(prizes);
        while(!onePoll.isEmpty()){
            Toy p = onePoll.poll();
            if(roll <= p.getChance()){
                return checkTies(onePoll,p);
            }
        }
        throw new Exception("Приз с такой вероятностью не найден");
    }

    ToyList assignChance(ToyList tl){
        this.totalWeight = 0;
        this.maxChance = 0;
        for (Toy t:tl.toys.values()){
            this.totalWeight += t.chanceWeight;
        }

        for (Toy t:tl.toys.values()){
            double ch = t.chanceWeight/totalWeight;
            t.setChance(ch);
            if(maxChance < ch ){
                maxChance = ch;
            }
        }
        return tl;
    }

    Toy checkTies(PriorityQueue<Toy> leftovers, Toy drawn  ){
        PriorityQueue<Toy> tiePoll = new PriorityQueue<>(leftovers);
        ArrayList<Toy> sameChance = new ArrayList<>();
        while(!tiePoll.isEmpty()){
            if(drawn.getChance() == tiePoll.peek().getChance()){
                sameChance.add(tiePoll.poll());
            }else {break;}
        }
        sameChance.add(drawn);
        int pickRandom = r.nextInt(sameChance.size());
        return sameChance.get(pickRandom);
    }


}

