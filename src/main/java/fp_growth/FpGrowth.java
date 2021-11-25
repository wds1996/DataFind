package fp_growth;
import org.javatuples.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FpGrowth {
    private double support;											//支持度阈值（比例值）
    private double supportNum;										//支持度阈值（绝对值）

    /*
     * @param1	:指定支持度阈值
     * @return	:无
     * @function:构造一个FpGrowth实例，以调用相关方法，分隔符默认为","，
     * 			 判断输入支持度阈值为比例值或绝对值
     */
    public FpGrowth(double support) {
        super();
        if(support >= 1)
            this.supportNum = support;
        else
            this.support = support;
    }

    //初始化事务
    public ArrayList<ArrayList<Integer>> createInitSet(ArrayList<ArrayList<Integer>> dataList_int) {
        ArrayList<ArrayList<Integer>> transactions = dataList_int;
        int countLine = dataList_int.size();
        //当给定的支持度阈值为比例值时，需要乘上事务总数求出支持度阈值绝对值
        if(supportNum == 0)
            //如给定的支持度阈值为0.4，事务总数为20条，则支持度阈值绝对值为：0.4 * 20 = 8
            supportNum = support * countLine;
        return transactions;
    }

    /*用于读入热编码的数据集
     * @param1	:输入文件路径
     * @param2	:给定判断项是否存在的标识，如给出的例子中标识为“1”
     * @return	:所有事务组成的二维列表
     * @function:将文件内容按行读出，一行为一条事务，按指定的分隔符分隔成项，存入列表
     * 			 文件内容要求为向量形式，如：牛奶，啤酒，咖啡
     * 									 1 ， 1 ， 1
     * 									 0 ， 1 ， 1
     */
    public ArrayList<ArrayList<String>> readVectorFile(String path, String exist) {
        ArrayList<ArrayList<String>> transactions = new ArrayList<ArrayList<String>>();
        File file = new File(path);
        Scanner reader = null;
        int countLine = 0;
        try {
            reader = new Scanner(file);								//表头是项目名称，单独处理、保存
            String[] itemNames = reader.nextLine().trim().split(",");
            while(reader.hasNext()){
                String line = reader.nextLine().trim();
                ArrayList<String> items = new ArrayList<String>(Arrays.asList(line.split(",")));
                ArrayList<String> appearedItems = new ArrayList<String>();
                for(int i = 0; i < items.size(); i++)
                    if(items.get(i).equals(exist))					//根据给定标识判断项是否出现
                        appearedItems.add(itemNames[i]);
                transactions.add(appearedItems);
                countLine++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reader.close();
        if(supportNum == 0)
            supportNum = support * countLine;

        return transactions;
    }

    /*
     * @param1	:所有事务，每条事务用一个ArrayList保存
     * @return	:返回一个二元组，第一个参数是排序后的频繁一项集，第二个参数是不满足支持度的项集list
     * @function:主要用于生成头表 ， 顺带返回不满足支持度的项集list
     */
    public  Pair<LinkedList<FP_TreeNode>,ArrayList<Integer>> buildHeaderTable(ArrayList<ArrayList<Integer>> transactions){
        LinkedList<FP_TreeNode> headerTable = new LinkedList<FP_TreeNode>();
        HashMap<Integer, FP_TreeNode> map = new HashMap<Integer, FP_TreeNode>();
        //用一个list将不满足支持的元素装起来
        ArrayList<Integer> out_iteams = new ArrayList<Integer>();
        //使用HashMap保存项名称及结点，使用结点的原因为方便计数和排序
        for(ArrayList<Integer> items: transactions){
            for(Integer item: items){
                if(map.containsKey(item))
                    map.get(item).inc(1);	//调用了FP_TreeNode的inc方法，使计数加1
				else {
                    FP_TreeNode node = new FP_TreeNode(item.toString(), 1);
                    map.put(item, node);
                }
            }
        }

        for(Integer name: map.keySet()){
            FP_TreeNode node = map.get(name);
            if(node.getCount() >= supportNum) //只保存支持度大于支持度阈值（绝对值）的项
                headerTable.add(node);
            else
                out_iteams.add(name);
        }
        //使用Collections的sort方法，需要对FP_TreeNode类实现一个排序类FP_TreeNodeSort
        Collections.sort(headerTable, new FP_TreeNodeSort());
        //使用map将 头表 和 删除元素列表一起返回
        Pair<LinkedList<FP_TreeNode>,ArrayList<Integer>> headerTable_out_iteams = new Pair<>(headerTable,out_iteams);

        return headerTable_out_iteams;
    }

    /*
     * @param1	:所有事务
     * @param2	:按支持度降序排列的频繁一项集
     * @return	:不满足支持度的项集list
     * @function:将所有事务按频繁项顺序排序并剔除不满足支持度的项集list后，返回排序后的新的事务
     */
    public ArrayList<ArrayList<Integer>> sortByFreqItem(ArrayList<ArrayList<Integer>> transactions,
                                                        ArrayList<Integer> out_iteams) {
        //保存排序后的所有事务
        ArrayList<ArrayList<Integer>> sortedTransactions = new ArrayList<ArrayList<Integer>>();
        for(ArrayList<Integer> transaction: transactions){
            ArrayList<Integer> sortedItem = new ArrayList<Integer>();	//保存排序后的一条事务
            sortedItem = transaction;
            sortedItem.removeAll(out_iteams);
            //将剔除不满足条件的项集后的事务加到新的事务集中
            sortedTransactions.add(sortedItem);
        }

        return sortedTransactions;
    }

    /*
     * @param1	:排好序的所有事务
     * @param2	:项头表，此时表中仅包含频繁一项集及其出现次数，没有指向相同结点的链
     * @return	:返回构建好的FP树的根结点
     * @function:利用所有事务和项头表
     */
    public FP_TreeNode buildFpGrowth(ArrayList<ArrayList<Integer>> transactions, LinkedList<FP_TreeNode> headerTable) {
        FP_TreeNode root = new FP_TreeNode("root", 0, null);	//树根结点，计数为0，无父结点
        for(ArrayList<Integer> items: transactions){
            FP_TreeNode parent = root;			//每次循环处理一条事务，每条事务的第一个结点都是父结点
            for(Integer item: items){
                //根据父结点判断当前结点是否存在，并获取正确的结点
                FP_TreeNode itemNode = exist(item, parent);
                //得到结点后，放入项头表中
                addSameNode(headerTable, itemNode);
                //父结点动态变化
                parent = itemNode;
            }
        }

        return root;
    }

    /*
     * @param1	:项头表
     * @param2	:当前结点
     * @return	:无
     * @function:为项头表中对应的项添加指向当前结点的链式关系
     */
    public void addSameNode(LinkedList<FP_TreeNode> headerTable, FP_TreeNode itemNode) {
        for(FP_TreeNode head: headerTable)
            if(head.getName().equals(itemNode.getName())){	//遍历项头表找到当前结点对应的项
                while(head.hasNextSameNode()){
                    head = head.getNextSameNode();		//寻找对应的项的链表，找到其指向的最后一个相同结点
                    if(head == itemNode)				//若遍历链表时发现当前结点已存在链式关系，即无需添加，直接返回
                        return;
                }
                head.setNextSameNode(itemNode);			//若链表中不存在当前结点，则使最后一个结点指向当前结点，形成链式关系
                return;
            }
    }

    /*
     * @param1	:项名
     * @param2	:父结点
     * @return	:项名对应的结点
     * @function:根据父结点，判断当前项是否存在，若已存在则其次数加一并返回该结点，
     * 			 若不存在则创建一个计数为1的结点并返回
     */
    public FP_TreeNode exist(Integer item, FP_TreeNode parent) {
        for(FP_TreeNode child: parent.getChilds())
            if(child.getName().equals(item)){
                child.inc(1);
                return child;
            }

        FP_TreeNode node = new FP_TreeNode(item.toString(), 1, parent);				//注意新创建结点后为当前结点添加父结点
        parent.addChild(node);										//父结点的子结点列表中也要添加当前结点
        return node;
    }

    /*
     * @param1	:FP树结点
     * @return	:该结点对应的条件模式基，以列表的形式存储
     * @function:根据当前结点，遍历其父结点直到找到根结点root，其路径上的所有结点项构成条件模式基
     */
    public ArrayList<Integer> getCPB(FP_TreeNode node){
        ArrayList<Integer> transaction = new ArrayList<Integer>();
        FP_TreeNode find_parent = node;
        while(find_parent.hasParent()){
            if(find_parent.getParent().getName().equals("root"))
                break;
            find_parent = find_parent.getParent();
            transaction.add(Integer.valueOf(find_parent.getName()));
        }

        return transaction;
    }

    /*
     * @param1	:无序的、针对当前项的所有事务
     * @param2	:当前项
     * @return	:频繁项集
     * @function:递归调用，根据所有事务和当前项，计算出包含当前项的频繁项集并返回
     */
    public HashMap<HashSet<Integer>,Integer> fp_growth(ArrayList<ArrayList<Integer>> transactions, HashSet<Integer> item){
        //用于保存频繁项集freqItems
        HashMap<HashSet<Integer>,Integer> freqItems = new HashMap<>();
        //构建头标 同时 找出不满足条件的项集
        Pair<LinkedList<FP_TreeNode>,ArrayList<Integer>> headerTable_out_iteams = buildHeaderTable(transactions);
        //拿出元组中的头表link
        LinkedList<FP_TreeNode> headerTable = headerTable_out_iteams.getValue0();
        //拿出不满足条件项集的元素集合，用于下面剔除操作
        ArrayList<Integer> out_iteams = headerTable_out_iteams.getValue1();
        //剔除不满足支持度的项，得到新的事务集
        transactions = sortByFreqItem(transactions, out_iteams);
        //对于当前项和事务，生成其对应的FP树和项头表
        FP_TreeNode root = buildFpGrowth(transactions, headerTable);
        //递归终止条件
        if(headerTable.size() == 0 || root == null)
            return freqItems;
        if(item == null){
            //当item == null时，说明第一次递归，在此处将频繁一项集放入频繁项集列表
            for(FP_TreeNode node: headerTable) {
                HashSet<Integer> key = new HashSet<>();
                key.add(Integer.valueOf(node.getName()));
                freqItems.put(key, node.getCount());
            }
        }
        //否则针对当前项，遍历项头表，与当前项组合即为一个频繁项集
        else{

            //#注意，该频繁项集并不一定是频繁二项集，因为当前项可能是多个项的组合
            for(int i = headerTable.size() - 1; i >= 0; i--){
                HashSet<Integer> key = new HashSet<>();
                //为便于存储和使用，项头表实际上也是用的FP_TreeNode类型表示，其NextSameNode指向的才是该项的所有结点的链表
                FP_TreeNode node = headerTable.get(i);
                key.add(Integer.valueOf(node.getName()));
                key.addAll(item);
                freqItems.put(key, node.getCount());
            }
        }
        //遍历项头表求当前项的条件模式基
        for(int i = headerTable.size() - 1; i >= 0; i--){		
            //二维列表保存针对新项集的所有事务
            ArrayList<ArrayList<Integer>> newTransactions = new ArrayList<ArrayList<Integer>>();
            FP_TreeNode node = headerTable.get(i);
            HashSet<Integer> newItem = new HashSet<>();
            if (item == null) {
                newItem.add(Integer.valueOf(node.getName()));
            }else {
                newItem.add(Integer.valueOf(node.getName()));
                newItem.addAll(item);
            }
            //将当前项和遍历取出的项组合作为新的项集
            while(node.hasNextSameNode()){
                node = node.getNextSameNode();
                //对取出的项，遍历其相同结点的链表，求出各条路径对应的条件模式基并保存
                for(int j = 0; j < node.getCount(); j++)			
                    newTransactions.add(getCPB(node));
            }
            //递归调用该函数，参数为新的项集对应的所有事务和该项集
            freqItems.putAll(fp_growth(newTransactions, newItem));	
        }
        //返回本次调用求出的频繁项集
        return freqItems;											
    }
}
