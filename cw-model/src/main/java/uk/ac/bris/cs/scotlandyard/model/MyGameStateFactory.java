package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableSet;
import org.checkerframework.checker.units.qual.A;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.*;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	private final class MyGameState implements GameState {

		private GameSetup setup;
		private ImmutableSet<Piece> remaining;
		private ImmutableList<LogEntry> log;
		private Player mrX;
		private List<Player> detectives;
		private ImmutableList<Player> everyone;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;

		private MyGameState(
				final GameSetup setup,
				final ImmutableSet<Piece> remaining,
				final ImmutableList<LogEntry> log,
				final Player mrX,
				final List<Player> detectives){
			if(mrXtest(mrX)) {
				this.mrX = mrX;
			}
			if(detectivetest(detectives)) {
				this.detectives = detectives;
			}
			if(setuptest(setup)) {
				this.setup = setup;
			}
			if(remainingtest(remaining)) {
				this.remaining = remaining;
			}
			this.log = log;
			winner = getWinner();
			moves = getAvailableMoves();
		}

		//Returns the game set up
		@Override
		public GameSetup getSetup() {

			return setup;

		}
		// Returns the players of the game as pieces
		@Override
		public ImmutableSet<Piece> getPlayers() {
			ArrayList<Piece> getPl = new ArrayList<>();
			getPl.add(mrX.piece());
			for (Player y : detectives) {
				getPl.add(y.piece());
			}
			return ImmutableSet.copyOf(getPl);
		}

		//Given a detective piece, it returns its location
		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			for (Player x : detectives) {
				if (x.piece().webColour() == detective.webColour())
					return Optional.of(x.location());
			}

			return Optional.empty();
		}

		//Given a piece as a paramether, returns each players ticketboard
		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			boolean contor = false;
			for(Player d : detectives){
				if(d.piece() == piece)
					contor = true;
			}
			if(piece.isMrX()){
				contor = true;
			}
			if (contor) {
				TicketBoard ticketBoard = new TicketBoard() {
					@Override
					public int getCount(@Nonnull ScotlandYard.Ticket ticket) {
						int count = 0;
						if (piece.isDetective()) {
							for (Player x : detectives) {
								if (x.piece().webColour() == piece.webColour()) {
									count += x.tickets().get(ticket);
								}
							}
							return count;
						}
						if (mrX.isMrX() && piece.isMrX()) {
							count += mrX.tickets().get(ticket);
							return count;
						}
						return count;
					}
				};
				return Optional.ofNullable(ticketBoard);
			}
			else return Optional.empty();
		}

		//Returns MrX travel log
		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() {

			return log;
		}

		//Returns the winner of the game
		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			boolean c = false;
			for(Player d : detectives){
				if(!makeSingleMoves(setup,detectives,d,d.location()).isEmpty()){
					c = true;
				}
			}
			if(!c){
				return ImmutableSet.of(mrX.piece());
			}
			if(remaining.contains(mrX.piece())){
				if(makeSingleMoves(setup,detectives,mrX, mrX.location()).isEmpty()){
					return turndetectivesintopieces(detectives);
				}
				if(log.size() == setup.rounds.size()){
					return ImmutableSet.of(mrX.piece());
				}
			}
			for( Player d : detectives){
				if(d.location() == mrX.location()){
					return turndetectivesintopieces(detectives);
				}
			}
			return ImmutableSet.of();
		}

		/**
		 * The method returns a list of all moves designated to the playing player,
		 * if there is no winner
		 * with the help of the helper methods makeSingleMoves and makeDoubleMoves
		 */
		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			ArrayList<Move> allMoves = new ArrayList<>();
			if(winner.isEmpty()){
				if(remaining.contains(mrX.piece())){
					ImmutableSet<Move.SingleMove> sm = makeSingleMoves(setup, detectives, mrX, mrX.location());
					for(Move.SingleMove s : sm){
						allMoves.add(s);
					}
					if(setup.rounds.size() - log.size() >= 2){
						ImmutableSet<Move.DoubleMove> dm = makeDoubleMoves(setup, detectives, mrX, mrX.location());
						for(Move.DoubleMove d : dm){
							allMoves.add(d);
						}}
				}
   					else {
					for(Player d : detectives){
						if(remaining.contains(d.piece())){
							ImmutableSet<Move.SingleMove> sm = makeSingleMoves(setup, detectives, d, d.location());
							for(Move.SingleMove s : sm){
								allMoves.add(s);
							}
						}}
				}}
			return ImmutableSet.copyOf(allMoves);
		}

		/**
		 * Return a new GameState given a move
		 * Throws a new exception is the move is empty
		 * It implements the Visitor pattern
		 */
		@Override public GameState advance(Move move) {
			if(!moves.contains(move)) throw new IllegalArgumentException("Illegal move: "+move);
			ArrayList<LogEntry> loglist = new ArrayList<>();
			for(LogEntry l : log){
				loglist.add(l);
			}
			Player justmovedplayer = move.visit(new Visitor<Player>(){
				 public Player visit(Move.SingleMove singleMove){
					if(singleMove.commencedBy() == mrX.piece()){
						mrX = mrX.at(singleMove.destination).use(singleMove.ticket);
						if(setup.rounds.get(log.size())){
							loglist.add(LogEntry.reveal(singleMove.ticket, mrX.location()));
						}
						else {
							loglist.add(LogEntry.hidden(singleMove.ticket));
						}
						return mrX;
					}
					else{
						for(Player d : detectives){
							if(d.piece()==singleMove.commencedBy()){
								mrX = mrX.give(singleMove.ticket);
								return d.at(singleMove.destination).use(singleMove.ticket);
							}
						}
					}
					return null;
				}
				 public Player visit(Move.DoubleMove doubleMove){
					mrX = mrX.at(doubleMove.destination1).use(doubleMove.ticket1).use(doubleMove.ticket2).use(ScotlandYard.Ticket.DOUBLE);
					if(setup.rounds.get(log.size())){
						loglist.add(LogEntry.reveal(doubleMove.ticket1, mrX.location()));
					}
					else{
						loglist.add(LogEntry.hidden(doubleMove.ticket1));
					}
					mrX = mrX.at(doubleMove.destination2);
					if(setup.rounds.get(log.size() + 1)){
						loglist.add(LogEntry.reveal(doubleMove.ticket2, mrX.location()));
					}
					else{
						loglist.add(LogEntry.hidden(doubleMove.ticket2));
					}
					return mrX;
				}
			});
			ArrayList<Piece> newremaining = createnewremaining(justmovedplayer, remaining);
			if(justmovedplayer.isMrX()){
				for(Player p : detectives){
					if(p.has(ScotlandYard.Ticket.UNDERGROUND) || p.has(ScotlandYard.Ticket.BUS) || p.has(ScotlandYard.Ticket.TAXI)){
						newremaining.add(p.piece());}
				}
				return new MyGameState(setup, ImmutableSet.copyOf(newremaining), ImmutableList.copyOf(loglist), justmovedplayer, detectives);
			}
			else{
				ArrayList<Player> newdetectives = new ArrayList<>();
				for(int i = 0; i < detectives.size(); i++){
					if(justmovedplayer.piece() != detectives.get(i).piece()){
						newdetectives.add(detectives.get(i));
					}
					else {
						newdetectives.add(justmovedplayer);
					}
				}
				if(newremaining.isEmpty()){
					newremaining.add(mrX.piece());
				}
				return new MyGameState(setup, ImmutableSet.copyOf(newremaining), log, mrX, ImmutableList.copyOf(newdetectives));
			}
		}
	}


	//Helper method
	//Creates a new ArrayList of the remaining players
	public static ArrayList<Piece> createnewremaining(Player justmovedplayer, ImmutableSet<Piece> remaining){
		ArrayList<Piece> newremaining = new ArrayList<>();
		for(Piece p : remaining){
			if(p!=justmovedplayer.piece()) {
				newremaining.add(p);
			}
		}
		return newremaining;
	}

	//Calculates SingleMoves
	//used in #advance
	private static ImmutableSet<Move.SingleMove> makeSingleMoves(
			GameSetup setup,
			List<Player> detectives,
			Player player,
			int source){
		final var singleMoves = new ArrayList<Move.SingleMove>();
		for(int destination : setup.graph.adjacentNodes(source)) {
			if(testDetectivelocation(detectives, destination)) {
				for (ScotlandYard.Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {
					if (player.has(t.requiredTicket())) {
						Move.SingleMove sm = new Move.SingleMove(player.piece(), source, t.requiredTicket(), destination);
						singleMoves.add(sm);
					}
				}
				if (player.isMrX()) {
					if (player.has(ScotlandYard.Ticket.SECRET)) {
						Move.SingleMove sm = new Move.SingleMove((player.piece()), source, ScotlandYard.Ticket.SECRET, destination);
						singleMoves.add(sm);
					}
				}
			}
		}
		return ImmutableSet.copyOf(singleMoves);
	}

	//Calculates DoubleMoves using helper methods
	//used in #advance
	private static ImmutableSet<Move.DoubleMove> makeDoubleMoves(
			GameSetup setup,
			List<Player> detectives,
			Player player,
			int source){
		final var doubleMoves = new ArrayList<Move.DoubleMove>();
		if(player.has(ScotlandYard.Ticket.DOUBLE)){
			for(int destination1 : setup.graph.adjacentNodes(source)){
				if(testDetectivelocation(detectives, destination1)) {
					for(ScotlandYard.Transport t1 : setup.graph.edgeValueOrDefault(source,destination1,ImmutableSet.of())){
						if(player.has(t1.requiredTicket())){
							for(int destination2 : setup.graph.adjacentNodes(destination1)){
								if(testDetectivelocation(detectives, destination2)){
									doubleMoves.addAll(caseFirstMoveNotSecret(setup, player, source, t1, destination1, destination2));
									if(player.has(ScotlandYard.Ticket.SECRET)){
										Move.DoubleMove dm = new Move.DoubleMove((player.piece()), source, t1.requiredTicket(), destination1, ScotlandYard.Ticket.SECRET, destination2);
										doubleMoves.add(dm);
									}
								}
							}
						}
					}
					if(player.has(ScotlandYard.Ticket.SECRET)){
						for(int destination2 : setup.graph.adjacentNodes(destination1)){
							if(testDetectivelocation(detectives, destination2)){
								doubleMoves.addAll(caseFirstMoveSecret(setup, player,source,destination1,destination2));
							}
						}
					}
				}
			}
		}
		return ImmutableSet.copyOf(doubleMoves);
	}

	//Helper method
	//Throws exceptions if MrX is null or empty
	public static boolean mrXtest(Player mrX){
		if(mrX == null) throw new NullPointerException("MrX is null");
		if(!mrX.isMrX()) throw new IllegalArgumentException("MrX is empty!");
		return true;
	}

	/**
	 * A method that provides various checks for the list of detectives
	 * If they have invalid tickets;
	 * the same color or location
	 * or are empty
	 * And throws exceptions
	 */
	public static boolean detectivetest(List<Player> detectives){
		for(Player x : detectives){
			if(x.equals(null)) throw new NullPointerException("One player is null");
			if(x.has(ScotlandYard.Ticket.SECRET)) throw new IllegalArgumentException("detective has secret ticket");
			if(x.has(ScotlandYard.Ticket.DOUBLE)) throw new IllegalArgumentException("detective has double ticket");
		}
		if(detectives.isEmpty()) throw  new IllegalArgumentException("Detectives is empty!");
		for(int i = 0 ; i < detectives.size() - 1; i ++){
			for(int j = i + 1; j < detectives.size(); j ++){
				if(detectives.get(i).piece().webColour() == detectives.get(j).piece().webColour()){
					throw new IllegalArgumentException("detectives have the same colour");
				}
				if(detectives.get(i).location() == detectives.get(j).location()){
					throw new IllegalArgumentException("can't have two detectives in the same location");
				}
			}
		}
		return true;
	}

	//Helper method
	//Throws exceptions if the rounds or the the graph are empty
	public static boolean setuptest(GameSetup setup){
		if(setup.rounds.isEmpty()) throw new IllegalArgumentException("Rounds is empty!");
		if(setup.graph.nodes().isEmpty()) throw new IllegalArgumentException("empty graph");
		return true;
	}

	//Helper method
	//Throws exceptions if the remaining are empty
	public static boolean remainingtest(ImmutableSet<Piece> remaining){
		if(remaining.isEmpty()) throw  new IllegalArgumentException("Remaining is empty!");
		return true;
	}

	//Helper method
	//Return a set containing the detectives as pieces
	public static ImmutableSet<Piece> turndetectivesintopieces(List<Player> detectives){
		ArrayList<Piece> newdetectives = new ArrayList<>();
		for(Player d : detectives){
			newdetectives.add(d.piece());
		}
		return ImmutableSet.copyOf(newdetectives);
	}

	//Helper method
	//Checks if a detectives location is a destination
	public static boolean testDetectivelocation(List<Player> detectives, int destination){
		for(Player x : detectives){
			if( x.location() == destination)
				return false;
		}
		return true;
	}

	/**
	 * Helper method
	 * Checks the case where the first move of mrX is not secret
	 * and returns the calculated doublemoves
	 */
	public static ArrayList<DoubleMove> caseFirstMoveNotSecret(GameSetup setup,
													Player player,
													int source,
													ScotlandYard.Transport t1,
													int destination1, int destination2){
		final var doubleMoves = new ArrayList<Move.DoubleMove>();
		for(ScotlandYard.Transport t2 : setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of())){
			//Checks for required ticket
			if( t1.requiredTicket() == t2.requiredTicket()){
				if(player.hasAtLeast(t1.requiredTicket(), 2)){
					Move.DoubleMove dm = new Move.DoubleMove(player.piece(),source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2);
					doubleMoves.add(dm);
				}
			}
			else{
				//Checks for required ticket
				if(player.has(t2.requiredTicket())){
					Move.DoubleMove dm = new Move.DoubleMove(player.piece(),source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2);
					doubleMoves.add(dm);
				}
			}
		}
		return doubleMoves;
	}

	/**
	 * Helper method
	 * Checks the case where the first move of mrX is secret
	 * returns the calculated double moves
	 */
	public static ArrayList<DoubleMove> caseFirstMoveSecret(GameSetup setup,
											  Player player,
											  int source,
											  int destination1, int destination2){
		final var doubleMoves = new ArrayList<Move.DoubleMove>();
		for(ScotlandYard.Transport t2 : setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of())){

			if(player.has(t2.requiredTicket())){
				Move.DoubleMove dm = new Move.DoubleMove(player.piece(),source, ScotlandYard.Ticket.SECRET, destination1, t2.requiredTicket(), destination2);
				doubleMoves.add(dm);
			}
		}
		if(player.hasAtLeast(ScotlandYard.Ticket.SECRET, 2)){
			Move.DoubleMove dm = new Move.DoubleMove(player.piece(),source, ScotlandYard.Ticket.SECRET, destination1, ScotlandYard.Ticket.SECRET, destination2);
			doubleMoves.add(dm);
		}
		return doubleMoves;
	}


	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {

		return new MyGameState(setup, ImmutableSet.of(Piece.MrX.MRX), ImmutableList.of(), mrX, detectives);
	}


}
