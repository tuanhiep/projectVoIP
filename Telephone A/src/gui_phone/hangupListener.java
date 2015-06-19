/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui_phone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
  
   
    class hangupListener implements ActionListener {
	Phone_gui irc;
	    
	public hangupListener (Phone_gui i) {
        	irc = i;
	}

	public void actionPerformed (ActionEvent e) {

		
		try {

                    Phone_gui.ua.hangup();
                    irc.call_button.setEnabled(true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
    }
