package com.mclaure.wordcounter;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.regex.*;
import java.util.stream.*;
 
public class WordCount 
{
    String SOURCE_FILE_NAME;
    String TARGET_FILE_NAME; 
    String HEADER = "Rank  Word                      Frequency\n"; 
    String HEADER_LINE = "====  ====                      =========\n";

    public WordCount(String sourceFileName)
    {
        String timeStamp = getCurrentTimeStamp();
        SOURCE_FILE_NAME = sourceFileName;
        TARGET_FILE_NAME = "WordCount_" + timeStamp + ".txt";
    }

    public String getCurrentTimeStamp() {
       String timeStamp = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date());
       return timeStamp.replace(" ","T");
    }
    
    private String GetFileContent() throws IOException
    {
        Path path = Paths.get(SOURCE_FILE_NAME);
        String text = "";

        if(Files.exists(path) && Files.isReadable(path))
        {
            byte[] bytes = Files.readAllBytes(path);
            text = new String(bytes);
        }
        else
        {
            System.out.println("The file ''" + SOURCE_FILE_NAME + "'' doesn't exists or is not accesible");
        }

        return text.toLowerCase();
    }

    private Map<String, Integer> GetFrecuencies(String content)
    {
        Map<String, Integer> frequencies = new HashMap<>();
        Pattern pattern = Pattern.compile("\\p{javaLowerCase}+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) 
        {
            String word = matcher.group();
            Integer current = frequencies.getOrDefault(word, 0);
            frequencies.put(word, current + 1);
        }

        return frequencies;
    }

    private List<Map.Entry<String, Integer>> GetWordCounts(Map<String, Integer> frequencies)
    {
        List<Map.Entry<String, Integer>> entries = frequencies.entrySet()
            .stream()
            .sorted((i1, i2) -> Integer.compare(i2.getValue(), i1.getValue()))
            .collect(Collectors.toList());
        
        return entries;
    }

    private void SaveToFile(List<Map.Entry<String, Integer>> wordCounts)
    {
        int rank = 1;
        Path path = Paths.get(TARGET_FILE_NAME);

        try(BufferedWriter writer = Files.newBufferedWriter(path)) 
        {
            writer.write(HEADER);
            writer.write(HEADER_LINE);            

            for (Map.Entry<String, Integer> entry : wordCounts) 
            {
                String line = String.format("%5d    %-20s    %5d\n", rank++, entry.getKey(), entry.getValue());
                writer.write(line);
            }
        }
        catch(Exception e)
        {
            System.out.println("I/O Exception: " + e.toString());
        }
    }

    public void CountWords()
    {
        String content = "";

        try 
        {
            content = GetFileContent();
        }
        catch(Exception e)
        {
            System.out.println("I/O Exception: " + e.toString());
        }
        
        if(content.length() > 0)
        {
            Map<String, Integer> frequences = GetFrecuencies(content);
            List<Map.Entry<String, Integer>> wordCounts = GetWordCounts(frequences);
            SaveToFile(wordCounts);  
        }
        else
        {
            System.out.println("File is missing or Empty"); 
        }
    }
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            String fileName = args[0];
            WordCount counter = new WordCount(fileName);
            counter.CountWords();
        }
        else
        {
            System.out.println("Error: [filePath] parameter is missing");
        }
    }
}
