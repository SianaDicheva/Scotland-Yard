# Scotland-Yard

Java implementations of the Scotland Yard game. 

This repository contains two versions of the CW game: cw-model and cw-ai.

CW Model

The cw-model directory includes the original version of the CW game. It provides a simple implementation of the game logic without any AI or scoring function. Players can take turns making moves until the game reaches a winning or draw state.

CW AI

The cw-ai directory contains an enhanced version of the CW game. It incorporates an AI player that utilizes a scoring function and implements the MiniMax algorithm with Alpha-Beta pruning. This AI player provides a challenging opponent for human players.

Scoring Function
The scoring function in the AI player evaluates the desirability of a game state. It assigns scores to different states, with higher scores indicating more favorable positions. The AI player uses these scores to make informed decisions about its moves.

MiniMax Algorithm with Alpha-Beta Pruning
The AI player in cw-ai utilizes the MiniMax algorithm with Alpha-Beta pruning to search for the best move. The MiniMax algorithm is a recursive algorithm that explores all possible moves and evaluates the resulting game states. Alpha-Beta pruning enhances the efficiency of the algorithm by pruning branches that are guaranteed to be less optimal.
