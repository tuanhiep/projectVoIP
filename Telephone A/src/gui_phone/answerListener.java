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
  /**
     * Classe traitant les action sur le bouton answer du GUI. 
     * 
     */
    class answerListener implements ActionListener {
	Phone_gui irc;
	
    
	public answerListener (Phone_gui i) {
        	irc = i;
	}
    
     
	public void actionPerformed (ActionEvent e) {
		
//		String s = irc.data.getText();
		try {
			Phone_gui.ua.answer();
                        Phone_gui.hangup_button.setEnabled(true);
                        Phone_gui.call_button.setEnabled(false);
                        Phone_gui.reject_button.setEnabled(false);
                       
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}
    } 