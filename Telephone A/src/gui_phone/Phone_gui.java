/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui_phone;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.TextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sip_manager.UA;

/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
public class Phone_gui {
	
   
    public static TextArea text; 
    public JFrame frame;

    /**
     * ref directe vers le user agent de communication 
     */
    public static UA ua; 
    public static TextField myAddress;
    public static TextField myPort;
    public static TextField hisAddress;
    public static TextField hisPort;
    public static Button answer_button ;
    public static Button hangup_button;
    public static Button call_button;
    public static Button reject_button;
    public static Button set_button;
    
    
    
    
    public Phone_gui() {
	
// initGui
	frame=new JFrame("Sip Phone from Ensimag");
	frame.setLayout(new FlowLayout());

        
        
// For enter my Ip Address and my Port to call 
        myAddress=new TextField(12);myPort=new TextField(4);
        JLabel myAdd_label = new JLabel("IP Address");
        JPanel mp= new JPanel();
        mp.add(myAdd_label);
        mp.add(myAddress);
        JLabel myPort_label = new JLabel("with port");
        mp.add(myPort_label);
        mp.add(myPort);
        frame.getContentPane().add(mp);
        frame.getContentPane().add(new JLabel("make a call to "));
// For enter his Ip Address and his Port to call
        hisAddress=new TextField(12);hisPort=new TextField(4);
        JLabel hisAdd_label = new JLabel("IP Address");
        JLabel hisPort_label = new JLabel("with port");
        JPanel hp=new JPanel();
        hp.add(hisAdd_label); hp.add(hisAddress);
        hp.add(hisPort_label); hp.add(hisPort);
        frame.getContentPane().add(hp);

// For warning in the screen of Phone
	text=new TextArea(20,80);
	text.setEditable(false);
	text.setForeground(Color.red);
       	frame.getContentPane().add(text);
	
// JPanel to hold the buttons 
        JPanel bp = new JPanel();
        
// Set up button	
        set_button = new Button("SET UP");
        set_button.setForeground(Color.blue);
        set_button.addActionListener(new setListener(this));
        bp.add(set_button);        
// Call button	
	call_button = new Button("CALL");
        call_button.setForeground(Color.blue);
        call_button.addActionListener(new callListener(this));
        bp.add(call_button);

	
// Answer button	
        answer_button = new Button("ANSWER");
        answer_button.setForeground(Color.blue);
	answer_button.addActionListener(new answerListener(this));
        bp.add(answer_button);
//	frame.add(answer_button);
// Hangup button	
	hangup_button = new Button("HANG UP");
        hangup_button.setForeground(Color.blue);
	hangup_button.addActionListener(new hangupListener(this));
        bp.add(hangup_button);
// Reject button	
	reject_button = new Button("REJECT");
        reject_button.setForeground(Color.blue);
	reject_button.addActionListener(new rejectListener(this));
        bp.add(reject_button);
	
	frame.getContentPane().add(bp);
	frame.setSize(800,500);
	text.setBackground(Color.black); 
        frame.getContentPane().setBackground(Color.CYAN);
        mp.setBackground(Color.red);
        hp.setBackground(Color.green);
        bp.setBackground(Color.gray);
	frame.show();	
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Phone_gui.call_button.setEnabled(false);
        Phone_gui.answer_button.setEnabled(false);
        Phone_gui.hangup_button.setEnabled(false);
        Phone_gui.reject_button.setEnabled(false);
    }
    
    public void setHandler(UA ua_para){
    	this.ua = ua_para;
    }
    
    /**
     * Affiche les message  dans le GUI. 
     */
     
    public static void Print(String msg) {
    	try {
    		Phone_gui.text.append(msg+"\n");
    	} catch (Exception ex) {
			ex.printStackTrace();
			return;
	}	
    }

      
}

