package apriori_hash;

import java.util.*;

public class Apriori_hash {

    public Map<HashSet<Integer>, Integer> getAll(ArrayList<ArrayList<Integer>> dataList_int, double min_support) {
        //创建一个物品项Set
        HashSet<Integer> goods = new HashSet<>();
        for (ArrayList<Integer> item_list:dataList_int) {
            goods.addAll(item_list);
        }
        //创建一个大的Set，里面保存物品组合的小Set
        HashSet<HashSet<Integer>> data_set = new HashSet<>();
        for (Integer good:goods) {
            HashSet<Integer> iteam = new HashSet<>();
            iteam.add(good);
            data_set.add(iteam);
        }
        //垂直事务分析，key用于存物品组合，value用于存这些物品出现的事务列表
        HashMap<HashSet<Integer>,ArrayList<Integer>> map = new HashMap<>();
        //创建一个用于存储频繁模式项的Map，Key表示频繁模式，Value表示出现频率
        HashMap<HashSet<Integer>,Integer> freqItems_map = new HashMap<>();
        int dataList_size = dataList_int.size();
        for (HashSet<Integer> good:data_set) {
            ArrayList<Integer> transation_list = new ArrayList<>();
            for (int i=0;i<dataList_size;i++){
                if (dataList_int.get(i).containsAll(good)){
                    transation_list.add(i);
                }
            }
            if (transation_list.size() >= min_support){
                map.put(good,transation_list);
                freqItems_map.put(good,transation_list.size());
            }
        }
        HashMap<HashSet<Integer>, Integer> freqItems = get_freqItems(map,min_support);
        freqItems_map.putAll(freqItems);
        return freqItems_map;
    }

    private HashMap<HashSet<Integer>,Integer> get_freqItems(HashMap<HashSet<Integer>, ArrayList<Integer>> map, double min_support) {
        if (map.size() <= 1){
            return null;
        }
        HashMap<HashSet<Integer>, ArrayList<Integer>> map_this = new HashMap<>();
        HashMap<HashSet<Integer>,Integer> freqItem_map = new HashMap<>();
        Set<HashSet<Integer>> keySets = map.keySet();
        ArrayList<HashSet<Integer>> key_list = new ArrayList<>(keySets);
        int size = key_list.size();
        for (int i = 0; i < size-1; i++) {
            HashSet<Integer> set_i = new HashSet<>();
            set_i.addAll(key_list.get(i));
            for (int j = i; j < size; j++) {
                HashSet<Integer> set_j = new HashSet<>();
                set_j.addAll(key_list.get(j));
                set_j.addAll(set_i);
                if (set_j.size() == set_i.size()+1){
                    ArrayList<Integer> list_a= new ArrayList<>();
                    ArrayList<Integer> list_b= new ArrayList<>();
                    list_a.addAll(map.get(key_list.get(i)));
                    list_b.addAll(map.get(key_list.get(j)));
                    list_a.retainAll(list_b);
                    if (list_a.size() >= min_support){
                        freqItem_map.put(set_j,list_a.size());
                        map_this.put(set_j,list_a);
                    }
                }
            }
        }
        HashMap<HashSet<Integer>, Integer> freqItems = get_freqItems(map_this, min_support);
        if (freqItems != null){
            freqItem_map.putAll(freqItems);
        }
        return freqItem_map;
    }
}
