// A Java program for a Server
import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

public class p2p
{
    private int[] ports = new int[2];
    private String[] neighbors = new String[5];
    private DataInputStream in       =  null;

    public static void main(String args[]) throws IOException
    {
        p2p server1 = new p2p();
        server1.getports();
        server1.getNeighbors();

        RunServer server = new RunServer(server1.ports[0], server1.ports[1]);
        	server.start();
      while(true){
        RunClient client1 = new RunClient(server1.neighbors[0], server1.ports[0], server1.ports[1]);
        RunClient client2 = new RunClient(server1.neighbors[1], server1.ports[0], server1.ports[1]);
        RunClient client3 = new RunClient(server1.neighbors[2], server1.ports[0], server1.ports[1]);
        RunClient client4 = new RunClient(server1.neighbors[3], server1.ports[0], server1.ports[1]);
        RunClient client5 = new RunClient(server1.neighbors[4], server1.ports[0], server1.ports[1]);

        System.out.println("enter command");
        server1.in = new DataInputStream(System.in);


        String line = server1.in.readLine();

        //connect funtion
          if (line.indexOf("connect")==0){
            System.out.println("clients connecting");
            try{
            client1.start();
            client2.start();
            client3.start();
            client4.start();
            client5.start();
            System.out.println("clients connected");


          while(true){
            System.out.println("------------------------------------");
            line = server1.in.readLine();
            if(!(line.equals("leave") || line.equals("exit"))){
            client1.getReq(line);
            client2.getReq(line);
            client3.getReq(line);
            client4.getReq(line);
            client5.getReq(line);
            }
            else{
              break;
            }
          }
          }
          catch(IOException e){

          }
        }
          //leave function
          if(line.equals("leave")){
            System.out.println("leaving");
            client1.getReq("leave");
            client2.getReq("leave");
            client3.getReq("leave");
            client4.getReq("leave");
            client5.getReq("leave");
            continue;
          }
          //exit function
          if(line.equals("exit")){
            System.out.println("exiting");
            System.exit(0);

          }

          System.out.println("command not found");

      }

    }
    //get port numbers for the servers
    public void getports(){
        BufferedReader reader;
      try{
        reader = new BufferedReader(new FileReader("config_peer.txt"));
        String line = reader.readLine();
        int i = 0;
        while(line != null){
          int lineInt = Integer.parseInt(line);
          ports[i] = lineInt;
          line = reader.readLine();
          i++;
        }

      }
      catch(IOException e){

        }

    }
    //get IP address of neighbors
    public void getNeighbors(){
      BufferedReader reader;
      try{
        reader = new BufferedReader(new FileReader("config_neighbors.txt"));
        String line = reader.readLine();
        int i = 0;
        while(line != null){
          neighbors[i] = line;
          line = reader.readLine();
          i++;
        }
        reader.close();
      }

      catch(IOException e){

        }

      }


}

//starts server
class RunServer extends Thread{
	//initialize socket and input stream
	private Socket          socket1   = null;
	private ServerSocket    server1   = null;
  private Socket          socket2   = null;
  private ServerSocket    server2   = null;
	private DataInputStream in       =  null;
  private DataOutputStream out     = null;
	private int portconn1;
  private int portconn2;

	// constructor with port
	public RunServer(int port1 , int port2){
			portconn1 = port1;
      portconn2 = port2;
	}

	public void run(){
		try
		{
				server1 = new ServerSocket(portconn1);
				System.out.println(" Communication Server started");
        server2 = new ServerSocket(portconn2);
        System.out.println("Transmission Server Started");
      while(true){
				System.out.println("Waiting for a client ...--- Communication");
        System.out.println("Waiting for a client... ---Transmission");

				socket1 = server1.accept();
				System.out.println("Client accepted --- Communication client");


        socket2 = server2.accept();
        System.out.println("Client accepted --- Transmission client");
				// takes input from the client socket
			  RunServerClient temp = new RunServerClient(socket1, socket2);
        temp.start();
      }
		}//try ends here
		catch(IOException i)
		{
				System.out.println("Server" + i);
		}//catch ends here
	}


  public void getFile(String name) throws IOException{
    try{
    FileInputStream fr = new FileInputStream(name);
    out.writeUTF("OK");
    byte b[] = new byte[2002];
    fr.read(b, 0 , b.length);
    OutputStream os = socket2.getOutputStream();
    os.write(b,0,b.length);
      }
    catch(IOException i)
        {
        out.writeUTF("file does not exist");
      }
  }

