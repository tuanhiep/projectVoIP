/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sip_manager;

import gui_phone.Phone_gui;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransactionState;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import sdp_manager.PortFinder;
import sdp_manager.SessionInfo;

/**
 *
 * @author canhchimbang : Tuan Hiep
 */
public class UAListener implements SipListener {

    //////////////////////////////////////////
    // The common parameter for initial setting
    private static SipStack sipStack=null;
    private String stack_name;
    private static AddressFactory addressFactory;
    private static HeaderFactory headerFactory;
    private static MessageFactory messageFactory;
    private Dialog dialog;
    private String myAddress ;
    private int myPort;
    private String hisAddress;
    protected static final String usageString = ">>>> is your class path set to the root?";
    public static SipProvider sipProvider;
    private ListeningPoint listening_point;
    // The particular parameter for each type of action
    public static boolean waitingInviteOK= false;
    public static boolean waitinganswer= false;
    static Timer timer_call;
    static Timer timer_answer;
    protected ServerTransaction inviteTid_Server;
    private ClientTransaction inviteTid_Client;
    private Request inviteRequest;
    private Request ackRequest;
    private Response okResponse;
    private Response byeResponse;
    private Response busyResponse;
    private ContactHeader contactHeader;
    
    // for the OUT_BOUND_PROXY of sip_stack : optional 
    String transport;
    public  static String peerHostPort;
    // for the sending 
    String fromName = "BigGuy";
    String fromSipAddress = "here.com";
    String fromDisplayName = "Hiep Dai Vuong";
    String toSipAddress = "there.com";
    String toUser = "LittleGuy";
    String toDisplayName = "Mai Van Cuong";

    ////////////////////////////////////////////////
    public UAListener(String stackName, String my_address, int my_port, String Transport, String his_add,String his_port) {
        // Initialize SipFactory--> Set PathName--> Set Properties--> Create SipStack 
        this.myAddress = my_address;
        this.myPort = my_port;
        // Set the OUT_BOUND_PROXY for sip_stack
        this.transport = Transport;
        this.hisAddress= his_add;
        this.peerHostPort = his_add+":"+his_port;
        this.stack_name = stackName;
        SipFactory sipFactory = null;
        UAListener.sipStack = null;
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        Properties properties = new Properties();

        properties.setProperty("javax.sip.OUTBOUND_PROXY", peerHostPort + "/"
                + transport);
        properties.setProperty("javax.sip.STACK_NAME", this.stack_name);
        // You need 16 for logging traces. 32 for debug + traces.
        // Your code will limp at 32 but it is best for debugging.
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                 "debug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                 "log.txt");
       // Drop the client connection after we are done with the transaction.
        properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS",
                "false");
        // Set to 0 (or NONE) in your production code for max speed.
        // You need 16 (or TRACE) for logging traces. 32 (or DEBUG) for debug + traces.
        // Your code will limp at 32 but it is best for debugging.
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");

        // Create Sip Stack by the properties    
        try {
            // Create SipStack object
            sipStack = sipFactory.createSipStack(properties);
            System.out.println("sipStack = " + sipStack);
        } catch (PeerUnavailableException e) {
            // could not find
            // gov.nist.jain.protocol.ip.sip.SipStackImpl
            // in the classpath
            System.out.println("Creating sip stack failed !");
            e.printStackTrace();
            System.err.println(e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            System.exit(0);
        }




        // Create Header Factory, Address Factory, Message Factory, Listening Point 
        // Create SipProvider correspond to SipListening Point and add the Provider to Listener

      try {
      headerFactory = sipFactory.createHeaderFactory();
          addressFactory = sipFactory.createAddressFactory();
           messageFactory = sipFactory.createMessageFactory();
           listening_point = sipStack.createListeningPoint(myAddress, myPort, transport);

           UAListener listener = this;

           UAListener.sipProvider = sipStack.createSipProvider(this.listening_point);
           System.out.println("udp provider " + UAListener.sipProvider);
           sipProvider.addSipListener(listener);

       } catch (Exception ex) {
           System.out.println(ex.getMessage());
           ex.printStackTrace();
            usage();
        }

    }

