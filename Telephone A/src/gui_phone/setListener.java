/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui_phone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import rtp_manager.MediaExchange;
import sip_manager.UA;

/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
class setListener implements ActionListener {
    Phone_gui irc;
    public setListener(Phone_gui i) {
        irc = i;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        try {
            // Get the ip address and port of the source and destination 
            String my_add= irc.myAddress.getText() ;
	    String his_add= irc.hisAddress.getText();
            String my_port=irc.myPort.getText();
            String his_port=irc.hisPort.getText();
            int myport= Integer.parseInt(my_port);
            // Create a listener for the user agent
            UA ua = new UA("Stack at "+my_add,my_add,myport,"udp",his_add,his_port);
            
            
            irc.setHandler(ua);
            irc.set_button.setEnabled(false);
            Phone_gui.call_button.setEnabled(true);
            Phone_gui.myAddress.setEnabled(false);
            Phone_gui.myPort.setEnabled(false);
            Phone_gui.hisAddress.setEnabled(false);
            Phone_gui.hisPort.setEnabled(false);
            Phone_gui.Print("You are at "+ my_add+"/"+my_port);
            Phone_gui.Print("You set up successfully your phone ! Now you can make a phone call to " + his_add+"/"+his_port);
	    Phone_gui.Print("Once you set up the phone, you can't change your configuration during the session ");
            Phone_gui.Print("If you want to change your configuration, please restart the phone !");
        } catch (Exception e1) {
			// TODO Auto-generated catch block
                        Phone_gui.Print("You haven't set up successfully your phone  !");
			e1.printStackTrace();
		}
    }

}
