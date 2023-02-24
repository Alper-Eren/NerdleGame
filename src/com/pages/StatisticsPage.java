package com.pages;

import com.gamedata.Game;
import com.style.Footer;
import com.style.MenuButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static com.Main.testPage;

/**
 * This is the welcoming page user first sees, it displays statistics of previous com.pages and options to play a new game
 * , continue an existing game if there is one, and a Test button to test the equation generating algorithm
 */
public class StatisticsPage extends Page {

    /**
     * Constructs the StatisticsPage using attached frame's width and height
     *
     * @param attachedFrame The frame page will reside on
     * @see StatisticsPage#StatisticsPage(int, int, JFrame)
     * @see StatisticsPage
     */
    public StatisticsPage(JFrame attachedFrame) {
        this(attachedFrame.getWidth(), attachedFrame.getHeight(), attachedFrame);
    }

    /**
     * Constructs the StatisticsPage using custom width and height inputs
     *
     * @param width         Target width
     * @param height        Target height
     * @param attachedFrame The frame page will reside on
     * @see StatisticsPage
     */
    public StatisticsPage(int width, int height, JFrame attachedFrame) {
        super(width, height, attachedFrame);
    }

    /**
     * @return
     */
    @Override
    public JPanel getPage() {
        return null;
    }

    @Override
    public JPanel createPage() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));


        JPanel topPanelScrollable = new JPanel();
        topPanelScrollable.setLayout(new BoxLayout(topPanelScrollable, BoxLayout.Y_AXIS));

        // Display statistics
        for (Map.Entry<String, Double> stat : Game.getGameStatistics().entrySet()) {
            JPanel statWrapper = new JPanel();
            statWrapper.setLayout(new BoxLayout(statWrapper, BoxLayout.X_AXIS));

            JLabel statDisplay = new JLabel();
            String displayText;

            if (Objects.equals(stat.getKey(), "Average Time Used (Successful Games)")) {
                displayText = String.format("%s : %02d:%02d", stat.getKey(), (int) ((stat.getValue() / 1000) / 60), (int) (stat.getValue() / 1000) % 60);
            } else {
                displayText = String.format("%s : %.2f", stat.getKey(), stat.getValue());
            }

            statDisplay.setText(displayText);
            statWrapper.add(statDisplay);
            statWrapper.add(Box.createHorizontalGlue());
            topPanelScrollable.add(statWrapper);
        }


        JScrollPane topPanelScrollPane = new JScrollPane(topPanelScrollable);
        topPanel.add(topPanelScrollPane);


        // Buttons Menu

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel buttonsWrapper = new JPanel();
        buttonsWrapper.setLayout(new GridLayout(1, 0));
        buttonsWrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

        MenuButton newGameButton = new MenuButton("Yeni Oyun");
        newGameButton.addActionListener(e -> new Game(attachedFrame));


        MenuButton continueGameButton = new MenuButton("Devam et");
        continueGameButton.setVisible(false);

        Game lastGame = Game.getLastGame();
        if (lastGame != null && lastGame.getState() == Game.GameStates.TBC) {
            continueGameButton.setVisible(true);
            continueGameButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    attachedFrame.setContentPane(lastGame.getGamePage().getPage());
                    lastGame.setStartTime(Instant.now());
                }
            });
        }

        MenuButton testButton = new MenuButton("Test");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attachedFrame.setContentPane(testPage.createPage());
                attachedFrame.revalidate();
                attachedFrame.repaint();
            }
        });

        buttonsWrapper.add(newGameButton);
        if (continueGameButton.isVisible()) {
            buttonsWrapper.add(continueGameButton);
        }
        buttonsWrapper.add(testButton);

        bottomPanel.add(buttonsWrapper);
        bottomPanel.add(Footer.getFooter());

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

}
