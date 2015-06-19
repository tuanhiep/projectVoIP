/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui_phone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sip_manager.UA;

/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
class rejectListener implements ActionListener {
    Phone_gui irc;
    public rejectListener(Phone_gui i) {
        this.irc=i;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       UA.reject();
       
    }

}
