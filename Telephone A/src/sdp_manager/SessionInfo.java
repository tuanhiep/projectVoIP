/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sdp_manager;

import java.util.Vector;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;



/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
public class SessionInfo {
    
    public String sdpData;
    public SdpFactory sdpFactory;
    public SessionInfo(String myadd, String myRTPport, String hisadd) {
        
        this.sdpFactory= SdpFactory.getInstance();
    
        this.sdpData ="v=0\r\n"
                + "o=hiep 13760799956958020 13760799956958020"+" IN IP4  "+myadd+"\r\n" 
                + "s=mysession session\r\n"
                + "c=IN IP4 "+hisadd+"\r\n"
                + "t=0 0\r\n" 
                + "m=audio "+myRTPport+" RTP/AVP 0 4 18\r\n"
                + "a=rtpmap:0 PCMU/8000\r\n" 
                + "a=ptime:20\r\n";
    }

    public SessionInfo(String SDP) {
        this.sdpFactory= SdpFactory.getInstance();
        this.sdpData=SDP;
        
    }
    
    

    
        
    public String getFromAdd() throws SdpParseException{
        SessionDescription sd=this.sdpFactory.createSessionDescription(this.sdpData);
       
        return(sd.getOrigin().getAddress());
     
    }
    
    public String getDesAdd() throws SdpParseException{
    
      SessionDescription sd=this.sdpFactory.createSessionDescription(this.sdpData);
       
        return(sd.getConnection().getAddress());
    
    }
    
    public String getMediaPort() throws SdpException {
       SessionDescription sd=this.sdpFactory.createSessionDescription(this.sdpData);
       Vector v=sd.getMediaDescriptions(true);
       String[] s = v.toString().split(" ");
       return s[1];
        
        
    
    }
    
    
    }
    
    
    
    
    


