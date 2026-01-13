package sossense.kontrolatzailea;

import java.awt.event.ActionListener;
import sossense.modelo.LoginModeloa;

public class LoginKontrolatzailea implements ActionListener {

    LoginModeloa m;

    public LoginKontrolatzailea(LoginModeloa m) {

        this.m = m;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        switch (e.getActionCommand()) {
            case "irten":
            //case "kantzelatu":
                m.itxiLehioa();
                break;
            case "ok":
                m.datuakKonprobatu();
                break;
        }

    }

}
