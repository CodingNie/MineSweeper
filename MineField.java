// Name: Ning Nie
// USC NetID: nnie
// CS 455 PA3
// Spring 2022


import java.util.Random;

/**
   MineField
      class with locations of mines for a game.
      This class is mutable, because we sometimes need to change it once it's created.
      mutators: populateMineField, resetEmpty
      includes convenience method to tell the number of mines adjacent to a location.
 */
public class MineField {
   
    // 2D array mineData is to simulate the matrix of the minefield
    // numMines is the number of mines (represented as "true" ) in this field, and should be non-negative
    // the numRows and numCols is the number of the row and column of the matrix, and should be positive number
    // the random object is for generating the random mines distribution
    private boolean[][] mineData;
    private int numMines;
    private int numRows;
    private int numCols;
    private Random random = new Random();
   
   
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in the array
      such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice versa.  numMines() for
      this minefield will corresponds to the number of 'true' values in mineData.
      @param mineData  the data for the mines; must have at least one row and one col,
                       and must be rectangular (i.e., every row is the same length)
    */
   public MineField(boolean[][] mineData) {
      assert mineData.length >= 1 && mineData[0].length >=1;
      this.numRows = mineData.length;
      this.numCols = mineData[0].length;
      this.mineData = new boolean[numRows][numCols];
      for (int i = 0; i < numRows; i++){
          for (int j = 0; j < numCols; j++){
              this.mineData[i][j] = mineData[i][j];
              if (this.hasMine(i, j)) numMines++;
          }
      }
   }
   
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a MineField, 
      numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
   public MineField(int numRows, int numCols, int numMines) {
      this.numRows = numRows;
      this.numCols = numCols;
      this.numMines = numMines;
      this.mineData = new boolean[numRows][numCols];
   }
   

   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on the minefield,
      ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col) and numMines() < (1/3 * numRows() * numCols())
    */
   public void populateMineField(int row, int col) {
      assert inRange(row, col);
      resetEmpty();
      int count = 0;
      while (count < numMines){
          int ranRow = random.nextInt(numRows);
          int ranCol = random.nextInt(numCols);
          if (this.mineData[ranRow][ranCol] == true || (ranRow == row && ranCol == col)){
              continue;
          }
          this.mineData[ranRow][ranCol] = true;
          count++;
      }
   }
   
   
   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or numCols()
      Thus, after this call, the actual number of mines in the minefield does not match numMines().  
      Note: This is the state a minefield created with the three-arg constructor is in 
         at the beginning of a game.
    */
   public void resetEmpty() {
       for (int i = 0; i < numRows; i++) {
           for (int j = 0; j < numCols; j++) {
               this.mineData[i][j] = false;
           }
       }
   }

   
  /**
     Returns the number of mines adjacent to the specified mine location (not counting a possible 
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
   public int numAdjacentMines(int row, int col) {
      assert inRange(row, col);
      int num = 0;
      for (int i = row - 1; i <= row + 1; i++){
          for (int j = col - 1; j <= col + 1; j++){
              if (i == row && j == col || inRange(i, j) == false) continue;
              if (this.hasMine(i, j)) num++;
          }
      }
      return num;
   }
   
   
   /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
   public boolean inRange(int row, int col) {
      if (row < 0 || col < 0 || row >=numRows || col >= numCols){
          return false;
      }
      return true;
   }
   
   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */  
   public int numRows() {
      return numRows;
   }
   
   
   /**
      Returns the number of columns in the field.
      @return number of columns in the field
   */    
   public int numCols() {
      return numCols;
   }
   
   
   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
   public boolean hasMine(int row, int col) {
      assert inRange(row, col);
      return this.mineData[row][col];
   }
   
   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg constructor,
      some of the time this value does not match the actual number of mines currently on the field.  See doc for that
      constructor, resetEmpty, and populateMineField for more details.
    * @return
    */
   public int numMines() {
       return numMines;
   }

   
   // <put private methods here>
   
         
}

