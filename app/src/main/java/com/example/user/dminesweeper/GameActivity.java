package com.example.user.dminesweeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    private TextView txtMineCount;
    private TextView txtTimer;
    private ImageButton btnSmile;

    private TableLayout mineField; // table layout to add mines to

    private Tile Tiles[][]; // Tiles for mine field
    private int TileDimension = 100; // width of each Tile
    private int TilePadding = 1; // padding between Tiles

    private int numberOfRowsInMineField ;
    private int numberOfColumnsInMineField;
    private int totalNumberOfMines;

    public static SortedSet <Integer> highScores = new TreeSet<Integer>();

    // timer to keep track of time elapsed
    private Handler timer = new Handler();
    private int secondsPassed = 0;

    private boolean isTimerStarted; // check if timer already started or not
    private boolean areMinesSet; // check if mines are planted in Tiles
    private boolean isGameOver;
    private int minesToFind; // number of mines yet to be discovered

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        totalNumberOfMines = intent.getIntExtra("NUM_OF_MINES", 3);
        numberOfRowsInMineField = intent.getIntExtra("NUM_OF_ROWS", 5);
        numberOfColumnsInMineField = numberOfRowsInMineField;

        txtMineCount = (TextView) findViewById(R.id.MineCount);
        txtTimer = (TextView) findViewById(R.id.Timer);

        btnSmile = (ImageButton) findViewById(R.id.Smiley);
        btnSmile.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                endExistingGame();
                startNewGame();
            }
        });


        mineField = (TableLayout)findViewById(R.id.MineField);

        showDialog("Click smiley to start New Game!" + System.lineSeparator() +
                "Short click - Revile Tile" + System.lineSeparator() +
                "Long click -  Flag Tile ", 2000, true, false);
    }

    private void startNewGame()
    {
        // plant mines and do rest of the calculations
        createMineField();
        // display all blocks in UI
        showMineField();

        minesToFind = totalNumberOfMines;
        isGameOver = false;
        secondsPassed = 0;
    }

    private void showMineField()
    {
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams((TileDimension + 2 * TilePadding) * numberOfColumnsInMineField, TileDimension + 2 * TilePadding));

            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                Tiles[row][column].setLayoutParams(new LayoutParams(
                        TileDimension + 2 * TilePadding,
                        TileDimension + 2 * TilePadding));
                Tiles[row][column].setPadding(TilePadding, TilePadding, TilePadding, TilePadding);
                tableRow.addView(Tiles[row][column]);
            }
            mineField.addView(tableRow,new TableLayout.LayoutParams(
                    (TileDimension + 2 * TilePadding) * numberOfColumnsInMineField, TileDimension + 2 * TilePadding));
        }
    }

    private void endExistingGame()
    {
        stopTimer(); // stop if timer is running
        txtTimer.setText("000"); // revert all text
        txtMineCount.setText("000"); // revert mines count
        btnSmile.setBackgroundResource(R.drawable.smile);

        // remove all rows from mineField TableLayout
        mineField.removeAllViews();

        // set all variables to support end of game
        isTimerStarted = false;
        areMinesSet = false;
        isGameOver = false;
        minesToFind = 0;
    }

    private void createMineField()
    {
        Tiles = new Tile[numberOfRowsInMineField + 2][numberOfColumnsInMineField + 2];

        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                Tiles[row][column] = new Tile(this);
                Tiles[row][column].setDefaults();

                final int currentRow = row;
                final int currentColumn = column;

                // add Click Listener
                Tiles[row][column].setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // start timer on first click
                        if (!isTimerStarted)
                        {
                            startTimer();
                            isTimerStarted = true;
                        }

                        // set mines on first click
                        if (!areMinesSet)
                        {
                            areMinesSet = true;
                            setMines(currentRow, currentColumn);
                        }

                        if (!Tiles[currentRow][currentColumn].isFlagged())
                        {
                            // open nearby Tiles till we get numbered Tiles
                            rippleUncover(currentRow, currentColumn);

                            // did we clicked a mine
                            if (Tiles[currentRow][currentColumn].hasMine())
                            {
                                // Oops, game over
                                finishGame(currentRow,currentColumn);
                            }

                            // check if we win the game
                            if (checkGameWin())
                            {
                                // mark game as win
                                winGame();
                            }
                        }
                    }
                });

                // this is treated as right mouse click listener
                Tiles[row][column].setOnLongClickListener(new OnLongClickListener()
                {
                    public boolean onLongClick(View view)
                    {

                        // open all surrounding Tiles
                        if (!Tiles[currentRow][currentColumn].isCovered() && (Tiles[currentRow][currentColumn].getNumberOfMinesInSorrounding() > 0) && !isGameOver)
                        {
                            int nearbyFlaggedTiles = 0;
                            for (int previousRow = -1; previousRow < 2; previousRow++)
                            {
                                for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                {
                                    if (Tiles[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                    {
                                        nearbyFlaggedTiles++;
                                    }
                                }
                            }

                            // then open nearby Tiles
                            if (nearbyFlaggedTiles == Tiles[currentRow][currentColumn].getNumberOfMinesInSorrounding())
                            {
                                for (int previousRow = -1; previousRow < 2; previousRow++)
                                {
                                    for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                    {
                                        // don't open flagged Tiles
                                        if (!Tiles[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                        {
                                            // open Tiles till we get numbered Tile
                                            rippleUncover(currentRow + previousRow, currentColumn + previousColumn);

                                            // did we clicked a mine
                                            if (Tiles[currentRow + previousRow][currentColumn + previousColumn].hasMine())
                                            {
                                                // oops game over
                                                finishGame(currentRow + previousRow, currentColumn + previousColumn);
                                            }

                                            // did we win the game
                                            if (checkGameWin())
                                            {
                                                // mark game as win
                                                winGame();
                                            }
                                        }
                                    }
                                }
                            }

                            // as we no longer want to judge this gesture so return

                            return true;
                        }

                        // if clicked Tile is enabled, clickable or flagged
                        if (Tiles[currentRow][currentColumn].isClickable() &&
                                (Tiles[currentRow][currentColumn].isEnabled() || Tiles[currentRow][currentColumn].isFlagged()))
                        {

                            // case 1. set blank Tile to flagged
                            if (!Tiles[currentRow][currentColumn].isFlagged() && !Tiles[currentRow][currentColumn].isQuestionMarked())
                            {
                                Tiles[currentRow][currentColumn].setTileAsDisabled(false);
                                Tiles[currentRow][currentColumn].setFlagIcon(true);
                                Tiles[currentRow][currentColumn].setFlagged(true);
                                minesToFind--; //reduce mine count
                                updateMineCountDisplay();
                            }
                            // case 2. set flagged to question mark
                            else if (!Tiles[currentRow][currentColumn].isQuestionMarked())
                            {
                                Tiles[currentRow][currentColumn].setTileAsDisabled(true);
                                Tiles[currentRow][currentColumn].setQuestionMarkIcon(true);
                                Tiles[currentRow][currentColumn].setFlagged(false);
                                Tiles[currentRow][currentColumn].setQuestionMarked(true);
                                minesToFind++; // increase mine count
                                updateMineCountDisplay();
                            }
                            // case 3. change to blank square
                            else
                            {
                                Tiles[currentRow][currentColumn].setTileAsDisabled(true);
                                Tiles[currentRow][currentColumn].clearAllIcons();
                                Tiles[currentRow][currentColumn].setQuestionMarked(false);
                                // if it is flagged then increment mine count
                                if (Tiles[currentRow][currentColumn].isFlagged())
                                {
                                    minesToFind++; // increase mine count
                                    updateMineCountDisplay();
                                }
                                // remove flagged status
                                Tiles[currentRow][currentColumn].setFlagged(false);
                            }

                            updateMineCountDisplay(); // update mine display
                        }

                        return true;
                    }
                });
            }
        }
    }

    private boolean checkGameWin()
    {
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                if (!Tiles[row][column].hasMine() && Tiles[row][column].isCovered())
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateMineCountDisplay()
    {
        if (minesToFind < 0)
        {
            txtMineCount.setText(Integer.toString(minesToFind));
        }
        else if (minesToFind < 10)
        {
            txtMineCount.setText("00" + Integer.toString(minesToFind));
        }
        else if (minesToFind < 100)
        {
            txtMineCount.setText("0" + Integer.toString(minesToFind));
        }
        else
        {
            txtMineCount.setText(Integer.toString(minesToFind));
        }
    }

    private void winGame()
    {
        stopTimer();
        isTimerStarted = false;
        isGameOver = true;
        minesToFind = 0; //set mine count to 0

        //set icon to cool dude
        btnSmile.setBackgroundResource(R.drawable.cool);

        updateMineCountDisplay(); // update mine count

        // disable all buttons
        // set flagged all un-flagged Tiles
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                Tiles[row][column].setClickable(false);
                if (Tiles[row][column].hasMine())
                {
                    Tiles[row][column].setTileAsDisabled(false);
                    Tiles[row][column].setFlagIcon(true);
                }
            }
        }

        // show message
        showDialog("You won in " + Integer.toString(secondsPassed) + " seconds!", 1000, false, true);
        //save time as a score
        saveTopTimeScore();

    }

    private void saveTopTimeScore(){
        highScores.add((Integer) secondsPassed);
    }

    private void finishGame(int currentRow, int currentColumn)
    {
        isGameOver = true; // mark game as over
        stopTimer(); // stop timer
        isTimerStarted = false;
        btnSmile.setBackgroundResource(R.drawable.sad);

        // show all mines
        // disable all Tiles
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                // disable Tile
                Tiles[row][column].setTileAsDisabled(false);

                // Tile has mine and is not flagged
                if (Tiles[row][column].hasMine() && !Tiles[row][column].isFlagged())
                {
                    // set mine icon
                    Tiles[row][column].setMineIcon(false);
                }

                // Tile is flagged and doesn't not have mine
                if (!Tiles[row][column].hasMine() && Tiles[row][column].isFlagged())
                {
                    // set flag icon
                    Tiles[row][column].setFlagIcon(false);
                }

                // Tile is flagged
                if (Tiles[row][column].isFlagged())
                {
                    // disable the Tile
                    Tiles[row][column].setClickable(false);
                }
            }
        }

        // trigger mine
        Tiles[currentRow][currentColumn].triggerMine();

        // show message
        showDialog("You tried for " + Integer.toString(secondsPassed) + " seconds!", 1000, false, false);
    }


    private void setMines(int currentRow, int currentColumn)
    {
        // set mines excluding the location where user clicked
        Random rand = new Random();
        int mineRow, mineColumn;

        for (int row = 0; row < totalNumberOfMines; row++)
        {
            mineRow = rand.nextInt(numberOfColumnsInMineField);
            mineColumn = rand.nextInt(numberOfRowsInMineField);
            if ((mineRow + 1 != currentColumn) || (mineColumn + 1 != currentRow))
            {
                if (Tiles[mineColumn + 1][mineRow + 1].hasMine())
                {
                    row--; // mine is already there, don't repeat for same Tile
                }
                // plant mine at this location
                Tiles[mineColumn + 1][mineRow + 1].plantMine();
            }
            // exclude the user clicked location
            else
            {
                row--;
            }
        }

        int nearByMineCount;

        // count number of mines in surrounding Tiles
        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                // for each Tile find nearby mine count
                nearByMineCount = 0;
                if ((row != 0) && (row != (numberOfRowsInMineField + 1)) && (column != 0) && (column != (numberOfColumnsInMineField + 1)))
                {
                    // check in all nearby Tiles
                    for (int previousRow = -1; previousRow < 2; previousRow++)
                    {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                        {
                            if (Tiles[row + previousRow][column + previousColumn].hasMine())
                            {
                                // a mine was found so increment the counter
                                nearByMineCount++;
                            }
                        }
                    }

                    Tiles[row][column].setNumberOfMinesInSurrounding(nearByMineCount);
                }
                // for side rows (0th and last row/column)
                // set count as 9 and mark it as opened
                else
                {
                    Tiles[row][column].setNumberOfMinesInSurrounding(9);
                    Tiles[row][column].OpenTile();
                }
            }
        }
    }

    private void rippleUncover(int rowClicked, int columnClicked)
    {
        // don't open flagged or mined rows
        if (Tiles[rowClicked][columnClicked].hasMine() || Tiles[rowClicked][columnClicked].isFlagged())
        {
            return;
        }

        // open clicked Tile
        Tiles[rowClicked][columnClicked].OpenTile();

        // if clicked Tile have nearby mines then don't open further
        if (Tiles[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0 )
        {
            return;
        }

        // open next 3 rows and 3 columns recursively
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 3; column++)
            {
                // check all the above checked conditions
                // if met then open subsequent Tiles
                if (Tiles[rowClicked + row - 1][columnClicked + column - 1].isCovered()
                        && (rowClicked + row - 1 > 0) && (columnClicked + column - 1 > 0)
                        && (rowClicked + row - 1 < numberOfRowsInMineField + 1) && (columnClicked + column - 1 < numberOfColumnsInMineField + 1))
                {
                    rippleUncover(rowClicked + row - 1, columnClicked + column - 1 );
                }
            }
        }
        return;
    }

    public void startTimer()
    {
        if (secondsPassed == 0)
        {
            timer.removeCallbacks(updateTimeElasped);
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer()
    {
        // disable call backs
        timer.removeCallbacks(updateTimeElasped);
    }

    // timer call back when timer is ticked
    private Runnable updateTimeElasped = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;

            if (secondsPassed < 10)
            {
                txtTimer.setText("00" + Integer.toString(secondsPassed));
            }
            else if (secondsPassed < 100)
            {
                txtTimer.setText("0" + Integer.toString(secondsPassed));
            }
            else
            {
                txtTimer.setText(Integer.toString(secondsPassed));
            }

            // add notification
            timer.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            timer.postDelayed(updateTimeElasped, 1000);
        }
    };

    private void showDialog(String message, int milliseconds, boolean useSmileImage, boolean useCoolImage)
    {
        // show message
        Toast dialog = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG);

        dialog.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout dialogView = (LinearLayout) dialog.getView();
        ImageView coolImage = new ImageView(getApplicationContext());
        if (useSmileImage)
        {
            coolImage.setImageResource(R.drawable.smile);
        }
        else if (useCoolImage)
        {
            coolImage.setImageResource(R.drawable.cool);
        }
        else
        {
            coolImage.setImageResource(R.drawable.sad);
        }
        dialogView.addView(coolImage, 0);
        dialog.setDuration(milliseconds);
        dialog.show();
    }
}
