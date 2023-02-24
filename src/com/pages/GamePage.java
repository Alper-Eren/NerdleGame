package com.pages;

import com.Main;
import com.gamedata.Game;
import com.style.Footer;
import com.style.MenuButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

/**
 *
 */
public class GamePage extends Page {

    private Game gameInstance;

    class KeyPadButton extends MenuButton {

        private final char buttonChar;
        private Game.Cell[][] gameBoard;

        private KeyPadButton(char buttonChar, Game.Cell[][] gameBoard) {
            super(String.valueOf(buttonChar));
            this.buttonChar = buttonChar;
            this.gameBoard = gameBoard;
        }

        {
            this.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameInstance.getSelectedCell().setValue(buttonChar);
                    if (gameInstance.getSelectedCol() < gameInstance.getCols() - 1) {
                        gameInstance.setSelectedCol(gameInstance.getSelectedCol() + 1);
                    }
                }
            });
        }

    }

    private GamePage(int width, int height, JFrame attachedFrame) {
        super(width, height, attachedFrame);
    }

    public GamePage(JFrame attachedFrame, Game gameInstance) {
        this(attachedFrame.getWidth(), attachedFrame.getHeight(), attachedFrame, gameInstance);
    }

    public GamePage(int width, int height, JFrame attachedFrame, Game gameInstance) {
        this(width, height, attachedFrame);
        this.gameInstance = gameInstance;
    }

    @Override
    public JPanel getPage() {
        return mainPanel;
    }

    @Override
    public JPanel createPage() {
        mainPanel.setLayout(new BorderLayout());

        Game.Cell[][] gameBoard = this.gameInstance.getGameBoard();
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JPanel gameBoardWrapper = new JPanel();
        gameBoardWrapper.setLayout(new BoxLayout(gameBoardWrapper, BoxLayout.Y_AXIS));
        gameBoardWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JPanel gameBoardPanel = createGameBoard(gameBoard);

        JPanel gameActionsMenu = new JPanel();
        gameActionsMenu.setLayout(new BoxLayout(gameActionsMenu, BoxLayout.X_AXIS));
        gameActionsMenu.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Has to be instantiated before guess button to reference
        JLabel warningLabel = new JLabel(" ");
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(warningLabel.getFont().deriveFont(14F));
        Box warningWrapper = Box.createHorizontalBox();
        warningWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        warningWrapper.add(Box.createHorizontalGlue());
        warningWrapper.add(warningLabel);


        MenuButton guessButton = new MenuButton("Tahmin Et");
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                warningLabel.setText(" ");

                for (int i = 0; i < gameBoard[0].length; i++) {
                    Game.Cell cell = gameInstance.getActiveRow()[i];
                    if (cell.getValue() == ' ') {
                        warningLabel.setText("Lütfen boþ hücreleri doldurun");
                        return;
                    }
                }

                if (!gameInstance.guessComputes()) {
                    // DOESN'T COMPUTE ALERT
                    warningLabel.setText("Bu ifade doðru bir denklem deðil");
                    return;
                }


                if (!gameInstance.evaluateGuess()) {
                    if (gameInstance.getSelectedRow() < 5) {
                        gameInstance.setSelectedRow(gameInstance.getSelectedRow() + 1);
                        for (Game.Cell cell : gameInstance.getActiveRow()) {
                            cell.setState(Game.CellStates.ACTIVE);
                        }
                        gameInstance.setSelectedCol(0);
                        gameInstance.getActiveRow()[0].setState(Game.CellStates.SELECTED);
                    } else {
                        // FINISH BY STRIKES
                        gameInstance.finishGame(0);
                    }
                } else {
                    // FINISH BY VICTORY
                    gameInstance.finishGame(1);
                }
            }
        });

        MenuButton continueButton = new MenuButton("Sonra Bitir");
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameInstance.finishGame(-1);
            }
        });

        gameActionsMenu.add(Box.createHorizontalGlue());
        gameActionsMenu.add(guessButton);
        gameActionsMenu.add(Box.createRigidArea(new Dimension(5, 0)));
        gameActionsMenu.add(continueButton);

        gameBoardWrapper.add(gameBoardPanel);

        JPanel timerWrapper = new JPanel();
        timerWrapper.setLayout(new BoxLayout(timerWrapper, BoxLayout.X_AXIS));

        JLabel timerLabel = new JLabel(" ");

        ActionListener timerAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timerLabel.setText(String.format("%02d:%02d", (int) ((gameInstance.calculateDuration() / 1000) / 60), (int) (gameInstance.calculateDuration() / 1000) % 60));
            }
        };
        Timer timer = new Timer(100, timerAction);
        timer.start();

        timerWrapper.add(timerLabel);

        leftPanel.add(timerLabel);
        leftPanel.add(gameBoardWrapper);
        leftPanel.add(gameActionsMenu);
        leftPanel.add(warningWrapper);
        leftPanel.add(Box.createVerticalGlue());

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // Key Pad
        JPanel rightTopPanel = new JPanel();

        JPanel keyPadWrapper = new JPanel();
        keyPadWrapper.setLayout(new GridLayout(6, 3));

        char[][] keyPadChars = {
                {'/', '*', '-'},
                {'9', '8', '7'},
                {'6', '5', '4'},
                {'3', '2', '1'},
                {'+', '0', '='}
        };

        for (char[] row : keyPadChars) {
            for (char c : row) {
                keyPadWrapper.add(new KeyPadButton(c, gameBoard));
            }
        }

        MenuButton deleteButton = new MenuButton("Sil");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameInstance.getSelectedCell().setValue(' ');
                if (gameInstance.getSelectedCol() > 0) {
                    gameInstance.getSelectedCell().setState(Game.CellStates.ACTIVE);
                    gameInstance.setSelectedCol(gameInstance.getSelectedCol() - 1);
                }
            }
        });

        keyPadWrapper.add(deleteButton);
        rightTopPanel.add(keyPadWrapper);


        rightPanel.add(rightTopPanel);
        rightPanel.add(Box.createVerticalGlue());


        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(Footer.getFooter(), BorderLayout.SOUTH);
        return mainPanel;
    }


    private JPanel createGameBoard(Game.Cell[][] gameBoard) {
        JPanel boardWrapper = new JPanel();
        boardWrapper.setLayout(new BoxLayout(boardWrapper, BoxLayout.Y_AXIS));
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel board = new JPanel();
        board.setLayout(new GridLayout(gameBoard.length, gameBoard[0].length));


        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {
                Game.Cell cell = gameBoard[row][col];
                board.add(cell.getButton());
                int thisRow = row;
                int thisCol = col;
                cell.getButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (gameInstance.getSelectedRow() == thisRow) {
                            if (gameInstance.getSelectedCell().getState() == Game.CellStates.SELECTED) {
                                gameInstance.getSelectedCell().setState(Game.CellStates.ACTIVE);
                            }
                            gameInstance.setSelectedCol(thisCol);
                            cell.setState(Game.CellStates.SELECTED);
                        }
                    }
                });
            }
        }

        boardWrapper.add(board);
        boardWrapper.setSize(new Dimension(gameBoard[0][0].getButton().getWidth() * gameBoard.length, gameBoard[0][0].getButton().getHeight() * gameBoard[0].length));
        return boardWrapper;
    }


}
