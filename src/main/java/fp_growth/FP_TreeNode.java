package fp_growth;

import java.util.ArrayList;
import java.util.Comparator;

public class FP_TreeNode {
    private String name;											//项名称
    private int count;												//支持度
    private FP_TreeNode parent;										//父结点
    private ArrayList<FP_TreeNode> childs;							//子结点（列表）
    private FP_TreeNode nextSameNode;								//指向下一个相同结点

    public FP_TreeNode() {
        super();
    }

    public FP_TreeNode(String name, int count) {
        super();
        this.name = name;
        this.count = count;
        this.childs = new ArrayList<FP_TreeNode>();
        this.parent = null;
        this.nextSameNode = null;
    }

    public FP_TreeNode(String name, int count, FP_TreeNode parent) {
        super();
        this.name = name;
        this.count = count;
        this.childs = new ArrayList<FP_TreeNode>();
        this.parent = parent;
        this.nextSameNode = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FP_TreeNode getParent() {
        return this.parent;
    }

    public boolean hasParent(){
        return this.parent == null ? false : true;
    }

    public void setParent(FP_TreeNode parent) {
        this.parent = parent;
    }

    public void addChild(FP_TreeNode child){
        this.childs.add(child);
    }

    public void addChilds(ArrayList<FP_TreeNode> childs){
        this.childs.addAll(childs);
    }

    public void delChild(FP_TreeNode child){
        this.childs.remove(child);
    }

    public FP_TreeNode getNextSameNode() {
        return this.nextSameNode;
    }

    public boolean hasNextSameNode(){
        return this.nextSameNode == null ? false : true;
    }

    public void setNextSameNode(FP_TreeNode nextSameNode) {
        this.nextSameNode = nextSameNode;
    }

    public ArrayList<FP_TreeNode> getChilds() {
        return childs;
    }

    public void inc(int i){
        this.count += i;
    }

    @Override
    public String toString() {
        return this.getName() + ":" + this.getCount();
    }
}

/*
 * @function：为实现使用Collections.sort方法对泛型为FP_TreeNode的List进行排序，需要实现Comparator接口
 */
class FP_TreeNodeSort implements Comparator<FP_TreeNode> {

    @Override
    public int compare(FP_TreeNode o1, FP_TreeNode o2) {
        int result = Integer.compare(o2.getCount(), o1.getCount());		//实现降序排列
        //int result = Integer.compare(o1.getCount(), o2.getCount());	//实现升序排列
        if(result != 0)
            return result;
        return o1.getName().compareTo(o2.getName());
    }
}
