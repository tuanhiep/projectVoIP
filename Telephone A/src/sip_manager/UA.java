/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sip_manager;

import gui_phone.Phone_gui;
import static gui_phone.Phone_gui.Print;
import rtp_manager.MediaExchange;
import sdp_manager.PortFinder;
import sdp_manager.SessionInfo;

/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
public class UA {

    public static UAListener  ua_listener;
    public static MediaExchange mex;
    public static String myaddress;
    public static String hisaddress;
    public static String myportRTP;
    public static String hisportRTP;
    public UA(String stack_name, String myAddress, int myPort, String transport, String hisAddress, String hisPort) {
    UA.ua_listener=new UAListener(stack_name,myAddress,myPort,transport,hisAddress,hisPort);
    UA.myaddress = myAddress;
    UA.hisaddress=hisAddress;
    
    
    
    }

   
// In the phase of media exchange, two User Agent talk to each other    
    public static void talk() throws Exception{
    
      Phone_gui.Print("You are talking...");
      Phone_gui.Print(".................................................................");
      Phone_gui.Print(".................................................................");
      Phone_gui.Print(".................................................................");
      Phone_gui.Print(".................................................................");
      // Start the flow media udp     

    MediaExchange med= new MediaExchange(UA.myportRTP,UA.hisaddress,UA.hisportRTP);
    UA.mex=med;
    UA.mex.start();
      
    };
// To call an user agent
    
    public void call(){
    ua_listener.sendInvite();
    Phone_gui.Print("You are calling the destination !");
    Phone_gui.call_button.setEnabled(false);
        
    };
    
// To answer a call  
    
    public void answer(){
    Phone_gui.Print("You answered the call ! ");
    ua_listener.sendInviteOK();
    
    };
// To hangup a call
    public void hangup(){
    
    ua_listener.sendBye();
    Phone_gui.Print("You hang-uped the phone !");
    
    };
    
    
    
    
 // To stop talking, close the session media
    
    public static void stopTalking(){
    Phone_gui.Print("You don't talk any more !");
    // Stop the flow media udp   
    UA.mex.stop();
            }
    
    
    public static void reject(){
    
   
    Phone_gui.Print("You have rejected a call !");
    Phone_gui.call_button.setEnabled(true);
    Phone_gui.reject_button.setEnabled(false);
    Phone_gui.answer_button.setEnabled(false);
    ua_listener.sendCancel();
    
    }
    };
    
    
    
    
    
    
    
    

