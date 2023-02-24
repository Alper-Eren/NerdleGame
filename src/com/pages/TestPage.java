package com.pages;

import com.Main;
import com.gamedata.Game;
import com.style.Footer;
import com.style.MenuButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Unit test page for verifying
 */
public class TestPage extends Page {

    public TestPage(JFrame attachedFrame) {
        this(attachedFrame.getWidth(), attachedFrame.getHeight(), attachedFrame);
    }

    public TestPage(int width, int height, JFrame attachedFrame) {
        super(width, height, attachedFrame);
    }

    @Override
    JPanel getPage() {
        return mainPanel;
    }

    @Override
    JPanel createPage() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel mainWrapper = new JPanel();
        mainWrapper.setLayout(new GridLayout(8, 1));

        JLabel equationDisplay = new JLabel(" ");

        MenuButton generateButton = new MenuButton("Denklem üret");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                equationDisplay.setText(Game.generateEquation());
            }
        });

        MenuButton homePage = new MenuButton("Ana sayfa");
        homePage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.mainFrame.setContentPane(Main.initialPage.createPage());
            }
        });

        mainWrapper.add(equationDisplay);
        mainWrapper.add(generateButton);
        mainWrapper.add(homePage);
        mainWrapper.add(Footer.getFooter());

        mainPanel.add(mainWrapper, BorderLayout.CENTER);

        return mainPanel;
    }

}
