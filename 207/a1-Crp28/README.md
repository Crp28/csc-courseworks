# Assignment 1 Instructions

For this assignment, we are going to be creating a program that lets you play a game of Othello.

You will be graded on: 
- Code Correctness (based on autotests -- so make sure you DO NOT modify any method names and type signatures)
- Code Design (having clean, simple code that avoids repetition)
- Documentation (having proper, complete javadocs for all your code)
- Effective use of git (making frequent commits with good log messages)

If you are unfamiliar with this game, make sure to check out these links first:

https://www.youtube.com/watch?v=Ol3Id7xYsY4&ab_channel=Howcast

https://www.eothello.com/

## Section 1 - Completing OthelloBoard.java

### Task 1.1: Warmup

Complete the easy methods in OthelloBoard.java:
- otherPlayer, getToken, validCoordinate, getCount

Do the following to add and commit your code:
```
cd a1-yourgithubusername/a1-code
git add OthelloBoard.java
git commit -m "Easy OthelloBoard methods completed"
```

### Task 1.2: checkForPattern Method

Complete the method checkForPattern in OthelloBoard.java, based on the method
documentation. This method will be useful for checking which players can
make valid moves on the board. For example, let's say I want to put my 'O'
token in the location (row=2, col=0). In the following board, if I used
checkForPattern starting from location (row=2,col=1) in the direction
drow=0 (the row is not being changed) and dcol=1 (moving right on the board),
you will see that the pattern 'X', 'X', 'X', 'O' exists, so 'O' will get returned
by checkForPattern, which means this is a valid move as long as the (2, 0)
location is empty. The checkForPattern method just checks if such patterns exist
on the board starting from the given position, in the given direction, and returns
the player token who can make a move in that location.

```
  0 1 2 3 4 5 6 7
 +-+-+-+-+-+-+-+-+
0| | | | | | | | |0
 +-+-+-+-+-+-+-+-+
1| | | | | | | | |1
 +-+-+-+-+-+-+-+-+
2| |X|X|X|O| | | |2
 +-+-+-+-+-+-+-+-+
3| | | |O|O| | | |3
 +-+-+-+-+-+-+-+-+
4| | | |X|O| | | |4
 +-+-+-+-+-+-+-+-+
5| | | | | | | | |5
 +-+-+-+-+-+-+-+-+
6| | | | | | | | |6
 +-+-+-+-+-+-+-+-+
7| | | | | | | | |7
 +-+-+-+-+-+-+-+-+
  0 1 2 3 4 5 6 7
```

Git add your file and then commit your method!
`git commit -m "OthelloBoard.checkForPattern method completed"`

### Task 1.3: flip Method

Complete the method flip in OthelloBoard.java, based on the method
documentation. This method will be useful for flipping all the tokens in a line
when a player makes a move that results in this happening. Given a starting
position and the offending player token (e.g. 'O'), the flip method will flip all
tokens which belong to the other player (e.g. 'X') to become the given offending
player (e.g. 'O'). The flipping will begin from the given starting position.

For example, in the board above, if I was going to put a 'O' token in the
location (row=2, col=0), then we would have to flip all the 'X' tokens
starting from (row=2, col=1) -- so that would involve the
directions drow=0 (we are staying in the same row) and dcol=1 (moving right).
After the flip is done, all the tokens in row 2 from col=1 to col=3 would
become 'O'.

This flip call would have been flip(2, 1, 0, 1, 'O') -- (2, 1) is the
starting position and (drow=0, dcol=1) as the directions -- and return 3 because
3 of the other player tokens would be flipped in total with this call.

After the flip:
```
  0 1 2 3 4 5 6 7
 +-+-+-+-+-+-+-+-+
0| | | | | | | | |0
 +-+-+-+-+-+-+-+-+
1| | | | | | | | |1
 +-+-+-+-+-+-+-+-+
2| |O|O|O|O| | | |2
 +-+-+-+-+-+-+-+-+
3| | | |O|O| | | |3
 +-+-+-+-+-+-+-+-+
4| | | |X|O| | | |4
 +-+-+-+-+-+-+-+-+
5| | | | | | | | |5
 +-+-+-+-+-+-+-+-+
6| | | | | | | | |6
 +-+-+-+-+-+-+-+-+
7| | | | | | | | |7
 +-+-+-+-+-+-+-+-+
  0 1 2 3 4 5 6 7
```

Git add your file and then commit your method!
`git commit -m "OthelloBoard.flip method completed"`

### Task 1.4: move Method

Complete the method move in OthelloBoard.java, based on the method
documentation. This method takes in the (row, col) position a player is going
to 'move to' (place a token on), and the player that is doing this move. If
the move is not valid (i.e. the coordinates for the position provided are off
the board or the given position is not empty), do nothing to the board and
return false. If the move is valid, then: (1) all affected tokens will need to be
flipped -- you need to check in every direction (up, down, left, right, diagonals)
surrounding the position, for any tokens that need to be flipped to become the
current player; (2) this given position will be marked the current player token.

