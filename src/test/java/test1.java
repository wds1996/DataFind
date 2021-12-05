import apriori.Apriori;
import fp_growth.FPTree_网上参考;

import java.util.*;

public class test1 {
    public static void main(String[] args) {
        FPTree_网上参考 fptree = new FPTree_网上参考(3);								//支持度阈值为0.3
        ArrayList<ArrayList<String>> transactions = fptree.readBacketFile("src/main/resources/product_data.txt");
        //ArrayList<ArrayList<String>> transactions = fptree.readVectorFile("/Your Path/2.txt", "1");
        for(String s: fptree.fp_growth(transactions, null))
            System.out.println(s);
    }
}
