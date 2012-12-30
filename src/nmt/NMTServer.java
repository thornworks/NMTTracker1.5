package nmt;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * accepts connections
 */
 
public class NMTServer extends Thread{
   
   private int port;
   ServerSocket s;
   NMTracker n;
   ArrayList<NMTSSocket> sockets;
   private boolean stopMe = false;
   
   public NMTServer(int port, NMTracker n){
      this.port=port;
      this.n=n;
      sockets=new ArrayList<NMTSSocket>();
      try{
         s=new ServerSocket(port);
      }
      catch(Exception e){
         System.out.println(e);
         e.printStackTrace();
         System.exit(0);
      }
   }
   
   /**
    * used to change the database when
    * a new database is loaded
    */
   public void changeDB(NMTracker dB){
      n=dB;
      for(NMTSSocket soc: sockets){
         soc.studentDB=dB;
      }
   }
   
   public void stopServer() {
      stopMe = true;
   }
   public void run(){
      while(!stopMe){
         try {
            Socket sock=s.accept();
            NMTSSocket a=new NMTSSocket(sock, n);
            a.start();
            sockets.add(a);
         }
         catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
         }
      }
      //now close all the sockets:
      for(NMTSSocket a : sockets) {
         a.closeSocket();
      }
   }
}