For example, if my board looked like below:
```
  0 1 2 3 4 5 6 7
 +-+-+-+-+-+-+-+-+
0| | | | | | | | |0
 +-+-+-+-+-+-+-+-+
1| | | | | |X| | |1
 +-+-+-+-+-+-+-+-+
2| | | | | |X| | |2
 +-+-+-+-+-+-+-+-+
3| | | |X|O|X| | |3
 +-+-+-+-+-+-+-+-+
4| | | |X|O|X|X| |4
 +-+-+-+-+-+-+-+-+
5| | | |X|O| | | |5
 +-+-+-+-+-+-+-+-+
6| | | | | | | | |6
 +-+-+-+-+-+-+-+-+
7| | | | | | | | |7
 +-+-+-+-+-+-+-+-+
  0 1 2 3 4 5 6 7
```

Then if I wanted to make a move at (row=5,col=2) with player 'O', then in the
right direction (drow=0, dcol=1) we would have to flip the 'X' at (5, 3) to
become 'O', and in the upper-right diagonal direction (drow=-1, dcol=1) we would
have to flip the 'X' at (4, 3) to become 'O' as well. Finally, the given position
(5, 2) would also become 'O', resulting in the board looking like:

```
  0 1 2 3 4 5 6 7
 +-+-+-+-+-+-+-+-+
0| | | | | | | | |0
 +-+-+-+-+-+-+-+-+
1| | | | | |X| | |1
 +-+-+-+-+-+-+-+-+
2| | | | | |X| | |2
 +-+-+-+-+-+-+-+-+
3| | | |X|O|X| | |3
 +-+-+-+-+-+-+-+-+
4| | | |O|O|X|X| |4
 +-+-+-+-+-+-+-+-+
5| | |O|O|O| | | |5
 +-+-+-+-+-+-+-+-+
6| | | | | | | | |6
 +-+-+-+-+-+-+-+-+
7| | | | | | | | |7
 +-+-+-+-+-+-+-+-+
  0 1 2 3 4 5 6 7
```

Git add your file and then commit your method!
`git commit -m "OthelloBoard.move method completed"`

### Task 1.5: Run OthelloBoard.java and check the output

After the previous methods have all been completed, you should be able to
run OthelloBoard.java to run all the test code written for you in the OthelloBoard.main
method. The output should be identical to the text in the othelloBoardOutput.txt
file included in this repository.

Fix any issues you find, clean up your code (about 20\% of your grade will be on your
code design - avoiding repeated code, using helper functions appropriately, etc.)
and then commit and push OthelloBoard.java.

```
git add OthelloBoard.java
git commit -m "OthelloBoard.java completed"
git push
```

## Section 2 - Completing Othello.java

### Task 2.1
Complete the Othello.move method based on the method documentation and the
hints provided within the comments in the file. Try to keep your code short
and clean, using helper methods from the OthelloBoard class as appropriate.

git commit -m "Othello.move completed"

### Task 2.2
Complete the Othello.getWinner method on the method documentation. Again, keep
your code short and clean. Use the P1, P2 and EMPTY variables from the OthelloBoard
class to access the needed character tokens.

```
git add Othello.java
git commit -m "Othello.getWinner completed"
```

### Task 2.3: Run Othello.java and check the output

After the previous tasks have all been completed, you should be able to
run Othello.java to run all the test code written for you in the Othello.main
method. The output should be similar to structure (but not identical, since the
game is random each time) to the text in the files gameOthelloSampleA.txt and
gameOthelloSampleB.txt included in this repository.

We also provided OthelloTest.java with a few test cases to help you test your code.
This is not a comprehensive set of tests, but gives you a starting point if you
want to add more tests of your own.

Fix any issues you find, clean up your code (20\% of your grade will be on your
code design - avoiding repeated code, using helper functions appropriately, etc.)
and then commit and push Othello.java.

```
git add Othello.java
git commit -m "Othello.java completed"
git push
```

## Section 3 - Playing 2-Player Othello Game + Documenting Code

### Task 3.1: Try out the game!

Open up the file OthelloControllerHumanVSHuman.java. If you have completed all
previous tasks correctly, you should be able to run this file and play a game
of Othello now!

To informally test things out: The file shortOthelloGame.moves included in this repository
provides some sample input (each pair of numbers represents an inputted row
and col to the Othello game that begins when you run the OthelloControllerHumanVSHuman
file), and shortOthelloGame.txt shows the output you should see if you enter
all these numbers as input in the given order. You can try inputting the provided
numbers and comparing the output you get to the one provided in this txt file.

### Task 3.2: Add Javadocs

Add documentation to all the methods in the OthelloControllerHumanVSHuman class
and PlayerHuman class. Follow the conventions of writing proper Javadocs
(https://axiom.utm.utoronto.ca/~207/20f/slides/1/javadoc.html#/)