    // Prepare the necessary parameters to Send when make a call
    // ok !
    public void sendInvite() {

        try {
            // Create the Request INVITE For the fist INVITE

            //////////////////////////////////////////////////////////////////////////////////
            // create >From Header
            SipURI fromAddress = addressFactory.createSipURI(fromName, fromSipAddress);
            Address fromNameAddress = addressFactory.createAddress(fromAddress);
            fromNameAddress.setDisplayName(fromDisplayName);
            FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "12345");

            // create To Header
            SipURI toAddress = addressFactory.createSipURI(toUser, toSipAddress);
            Address toNameAddress = addressFactory.createAddress(toAddress);
            toNameAddress.setDisplayName(toDisplayName);
            ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);

            // create Request URI
            SipURI requestURI = addressFactory.createSipURI(toUser, peerHostPort);

            // Create ViaHeaders

            ArrayList viaHeaders = new ArrayList();
            String ipAddress = this.listening_point.getIPAddress();
            ViaHeader viaHeader = headerFactory.createViaHeader(ipAddress,
                    sipProvider.getListeningPoint(transport).getPort(),
                    transport, null);

            // add via headers
            viaHeaders.add(viaHeader);

            // Create ContentTypeHeader
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");

            // Create a new CallId header : return the unique id for each dialogues between two Sip application
            CallIdHeader callIdHeader = sipProvider.getNewCallId();

            // Create a new Cseq header
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);

            // Create a new MaxForwardsHeader
            MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

            // Create the request.
            Request request = messageFactory.createRequest(requestURI,
                    Request.INVITE, callIdHeader, cSeqHeader, fromHeader,
                    toHeader, viaHeaders, maxForwards);

            ///////////////////////////////////////////////////////////////////////////////////    


            // Add more header for the request , add the contact Address: the address of the sender

         
            SipURI contactUrl = addressFactory.createSipURI(fromName, myAddress);
            contactUrl.setPort(this.listening_point.getPort());
            contactUrl.setLrParam();

            // Create the contact name address.
            SipURI contactURI = addressFactory.createSipURI(fromName, myAddress);
            contactURI.setPort(sipProvider.getListeningPoint(transport).getPort());
            Address contactAddress = addressFactory.createAddress(contactURI);

            // Add the contact address.
            contactAddress.setDisplayName(fromName);

            contactHeader = headerFactory.createContactHeader(contactAddress);
            request.addHeader(contactHeader);

            // You can add extension headers of your own making
            // to the outgoing SIP request.
            // Add the extension header.
            Header extensionHeader = headerFactory.createHeader("My-Header",
                    "my header value");
            request.addHeader(extensionHeader);
            
///////////////////////////////////////////////////////////////////////////////////////:
           // add SDP details to the content of Message SIP : INVITE : ok !
            UA.myportRTP= ""+PortFinder.findFreePort(1000);             
            SessionInfo si = new SessionInfo(UA.myaddress,UA.myportRTP,UA.hisaddress);
            byte[] contents = (si.sdpData).getBytes();
            request.setContent(contents, contentTypeHeader);
   
