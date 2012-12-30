package nmt;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Sends data to the server
 * and requests data from the server
 */
public class NMTCSocket{
   
   Socket s;
   ObjectOutputStream out;
   ObjectInputStream in;
   
   public NMTCSocket(InetAddress ip, int p){
    try{
      s=new Socket(ip, p);
      out=new ObjectOutputStream(s.getOutputStream());
      in=new ObjectInputStream(s.getInputStream());
      System.out.println("Connection Created");
    }
    catch(Exception e){
        e.printStackTrace();
    }
   }
   
   public int numTeams(){
      try{
         out.writeObject("numTeams");
         return ((Integer)in.readObject()).intValue();
      }
      catch(Exception e){
         e.printStackTrace();
         return 0;
      }
   }
   
   public int[] getTeamsFromSchools(String school){
      try{
         out.writeObject("getTeamsFromSchools");
         out.writeObject(school);
         int[] i=new int[((Integer)in.readObject()).intValue()];
         for(int x=0; x<i.length; x++)
            i[x]=((Integer)in.readObject()).intValue();
         return i;
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   public boolean removeTeam(int teamNum){
    try{
       out.writeObject("removeTeam");
       out.writeObject(new Integer(teamNum));
       return ((Boolean)in.readObject()).booleanValue();
    }
    catch(Exception e){
      e.printStackTrace();
      return false;
    }
   }
   
   public boolean containsTeamNum(char c, int tN){
    try{
      out.writeObject("containsTeamNum");
      out.writeObject(new Character(c));
      out.writeObject(new Integer(tN));
      return ((Boolean)in.readObject()).booleanValue();
    }
    catch(Exception e){
      e.printStackTrace();
      return false;
    }
   }
   
   public void addTeam(Team t){
    try{
      out.writeObject("addTeam");
      out.writeObject(t);
    }
    catch(Exception e){
      e.printStackTrace();
      return;
    }
   }
   
   public Team getTeam(int tN){
    try{
      out.writeObject("getTeam");
      out.writeObject(new Integer(tN));
      return (Team)in.readObject();
    }
    catch(Exception e){
      e.printStackTrace();
      return null;
    }
   }
   
   public ArrayList<Integer> getListTeamNum(char division){
      try{
         out.writeObject("getListTeamNum");
         out.writeObject(new Character(division));
         return (ArrayList<Integer>)in.readObject();
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
    public ArrayList<Integer> getTeamsNoScore() {
      try{
         out.writeObject("getTeamsNoScore");
         return (ArrayList<Integer>)in.readObject();
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   /**
    *
    * returns the school of the team 
    * with number teamNum
    */
   public String getSchool(int teamNum){
      try{
         out.writeObject("getSchool");
         out.writeObject(new Integer(teamNum));
         return (String)in.readObject();
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   
   /**
    *
    * returns the status of each team's data being entered
    * for all complete
    */
   public ArrayList<Integer> getAllComplete(){
      try{
         out.writeObject("getAllComplete");
         return (ArrayList<Integer>)in.readObject();
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   /**
    *
    * returns the status of each team's data being entered
    * for GL complete
    */
   public ArrayList<Integer> getGLComplete(){
      try{
         out.writeObject("getGLComplete");
         return (ArrayList<Integer>)in.readObject();
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   /**
    *
    * returns the status of each team's data being entered
    * for ML complete
    */
   public ArrayList<Integer> getMLComplete(){
      try{
         out.writeObject("getMLComplete");
         return (ArrayList<Integer>)in.readObject();
      }
      catch(Exception e){
         e.printStackTrace();
         return null;
      }
   }
   
   /**
    *  Close the connection to the server.
    *  Call this when the client window exits.
    */
   public void closeConnection() {
      try {
         s.close();
      }
      catch(IOException e) { 
         System.out.println("Could not close connection from this client.");
      }
   }

}