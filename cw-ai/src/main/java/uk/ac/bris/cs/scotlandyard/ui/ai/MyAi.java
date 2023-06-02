package uk.ac.bris.cs.scotlandyard.ui.ai;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import io.atlassian.fugue.Pair;
import uk.ac.bris.cs.scotlandyard.model.*;
public class MyAi implements Ai {
	@Nonnull
	@Override
	public String name() {
		return "Prost";
	}

	@Nonnull @Override public Move pickMove(
			@Nonnull Board board,
			Pair<Long, TimeUnit> timeoutPair) {
		var moves = board.getAvailableMoves().asList();
		GameStateSimulation gss = new GameStateSimulation(moves.get(0).source(), detectivelocations(board));
		ArrayList<Integer> scores = score(board, gss);
		int moveDestination = minimax(board, true, 5, gss,Integer.MIN_VALUE,Integer.MAX_VALUE);
		int chosenMove = 0;
		for(int i = 0; i < moves.size(); i++ ){
			Integer x = moves.get(i).visit(new Move.Visitor<>(){
				@Override public Integer visit ( Move.SingleMove singleMove){
					if(singleMove.destination == moveDestination){
						return 1;
					}
					else return 0;
				}
				@Override public Integer visit(Move.DoubleMove doubleMove){
					if(doubleMove.destination2 == moveDestination){
						return 1;
					}
					else return 0;
				}
			});
			if( x == 1 ){
				chosenMove = i;
			}
		}
		return moves.get(chosenMove);
	}

	public ArrayList<Integer> score(Board board, GameStateSimulation gss){
		var moves = board.getAvailableMoves().asList();
		ArrayList<Integer> scores = new ArrayList<>();
		for(Move m : moves){
			Integer x = m.visit(new Move.Visitor<>(){
				@Override public Integer visit ( Move.SingleMove singleMove){
					int x = distancefromdetectivessinglemove(board.getSetup().graph, singleMove, gss);
					x = x + neearbydetectivesSingleMove(board.getSetup().graph, singleMove, gss);
					x = x + numberofadjacentnodesSM(board.getSetup().graph, singleMove);
					return x;
				}
				@Override public Integer visit(Move.DoubleMove doubleMove){
					int x = distancefromdetectivesdoublemove(board.getSetup().graph, doubleMove, gss);
					x = x + neearbydetectivesDOUBLEeMove(board.getSetup().graph, doubleMove, gss);
					x = x + numberofadjacentnodesDM(board.getSetup().graph, doubleMove);
					return x;
				}
			});
			scores.add(x);

		}
		return scores;
	}

	public ArrayList<Integer> detectivelocations(Board board){
		int x = 0;
		ArrayList<Integer> locations = new ArrayList<>();
		for( Piece d : board.getPlayers()){
			if(d == Piece.Detective.RED){
				x = board.getDetectiveLocation(Piece.Detective.RED).get();
				locations.add(x);
			}
			if(d == Piece.Detective.BLUE){
				x =board.getDetectiveLocation(Piece.Detective.BLUE).get();
				locations.add(x);
			}
			if(d == Piece.Detective.GREEN){
				x =  board.getDetectiveLocation(Piece.Detective.GREEN).get();
				locations.add(x);
			}
			if(d == Piece.Detective.WHITE){
				x = board.getDetectiveLocation(Piece.Detective.WHITE).get();
				locations.add(x);
			}
			if(d == Piece.Detective.YELLOW){
				x =board.getDetectiveLocation(Piece.Detective.YELLOW).get();
				locations.add(x);
			}
		}
		return locations;
	}
	int distancefromdetectivessinglemove(ImmutableValueGraph<Integer, com.google.common.collect.ImmutableSet<uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport>> graph, Move.SingleMove ms, GameStateSimulation gss){
		int x = 0;
		for( Integer d : gss.getDetectivelocations()){
			x = x + shortpath(graph, ms.destination, d);
		}
		return x / gss.getDetectivelocations().size();
	}
	int distancefromdetectivesdoublemove(ImmutableValueGraph<Integer, com.google.common.collect.ImmutableSet<uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport>> graph,
										 Move.DoubleMove md,
										 GameStateSimulation gss){
		int x = 0;
		for( Integer d : gss.getDetectivelocations()){
			x = x + shortpath(graph, md.destination2, d);
		}
		return x / gss.getDetectivelocations().size();
	}
	public int neearbydetectivesSingleMove(ImmutableValueGraph<Integer, com.google.common.collect.ImmutableSet<uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport>> graph,
										   Move.SingleMove ms,
										   GameStateSimulation gss){
		int x = 0;
		for(int n : graph.adjacentNodes(ms.destination)){
			for(Integer d : gss.getDetectivelocations()){
				if(n == d){
					x = x - 20;
				}
			}
		}
		return x;
	}
	public int neearbydetectivesDOUBLEeMove(ImmutableValueGraph<Integer, com.google.common.collect.ImmutableSet<uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport>> graph,
											Move.DoubleMove md,
											GameStateSimulation gss){
		int x = 0;
		for(int n : graph.adjacentNodes(md.destination2)){
			for(Integer d : gss.getDetectivelocations()){
				if( n == d){
					x = x - 20;
				}
			}
		}
		return x;
	}

