/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sip_manager;


import gui_phone.Phone_gui;
import java.util.TimerTask;

/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
 class MyTimerTask extends TimerTask {
        UAListener ua_listener;

        public MyTimerTask(UAListener ualistener) {
            this.ua_listener = ualistener;

        }

        public void run() {
            
            Phone_gui.Print("............................");
            Phone_gui.Print("...Waited");
            Phone_gui.Print("Ended the call !");
           if(UAListener.waitingInviteOK==true){
            UAListener.waitingInviteOK=false;
           
           }
           if(UAListener.waitinganswer==true){
           
            Phone_gui.answer_button.setEnabled(false);
            Phone_gui.reject_button.setEnabled(false);
           
           }
           
            Phone_gui.call_button.setEnabled(true);
            
            Phone_gui.Print("You can make a new phone call  !");
            
            
        }

    }