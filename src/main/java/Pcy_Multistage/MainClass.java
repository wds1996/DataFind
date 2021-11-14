package Pcy_Multistage;

import java.io.*;
import java.util.*;

public class MainClass {
    public static void main(String[] args) {
        String fileName = "src/main/resources/retail.txt";
        int PCYHashSize = 10000;
        int totalLines = 88162;
        int lineCheck = 0;
        // 使用HashMap和ArrayList结构存储和计算项目的频率v
        HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
        ArrayList<String> items = new ArrayList<String>();
        // ArrayList用于保存频繁项
        ArrayList<String> frequentItems = new ArrayList<String>();
        // 使用HashMap保持由单个频繁项组成的对的计数
        HashMap<String, Integer> pairCount = new HashMap<String, Integer>();
        // 用于保存频繁对的ArrayList
        ArrayList<String> frequentPairs = new ArrayList<String>();

        Scanner input = new Scanner(System.in);
        System.out.print("输入要使用的数据的小数百分比 (E.g. 50% is 0.50): ");
        float chunk = input.nextFloat();
        System.out.print("输入要使用的小数支持百分比 (E.g. 20% is 0.20): ");
        float support = input.nextFloat();
        float realTotalLines = chunk * totalLines;
        float realSupport = support * realTotalLines;
        //Start timer
        Long startTime = System.currentTimeMillis();
        // 首先传递数据以读取和解析项目，同时跟踪其频率
        Map<Integer, Integer> PCYMap = new HashMap<Integer, Integer>();
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int lineCount = 0;
            while ((strLine = br.readLine()) != null && lineCount <= realTotalLines) {
                String[] tokens = strLine.split(" ");
                for (int i = 0; i < tokens.length; i++) {
                    if (itemCount.containsKey(tokens[i]) && items.contains(tokens[i]))
                        itemCount.put(tokens[i], itemCount.get(tokens[i]) + 1);
                    else {
                        itemCount.put(tokens[i], 1);
                        items.add(tokens[i]);
                    }
                }
                lineCount++;
                //PCY
                for (int i = 0; i < tokens.length; i++) {
                    int itemNoI = Integer.parseInt(tokens[i]);
                    for (int j = i + 1; j < tokens.length; j++) {
                        int itemNoJ = Integer.parseInt(tokens[j]);
                        int hashValue = (itemNoI + itemNoJ) % PCYHashSize;
                        if (PCYMap.containsKey(hashValue)) {
                            int currentNum = PCYMap.get(hashValue);
                            currentNum++;
                            PCYMap.put(hashValue, currentNum);
                        } else {
                            PCYMap.put(hashValue, 1);
                        }
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        // 将常用项添加到列表中
        addFrequentItems (items, itemCount, realSupport, frequentItems);
        //PCY位映射
        Map<Integer, Boolean> PCYBitMap = new HashMap<Integer, Boolean>();

        for (int key : PCYMap.keySet()) {
            int count = PCYMap.get(key);
            if (count > realSupport) {
                PCYBitMap.put(key, true);
            } else {
                PCYBitMap.put(key, false);
            }
        }
        Map<Integer, Integer> PCYMap2 = new HashMap<Integer, Integer>();

        // 第二次传递数据以从第1次传递中重新传递频繁对
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int lineCount = 0;
            while ((strLine = br.readLine()) != null && lineCount <= realTotalLines) {
                String[] tokens = strLine.split(" ");
                for (int i = 0; i < tokens.length; i++) {
                    String itemNoI = tokens[i];
                    for (int j = i + 1; j < tokens.length; j++) {
                        String itemNoJ = tokens[j];
                        int hashValue = (Integer.parseInt(itemNoI.trim()) + Integer.parseInt(itemNoJ.trim())) % PCYHashSize;
                        if (frequentItems.contains(itemNoI) && frequentItems.contains(itemNoJ)) {
                            if (PCYBitMap.get(hashValue)) {
                                if (PCYMap2.containsKey(hashValue)) {
                                    int currentNum = PCYMap2.get(hashValue);
                                    currentNum++;
                                    PCYMap2.put(hashValue, currentNum);
                                } else {
                                    PCYMap2.put(hashValue, 1);
                                }
                            }
                        }
                    }

                    lineCount++;
                }
            }
            in.close();
            lineCheck = lineCount;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        //PCY Bit Map 2
        Map<Integer, Boolean> PCYBitMap2 = new HashMap<Integer, Boolean>();
        for (int key : PCYMap2.keySet()) {
            int count = PCYMap2.get(key);
            if (count > realSupport) {
                PCYBitMap2.put(key, true);
            } else {
                PCYBitMap2.put(key, false);
            }
        }
        // Third pass
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            int lineCount = 0;

            while ((strLine = br.readLine()) != null && lineCount <= realTotalLines) {
                String[] tokens = strLine.split(" ");
                for(int i=0; i<tokens.length; i ++) {
                    String itemNoI = tokens[i];
                    for (int j = i + 1; j < tokens.length; j++) {
                        String itemNoJ = tokens[j];
                        int hashValue = (Integer.parseInt(itemNoI.trim()) + Integer.parseInt(itemNoJ.trim()))%PCYHashSize;
                        if(frequentItems.contains(itemNoI) && frequentItems.contains(itemNoJ)){
                            if(PCYBitMap.get(hashValue) && PCYBitMap2.get(hashValue)) {
                                String itemKey = itemNoI + "-" + itemNoJ;
                                if (pairCount.containsKey(itemKey)) {
                                    int currentNum = pairCount.get(itemKey);
                                    currentNum++;
                                    pairCount.put(itemKey, currentNum);
                                } else {
                                    pairCount.put(itemKey, 1);
                                }
                            }
                        }
                    }

                }

                lineCount++;
            }
            in.close();
            lineCheck = lineCount;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        //如果>超过支持且没有，则将对添加到frequentPairs列表中
        for(String key:pairCount.keySet()){
            int count = pairCount.get(key);
            if(count > realSupport){
                frequentPairs.add(key);
            }
        }
        //End timer
        long endTime = System.currentTimeMillis();
        //Calculate runtime
        long runTime = endTime - startTime;
        System.out.println("Runtime: " + runTime);
        System.out.println("realSupport: " + realSupport + " realTotalLines: " + realTotalLines + " LC: " + lineCheck);
        System.out.println("items: " + items.size());
        System.out.println(items);
        System.out.println("F-items: " + frequentItems.size());
        System.out.println(frequentItems);
        System.out.println("pairsCount: " + pairCount.size());
        System.out.println(pairCount);
        System.out.println("F-pairs: " + frequentPairs.size());
        System.out.println(frequentPairs);
    }


    // Add frequent items to frequentItems
    static void addFrequentItems (ArrayList<String> items, HashMap<String, Integer> itemCount, Float realSupport, ArrayList<String> frequentItems) {
        for (int i = 0; i < items.size(); i++) {
            if (itemCount.get(items.get(i)) >= realSupport && !frequentItems.contains(items.get(i)))
                frequentItems.add(items.get(i));
        }
    }


}
