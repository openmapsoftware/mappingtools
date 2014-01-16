package com.openMap1.mapper.query;
import java.awt.Color;

/**
 *  Contains the contents of a cell in tabular output of query answers.
 *  Defines the text to be output in the cell, and the foreground colour for the text.
 *  But the Eclipse implementation does not yet show colours.
*/
  public class CellContent
  {
        private String text;  // used for sorting rows
        private Color colour; // = Black, Red or Green

        // cell with single content
        public CellContent(String t, Color c)
        {
            text = t;
            colour = c;
        }

        // cell with single content, black
        public CellContent(String t)
        {
            text = t;
            colour = Color.black;
        }
        
        public void setText(String t) {text = t;}
        public String getText() {return text;}
        
        public Color getColour() {return colour;}
  }


