package com.pages;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Pages are instantiable and refreshable, Page super class provides a common access
 */
public abstract class Page implements Serializable {

    private int width;
    private int height;
    protected JFrame attachedFrame;
    protected JPanel mainPanel;

    public Page(JFrame attachedFrame) {
        this(attachedFrame.getWidth(), attachedFrame.getHeight(), attachedFrame);
    }

    public Page(int width, int height, JFrame attachedFrame) {
        this.width = width;
        this.height = height;
        this.attachedFrame = attachedFrame;
        this.mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
    }

    abstract JPanel getPage();

    abstract JPanel createPage();


}
