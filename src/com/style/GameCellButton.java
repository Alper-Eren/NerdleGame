package com.style;

import com.gamedata.Game;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class GameCellButton extends MenuButton {

    public static HashMap<Game.CellStates,Color> cellStateColors = new HashMap<>();

    static {
        cellStateColors.put(Game.CellStates.INACTIVE,Color.DARK_GRAY);
        cellStateColors.put(Game.CellStates.ACTIVE,Color.GRAY);
        cellStateColors.put(Game.CellStates.CORRECT,Color.GREEN);
        cellStateColors.put(Game.CellStates.INCORRECT,Color.RED);
        cellStateColors.put(Game.CellStates.SELECTED,Color.LIGHT_GRAY);
        cellStateColors.put(Game.CellStates.PARTIAL, Color.YELLOW);
    }

    public GameCellButton(String buttonText, int size) {
        super(buttonText);
        this.setPreferredSize(new Dimension(size, size));
        this.setOpaque(true);
        this.setContentAreaFilled(false);
        setBorder(BorderFactory.createLineBorder(Color.PINK));
        setBorderPainted(true);
    }



}
