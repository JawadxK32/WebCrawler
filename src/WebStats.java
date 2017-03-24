/*
Name:   Farzad Vafaeinezhad and Jawad Khudadad
Course: CPS506, Winter 2016, Assignment #1
Due:    2016.02.11 23:59
Credit: This is entirely my own work.
*/

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebStats implements Runnable{
    
    static List<String> urlList = new ArrayList<String>();
    static HashMap<String, HashMap<String,Integer>> mapHashMap = new HashMap<>();
    static int maxPath=3;
    static int maxPages=10;
    static int numPaths=0;
    static int numPages=1;
    
    //constructior for new threads
    String myUrl;
    WebStats(String url, int path){
        myUrl=url;
        numPaths=path;
        
    }
    
    public static void main(String[] args) {
    
    //Arguments will be assigned here, if not assigned, defaults of 10 and 3 will be used.       
    if (args.length == 5 && args[0].equals("-pages") && args[2].equals("-path")){

        try{
            maxPages = Integer.parseInt(args[1]);
            } 
        catch (NumberFormatException e) {
            System.out.println("Improper max pages, will use 10 as default.");
        }
        
        try{
            maxPath = Integer.parseInt(args[3]);
        }
        catch (NumberFormatException e) {
            System.out.println("Improper maxPath, will use 3 as default.");
        }
        
        if(numPages<maxPages-1){
                        WebStats worker = new WebStats(args[4],0);   
                        Thread thread = new Thread(worker);  
                        thread.start(); 
                        }
            
        
    }
    else{System.out.println("Wrong arguments, use format: java WebStats -pages 20 -path 2 someURL");}
    
    
    }
    
    //This is the threaded call, it will go ahead and call the parse method with the new url and path number
    public void run()   
    {  
        numPages++;
        System.out.println("\nNUMBER OF LINKS ON PAGE: " + myUrl + " IS: " + parse(myUrl,numPaths) + " AND is on PATH:" + numPaths);
        HashMap<String, Integer> map = new HashMap<>();
        map = mapHashMap.get(myUrl);
        for (HashMap.Entry<String,Integer> entry : map.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
        }
      
    }  
    
    //This method does all the work, it takes in the HTML page, finds all the tags and new urls as well.
    public static int parse(String website, int pathnum){
        try {           //connection
			URL url = new URL(website);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); 
                        urlConnection.addRequestProperty("User-Agent", "Mozilla/4.76"); 
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();
                        
                                            
                        //TAG FINDING
                        Pattern x = Pattern.compile("< ?([A-Za-z]+)"); 
                        Matcher z = x.matcher(result);
                        HashMap<String, Integer> map = new HashMap<>();
                        while (z.find()) {
                            if(!map.containsKey(z.group(1))){
                                map.put(z.group(1),1);
                            }
                            else{
                              map.put(z.group(1),map.get(z.group(1))+1);
                            }
                        }
                        if (map.size()==0) throw new IOException();
                        
                        //putting url and all tags in side
                        mapHashMap.put(website,map);
                        
                        //PATTERN FOR URL FINDING
                        Pattern p = Pattern.compile("<a href=\"(http.*?)\"");
                        Matcher m = p.matcher(result);
                        //finding all the urls and putting into an arraylist
                        while (m.find()) {
                        if(!urlList.contains(m.group(1))){    
                        urlList.add(m.group(1)); // this variable should contain the link URL
                        if(numPages<maxPages&&pathnum<=maxPath){
                        WebStats worker = new WebStats(m.group(1),pathnum+1);   
                        Thread thread = new Thread(worker);  
                        thread.start(); 
                        }
                        }
                        }
                        if (urlList.size()==0) throw new IOException();
                        
                        return urlList.size();
                        
		} catch (MalformedURLException e) {
                    System.out.println("\nACCESS DENIED! URL: " + website + " CANNOT BE ACCESSED :(");
		} catch (IOException e) {
                    System.out.println("\nACCESS DENIED! URL: " + website + " CANNOT BE ACCESSED :(");
                        
		}
        
         return 0;       
        }

}
