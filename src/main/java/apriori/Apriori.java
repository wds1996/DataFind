package apriori;

import org.javatuples.Quintet;
import java.util.*;

public class Apriori {
    /**
     * 这个是用来找出1-频繁项集的方法，因为1-频繁项集的特殊性，
     * 所以需要特别的方法来返回1-频繁项集
     */
    public HashSet<HashSet<Integer>> build_c1(ArrayList<ArrayList<Integer>> dataList) {
        HashSet<HashSet<Integer>> c1 = new HashSet<>();
        for (ArrayList<Integer> iteams : dataList) {
            //这是把所有的购买记录一条条的筛选出来
            for (int iteam : iteams) {
                HashSet<Integer> set = new HashSet<>();
                set.add(iteam);
                c1.add(set);
            }
        }
        //System.out.println(c1);
        return c1;
    }
    /**
     *根据候选k项集生成频繁k项集，依据min_support
     *     param data_set: 数据集 list类型
     *     param ck: 候选k项集 list类型，list装frozenset
     *     param min_support: float 最小支持度
     *     return: lk dict类型
     */
    public Map<HashSet<Integer>, Double> ck_2_lk(ArrayList<ArrayList<Integer>> dataList_int, HashSet<HashSet<Integer>> ck, double min_support) {
        Map<HashSet<Integer>, Double> lk = new HashMap<>();
        Map<HashSet<Integer>, Integer> resultSetMap = new HashMap<>();
        for (ArrayList<Integer> row : dataList_int) {
            //这是把所有的购买记录一条条的筛选出来
            for (HashSet<Integer> iteams : ck) {
//                ArrayList<Integer> list_iteam = new ArrayList<>();
//                for (int iteam : iteams) {
//                    list_iteam.add(iteam);
//                }
                if (row.containsAll(iteams)){
                    if (resultSetMap.get(iteams)==null){
                        resultSetMap.put(iteams,1);
                    }else {
                        resultSetMap.put(iteams,resultSetMap.get(iteams)+1);
                    }
                }
            }
        }
        //通过最小支持度的值来过滤
//        int length = dataList_int.size();
//        for (HashSet<Integer> key:resultSetMap.keySet()) {
//            double percent = (1.0*resultSetMap.get(key))/length;
//            if (percent > min_support){
//                lk.put(key,percent);
//            }
//        }
        //通过最少出现次数来过滤
        for (HashSet<Integer> key:resultSetMap.keySet()) {
            double support = resultSetMap.get(key);
            if (support >= min_support){
                lk.put(key,support);
            }
        }
        return lk;
    }
    /**
     *对频繁项进行剪枝操作
     */
    public HashSet<HashSet<Integer>> ck_plus_prune(HashSet<HashSet<Integer>> all_ck_plus,Map<HashSet<Integer>, Double> lk) {
        HashSet<HashSet<Integer>> ck_plus_1 = new HashSet<>();
        HashSet<HashSet<Integer>> keyset = new HashSet<>(lk.keySet());
        for (HashSet<Integer> ckset:all_ck_plus) {
            HashSet<HashSet<Integer>> subsets = new HashSet<>();
            ArrayList<Integer> cklist = new ArrayList(ckset);
            ArrayList<ArrayList<Integer>> getsubsets = Make_rules.getSubsets(cklist);
            int sub_length = ckset.size()-1;
            for (ArrayList<Integer> sub:getsubsets) {
                if (sub.size()==sub_length){
                    subsets.add(new HashSet<>(sub));
                }
            }
            HashSet<HashSet<Integer>> set = new HashSet(subsets);
            if (keyset.containsAll(set)){
                ck_plus_1.add(ckset);
            }
        }
        return ck_plus_1;
    }

    /**
     *将频繁k项集（lk）转为候选k+1项集
     *     :param lk: 频繁k项集 dict
     *     :return: ck_plus_1
     */
    public HashSet<HashSet<Integer>> lk_2_ck_plus_1(Map<HashSet<Integer>, Double> lk) {
        ArrayList<HashSet<Integer>> lk_list = new ArrayList();
        lk_list.addAll(lk.keySet());
        HashSet<HashSet<Integer>> all_ck_plus_1 = new HashSet();
        int lk_size = lk.size();
        if (lk_size > 1){
            //获取频繁项集的长度
            int k = lk_list.get(0).size();
            for (int i = 0; i < lk_size-1; i++) {
                for (int j = i+1; j < lk_size; j++) {
                    HashSet<Integer> set = new HashSet<>();
                    set.addAll(lk_list.get(i));
                    set.addAll(lk_list.get(j));
                    if (set.size() == k+1){
                        all_ck_plus_1.add(set);
                    }
                }
            }
        }
        HashSet<HashSet<Integer>> ck_plus_1 = ck_plus_prune(all_ck_plus_1, lk);
        return ck_plus_1;
    }


    public Map<HashSet<Integer>, Double> getAll(ArrayList<ArrayList<Integer>> dataList_int, double min_support) {
        HashSet<HashSet<Integer>> c1 = new HashSet<>();
        c1.addAll(build_c1(dataList_int));
        Map<HashSet<Integer>, Double> l1 = ck_2_lk(dataList_int, c1, min_support);
        //二次修改最小支持度
        //min_support = 6;
        Map<HashSet<Integer>, Double> LK = l1;
        Map<HashSet<Integer>, Double> LastK = l1;
        while (LK.size()>1){
            HashSet<HashSet<Integer>> ck_plus_1 = lk_2_ck_plus_1(LK);
            LK = ck_2_lk(dataList_int, ck_plus_1, min_support);
            if (LK.size()>0){
                LastK.putAll(LK);
            }else {
                break;
            }
//            System.out.println(L);
        }
        return LastK;
    }
}
