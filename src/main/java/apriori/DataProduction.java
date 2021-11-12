package apriori;

import java.io.*;
import java.util.ArrayList;

public class DataProduction {
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }
    public static void main(String[] args) {
        String s = readFileContent("src/main/resources/list.txt");
        String[] list = s.split("、");
        int list_length = list.length;
        for (int i=0;i<100000;i++){
            for (int j=0;j<list_length;j++){
                int temp = (int)(Math.random()*j);
            }
        }
    }
}
