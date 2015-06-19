/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rtp_manager;

import gui_phone.Phone_gui;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.DefaultStreamConnector;
import org.jitsi.service.neomedia.MediaDirection;
import org.jitsi.service.neomedia.MediaService;
import org.jitsi.service.neomedia.MediaStream;
import org.jitsi.service.neomedia.MediaStreamTarget;
import org.jitsi.service.neomedia.MediaType;
import org.jitsi.service.neomedia.MediaUseCase;
import org.jitsi.service.neomedia.StreamConnector;
import org.jitsi.service.neomedia.device.MediaDevice;
import org.jitsi.service.neomedia.format.MediaFormat;
import sdp_manager.SessionInfo;

/**
 *
 * @author Strong man
 */
public class MediaExchange {
    
  

    private int localPortBase;

    private MediaStream mediaStream;

    private InetAddress remoteAddr;

    private int remotePortBase;

    public MediaExchange(
            String localPortBase,
            String remoteHost, String remotePortBase)
            throws Exception {
        this.localPortBase
                = (localPortBase == null)
                ? -1
                : Integer.valueOf(localPortBase).intValue();
        this.remoteAddr = InetAddress.getByName(remoteHost);
        this.remotePortBase = Integer.valueOf(remotePortBase).intValue();
    
    
    
    
    
    
    }
    
    public void start() throws Exception {

        LibJitsi.start();
        /*
         * Prepare for the start of the transmission i.e. initialize the
         * MediaStream instances.
         */

        MediaService mediaService = LibJitsi.getMediaService();

        /*
         * The default MediaDevice (for a specific MediaType) is configured
         * (by the user of the application via some sort of UI) into the
         * ConfigurationService. If there is no ConfigurationService
         * instance known to LibJitsi, the first available MediaDevice of
         * the specified MediaType will be chosen by MediaService.
         */
        try {

            MediaDevice device = mediaService.getDefaultDevice(MediaType.AUDIO, MediaUseCase.CALL);

            this.mediaStream = mediaService.createMediaStream(device);

            // direction :In a call, the MediaStream's direction will most commonly be set to SENDRECV.
            mediaStream.setDirection(MediaDirection.SENDRECV);

            // format
            String encoding = "PCMU";
            double clockRate = 8000;

            /*
             *Its RTP transmission has no static RTP payload type number
             * assigned.
             */
            // Set format    
            MediaFormat format
                    = mediaService.getFormatFactory().createMediaFormat(
                            encoding,
                            clockRate);

            mediaStream.setFormat(format);

            // Set connector
            StreamConnector connector;

            if (this.localPortBase == -1) {
                connector = new DefaultStreamConnector();
            } else {

                int localRTPPort = this.localPortBase;
                int localRTCPPort = this.localPortBase + 1;

                connector = new DefaultStreamConnector(
                        new DatagramSocket(localRTPPort),
                        new DatagramSocket(localRTCPPort));
            }
            mediaStream.setConnector(connector);

            // target
            /*
             * The RTCP port is right after the RTP port.
             */
            int remoteRTPPort = this.remotePortBase;
            int remoteRTCPPort = this.remotePortBase + 1;

            mediaStream.setTarget(
                    new MediaStreamTarget(
                            new InetSocketAddress(remoteAddr, remoteRTPPort),
                            new InetSocketAddress(remoteAddr, remoteRTCPPort)));
            // name
            /*
             * The name is completely optional and it is not being used by the
             * MediaStream implementation at this time, it is just remembered so
             * that it can be retrieved via MediaStream#getName(). It may be
             * integrated with the signaling functionality if necessary.
             */
            mediaStream.setName("AUDIO");

            /*
             * Do start the transmission i.e. start the initialized MediaStream
             * instances.
             */
            this.mediaStream.start();

        } catch (Exception e) {

            Phone_gui.Print("Can not find audio device !");

        }

    }

    public void stop() {

        if (this.mediaStream != null) {

            this.mediaStream.stop();
            this.mediaStream = null;
        }

        LibJitsi.stop();
    }

}
