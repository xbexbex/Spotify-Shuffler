package com.gui;

import com.logic.MainLogic;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.*;

public class MainGUI implements Runnable {

    private JFrame frame;
    private MainLogic click;

    @Override
    public void run() {
        frame = new JFrame("SpotifySystem");
        frame.setPreferredSize(new Dimension(300, 150));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        website();

        frame.pack();
        frame.setVisible(true);
    }

    private void loginComponents(Container container) {
        GridLayout layout = new GridLayout(4, 1);
        container.setLayout(layout);

        JTextField username = new JTextField();
        JTextField password = new JTextField();
        JTextField testoutput = new JTextField();
        container.add(username);
        container.add(password);
        container.add(testoutput);
        testoutput.setEnabled(false);

        JButton login = new JButton("Log in");
        container.add(login);

        click = new MainLogic(login, username, password, testoutput);
        login.addActionListener(click);
    }

    private void website() {
        JEditorPane site = new JEditorPane();
        site.setEditable(false);
        frame.add(site);

        try {
            site.setPage("http://www.google.com");
        } catch (IOException e) {
            site.setContentType("text/html");
            site.setText("<html>Could not load</html>");
        }

        JScrollPane scrollPane = new JScrollPane(site);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.setPreferredSize(new Dimension(400, 600));
        frame.setVisible(true);
    }
}
