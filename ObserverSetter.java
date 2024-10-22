package teamcode;

import java.util.ArrayList;
import java.util.List;
//public class GlobalVar{
    //public static boolean CurrentModeOfBreak;

//}
interface Observer{
    void update(boolean BreakOn);
}
 class EventSource {
   //public static boolean BreakOn;

     List<Observer> observers = new ArrayList<>();

    public  void notifyObservers(boolean BreakOn) {
        observers.forEach(observer -> observer.update(BreakOn));
    }
    public  void addObserver(Observer observer) {
        observers.add(observer);
    }
}

public class ObserverSetter implements Observer{

    public static boolean CurrentModeOfBreak;

    @Override
    public void update(boolean BreakOn) {

        CurrentModeOfBreak = BreakOn;
    }
}
