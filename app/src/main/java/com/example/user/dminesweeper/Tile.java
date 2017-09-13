package com.example.user.dminesweeper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


public class Tile extends Button {
    private boolean isCovered; // is Tile covered yet
    private boolean isMined; // does the Tile has a mine underneath
    private boolean isFlagged; // is Tile flagged as a potential mine
    private boolean isQuestionMarked; // is Tile question marked
    private boolean isClickable; // can Tile accept click events
    private int numberOfMinesInSurrounding; // number of mines in nearby Tiles

    public Tile(Context context)
    {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public Tile(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    // set default properties for the Tile
    public void setDefaults()
    {
        isCovered = true;
        isMined = false;
        isFlagged = false;
        isQuestionMarked = false;
        isClickable = true;
        numberOfMinesInSurrounding = 0;

        this.setBackgroundResource(R.drawable.square_blue);
        setBoldFont();
    }

    // mark the Tile as disabled/opened
    // update the number of nearby mines
    public void setNumberOfSurroundingMines(int number)
    {
        this.setBackgroundResource(R.drawable.square_grey);

        updateNumber(number);
    }

    // set mine icon for Tile
    // set Tile as disabled/opened if false is passed
    public void setMineIcon(boolean enabled)
    {
        this.setText("M");

        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.square_grey);
            this.setTextColor(Color.RED);
        }
        else
        {
            this.setTextColor(Color.BLACK);
        }
    }

    // set mine as flagged
    // set Tile as disabled/opened if false is passed
    public void setFlagIcon(boolean enabled)
    {
        this.setText("F");

        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.square_grey);
            this.setTextColor(Color.RED);
        }
        else
        {
            this.setTextColor(Color.BLACK);
        }
    }

    // set mine as question mark
    // set Tile as disabled/opened if false is passed
    public void setQuestionMarkIcon(boolean enabled)
    {
        this.setText("?");

        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.square_grey);
            this.setTextColor(Color.RED);
        }
        else
        {
            this.setTextColor(Color.BLACK);
        }
    }

    // set Tile as disabled/opened if false is passed
    // else enable/close it
    public void setTileAsDisabled(boolean enabled)
    {
        if (!enabled)
        {
            this.setBackgroundResource(R.drawable.square_grey);
        }
        else
        {
            this.setBackgroundResource(R.drawable.square_blue);
        }
    }

    // clear all icons/text
    public void clearAllIcons()
    {
        this.setText("");
    }

    // set font as bold
    private void setBoldFont()
    {
        this.setTypeface(null, Typeface.BOLD);
    }

    // uncover this Tile
    public void OpenTile()
    {
        // cannot uncover a mine which is not covered
        if (!isCovered)
            return;

        setTileAsDisabled(false);
        isCovered = false;

        // check if it has mine
        if (hasMine())
        {
            setMineIcon(false);
        }
        // update with the nearby mine count
        else
        {
            setNumberOfSurroundingMines(numberOfMinesInSurrounding);
        }
    }

    // set text as nearby mine count
    public void updateNumber(int text)
    {
        if (text != 0)
        {
            this.setText(Integer.toString(text));

            // select different color for each number
            // we have already skipped 0 mine count
            switch (text)
            {
                case 1:
                    this.setTextColor(Color.BLUE);
                    break;
                case 2:
                    this.setTextColor(Color.rgb(0, 100, 0));
                    break;
                case 3:
                    this.setTextColor(Color.RED);
                    break;
                case 4:
                    this.setTextColor(Color.rgb(85, 26, 139));
                    break;
                case 5:
                    this.setTextColor(Color.rgb(139, 28, 98));
                    break;
                case 6:
                    this.setTextColor(Color.rgb(238, 173, 14));
                    break;
                case 7:
                    this.setTextColor(Color.rgb(47, 79, 79));
                    break;
                case 8:
                    this.setTextColor(Color.rgb(71, 71, 71));
                    break;
                case 9:
                    this.setTextColor(Color.rgb(205, 205, 0));
                    break;
            }
        }
    }

    // set Tile as a mine underneath
    public void plantMine()
    {
        isMined = true;
    }

    // mine was opened
    // change the Tile icon and color
    public void triggerMine()
    {
        setMineIcon(true);
        this.setTextColor(Color.RED);
    }

    // is Tile still covered
    public boolean isCovered()
    {
        return isCovered;
    }

    // does the Tile have any mine underneath
    public boolean hasMine()
    {
        return isMined;
    }

    // set number of nearby mines
    public void setNumberOfMinesInSurrounding(int number)
    {
        numberOfMinesInSurrounding = number;
    }

    // get number of nearby mines
    public int getNumberOfMinesInSorrounding()
    {
        return numberOfMinesInSurrounding;
    }

    // is Tile marked as flagged
    public boolean isFlagged()
    {
        return isFlagged;
    }

    // mark Tile as flagged
    public void setFlagged(boolean flagged)
    {
        isFlagged = flagged;
    }

    // is Tile marked as a question mark
    public boolean isQuestionMarked()
    {
        return isQuestionMarked;
    }

    // set question mark for the Tile
    public void setQuestionMarked(boolean questionMarked)
    {
        isQuestionMarked = questionMarked;
    }

    // can Tile receive click event
    public boolean isClickable()
    {
        return isClickable;
    }

    // disable Tile for receive click events
    public void setClickable(boolean clickable)
    {
        isClickable = clickable;
    }
}
