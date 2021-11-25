package apriori;

import org.javatuples.Quintet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Make_rules {

    /**
     * 求一个集合全部的非空真子集
     * 思路：如果集合S（A,B,C,D）。其大小为4。拥有2的4次方个子集，即0-15，二进制表示为0000，0001。...，1111。
     * 相应的子集为空集。{D}，...。{A,B,C,D}。
     */
    public static ArrayList<ArrayList<Integer>> getSubsets(ArrayList<Integer> subList) {
        ArrayList<ArrayList<Integer>> allsubsets = new ArrayList<ArrayList<Integer>>();
        int max = 1 << subList.size();
        for(int loop = 0; loop < max; loop++) {
            int index = 0;
            int temp = loop;
            ArrayList<Integer> currentCharList = new ArrayList<Integer>();
            while(temp > 0) {
                if((temp & 1) > 0) {
                    currentCharList.add(subList.get(index));
                }
                temp>>=1;
                index++;
            }
            if (currentCharList.size()!=0&&currentCharList.size()!=subList.size()){
                allsubsets.add(currentCharList);
            }
        }
        return allsubsets;
    }
    /*
    生成规则列表
     */
    public ArrayList<ArrayList<HashSet<Integer>>> rules_from_item(HashSet<Integer> lk){
        ArrayList<ArrayList<HashSet<Integer>>> rules = new ArrayList<>();
        ArrayList<Integer> list = new ArrayList<>(lk);
        ArrayList<ArrayList<Integer>> sublist = getSubsets(list);
        for (ArrayList<Integer> iteam:sublist) {
            ArrayList<HashSet<Integer>> rule = new ArrayList<>();
            HashSet<Integer> setleft = new HashSet<>(iteam);
            rule.add(setleft);
            HashSet<Integer> set = new HashSet<>(list);
//            System.out.println("删除前的set"+set);
            set.removeAll(setleft);
//            System.out.println("删除后的set"+set);
            rule.add(set);
            rules.add(rule);
        }
//        System.out.println(rules);
        return rules;
    }

    public ArrayList rules_from_freqItems(Map<HashSet<Integer>, Double> L, double min_confidence){
        ArrayList<ArrayList<HashSet<Integer>>> rules = new ArrayList();
        Set<HashSet<Integer>> key = L.keySet();
        for (HashSet<Integer> Lk:key) {
            if (Lk.size()>1){
                rules.addAll(rules_from_item(Lk));
            }
        }
        //System.out.println(rules);
        ArrayList<Quintet<HashSet<Integer>,HashSet<Integer>,Double,Double,Double>> result = new ArrayList<>();
        for (ArrayList<HashSet<Integer>> list:rules) {
            HashSet<Integer> left = new HashSet<>();
            left.addAll(list.get(0));
            HashSet<Integer> right = new HashSet<>();
            right.addAll(list.get(1));
            list.get(0).addAll(list.get(1));
            double support = L.get(list.get(0));
            double confidence = support * 1.0 / L.get(left);
            double lift = confidence * 1.0 / L.get(right);
            //System.out.println(left+" "+L.get(left));
            if (confidence >= min_confidence){
                result.add(Quintet.with(left, right, support, confidence, lift));
            }
        }
        return result;
    }
}
