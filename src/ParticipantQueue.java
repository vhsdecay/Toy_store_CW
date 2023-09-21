import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

public class ParticipantQueue implements Iterable<Participant> {
    PriorityQueue<Participant> drawQueue;

    public ParticipantQueue(Collection<Participant> list) {
        this.drawQueue = new PriorityQueue<>(list.size());
        this.drawQueue.addAll(list);
    }

    public ParticipantQueue() {
        this.drawQueue = new PriorityQueue<>();
    }

    void addParticipant(Participant p){
        this.drawQueue.add(p);
    }

    class ParticipantIterator implements Iterator<Participant>{
        Participant current;
        public ParticipantIterator(PriorityQueue<Participant> participants) {
            this.current = participants.peek();
        }

        @Override
        public boolean hasNext() {
            return !drawQueue.isEmpty();
        }

        @Override
        public Participant next() {
            return drawQueue.poll();
        }
    }


    @Override
    public Iterator<Participant> iterator() {
        return new ParticipantIterator(drawQueue);
    }

}
