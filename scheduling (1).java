package com.amazon.amabot.impl;

import java.io.IOException;

import com.amazon.skeleton.schedule.ScheduleRequest;
import com.amazon.skeleton.schedule.ScheduleRequestInputStream;
import com.amazon.skeleton.schedule.ScheduleRequestWriter;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.HashMap;
import java.util.Comparator;

/**
 * FILL IN A DESCRIPTION OF YOUR ALGORITHM HERE
 * 
 * Description:
 * The General idea is to build a heap(min-heap) with comparing values to be the start time of requests.
 * I extract top of heap as the main iteration process until there is no more element in the heap; 
 * for each location, I used a tuple(storing 3 values) to keep track of 3 pieces of end time. 
 * Tuple for each location is initialized to be (0,0,0). When top of heap belongs to a specific location,
 * the tuple for that location will do a check to see if the minimum value in tuple is larger than the 
 * request's start time(from top of heap). If the minimum value is larger, this means current request is 
 * invalid and we can add the request to Schedule-Rejection list; If it's smaller, this means current request  
 * can update the minimum value of tuple to be the current request's end time, and add the request to 
 * Schedule-Output list. 
 * To judge if the same content appears multiple times in an overlapped interval, I use a hashmap of hashmap to
 * realize this mechanism. here is an illustration: HashMap(LocationId, HashMap(contentID, end Time)). First 
 * HashMap has key locationID, second HashMap stores most recent contentID's end time. Given a request for
 * specific location, if HashMap shows that last time's contentID(same as request's contentID)'s end time is 
 * bigger than current request's start time, this means current request will have conflict with previous 'self'.
 * It should be added to Schedule-Rejection list.
 * 
 * 
 * @author: xihao zhu
 */
public class Scheduling {

	/**
	 * This is an old and basic version which works at 2:00 pm
	 */
	public static void schedule2(ScheduleRequestInputStream scheduleRequestsIn,
			ScheduleRequestWriter scheduleRequestsOut,
			ScheduleRequestWriter scheduleRequestsReject) throws IOException {
		
		
		HashMap<String, ArrayList<Integer>> locationMap= new HashMap<String, ArrayList<Integer>>();
		HashMap<String, HashMap<String, Integer>> conflictContentMap=new HashMap<String,  HashMap<String, Integer>>();
		Queue<ScheduleRequest> heap=new PriorityQueue<>(3,requestComparator);
		ArrayList<ScheduleRequest> removeList=new ArrayList<ScheduleRequest>();
		ArrayList<ScheduleRequest> scheduleList=new ArrayList<ScheduleRequest>();
		ScheduleRequest rqst;
		
		
		while(scheduleRequestsIn.hasNext()){
			rqst=scheduleRequestsIn.next();
//			System.out.println(rqst.contentId+ "   "+ rqst.locationId);
			if(locationMap.get(rqst.locationId)==null){
				ArrayList<Integer> list=new ArrayList<Integer>(triplet());
				locationMap.put(rqst.locationId, list);
				conflictContentMap.put(rqst.locationId, new HashMap<String, Integer>());
			}
			heap.offer(rqst);
		}
		
		
		while(heap.size()>0){
			rqst=heap.poll();
			if(locationMap.get(rqst.locationId)==null){
				System.out.println("locationMap error, no entry named that location");
				break;
			}
			
			
				
			ArrayList<Integer> list=locationMap.get(rqst.locationId);
			int minIdx=TripletMin(list);
			if(list.get(minIdx)>rqst.startTime){
				removeList.add(rqst);
			}
			else{
				if(conflictContentMap.get(rqst.locationId).get(rqst.contentId)==null){
					conflictContentMap.get(rqst.locationId).put(rqst.contentId, rqst.endTime);
				}
				else{
					int endTime=conflictContentMap.get(rqst.locationId).get(rqst.contentId);
					if(endTime>rqst.startTime){
						removeList.add(rqst);
						continue;
					}
					else{
						conflictContentMap.get(rqst.locationId).put(rqst.contentId, rqst.endTime);
					}
				}
				list.set(minIdx, rqst.endTime);
				locationMap.put(rqst.locationId, list);
				scheduleList.add(rqst);
			}
		}
		
		
//		printlist(scheduleList,0, 100);
//		System.out.println(scheduleList.size()+"   "+removeList.size()+"  "+locationMap.size());
		
		write(scheduleList, scheduleRequestsOut);
		write(removeList, scheduleRequestsReject);
		
	}
	
	
	/**
	 * This is a version which has documentations and split implementations into functions. works at 3:00pm
	 */
	public static void schedule(ScheduleRequestInputStream scheduleRequestsIn,
			ScheduleRequestWriter scheduleRequestsOut,
			ScheduleRequestWriter scheduleRequestsReject) throws IOException {
		
		HashMap<String, ArrayList<Integer>> locationMap= new HashMap<String, ArrayList<Integer>>();
		HashMap<String, HashMap<String, Integer>> conflictContentMap=new HashMap<String,  HashMap<String, Integer>>();
		Queue<ScheduleRequest> heap=new PriorityQueue<>(3,requestComparator);
		ArrayList<ScheduleRequest> removeList=new ArrayList<ScheduleRequest>();
		ArrayList<ScheduleRequest> scheduleList=new ArrayList<ScheduleRequest>();
		
		initialize( scheduleRequestsIn, locationMap, conflictContentMap, heap);
		mainImplementation( scheduleRequestsIn, locationMap,conflictContentMap, heap,removeList,scheduleList);
		write(scheduleList, scheduleRequestsOut);
		write(removeList, scheduleRequestsReject);
	}
	
	
	/**
	 * build comparator for ScheduleRequest
	 */	
	public static Comparator<ScheduleRequest> requestComparator = new Comparator<ScheduleRequest>(){

		@Override
		public int compare(ScheduleRequest lhs, ScheduleRequest rhs) {
			if (lhs.startTime > rhs.startTime) return +1;
	        if (lhs.startTime==rhs.startTime) return 0;
	        return -1;
        }
	};
	
	
	