  public void closeConn() throws IOException{
    socket2.close();
    socket1.close();
  }
}

//establishes a client to the specified server
class RunClient extends Thread{
  private Socket socket1            = null;
  private Socket socket2           = null;
  private DataInputStream  in   = null;
  private DataOutputStream out     = null;
	private String addr;
	private int portconn1;
  private int portconn2;
  // constructor to put ip address and port
  public RunClient(String address, int port1, int port2){
  		addr = address;
  		portconn1 = port1;
      portconn2 = port2;
	}

  public void run()
  {
      // establish a connection
      System.out.println("Waiting for Server");
      try
      {
          socket1 = new Socket(addr, portconn1);
          System.out.println("Connected -- communication");
          socket2 = new Socket(addr, portconn2);
          System.out.println("Connected -- transmission");

          out    = new DataOutputStream(socket1.getOutputStream());
      }
      catch(UnknownHostException u)
      {
      }
      catch(IOException i)
      {

      }


  }
    //function to recieve file
  public void recievefile(String filename) throws IOException{
    byte b[] = new byte[20002];
    InputStream is = socket2.getInputStream();
    String reqdfile = "obtained/" + filename;
    FileOutputStream fr = new FileOutputStream(reqdfile);
    is.read(b,0,b.length);
    fr.write(b, 0 , b.length);

  }
    //function to send request to server
  public void getReq( String a) throws IOException {

    try{
    in = new DataInputStream(new BufferedInputStream(socket1.getInputStream()));
    String line = "";
    String line2 ="";


       line = new String(a);
        if(line.indexOf("get")== 0){
          System.out.println(line.substring(4));
          out.writeUTF(line);
          try {
          Thread.sleep(1000);
              } catch (InterruptedException e) {

              }
          line2 = in.readUTF();
           System.out.println("Server:" + line2);
              //if file exists recieve file
          if(line2.equals("OK")){
            System.out.println("recieved file");
          recievefile(line.substring(4));
              }
              //respond if file does not exist
          else{
            System.out.println("file unavailable");
          }
          }
          //execute leave function
          else if(line.indexOf("leave")==0){
            try
            {
                out.writeUTF("leave");
                socket1.close();
                socket2.close();
            }
            catch(IOException i)
            {
                System.out.println("socket connection was not established");
            }
          }
          //identify invalid command
        else{
          System.out.println("invalid command");
          out.writeUTF(line);
        }
      }//try ends

      catch(IOException e){

      }

    }


}



//creates new server thread every time a connection is accepted.
class RunServerClient extends Thread{
	//initialize socket and input stream
	private Socket          socket1   = null;
	private ServerSocket    server1   = null;
  private Socket          socket2   = null;
  private ServerSocket    server2   = null;
	private DataInputStream in       =  null;
  private DataOutputStream out     = null;
	private int portconn1;
  private int portconn2;

	// constructor with port
	public RunServerClient(Socket a, Socket b){
			socket1 = a;
      socket2 = b;
	}

	public void run(){
		try
		{

				// takes input from the client socket
				in = new DataInputStream(new BufferedInputStream(socket1.getInputStream()));
        out= new DataOutputStream(socket1.getOutputStream());

				String line = "";

				// reads message from client until "Over" is sent
				while (!line.equals("leave"))
				{
						try
						{

								line = in.readUTF();
								if(line.indexOf("get")==0){
                  String req = line.substring(4);
                    System.out.println("server:" + req);
                      getFile(req);
                }
                else{
                  out.writeUTF("Server: Could not understand request");
                }

						}
						catch(IOException i)
						{
								System.out.println(i);
						}
				}
				System.out.println("Closing connection");

				// close connection
        socket2.close();
				socket1.close();
				in.close();
		}//try ends here
		catch(IOException i)
		{
				System.out.println("lol");
		}//catch ends here
	}


  public void getFile(String name) throws IOException{
    try{
    FileInputStream fr = new FileInputStream("shared/" + name);
    out.writeUTF("OK");
    byte b[] = new byte[2002];
    fr.read(b, 0 , b.length);
    OutputStream os = socket2.getOutputStream();
    os.write(b,0,b.length);
      }
    catch(IOException i)
        {
        out.writeUTF("file does not exist");
      }
  }


}