	public int numberofadjacentnodesSM(ImmutableValueGraph<Integer, com.google.common.collect.ImmutableSet<uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport>> graph,
									   Move.SingleMove m){
		int x =  graph.adjacentNodes(m.destination).size();
		if(x > 3) {
			return x *2;
		}
		else return x;
	}
	public int numberofadjacentnodesDM(ImmutableValueGraph<Integer, ImmutableSet<ScotlandYard.Transport>> graph,
									   Move.DoubleMove m){
		int x = graph.adjacentNodes(m.destination2).size();
		if(x > 3) {
			return x *2;
		}
		else return x;
	}

	//https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
	//We the Wikipedia webpage for inspiration and to see the pseudocode
	int shortpath(ImmutableValueGraph<Integer, com.google.common.collect.ImmutableSet<uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Transport>> graph,
				  Integer source,
				  Integer target) {
		ArrayList<Integer> Q = new ArrayList<>();
		Integer[] dist = new Integer[graph.nodes().size() + 1];
		Integer[] prev =  new Integer[graph.nodes().size() + 1 ];
		Integer u;
		Integer alt;
		for(Integer v : graph.nodes()){
			dist[v] = graph.nodes().size() + 1;
			prev[v] = - 1;
			Q.add(v);
		}
		dist[source] = 0;
		while(!Q.isEmpty()) {
			u = Q.get(0);
			for (Integer minim : Q) {
				if (dist[minim] < dist[u]) {
					u = minim;
				}
			}
			Q.remove(u);
			if(u == target)
			{
				Q.clear();
			}
			for (Integer v : graph.adjacentNodes(u)) {
				if(Q.contains(v)) {
					alt = dist[u] + 1;
					if (alt < dist[v]) {
						dist[v] = alt;
						prev[v] = u;
					}
				}
			}
		}
		return dist[target];
	}

	//https://en.wikipedia.org/wiki/Minimax#Game_theory
	//We the Wikipedia webpage for inspiration and to see the pseudocode
	private Integer minimax(Board board, boolean isMax, int depth, GameStateSimulation gameStateSimulation,int alpha,int beta) {
		var moves = board.getAvailableMoves().asList();
		if (depth == 0) {
			return gameStateSimulation.getmrXposition();
		}
		int bestScore;
		if (isMax) {
			bestScore = Integer.MIN_VALUE;
			for (Move m : moves) {
				Integer destination = m.visit(new Move.Visitor<>() {
					@Override
					public Integer visit(Move.SingleMove singleMove) {
						return singleMove.destination;
					}
					@Override
					public Integer visit(Move.DoubleMove doubleMove) {
						return doubleMove.destination2;
					}
				});
				GameStateSimulation gameStateSimulation2 = new GameStateSimulation(destination, detectivelocations(board));
				int score = minimax(board, false, depth - 1, gameStateSimulation, alpha, beta);
				bestScore = Integer.max(score, bestScore);
				alpha = Integer.max(alpha, bestScore);
				if (beta <= alpha) {
					break;
				}
				return bestScore;
			}
			return bestScore;
		}
		else {
			bestScore = Integer.MAX_VALUE;
			for (Move m : moves) {
				Integer destination = m.visit(new Move.Visitor<>() {
					@Override
					public Integer visit(Move.SingleMove singleMove) {
						return singleMove.destination;
					}
					@Override
					public Integer visit(Move.DoubleMove doubleMove) {
						return doubleMove.destination2;
					}
				});
				GameStateSimulation gameStateSimulation2 = new GameStateSimulation(destination, detectivelocations(board));
				int score = minimax(board, true, depth - 1, gameStateSimulation, alpha, beta);
				bestScore = Integer.min(score, bestScore);
				beta = Integer.min(beta, bestScore);
				if (beta <= alpha) {
					break;
				}
				return bestScore;
			}
			return bestScore;
		}

	}
}