	/**
	 * Get minimum value's index in a tuple 
	 * 
	 */	
	public static int TripletMin(ArrayList<Integer> tuple){
		int min=0;
		if(tuple.size()!=3) System.out.println("tuple size wrong here");
		for(int i=0; i<tuple.size();i++){
			min=tuple.get(min)<tuple.get(i)?min:i;
		}
		return min;
	}
	
	
	/**
	 * initialize tuples 
	 * 
	 */	
	public static ArrayList<Integer> triplet(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		list.add(0);
		list.add(0);
		list.add(0);
		return list;
	}
	
	
	
	/**
	 * personal check for arraylist
	 * 
	 */	
	public static void printlist(ArrayList<ScheduleRequest> list, int start, int end){
		ScheduleRequest rqst;
		String location=list.get(start).locationId;
		for(int i=start;i<=end;i++){
			rqst=list.get(i);
			if(rqst.locationId.equals(location))
			System.out.println(rqst.locationId+"  "+rqst.contentId + "   "+ rqst.startTime+"    "+rqst.endTime);
		}
	}
	
	/**
	 * Write  arrayList to destination file
	 * 
	 */	
	public static void write(ArrayList<ScheduleRequest> List,ScheduleRequestWriter writer) throws IOException{
		for(ScheduleRequest rqst: List){
			writer.writeScheduleRequest(rqst);
		}
		
	}
	
	/**
	 * Go through input file and initialize data structures
	 * 
	 */
	public static void initialize(ScheduleRequestInputStream scheduleRequestsIn, 
			HashMap<String, ArrayList<Integer>> locationMap,
			HashMap<String, HashMap<String, Integer>> conflictContentMap,
			Queue<ScheduleRequest> heap) throws IOException {
		
		ScheduleRequest rqst;
		while(scheduleRequestsIn.hasNext()){
			rqst=scheduleRequestsIn.next();
//			System.out.println(rqst.contentId+ "   "+ rqst.locationId);
			if(locationMap.get(rqst.locationId)==null){
				ArrayList<Integer> list=new ArrayList<Integer>(triplet());
				locationMap.put(rqst.locationId, list);
				conflictContentMap.put(rqst.locationId, new HashMap<String, Integer>());
			}
			heap.offer(rqst);
		}
		
	}
	
	
	/**
	 * This is the main implementation for scheduling task, it extract min of heap one at a time, 
	 * and do the checking for any violation of rules, until the heap is empty.  
	 * After this process, scheduleList will be Schedule-Output result and 
	 * removeList will be Schedule-Rejection result.  
	 * 
	 */
		public static void mainImplementation(ScheduleRequestInputStream scheduleRequestsIn, 
				HashMap<String, ArrayList<Integer>> locationMap,
				HashMap<String, HashMap<String, Integer>> conflictContentMap,
				Queue<ScheduleRequest> heap,
				ArrayList<ScheduleRequest> removeList,
				ArrayList<ScheduleRequest> scheduleList) throws IOException {
			

			ScheduleRequest rqst;
			
			while(heap.size()>0){
				rqst=heap.poll();
				if(locationMap.get(rqst.locationId)==null){
					System.out.println("locationMap error, no entry named that location");
					break;
				}
				ArrayList<Integer> list=locationMap.get(rqst.locationId);
				int minIdx=TripletMin(list);
				if(list.get(minIdx)>rqst.startTime){
					removeList.add(rqst);
				}
				else{
					if(conflictContentMap.get(rqst.locationId).get(rqst.contentId)==null){
						conflictContentMap.get(rqst.locationId).put(rqst.contentId, rqst.endTime);
					}
					else{
						int endTime=conflictContentMap.get(rqst.locationId).get(rqst.contentId);
						if(endTime>rqst.startTime){
							removeList.add(rqst);
							continue;
						}
						else{
							conflictContentMap.get(rqst.locationId).put(rqst.contentId, rqst.endTime);
						}
					}
					list.set(minIdx, rqst.endTime);
					locationMap.put(rqst.locationId, list);
					scheduleList.add(rqst);
				}
			}
			
		}
	
}
	