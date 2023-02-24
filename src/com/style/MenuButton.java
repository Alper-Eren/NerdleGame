package com.style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Subclass of JButton overriding MouseListener events for dynamic styling and extra initial settings related to palette
 */
public class MenuButton extends JButton {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_PRESSED = 1;
    public static final int STATE_DISABLED = -1;
    private int state = 0;
    private Color idleBackGroundColor;

    /**
     * Constructs a MenuButton overriding MouseListener events and containing additional implementation for styling
     *
     * @param buttonText Text to be displayed, can be null for no text
     * @param icon ImageIcon to be displayed, can be null for no Icon
     */
    public MenuButton(String buttonText, ImageIcon icon) {
        super(buttonText, icon);
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.idleBackGroundColor = this.getBackground();
    }

    /**
     * Calls {@link #MenuButton} with null argument for icon parameter
     */
    public MenuButton(String buttonText) {
        this(buttonText, null);
    }

    /**
     * Calls {@link #MenuButton} with null argument for buttonText parameter
     */
    public MenuButton(ImageIcon icon) {
        this(null, icon);
    }

    // Implement Styling
    {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                setBackground(idleBackGroundColor.darker().darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                switch (state) {
                    case STATE_NORMAL -> setBackground(idleBackGroundColor);
                    case STATE_PRESSED -> setBackground(idleBackGroundColor.darker());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setBackground(idleBackGroundColor.darker());
                setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                switch (state) {
                    case STATE_NORMAL -> setBackground(idleBackGroundColor);
                    case STATE_PRESSED -> setBackground(idleBackGroundColor.darker());
                }

            }
        });
    }


    public int getState() {
        return state;
    }

    public int switchState() {
        if (state==STATE_NORMAL||state==STATE_PRESSED) {
            setState(1-state);
        }
        return state;
    }

    public void setState(int state) {
        this.state = state;
        if (state== STATE_NORMAL) {
            setBackground(idleBackGroundColor);
        }

        if (state==STATE_PRESSED) {
            setBackground(idleBackGroundColor.darker());
            setOpaque(true);
        }
    }

    public Color getIdleBackGroundColor() {
        return idleBackGroundColor;
    }

    public void setIdleBackGroundColor(Color idleBackGroundColor) {
        this.idleBackGroundColor = idleBackGroundColor;
        setBackground(idleBackGroundColor);
    }
}
