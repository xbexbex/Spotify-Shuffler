package com.logic;

import com.logic.Server;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.*;
import javax.xml.ws.http.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 *
 * @author Owner
 */
public class MainLogic implements ActionListener {

    private JButton login;
    private JTextField username;
    private JTextField password;
    private JTextField testoutput;

    public MainLogic(JButton login, JTextField username, JTextField password, JTextField testoutput) {
        this.login = login;
        this.username = username;
        this.password = password;
        this.testoutput = testoutput;

    }

    MainLogic() {
        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String un = username.getText();
            String pw = password.getText();
        }
    }
}
