package amzon;

import genome.Region;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class ScheduingAlgoJuhi {

	  public static void main(String[] args) 
	  {
	    ArrayList<Request> requests = createReqArray();
	    ArrayList<Request> acceptedReq = new ArrayList<Request>();
	    ArrayList<Request> rejectedReq = new ArrayList<Request>();
	    HashMap<Integer, HashMap<String, Set<Integer>>> acceptedMap = new HashMap<Integer, HashMap<String, Set<Integer>>>();
	    
	    // create a empty map for location and content set
	    HashMap<String, Set<Integer>> contentsAtLocation = new HashMap<String, Set<Integer>>();
	    Set<Integer> topContent = new HashSet<Integer>();
	    Set<Integer> botContent = new HashSet<Integer>();
	    	    
	    for (Request req : requests) 	
	    {
	    	
	    	int start = req.getStart();// 10,12,15,13,12,25,31
	        int end = req.getEnd();//15,18,20,18,16,30,35
	        String location = req.getLocation();//top,top,top,top,top,top,top
	        int contentId = req.getContentId();//10,2,3,6,2,25,25
	        boolean isOkay = true;
	        for (int i = start; i <= end; i++) 
	        {
	        	if (acceptedMap.containsKey(i)||contentsAtLocation.containsKey(location)) 
	    	    {
	        		
		        		Set<Integer> locSet =contentsAtLocation.get(location);
			
		      			if(locSet.contains(contentId)||locSet.size()>=3)
		      			{
		      				isOkay=false;		      			
		      			}
		      	  	
		        }
	        }
	        if (isOkay) 
	        {
	    	   for (int i = start; i <= end; i++)
	    	   {
	    		  	// if entry for time is present
	    		  	if (acceptedMap.containsKey(i)&&contentsAtLocation.containsKey(location)) 
	    		  	{
	    		  		Set<Integer> contents = new HashSet<Integer>();
					             
					    contents = contentsAtLocation.get(location);
	           
					   // contents.add(contentId);
					    // No need to add 
					    contentsAtLocation.put(location, contents);
									   
					    acceptedMap.put(i, contentsAtLocation);
		    		}   
	    		  	else  
	    		  	{		       			
		            	if(location=="top")
		            	{
		            		topContent.add(contentId);
		            		contentsAtLocation.put(location, topContent);
		            	}
		            	else if(location=="bot")
		            	{
		            		botContent.add(contentId);// botContent=[10]
		            		contentsAtLocation.put(location, botContent);// contentsAtLocation=bot->10
		            	}	              
		              acceptedMap.put(i, contentsAtLocation);// {10->bot->10,
	    		  	}	         
	         }	       
	        acceptedReq.add(req);// write to file
	      } 
	      else 
	      {
	        rejectedReq.add(req);
	      }
	    }
	    System.out.println("Rejected List");
		printList(rejectedReq);
		System.out.println("***************");
		System.out.println("Accepted List");
		printList(acceptedReq);
	  }

	  public static void writeToFile(File fileName, Request req) 
	  {

	  }
	  public static void printList(ArrayList<Request> list)
	  {
		  System.out.println("Loc"+"   "+"Con"+"  "+"Sta"+"  "+"End");
		  for (Request req : list)
		  {
			  System.out.println(req.getLocation()+"   "+req.getContentId()+"    "+req.getStart()+"   "+req.getEnd());
		  }
	  }
	  
	  /* File Reading 
	   *	File fileName=new File("/Users/Akki/Documents/input.txt");
		// Scan the values into a new scanner
		Scanner input = null;
		boolean exists=true;
		try {
			input = new Scanner(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			exists=false;
			System.out.println("Please Enter the path of the Input file for fileName variable");
			
			//e.printStackTrace();
		}
		
		String location=null, String contentId=null, int start=0;int end=0;Region region;
		// Create a new ArrayList with Region type
		List<Region> regionList=new ArrayList<Region>();
		// top c0001 10 12
		if(exists==true){
		while(input.hasNextLine())
		{			
			StringTokenizer st = new StringTokenizer(input.nextLine());
			 while (st.hasMoreTokens()) 
			 {
			     
			     location=st.nextToken();
			     contentId=st.nextToken();
			     start=Integer.parseInt(st.nextToken());
			     end=Integer.parseInt(st.nextToken());
			 }
			 // Add each dataset to the list
			region=new Region(start,end, null);
			regionList.add(region);
		}
		// Close the file after reading all the input data
		input.close();		
			
	   */ // Output to the file
	  /*
	   * public static void outputPart1() throws FileNotFoundException{
		//Output Part 1 file
		File file = new File("Path where you want to save the file"); 
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
	}
	   */
	  
	  public static ArrayList<Request> createReqArray() {
		    //10,2,3,6,2,25,25
		    ArrayList<Request> result = new ArrayList<Request>();
		    Request req = new Request();
		    req.setLocation("bot");
		    req.setStart(10);
		    req.setEnd(20);
		    req.setContentId(10);

		    result.add(req);

		    Request req1 = new Request();
		    req1.setLocation("top");
		    req1.setStart(12);
		    req1.setEnd(13);
		    req1.setContentId(88);
		    result.add(req1);

		    Request req2 = new Request();
		    req2.setLocation("bot");
		    req2.setStart(14);
		    req2.setEnd(15);
		    req2.setContentId(10);
		    result.add(req2);
		    
		    Request req3 = new Request();
		    req3.setLocation("top");
		    req3.setStart(10);
		    req3.setEnd(19);
		    req3.setContentId(9);
		    result.add(req3);
		    
		    Request req4 = new Request();
		    req4.setLocation("top");
		    req4.setStart(10);
		    req4.setEnd(11);
		    req4.setContentId(9);
		    result.add(req4);
		    
		   
		    Request req5 = new Request();
		    req5.setLocation("bot");
		    req5.setStart(100);
		    req5.setEnd(111);
		    req5.setContentId(104);
		    result.add(req5);
		    
		    Request req6 = new Request();
		    req6.setLocation("top");
		    req6.setStart(10);
		    req6.setEnd(11);
		    req6.setContentId(6);
		    result.add(req6);
		    
		    Request req7 = new Request();
		    req7.setLocation("top");
		    req7.setStart(10);
		    req7.setEnd(11);
		    req7.setContentId(1);
		    result.add(req7);
		    
		    return result;
		  }
}