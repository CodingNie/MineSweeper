// Name: Ning Nie
// USC NetID: nnie
// CS 455 PA3
// Spring 2022


/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
  user can see about the minefield). Client can call getStatus(row, col) for any square.
  It actually has data about the whole current state of the game, including  
  the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
  It also has mutators related to actions the player could do (resetGameDisplay(), cycleGuess(), uncover()),
  and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms
  the Model for the game application, whereas GameBoardPanel is the View and Controller, in the MVC design pattern.
  It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from 
  outside this class via the getMineField accessor.  
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method 
   // getStatus(row, col)).
   
   // The following are the covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // The following are the uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------   
  
   // the visibleField represent the current status of the mineField, showing the relative numbers
   // the dimension of the visibleField is the same as the mineField because of this kind of representing
   // the visibleField share the same mineField object without defensive copy
   private MineField mineField;
   private int[][] visibleField;
   private int numRows;
   private int numCols;
   private int numMines;
   // fill the eight square with the direction of left-right, top-down, topleft-bottomright, topright-bottomleft
   // these combinations of numbers represent the direction while implementing the flood-fill algorithm
   private static int[] ROW_FILL_PARA = {0, 0, -1, 1, -1, 1, -1, 1};
   private static int[] COL_FILL_PARA = {-1, 1, 0, 0, -1, 1, 1, -1};


   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the mines covered up, no mines guessed, and the game
      not over.
      @param mineField  the minefield to use for for this VisibleField
    */
   public VisibleField(MineField mineField) {
      this.mineField = mineField;
      this.numRows = mineField.numRows();
      this.numCols = mineField.numCols();
      this.numMines = mineField.numMines();
      visibleField = new int[numRows][numCols];
      resetGameDisplay();
   }

   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */     
   public void resetGameDisplay() {
      for (int i = 0; i < numRows; i++){
         for (int j = 0; j < numCols; j++){
            visibleField[i][j] = COVERED;
         }
      }
   }
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
   public MineField getMineField() {
      return this.mineField;
   }
   
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the beginning of the class
      for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      assert getMineField().inRange(row, col);
      return visibleField[row][col];
   }

   
   /**
      Returns the the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
      or not.  Just gives the user an indication of how many more mines the user might want to guess.  This value can
      be negative, if they have guessed more than the number of mines in the minefield.     
      @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      int numGuess = 0;
      for (int i = 0; i < numRows; i++){
         for (int j = 0; j < numCols; j++){
            if (getStatus(i, j) == MINE_GUESS) numGuess++;
         }
      }
      return this.numMines - numGuess;
   }
 
   
   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
      changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
      changes it to COVERED again; call on an uncovered square has no effect.  
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {
      assert getMineField().inRange(row, col);
      if (visibleField[row][col] == COVERED){
         visibleField[row][col] = MINE_GUESS;
      }else if (visibleField[row][col] == MINE_GUESS){
         visibleField[row][col] = QUESTION;
      }else if (visibleField[row][col] == QUESTION){
         visibleField[row][col] = COVERED;
      }
   }

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in 
      the neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form 
      (possibly along with parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {
      assert getMineField().inRange(row, col);
      if (mineField.hasMine(row, col)) { // uncover a mine
         visibleField[row][col] = EXPLODED_MINE;
         return false;
      }else if (this.mineField.numAdjacentMines(row, col) != 0){ // uncover a number, early return
         visibleField[row][col] = this.mineField.numAdjacentMines(row, col);
         return true;
      }else { // uncover a zero, execute flood-fill
         floodfill(row, col);
      }
      return true;
   }
 
   
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game over
    */
   public boolean isGameOver() {
      int numUncovered = 0;
      for (int i = 0; i < numRows; i++){
         for (int j = 0; j < numCols; j++){
            if (visibleField[i][j] == EXPLODED_MINE){ // lose the game
               loseStatus();
               return true;
            }else if (isUncovered(i, j)){
               numUncovered++;
            }
         }
      }
      if (numUncovered == (numCols * numRows - numMines)){ // win the game
         winStatus();
         return true;
      }
      return false;
   }

   /**
    when the client arrives the status of win, where uncovered all the numbers ranging [0, 8]
    we display all the remaining squares (which certainly to be the mines) as MINE_GUESS
    */
   private void winStatus(){
      for (int i = 0; i < numRows; i++){
         for (int j = 0; j < numCols; j++){
            if (!isUncovered(i, j)){
               visibleField[i][j] = MINE_GUESS;
            }
         }
      }
   }

   /**
    when the client arrives the status of lose, we deal the square with corresponding change
    the status need specially treating containing the incorrect guessing and no guessing
    */
   private void loseStatus(){
      for (int i = 0; i < numRows; i++){
         for (int j = 0; j < numCols; j++){
            if (visibleField[i][j] == MINE_GUESS && !mineField.hasMine(i, j)){
               // square with incorrect guessing
               visibleField[i][j] = INCORRECT_GUESS;
            }else if ((visibleField[i][j] == COVERED || visibleField[i][j] == QUESTION) && mineField.hasMine(i, j)){
               // square with no guessing or question
               visibleField[i][j] = MINE;
            }
               // leave other status unchanged, including the correct guessing, question mark, number [0, 8], etc.
         }
      }
   }
 
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      assert mineField.inRange(row, col);
      return visibleField[row][col] >=0 && visibleField[row][col] <=8;
      //return false;
   }
   
 
   // <put private methods here>
   /**
    use flood-fill method to open the square with non-adjacent mine recursively
    the detailed recursion condition is as follows
    */
   private void floodfill(int row, int col){
      // check if the index is in the boundary during recursion, return when touching the boundary
      if (!mineField.inRange(row, col)) return;
      // check if the square is marked with guessing, return when touching the marked square as requested
      if (getStatus(row, col) == MINE_GUESS) return;
      // avoid repeatedly opening the uncovered square
      if (isUncovered(row, col)) return;
      // uncover it and form the boundary when touching the mine-adjacent square
      if (mineField.numAdjacentMines(row, col) != 0){
         visibleField[row][col] = mineField.numAdjacentMines(row, col);
         return;
      }else if (mineField.numAdjacentMines(row, col) == 0){ // recursively opening the square with none adjacent mine
         visibleField[row][col] = mineField.numAdjacentMines(row, col);
         for (int i = 0; i < ROW_FILL_PARA.length; i++){
            // fill the eight directions as described in the instance variable
            int rowForFill = row + ROW_FILL_PARA[i];
            int colForFill = col + COL_FILL_PARA[i];
            floodfill(rowForFill, colForFill);
         }
      }
   }

}
