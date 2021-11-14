package apriori;

import com.csvreader.CsvReader;
import org.javatuples.Quintet;

import java.nio.charset.Charset;
import java.util.*;

public class AprioriMain {
    /**
     * 读取 csv 文件
     */
    public static List<String[]> readCsvFile(String readCsvFilePath) {
        // 缓存读取的数据
        List<String[]> content = new ArrayList<>();
        try {
            // 创建 CSV Reader 对象, 参数说明（读取的文件路径，分隔符，编码格式)
            CsvReader csvReader = new CsvReader(readCsvFilePath, ',', Charset.forName("UTF-8"));
            // 跳过表头
            //csvReader.readHeaders();
            // 读取除表头外的内容
            while (csvReader.readRecord()) {
                // 读取一整行
                String line = csvReader.getRawRecord();
//                System.out.println(line);
                content.add(csvReader.getValues());
            }
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        double min_support = 10;
        String CSV_FILE_PATH = "src/main/resources/product_data.txt";
        List<String[]> dataList = new ArrayList<>();
        dataList = readCsvFile(CSV_FILE_PATH);
        //测试输出10条输出
        System.out.println("----------------------输出读入文件十条数据----------------------");
        for (int i = 0; i < 10; i++) {
            for (String iteam: dataList.get(i)) {
                System.out.print(iteam+" ");
            }
            System.out.println();
        }
        HashSet<String> dataSet= new HashSet<String>();
        for (String[] slist:dataList) {
            dataSet.addAll(Arrays.asList(slist));
        }
        //字符串转对应编号，压缩计算
        Map<String, Integer> string_2_index = new HashMap<>();
        //编号转回字符串
        Map<Integer, String> index_2_string = new HashMap<>();
        int index = 0;
        for (String key:dataSet) {
            string_2_index.put(key,index);
            index_2_string.put(index,key);
            index++;
        }
        //创建计算用的整形数组，计算的时候用整数计算更节约空间和时间
        ArrayList<ArrayList<Integer>> dataList_int = new ArrayList<>();
        for (String[] slist:dataList) {
            int length = slist.length;
            ArrayList<Integer> List_int = new ArrayList<>();
            for (int i=0;i<length;i++) {
                List_int.add(i,string_2_index.get(slist[i]));
            }
            dataList_int.add(List_int);
        }
        //测试输出10条输出
        System.out.println("----------------------输出转换为数字的十条数据----------------------");
        for (int i = 0; i < 10; i++) {
            for (Integer iteam: dataList_int.get(i)) {
                System.out.print(iteam+" ");
            }
            System.out.println();
        }
        Apriori apriori = new Apriori();

        //单步测试时的代码
//        HashSet<HashSet<Integer>> c1 = new HashSet<>();
//        c1.addAll(apriori.build_c1(dataList_int));
//        System.out.println(c1);
//        Map<HashSet<Integer>, Double> lk = apriori.ck_2_lk(dataList_int, c1, min_support);
//        HashSet<HashSet<Integer>> ck_plus_1 = apriori.lk_2_ck_plus_1(lk);

        Map<HashSet<Integer>, Double> all_lk = apriori.getAll(dataList_int, min_support);
        //输出所有频繁项集
//        System.out.println("----------------------输出所有频繁项集----------------------");
//        int pinfan_index = 1;
//        while (true){
//            int p = 0;
//            System.out.println("----------------------频繁"+pinfan_index+"项集----------------------");
//            for (HashSet<Integer> set:all_lk.keySet()) {
//                if (set.size()==pinfan_index){
//                    p++;
//                    for (int i:set) {
//                        System.out.print(index_2_string.get(i)+" ");
//                    }
//                    System.out.println("支持度："+all_lk.get(set));
//                }
//            }
//            pinfan_index++;
//            if (p==0){
//                break;
//            }
//        }

        ArrayList<Quintet<HashSet<Integer>,HashSet<Integer>,Double,Double,Double>> result = apriori.rules_from_L(all_lk, 0.2);

        System.out.println("----------------------输出关联规则----------------------");
        for (Quintet<HashSet<Integer>,HashSet<Integer>,Double,Double,Double> iteam:result) {
            ArrayList<Integer> left = new ArrayList<>(iteam.getValue0());
            ArrayList<Integer> right = new ArrayList<>(iteam.getValue1());
            for (int i:left) {
                System.out.print(index_2_string.get(i)+" ");
            }
            System.out.print("-->");
            for (int i:right) {
                System.out.print(index_2_string.get(i)+" ");
            }
            System.out.println(" 支持度："+iteam.getValue2()+" 置信度："+iteam.getValue3()+" 提升度："+iteam.getValue4());
        }
        long end = System.currentTimeMillis();
        System.out.println("程序运行时间："+(end-start)+"ms");
    }
}
