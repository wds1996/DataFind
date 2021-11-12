package apriori;

import java.util.*;

public class test {
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
//    public static void main(String[] args) {
//        HashSet<Integer> set = new HashSet<>();
//        set.add(1);
//        set.add(2);
//        set.add(3);
//        set.add(4);
//        ArrayList<Integer> list = new ArrayList(set);
//        ArrayList<ArrayList<Integer>> result = getSubsets(list);
//        System.out.println(result);
//    }
    public static void main(String[] args) {
        HashSet<HashSet<Integer>> ck_plus=new HashSet<>();
        HashSet<Integer> ck_plus1=new HashSet<>();
        HashSet<Integer> ck_plus2=new HashSet<>();
        HashSet<Integer> ck_plus3=new HashSet<>();
        ck_plus1.add(1);
        ck_plus1.add(2);
        ck_plus1.add(4);
        ck_plus2.add(1);
        ck_plus2.add(3);
        ck_plus2.add(4);
        ck_plus3.add(2);
        ck_plus3.add(3);
        ck_plus3.add(4);
        ck_plus.add(ck_plus1);
        ck_plus.add(ck_plus2);
        ck_plus.add(ck_plus3);
        System.out.println(ck_plus);
        Map<HashSet<Integer>, Double> lk = new HashMap<>();
        HashSet<Integer> lk1=new HashSet<>();
        HashSet<Integer> lk2=new HashSet<>();
        HashSet<Integer> lk3=new HashSet<>();
        HashSet<Integer> lk4=new HashSet<>();
        HashSet<Integer> lk5=new HashSet<>();
        lk1.add(1);
        lk1.add(2);
        lk2.add(2);
        lk2.add(4);
        lk3.add(1);
        lk3.add(4);
        lk4.add(1);
        lk4.add(3);
        lk5.add(3);
        lk5.add(4);
        lk.put(lk1,0.1);
        lk.put(lk2,0.1);
        lk.put(lk3,0.1);
        lk.put(lk4,0.1);
        lk.put(lk5,0.1);
        System.out.println(lk);
        Apriori apriori = new Apriori();
        HashSet<HashSet<Integer>> hashSets = apriori.ck_plus_prune(ck_plus,lk);
        System.out.println(hashSets);

    }
}
