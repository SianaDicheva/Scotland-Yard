package uk.ac.bris.cs.scotlandyard.ui.ai;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.*;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;
public class GameStateSimulation  {
    private Integer mrXposition;
    private ArrayList<Integer> detectivelocations;
    GameStateSimulation(Integer mrXposition, ArrayList<Integer> detectivelocations){
        this.mrXposition = mrXposition;
        this.detectivelocations = detectivelocations;
    }
    public Integer getmrXposition(){
        return mrXposition;
    }
    public ArrayList<Integer> getDetectivelocations(){
        return detectivelocations;
    }
    public void setmrXposition(Integer newposition){
        mrXposition = newposition;
    }

    public void setdetectiveposition(Integer newposition, int index){
        detectivelocations.remove(index);
        detectivelocations.add(index, newposition);
    }

}
