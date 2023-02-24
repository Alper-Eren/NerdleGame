package com;

import com.pages.StatisticsPage;
import com.pages.TestPage;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class Main {

    // Pages that do not change or belong to an object can be kept as static references
    public static JFrame mainFrame = new JFrame();
    public static StatisticsPage initialPage = new StatisticsPage(mainFrame);
    public static TestPage testPage = new TestPage(mainFrame);

    public static void main(String[] args) {


        //region Font set up
        FontUIResource font = new FontUIResource("Gotham", Font.BOLD, 18);

        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("MenuBar.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("PopupMenu.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ProgressBar.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Viewport.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);
        UIManager.put("Spinner.font", font);
        UIManager.put("IconButton.font", font);
        UIManager.put("FormattedTextField.font", font);
        UIManager.put("TabbedPane.smallFont", font.deriveFont((float) 8));
        UIManager.put("InternalFrame.titleFont", font);
        //endregion


        mainFrame.setSize(600,800);
        mainFrame.setContentPane(initialPage.createPage());
        mainFrame.setLocationRelativeTo(null); // Center frame
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
