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
     * Classe traitant les action sur le bouton call du GUI. 
     * 
     */
    class callListener implements ActionListener  {
	Phone_gui irc;
	
    
	public callListener (Phone_gui i) {
        	irc = i;
	}
	
    
	public void actionPerformed (ActionEvent e) {
		// TO DO !!!
		  // emission d'une commande say au forum via le traitant de communication
		  // le msg est dans irc.data.getText()
//		String msg= irc.data.getText();
		try {
                    
                    Phone_gui.ua.call();
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
    } 