/////////////////////////////////////////////////////////////////////////////////            

            // You can add as many extension headers as you want.

            extensionHeader = headerFactory.createHeader("My-Other-Header",
                    "my new header value ");
            request.addHeader(extensionHeader);

            Header callInfoHeader = headerFactory.createHeader("Call-Info",
                    "<http://www.antd.nist.gov>");
            request.addHeader(callInfoHeader);

            // Create the client transaction.
            inviteTid_Client = sipProvider.getNewClientTransaction(request);

            // send the request out.
            inviteTid_Client.sendRequest();

            dialog = inviteTid_Client.getDialog();
            //////////////////////
            
            // waiting here
            timer_call= new Timer();
            timer_call.schedule(new MyTimerTask(this), 10000);
            UAListener.waitingInviteOK= true;
            ////////////////////////
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            usage();
        }




    }

    private static void usage() {
        System.out.println(usageString);
        System.exit(0);

    }

    @Override
    public void processRequest(RequestEvent requestEvent) {

        Request request = requestEvent.getRequest();
        ServerTransaction serverTransactionId = requestEvent
                .getServerTransaction();

        System.out.println("\n\nRequest " + request.getMethod()
                + " received at " + sipStack.getStackName()
                + " with server transaction id " + serverTransactionId);

        if (request.getMethod().equals(Request.INVITE)) {
            processInvite(requestEvent, serverTransactionId);
        } else if (request.getMethod().equals(Request.ACK)) {
            processAck(requestEvent, serverTransactionId);
        } else if (request.getMethod().equals(Request.BYE)) {
            processBye(requestEvent, serverTransactionId);
        } else if (request.getMethod().equals(Request.CANCEL)) {
            
                
        Phone_gui.Print("Your call is rejected !");
         try {
            if (inviteTid_Server.getState() != TransactionState.COMPLETED) {
                System.out.println(this.stack_name + ": Dialog state before 200: "
                        + inviteTid_Server.getDialog().getState());
                inviteTid_Server.terminate();
                this.inviteTid_Client.terminate();
                System.out.println(this.stack_name + ": Dialog state after 200: "
                        + inviteTid_Server.getDialog().getState());
                 
                 UAListener.timer_call.cancel();
                 UAListener.waitingInviteOK=false;
                 Phone_gui.call_button.setEnabled(true);
            }
        } catch (SipException ex) {
            ex.printStackTrace();
        }
            
            
        } else {
            try {
                serverTransactionId.sendResponse(messageFactory.createResponse(202, request));

                // send one back
                SipProvider prov = (SipProvider) requestEvent.getSource();
                Request refer = requestEvent.getDialog().createRequest("REFER");
                requestEvent.getDialog().sendRequest(prov.getNewClientTransaction(refer));

            } catch (SipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvalidArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }




    }

    // To execute the received Response , the Response may be : OK, CANCEL  :ok !
    @Override
    public void processResponse(ResponseEvent responseReceivedEvent) {
        System.out.println("Got a response");
        Response response = (Response) responseReceivedEvent.getResponse();
        ClientTransaction tid = responseReceivedEvent.getClientTransaction();
        CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);

        System.out.println("Response received : Status Code = "
                + response.getStatusCode() + " " + cseq);


        if (tid == null) {

            // RFC3261: MUST respond to every 2xx
            if (ackRequest != null && dialog != null) {
                System.out.println("re-sending ACK");
                try {
                    dialog.sendAck(ackRequest);
                } catch (SipException se) {
                    se.printStackTrace();
                }
            }
            return;
        }
//        // If the caller is supposed to send the bye
//        if (this.callerSendsBye && !byeTaskRunning) {
//            byeTaskRunning = true;
//            new Timer().schedule(new ByeTask(dialog), 4000);
//        }
        System.out.println("transaction state is " + tid.getState());
        System.out.println("Dialog = " + tid.getDialog());
        System.out.println("Dialog State is " + tid.getDialog().getState());

        try {
            if (response.getStatusCode() == Response.OK) {
                if (cseq.getMethod().equals(Request.INVITE)&& UAListener.waitingInviteOK==true) {
                    UAListener.waitingInviteOK=false;
                    timer_call.cancel();
                    System.out.println("Dialog after 200 OK  " + dialog);
                    System.out.println("Dialog State after 200 OK  " + dialog.getState());
                    ackRequest = dialog.createAck(((CSeqHeader) response.getHeader(CSeqHeader.NAME)).getSeqNumber());
                    System.out.println("Sending ACK");
                    dialog.sendAck(ackRequest);
                    
///////////////////////////////////////////////////////////////////////////////
     Phone_gui.Print("The phone at destination "+UAListener.peerHostPort+" is ringing !!!!!!!!!");
     String sdpres= new String(response.getRawContent());
     SessionInfo si = new SessionInfo(sdpres);
     UA.hisportRTP=si.getMediaPort();
     
     Phone_gui.Print("His port for RTP session is :"+ UA.hisportRTP);
                    
  // User Agent talk here because he receive an InviteOK ! He open the talk                 
                   
                   Phone_gui.Print("You are at " + this.myAddress+"/"+this.myPort+ " and you start talking :");
                   Phone_gui.Print("You can end the call by click HANG UP");
                   Phone_gui.hangup_button.setEnabled(true);
                   Phone_gui.call_button.setEnabled(false);
                   UA.talk();
///////////////////////////////////////////////////////////////////////////////                    
                    // JvB: test REFER, reported bug in tag handling
                    // dialog.sendRequest(  sipProvider.getNewClientTransaction( dialog.createRequest("REFER") ));

                } else if (cseq.getMethod().equals(Request.CANCEL)) {
                    if (dialog.getState() == DialogState.CONFIRMED) {
                        // oops cancel went in too late. Need to hang up the
                        // dialog by sending a BYE
                        System.out.println("Sending BYE -- cancel went in too late !!");
                        Request byeRequest = dialog.createRequest(Request.BYE);
                        ClientTransaction ct = sipProvider.getNewClientTransaction(byeRequest);
                        dialog.sendRequest(ct);

                    }

                }else if(cseq.getMethod().equals(Request.BYE)){
                ////////////////////////////////////////////////////////
                    //  The dialog is terminated by we got a BYE OK. User Agent stop talking
                
                UA.stopTalking();
                Phone_gui.hangup_button.setEnabled(false);
                Phone_gui.call_button.setEnabled(true);
                Phone_gui.Print("Dialog is terminated !");
                Phone_gui.Print("You can make a new phone call ");
                
                
                }
                
            }
            
            else if (response.getStatusCode() == Response.RINGING){
            // Get a ringing 
            
            
            }
            else if (response.getStatusCode() == Response.BUSY_HERE){
            
            Phone_gui.Print("Your call is rejected !");
         try {
            this.inviteTid_Client.terminate();
                 
                 UAListener.timer_call.cancel();
                 UAListener.waitingInviteOK=false;
                 Phone_gui.call_button.setEnabled(true);
            
        } catch (SipException ex) {
            ex.printStackTrace();
        }
            
            }
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }


    }

    public void sendCancel() {
        try {
            
            System.out.println("Sending cancel");
      
                
            this.inviteTid_Server.sendResponse(this.busyResponse);

            UAListener.timer_answer.cancel();
            UAListener.waitinganswer=false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

        Transaction transaction;
        if (timeoutEvent.isServerTransaction()) {
            transaction = timeoutEvent.getServerTransaction();
        } else {
            transaction = timeoutEvent.getClientTransaction();
        }
        System.out.println("state = " + transaction.getState());
        System.out.println("dialog = " + transaction.getDialog());
        System.out.println("dialogState = "
                + transaction.getDialog().getState());
        System.out.println("Transaction Time out");


    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {

        System.out.println("IOException");

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        if (transactionTerminatedEvent.isServerTransaction()) {
            System.out.println("Transaction terminated event recieved"
                    + transactionTerminatedEvent.getServerTransaction());
        } else {
            System.out.println("Transaction terminated "
                    + transactionTerminatedEvent.getClientTransaction());
        }


    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {


        System.out.println("Dialog terminated event recieved");
        Dialog d = dialogTerminatedEvent.getDialog();
        System.out.println("Local Party = " + d.getLocalParty());


    }

    // To execute the received INVITE by send the RINGING and run the sendINVITEOK :ok !
    private void processInvite(RequestEvent requestEvent, ServerTransaction serverTransaction) {

        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        try {
            System.out.println(this.stack_name + ": got an Invite sending Trying");
            // System.out.println("shootme: " + request);
            // Create the response RINGING correspond to the INVITE request
            Response response = messageFactory.createResponse(Response.RINGING,
                    request);
            this.busyResponse=messageFactory.createResponse(Response.BUSY_HERE, request);
            ServerTransaction st = requestEvent.getServerTransaction();
      
            if (st == null) {
                st = sipProvider.getNewServerTransaction(request);
            }
            dialog = st.getDialog();

            st.sendResponse(response);
           
            Phone_gui.Print("Ringing !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            // waiting here
            waitinganswer=true;
            timer_answer= new Timer();
            timer_answer.schedule(new MyTimerTask(this), 10000);
            
            Phone_gui.answer_button.setEnabled(true);
            Phone_gui.call_button.setEnabled(false);
            Phone_gui.reject_button.setEnabled(true);
            Phone_gui.Print("You can answer or reject the call ");
            
            
// Create the response OK to confirm the dialogue but here it's not be sent , just prepare
            
            this.okResponse = messageFactory.createResponse(Response.OK,
                    request);
            Address address = addressFactory.createAddress(this.stack_name + " <sip:"
                    + myAddress + ":" + myPort + ">");
            ContactHeader contactHeader = headerFactory.createContactHeader(address);
            response.addHeader(contactHeader);
            ToHeader toHeader = (ToHeader) okResponse.getHeader(ToHeader.NAME);
            toHeader.setTag("4321"); // Application is supposed to set.
            okResponse.addHeader(contactHeader);
            this.inviteTid_Server = st;
            // Defer sending the OK to simulate the phone ringing.
            // Answered in 1 second ( this guy is fast at taking calls)
            this.inviteRequest = request;
            
///////////////////////////////////////////////////////////
       // Prepare the parameter for the media session by get RTP port and fromAddress     
        String sdpContent = new String(request.getRawContent());
        SessionInfo s = new SessionInfo(sdpContent);
                
        String fromAddress= s.getFromAdd();
        String RTP_port = s.getMediaPort();
        UA.hisaddress= fromAddress;
        UA.hisportRTP=RTP_port;
        int hisport = Integer.parseInt(UA.hisportRTP);
        UA.myportRTP= ""+PortFinder.findFreePort(hisport);  
        Phone_gui.Print("You are at "+ UA.myaddress+"/"+this.myPort);
        Phone_gui.Print("You have received a call from "+ UA.hisaddress);
        Phone_gui.Print("His port for RTP session is :"+ UA.hisportRTP);
// Add sdp data content to the response RINGING       
        
        SessionInfo sei = new SessionInfo(UA.myaddress,UA.myportRTP,UA.hisaddress);
        
        byte[] contents= (sei.sdpData).getBytes();
        ContentTypeHeader contentTypeHeaderRes = headerFactory.createContentTypeHeader("application", "sdp");
        okResponse.setContent(contents, contentTypeHeaderRes);
        
        
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }

    }
    // To send the OK after receive the INVITE  :ok !

    void sendInviteOK() {

        try {
            timer_answer.cancel();
            if (inviteTid_Server.getState() != TransactionState.COMPLETED) {
                System.out.println(this.stack_name + ": Dialog state before 200: "
                        + inviteTid_Server.getDialog().getState());
                inviteTid_Server.sendResponse(okResponse);
                System.out.println(this.stack_name + ": Dialog state after 200: "
                        + inviteTid_Server.getDialog().getState());
            }
        } catch (SipException ex) {
            ex.printStackTrace();
        } catch (InvalidArgumentException ex) {
            ex.printStackTrace();
        }
    }

// To process the received Ack , when the caller send a BYE, send the Bye to terminate the dialog :ok !
    private void processAck(RequestEvent requestEvent, ServerTransaction serverTransactionId) {
        try {
            System.out.println(this.stack_name + ": got an ACK! ");
            System.out.println("Dialog State = " + dialog.getState());
            SipProvider provider = (SipProvider) requestEvent.getSource();
           
            ///////////////////////////////////////////////////////////////////
            
            // User Agent talk here
//            System.out.println(this.toDisplayName+" starts talking");
            Phone_gui.Print("You are at " + UA.myaddress+"/"+this.myPort+ " and you start talking :");
            Phone_gui.Print("You can end the call by click HANG UP");
            UA.talk();
            Phone_gui.answer_button.setEnabled(false);
            Phone_gui.hangup_button.setEnabled(true);
            Phone_gui.call_button.setEnabled(false);
            ////////////////////////////////////////////////////////////////////
            

            
        } catch (Exception ex) {
            ex.printStackTrace();
        }







    }
    
   public void sendBye(){
    
   ByeTask bye= new ByeTask(this.dialog);
   bye.run();
    
    
    }

   public void sendByeOK(){
   
      try {
            if (inviteTid_Server.getState() != TransactionState.COMPLETED) {
                System.out.println(this.stack_name + ": Dialog state before 200: "
                        + inviteTid_Server.getDialog().getState());
                inviteTid_Server.sendResponse(okResponse);
                System.out.println(this.stack_name + ": Dialog state after 200: "
                        + inviteTid_Server.getDialog().getState());
                
         ////////////////////////////////////////////////////      
                // User agent stop talking here
                UA.stopTalking();
                
         ///////////////////////////////////////////////////       
                
            }
        } catch (SipException ex) {
            ex.printStackTrace();
        } catch (InvalidArgumentException ex) {
            ex.printStackTrace();
        }
   
   }
   /**
     * Process the bye request.
     */
    public void processBye(RequestEvent requestEvent,
            ServerTransaction serverTransactionId) {
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        Dialog dialog = requestEvent.getDialog();
        System.out.println("local party = " + dialog.getLocalParty());
        try {
            System.out.println(this.stack_name+":  got a bye sending OK.");
            Response response = messageFactory.createResponse(200, request);
            serverTransactionId.sendResponse(response);
            System.out.println("Dialog State is "
                    + serverTransactionId.getDialog().getState());
            
            /////////////////////////////////////////////////////////
            // We got a BYE, so the dialog is terminated ! User Agent stop talking
            
            Phone_gui.Print("The phone at destination is hang-uped. Dialog is terminated !");
            
            UA.stopTalking();
            Phone_gui.Print("You can make a new phone call ");
            Phone_gui.call_button.setEnabled(true);
            Phone_gui.hangup_button.setEnabled(false);
            ////////////////////////////////////////////////////////

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);

        }
    }

    private void processCancel(RequestEvent requestEvent, ServerTransaction serverTransactionId) {
       
    
        
    }
}
