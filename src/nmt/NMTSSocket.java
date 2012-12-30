package nmt;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
/**
 * this class recieves data and
 * passes it on to the database
 */

public class NMTSSocket extends Thread{
   
   Socket s;
   ObjectOutputStream out;
   ObjectInputStream in;
   NMTracker studentDB;
   boolean stopThisThread = false;
   
   public NMTSSocket(Socket s, NMTracker studentDB){
      try{
         this.studentDB=studentDB;
         this.s=s;
         out=new ObjectOutputStream(s.getOutputStream());
         in=new ObjectInputStream(s.getInputStream());
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }
   
   public void run(){
      while(!stopThisThread){
         try{
            String needed=in.readObject().toString();
            if(needed.equals("numTeams"))
               out.writeObject(new Integer(studentDB.numTeams()));
               
            else if(needed.equals("getTeamsFromSchools")){
               int[] i=studentDB.getTeamsFromSchools(in.readObject().toString());
               out.writeObject(new Integer(i.length));
               for(int x=0; x<i.length; x++){
                  out.writeObject(new Integer(i[x]));
               }
            }
            else if(needed.equals("removeTeam"))
               out.writeObject(new Boolean(studentDB.removeTeam(((Integer)in.readObject()).intValue())));

            else if(needed.equals("containsTeamNum")){
               char c=((Character)in.readObject()).charValue();
               int i=((Integer)in.readObject()).intValue();
               out.writeObject(new Boolean(studentDB.containsTeamNum(c, i)));
            }
            else if(needed.equals("addTeam")){
               Team t=(Team)in.readObject();
               studentDB.addTeam(t);
            }
            else if(needed.equals("getTeam"))
               out.writeObject(studentDB.getTeam(((Integer)in.readObject()).intValue()));
            
            else if(needed.equals("getListTeamNum"))
               out.writeObject(studentDB.getListTeamNum(((Character)in.readObject()).charValue()));
            
            else if(needed.equals("getTeamsNoScore")) 
            	out.writeObject(studentDB.getTeamsNoScore());
            
            else if(needed.equals("getSchool"))
               out.writeObject(studentDB.getTeam((((Integer)in.readObject()).intValue())).getSchool());
            
            else if(needed.equals("getAllComplete")){
               ArrayList<Integer> c=new ArrayList<Integer>();
               for(Team t: studentDB.upperDivisionTeams.values())
                  if(t.allDataEntered())
                     c.add(t.getTeamNum());
               for(Team t: studentDB.lowerDivisionTeams.values())
                  if(t.allDataEntered())
                     c.add(t.getTeamNum());
               out.writeObject(c);
            }
            else if(needed.equals("getGLComplete")){
               ArrayList<Integer> c=new ArrayList<Integer>();
               for(Team t: studentDB.upperDivisionTeams.values())
                  if(t.completedGL() && !t.completedML())
                     c.add(t.getTeamNum());
               for(Team t: studentDB.lowerDivisionTeams.values())
                  if(t.completedGL() && !t.completedML())
                     c.add(t.getTeamNum());
               out.writeObject(c);
            }
            else if(needed.equals("getMLComplete")){
               ArrayList<Integer> c=new ArrayList<Integer>();
               for(Team t: studentDB.upperDivisionTeams.values())
                  if(t.completedML() && !t.allDataEntered())
                     c.add(t.getTeamNum());
               for(Team t: studentDB.lowerDivisionTeams.values())
                  if(t.completedML() && !t.allDataEntered())
                     c.add(t.getTeamNum());
               out.writeObject(c);
            }
         }
         catch(SocketException s) {
           System.out.println("The connection to a client was lost.");
            stopThisThread = true;
         }
         catch(IOException e){
            System.out.println(
              "An IO error was encountered while communicating with a client:\n" + 
               e.toString());
            stopThisThread = true;
         }
         //all Class Casts should work fine.
         catch(ClassNotFoundException c) {         }
      }
   }
   public void closeSocket() {
      stopThisThread = true;
   }
}