package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.*;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model> {
	private final class MyModel implements Model{
		private List<Observer> observers;
		private GameSetup setup;
		private Player mrX;
		private ImmutableList<Player> detectives;
		private Board.GameState board;
		private MyModel(final GameSetup setup,
						final Player mrX,
						final ImmutableList<Player> detectives) {
			this.detectives=detectives;
			this.mrX=mrX;
			this.setup=setup;
			observers = new ArrayList<Observer>();
			MyGameStateFactory iwishtodieaslowandpainfulldeath = new MyGameStateFactory();
			board = iwishtodieaslowandpainfulldeath.build(setup, mrX, detectives);

		}
		//Returns the current board
		@Nonnull
		@Override
		public Board getCurrentBoard() {
			return this.board;
		}

		//registers a new observer
		@Override
		public void registerObserver(@Nonnull Observer observer) {
			if(observer == null) throw new NullPointerException();
			if(!observers.contains(observer)) {
				observers.add(observer);
			}
			else{
				throw new IllegalArgumentException();
			}
		}

		//unregister an observer
		@Override
		public void unregisterObserver(@Nonnull Observer observer) {

			if(observers.contains(observer)) {
				observers.remove(observer);
			}
			else{
				if( observer == null){
					throw new NullPointerException("observer is null");}
				else {
					throw new IllegalArgumentException();
				}
			}
		}

		//Returns a set containing the observers
		@Nonnull
		@Override
		public ImmutableSet<Observer> getObservers() {
			return ImmutableSet.copyOf(observers);
		}

		// Check if the game is over,
		// and inform the observers about the new state of the game
		@Override
		public void chooseMove(@Nonnull Move move) {
			board = board.advance(move);
			if(!board.getWinner().isEmpty()){
				for(Observer o : observers){
					o.onModelChanged(board, Observer.Event.GAME_OVER);
				}
			}
			else{
				for(Observer o : observers){
					o.onModelChanged(board, Observer.Event.MOVE_MADE);
				}
			}
		}
	}
	@Nonnull @Override public Model build(GameSetup setup,
										  Player mrX,
										  ImmutableList<Player> detectives) {
		// TODO
		return new MyModel(setup,mrX,detectives);
	}
}
