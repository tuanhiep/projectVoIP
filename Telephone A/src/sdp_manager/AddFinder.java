/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sdp_manager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 *
 * @author canhchimbang  : Tuan Hiep
 */
public class AddFinder {
    
    
    
    public static String FindAddress(){
    
         
    String ip;
    try {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp())
                continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while(addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if(addr instanceof Inet4Address){
                ip = addr.getHostAddress();
                System.out.println(iface.getDisplayName() + " ohyeah " + ip);
                return ip;
                }}
        }
    } catch (SocketException e) {
        throw new RuntimeException(e);
    }
        return null;

    
    
    
    
    
    
    
    }
    
    
    

}
