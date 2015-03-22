package com.oracle.competition.tookitaki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

import javax.swing.JOptionPane;

public class MainTookitakiSCC {
	// Change this file name in case you want to change file
	static File file = new File("assets/input.txt");

	// Stack for keeping track on vertex under process
	static Stack<Integer> stack = new Stack<Integer>();
	// For faster access hashmap
	static HashMap<Integer, GraphData> graph = new HashMap<Integer, GraphData>();
	static int count = 0;

	// Variables for extracting data from file
	static String sData = null;
	static String[] sVertex = null;
	static Integer iVertexSource = null;
	static Integer iVertexDestination = null;
	static Integer SCC = 0;
	private static final long MEGABYTE = 1024L * 1024L;
	final static int INFINITE = -9999;

	// convert bytes to mb
	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	// Design of each vertex in graph
	static class GraphData {
		// Adjacency list of each vertex
		List<Integer> lstAdj = new ArrayList<Integer>();
		Integer index = INFINITE;
		Integer low = INFINITE;
		// Whether vertex is part of Stack or not
		Boolean bStackMember = false;

	}

	public static void main(String[] args) throws IOException {

		long heapSize = bytesToMegabytes(Runtime.getRuntime().totalMemory());
		// get Start time for program
		final long startTime = System.currentTimeMillis();
		// Print the jvm heap size.
		System.out.println("Heap Size = " + heapSize + "MB");

		// For faster read from file and buffer ahead data
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));

			while ((sData = reader.readLine()) != null) {
				// For ignoring black lines and space before and after
				sData = sData.trim();
				if (!(sData.equalsIgnoreCase(""))) {
					sVertex = sData.split(" ");
					iVertexSource = Integer.parseInt(sVertex[0]);
					iVertexDestination = Integer.parseInt(sVertex[1]);
					if (!(graph.containsKey(iVertexSource))) {
						graph.put(iVertexSource, new GraphData());
					}
					if (!(graph.containsKey(iVertexDestination))) {
						graph.put(iVertexDestination, new GraphData());
					}
					graph.get(iVertexSource).lstAdj.add(iVertexDestination);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}

		SCCLead();
		System.out
				.println("-------------------------------------------------\nGraph details :");
		// Print Complete graph with adjacency list
		for (Entry<Integer, GraphData> entry : graph.entrySet()) {

			System.out.println(entry.getKey() + "->"
					+ entry.getValue().lstAdj.toString());
		}
		System.out.println("-------------------------------------------------");
		System.out.println("Strongly Connected Components = " + SCC);
		final long endTime = System.currentTimeMillis();
		// Get the Java runtime
		Runtime runtime = Runtime.getRuntime();
		// Run the garbage collector
		runtime.gc();
		// Calculate the used memory
		long memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Used memory is bytes: " + memory);
		System.out.println("Used memory is megabytes: "
				+ bytesToMegabytes(memory));
		System.out.println("Total execution time(Milli Seconds): "
				+ (endTime - startTime));
		// Show swing dialogue box
		JOptionPane.showMessageDialog(null, "Strongly Connected Components: "
				+ SCC + "\n(For more details check Console)"
				+ "\nTime taken in MilliSeconds: " + (endTime - startTime)
				+ "\nUsed memory in bytes: " + memory,
				"Strongly Connected Components",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// Function for initiating from each lead of components
	public static void SCCLead() {
		for (Entry<Integer, GraphData> entry : graph.entrySet()) {

			if (entry.getValue().index == INFINITE) {

				SCCInside(entry.getKey());
			}
		}
	}

	// Function for performing DFS with each lead and find out component
	public static void SCCInside(Integer iKey) {
		graph.get(iKey).index = graph.get(iKey).low = ++count;
		stack.push(iKey);
		graph.get(iKey).bStackMember = true;
		for (Integer iAdj : graph.get(iKey).lstAdj) {
			if (graph.get(iAdj).index == INFINITE) {
				SCCInside(iAdj);
				graph.get(iKey).low = min(graph.get(iKey).low,
						graph.get(iAdj).low);
			} else if (graph.get(iAdj).bStackMember == true) {
				graph.get(iKey).low = min(graph.get(iKey).low,
						graph.get(iAdj).index);
			}
		}
		if (graph.get(iKey).index == graph.get(iKey).low) {
			System.out.print("Component No " + (SCC + 1) + " :");
			Integer iPop = stack.pop();
			for (; iPop != iKey; iPop = stack.pop()) {
				System.out.print("-" + iPop);
				graph.get(iPop).bStackMember = false;

			}
			graph.get(iPop).bStackMember = false;
			System.out.println("-" + iPop);
			SCC++;

		}
	}

	// Function for finding minimum value out of 2 values
	public static Integer min(Integer iValue1, Integer iValue2) {
		if (iValue1 > iValue2) {
			return iValue2;
		} else {
			return iValue1;
		}
	}
}
