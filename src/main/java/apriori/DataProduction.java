package apriori;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class DataProduction {
    //将文件列表中的数据转换为一个数组
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
    public static void write_data(ArrayList<ArrayList<String>> data) throws IOException {
        //创建字符缓冲输出流对象
        BufferedWriter bw = new BufferedWriter(new FileWriter("src\\main\\resources\\product_data.txt"));
        for (ArrayList<String> array:data) {
            int last = array.size();
            //遍历集合，得到每一个字符串数据
            for(String s : array) {
                //调用字符缓冲输出流对象的方法写数据
                bw.write(s);
                if (s!=array.get(last-1)){
                    bw.write(",");
                }
            }
            bw.newLine();
            bw.flush();
        }
        //释放资源
        bw.close();
    }
    public static void main(String[] args) {
        String s = readFileContent("src/main/resources/list.txt");
        String[] list = s.split("、");
        ArrayList<ArrayList<String>> data= new ArrayList<>();
        for (int i=0;i<1000;i++){
            String[] temp_arry = list.clone();
            int list_length = list.length;
            Random rand = new Random();
            int n = rand.nextInt(8) + 3;
            ArrayList<String> iteam= new ArrayList<>();
            for(int j=0; j<n; j++)
            {
                int temp = (int)(Math.random()*list_length);
                iteam.add(temp_arry[temp]);
                temp_arry[temp] = list[list_length-1];
                list_length--;
            }
            data.add(iteam);
        }
        //System.out.println(data);
        try {
            write_data(data);
        } catch (IOException e) {
            System.out.println("写入失败");
            e.printStackTrace();
        }
    }
}
