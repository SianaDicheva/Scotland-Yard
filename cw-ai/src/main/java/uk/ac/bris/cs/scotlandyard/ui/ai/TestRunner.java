package uk.ac.bris.cs.scotlandyard.ui.ai;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard;
import java.io.IOException;
public class TestRunner {
    public static void main(String[] args){
        Tests idkwhyineedtodothis = new Tests();
        try {
            idkwhyineedtodothis.testshortpath(ScotlandYard.standardGraph());
            idkwhyineedtodothis.testdistancefromdtectivesSingleMove(ScotlandYard.standardGraph());
            idkwhyineedtodothis.testdistanceframdetectivesDubleMove(ScotlandYard.standardGraph());
            idkwhyineedtodothis.testnumberofadjecentnodesSingleMove(ScotlandYard.standardGraph());
            idkwhyineedtodothis.testnumberofadjecentnodesDoubleMove(ScotlandYard.standardGraph());
            idkwhyineedtodothis.testnearbydetectivesSingleMove(ScotlandYard.standardGraph());
            idkwhyineedtodothis.testnearbydetectivesDoubleMove(ScotlandYard.standardGraph());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("All tests pass");
    }
}