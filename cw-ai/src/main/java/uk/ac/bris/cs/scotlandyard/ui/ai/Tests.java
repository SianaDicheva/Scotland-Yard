package uk.ac.bris.cs.scotlandyard.ui.ai;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import org.junit.jupiter.api.Test;
import uk.ac.bris.cs.scotlandyard.model.*;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class Tests {
    @Test
    public void testshortpath(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph){
        Integer mrXposition = 75;
        ArrayList<Integer> detectevesposition = new ArrayList<Integer>();
        detectevesposition.add(120);
        detectevesposition.add(45);
        detectevesposition.add(22);
        detectevesposition.add(35);
        detectevesposition.add(43);
        detectevesposition.add(119);
        MyAi testAI = new MyAi();
        assertThat(testAI.shortpath(graph, mrXposition, 120) == 5).isTrue();
        assertThat(testAI.shortpath(graph, mrXposition, 45) == 2).isTrue();
        assertThat(testAI.shortpath(graph, mrXposition, 22) == 4).isTrue();
        assertThat(testAI.shortpath(graph, mrXposition, 35) == 5).isTrue();
        assertThat(testAI.shortpath(graph, mrXposition, 43) == 3).isTrue();
        assertThat(testAI.shortpath(graph, mrXposition, 119) == 7).isTrue();
    }


    @Test
    public void testdistancefromdtectivesSingleMove(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph){
        Integer mrXposition = 75;
        ArrayList<Integer> detectevesposition = new ArrayList<Integer>();
        detectevesposition.add(120);
        detectevesposition.add(45);
        detectevesposition.add(22);
        detectevesposition.add(35);
        detectevesposition.add(43);
        detectevesposition.add(119);
        Move.SingleMove sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 58);
        MyAi testAI = new MyAi();
        GameStateSimulation gss = new GameStateSimulation(mrXposition,detectevesposition);
        assertThat(testAI.distancefromdetectivessinglemove(graph,sm,gss) == 3).isTrue();
        sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 74);
        assertThat(testAI.distancefromdetectivessinglemove(graph,sm,gss) == 3).isTrue();
        sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94);
        assertThat(testAI.distancefromdetectivessinglemove(graph,sm,gss) == 4).isTrue();
        sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 59);
        assertThat(testAI.distancefromdetectivessinglemove(graph,sm,gss) == 4).isTrue();
    }


    @Test
    public void testdistanceframdetectivesDubleMove(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph) {
        Integer mrXposition = 75;
        ArrayList<Integer> detectevesposition = new ArrayList<Integer>();
        detectevesposition.add(120);
        detectevesposition.add(45);
        detectevesposition.add(22);
        detectevesposition.add(35);
        detectevesposition.add(43);
        detectevesposition.add(119);
        GameStateSimulation gss = new GameStateSimulation(mrXposition, detectevesposition);
        MyAi testAI = new MyAi();
        Move.DoubleMove dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 74);
        assertThat(testAI.distancefromdetectivesdoublemove(graph, dm, gss) == 3).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 75);
        assertThat(testAI.distancefromdetectivesdoublemove(graph, dm, gss) == 4).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 77);
        assertThat(testAI.distancefromdetectivesdoublemove(graph, dm, gss) == 4).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 93);
        assertThat(testAI.distancefromdetectivesdoublemove(graph, dm, gss) == 4).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 95);
        assertThat(testAI.distancefromdetectivesdoublemove(graph, dm, gss) == 4).isTrue();
    }

    @Test
    public void testnearbydetectivesSingleMove(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph){
        Integer mrXposition = 75;
        ArrayList<Integer> detectevesposition = new ArrayList<Integer>();
        detectevesposition.add(74);
        detectevesposition.add(45);
        detectevesposition.add(22);
        detectevesposition.add(35);
        detectevesposition.add(43);
        detectevesposition.add(119);
        GameStateSimulation gss = new GameStateSimulation(mrXposition, detectevesposition);
        MyAi testAI = new MyAi();
        Move.SingleMove sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94);
        assertThat(testAI.neearbydetectivesSingleMove(graph, sm, gss) == -20).isTrue();
        gss.setdetectiveposition(77, 1);
        assertThat(testAI.neearbydetectivesSingleMove(graph, sm, gss) == -40).isTrue();
        gss.setdetectiveposition(93, 2);
        assertThat(testAI.neearbydetectivesSingleMove(graph, sm, gss) == -60).isTrue();
        gss.setdetectiveposition(95, 3);
        assertThat(testAI.neearbydetectivesSingleMove(graph, sm, gss) == -80).isTrue();
    }

    @Test
    public void testnearbydetectivesDoubleMove(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph){
        Integer mrXposition = 75;
        ArrayList<Integer> detectevesposition = new ArrayList<Integer>();
        detectevesposition.add(73);
        detectevesposition.add(45);
        detectevesposition.add(22);
        detectevesposition.add(35);
        detectevesposition.add(43);
        detectevesposition.add(119);
        GameStateSimulation gss = new GameStateSimulation(mrXposition, detectevesposition);
        MyAi testAI = new MyAi();
        Move.DoubleMove dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 74);
        assertThat(testAI.neearbydetectivesDOUBLEeMove(graph, dm, gss) == -20).isTrue();
        gss.setdetectiveposition(58, 1);
        assertThat(testAI.neearbydetectivesDOUBLEeMove(graph, dm, gss) == -40).isTrue();
        gss.setdetectiveposition(92, 2);
        assertThat(testAI.neearbydetectivesDOUBLEeMove(graph, dm, gss) == -60).isTrue();
        gss.setdetectiveposition(46, 3);
        assertThat(testAI.neearbydetectivesDOUBLEeMove(graph, dm, gss) == -80).isTrue();
    }

    @Test
    public void testnumberofadjecentnodesSingleMove(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph){
        Integer mrXposition = 75;
        Move.SingleMove sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 58 );
        MyAi testAI = new MyAi();
        assertThat(testAI.numberofadjacentnodesSM(graph, sm) == 18).isTrue();
        sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 74 );
        assertThat(testAI.numberofadjacentnodesSM(graph, sm) == 12).isTrue();
        sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94 );
        assertThat(testAI.numberofadjacentnodesSM(graph, sm) == 10).isTrue();
        sm = new Move.SingleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 59 );
        assertThat(testAI.numberofadjacentnodesSM(graph, sm) == 8).isTrue();
    }



    @Test
    public void testnumberofadjecentnodesDoubleMove(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph){
        Integer mrXposition = 75;
        MyAi testAI = new MyAi();
        Move.DoubleMove dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 74);
        assertThat(testAI.numberofadjacentnodesDM(graph, dm)== 12).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 75);
        assertThat(testAI.numberofadjacentnodesDM(graph, dm) == 8).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 77);
        assertThat(testAI.numberofadjacentnodesDM(graph, dm) == 14).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 93);
        assertThat(testAI.numberofadjacentnodesDM(graph, dm) == 3).isTrue();
        dm = new Move.DoubleMove(Piece.MrX.MRX, mrXposition, ScotlandYard.Ticket.TAXI, 94, ScotlandYard.Ticket.TAXI, 95);
        assertThat(testAI.numberofadjacentnodesDM(graph, dm) == 3).isTrue();
    }


}
