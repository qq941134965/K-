import java.io.*;
import java.util.Arrays;
import java.util.Stack;
import java.lang.*;
import java.util.Queue;
import java.util.LinkedList;
import org.apache.commons.math3.distribution.LaplaceDistribution;
/*
以下是K叉平均树算法的实现过程，主要包括：
  public class KTreeNode{}
  public static void createKTree(int[] numberArray, double m, double l)

  public static float calcNum(KTreeNode root, int K);

  public static void addNoise(float lambda,KTreeNode root, int k, int l);
*/






//以下是K叉平均树的基本函数
public class KTreeNode{

    @Override
    public String toString() {
        return "KTreeNode{" +
                "value=" + value +
                ", subNode=" + Arrays.toString(subNode) +
                '}';
    }

    double value;
    KTreeNode[] subNode;

    public KTreeNode(){
        super();
        value = 0;
    };

    public KTreeNode(int value){
        super();
        this.value = value;
    }

    public void setValue(double value){
        this.value = value;
    }

    public void addLaplaceNoise(double noise){
        this.value = this.value + noise;
    }
    public double getValue(){
        return value;
    }

    public void setKTreeNode(KTreeNode[] kNode){
        this.subNode = kNode;
    }

    public KTreeNode[] getKTreeNode(){
        return subNode;
    }

    public boolean SubtreeIsEmpty(){
           if(subNode == null) return true;
           else return false;
    }

    public void displayTreeValue(){
        if(this == null){
            System.out.println("null tree");
            return;
        }
        Stack<KTreeNode> displayResult = new Stack<KTreeNode>();
        displayResult.push(this);
        while(!displayResult.isEmpty()){
            KTreeNode tree = (KTreeNode)displayResult.pop();
            System.out.println(tree.getValue());
            if(tree.getKTreeNode() != null){
                for(KTreeNode subTree:tree.getKTreeNode()){
                    displayResult.push(subTree);
                }
            }
        }

    }

    public void dispFromDeepToRoot(){
        if(this == null){
            System.out.println("null tree");
            return;
        }
        Stack<KTreeNode> displayResult = new Stack<KTreeNode>();
        Stack<KTreeNode> storeUse = new Stack<KTreeNode>();
        displayResult.push(this);
        storeUse.push(this);
        while(!storeUse.isEmpty()){
            KTreeNode tree = (KTreeNode)storeUse.pop();

            if(tree.getKTreeNode() != null){
                for(KTreeNode subTree:tree.getKTreeNode()){
                    displayResult.push(subTree);
                    storeUse.push(subTree);
                }
            }
        }
        while(!displayResult.isEmpty()){
            KTreeNode tree = displayResult.pop();
            System.out.println(tree.getValue());
        }

    }

    public double calcChildrenSum(){
          KTreeNode[] subtree = this.getKTreeNode();
          double sum = 0;
          for(KTreeNode tree:subtree){
              sum +=tree.getValue();
          }
          return sum;
    }




    // 以下是静态方法calcNum，主要用于计算出K叉树中非叶节点的值。
    // 用到了递归。
    public static double calcNum(KTreeNode root, int K){
        if(root.getKTreeNode() == null){
            return root.getValue();
        }
        else{
            double sum = 0d;
            KTreeNode[] subTree = root.getKTreeNode();

            for(int i = 0; i < K; i++){
                sum += calcNum(subTree[i], K);
            }
            sum = sum / K;
            root.setValue(sum);
            return sum;
        }
    }


    //  下列是静态方法，用于创建一个K叉平衡树
//  其中，m 是频率矩阵属性的大小, 树的深度为l + 1。
//  int[] numberArray是对应各属性的值。
    public static KTreeNode createKTree(int[] numberArray, double m, double l){

        int k = (int)Math.ceil((double)Math.pow(m, 1 / l));

        QueueUseStack queue = new QueueUseStack();
        //K叉平均树里的根树
        KTreeNode root = new KTreeNode();
        queue.push(root);

        if(queue.isEmpty()){
            System.out.println("there is something wrong with queue");
            return null;
        }

        for(int i = 0; i < l; i++){
            for(int j = 0; j < Math.pow(k, i); j++){

                KTreeNode[] subTreeArray = new KTreeNode[k];
                for(int ii = 0; ii < k; ii++){
                    subTreeArray[ii] = new KTreeNode();
                }
                KTreeNode t = queue.pop();

                if(t == null) {
                    System.out.println(i);
                    System.out.println(j);
                    System.out.println(k);
                    System.out.println("this is something wrong with KTreeNode");
                    return null;
                }
                t.setKTreeNode(subTreeArray);

//                for(KTreeNode tree3:subTreeArray){
//                    queue.push(tree3);
//                }
                for(int ii = 0; ii < k; ii++){
                    queue.push(subTreeArray[ii]);
                    System.out.println("ii is " + ii);
                }

            }

        }

        for(int i = 0; i < m; i++){
            KTreeNode tree_ = queue.pop();
            tree_.setValue(numberArray[i]);
        }

        while(true){
            KTreeNode t = queue.pop();
            if(t == null) break;
            t.setValue(0f);
        }

        KTreeNode.calcNum(root, k);
        return root;



    }

    public static void addNoise(KTreeNode root, int k, int l){
        // 根据树的高度添加噪声，并且给出对应的结果。
        System.out.println("this is just a test");
        //首先往每一层上加噪声
        int laplaceNum = 2 * ( l + 1 );
        LaplaceDistribution[] ld = new LaplaceDistribution[l+1];
        for(int i = 0, j = l; i < l+1; i++, j--){
            ld[i] = new LaplaceDistribution(0, laplaceNum / Math.pow(k,j));
        }


        QueueUseStack queue = new QueueUseStack();
        if(root == null){
            System.out.println("root is null");
            return;
        }
        queue.push(root);
        for(int i = 0; i < l+1; i++){
            for(int j = 0; j < Math.pow(k,i); j++){
                KTreeNode tree = queue.pop();
                tree.addLaplaceNoise(ld[i].sample());
                if(tree.SubtreeIsEmpty()) continue;
                for(KTreeNode subTree_:tree.getKTreeNode()){
                   queue.push(subTree_);
                }
            }

        }

    }

    public static double[] getFrquencyMatrix(KTreeNode root, int m, int k, int l){
        //k > 1
        int total_number =(int)(Math.pow(k, l+1) - 1) / (k - 1);
        int total_number_except_leaf_node =  total_number  - (int)Math.pow(k, l);

        double[] dp_array = new double[total_number];
        dp_array[0] = root.getValue();
        QueueUseStack queue2 = new QueueUseStack();
        queue2.push(root);
        int n = 1;
        int father_index = 0;
        for(int i = 0; i < l; i++){
            for(int j = 0; j < Math.pow(k,i); j++){
                KTreeNode tree_ = queue2.pop();
                double sumOfSubtree = tree_.calcChildrenSum();
                KTreeNode[] subtree_ = tree_.getKTreeNode();

                for(KTreeNode t:subtree_){
                    dp_array[n] = +dp_array[father_index] + t.getValue()- sumOfSubtree / k;
                    n++;
                    queue2.push(t);
                }
                father_index++;
            }
        }
        double[] result_array = new double[m];
        int an = 0;
        for(int i = total_number_except_leaf_node; i < total_number_except_leaf_node + m; i++){
           result_array[an] = dp_array[i];
           an++;
        }
        return result_array;

    }

//    public static int calcNumOfQueue(QueueUseStack q){
//        int num = 0;
//        if(q == null) return 0;
//        while(!q.isEmpty()){
//            num++;
//            q.pop();
//        }
//        return num;
//    }

    public static double addIntervalSum(int start, int interval, double[] array){
        double sum = 0;
        for(int i = start; i < start + interval; i++){
            sum += array[i];
        }
        return sum;
    }

    public static void main(String[] args){

        KTreeNode tree_ = new KTreeNode(5);
        KTreeNode[] subtree_ = {new KTreeNode(1), new KTreeNode(2), new KTreeNode(3)};

        tree_.setKTreeNode(subtree_);
        tree_.displayTreeValue();
        System.out.println("this is main function");



        System.out.println("up is my test code, below is my real code");


//      以下是测试用例1
        int[] numberArray = new int[1000];
        double m = 100d, l = 3d;
        for(int x = 0; x < (int)m / 2 ; x++){
            numberArray[x] = 10000 + x * 10;
//            numberArray[x] = x * 2;
//            numberArray[x] = x * 3;

        }

        for(int x = 50; x < (int)m; x++){
            numberArray[x] = 10000 - x * 10;
        }

        double[] numberRight = new double[100];
        for(int i = 0; i < (int)m; i++){
            numberRight[i] = numberArray[i];
        }
//        以上是测试用例1

//        以下是测试用例2
//        int[] numberArray = new int[1000];
//        double m = 100d, l = 3d;
//        for(int x = 0; x < (int)m / 2 ; x++){
//            numberArray[x] = 1000 + x * 10;
////            numberArray[x] = x * 2;
////            numberArray[x] = x * 3;
//
//        }
//
//        for(int x = 50; x < (int)m; x++){
//            numberArray[x] = 1000 - x * 10;
//        }
//
//        double[] numberRight = new double[1000];
//        for(int i = 0; i < (int)m; i++){
//            numberRight[i] = numberArray[i];
//        }
//        以上是测试用例2



        int k = (int)Math.ceil((double)Math.pow(m, 1 / l));
        KTreeNode result= createKTree(numberArray, m, l);
        addNoise(result, k, (int)l);
        //result.displayTreeValue();
        double[] result_array = getFrquencyMatrix(result, (int)m, k , (int)l);

        for(int i = 0; i < (int)m; i++){
            System.out.print(numberRight[i]);
            System.out.print("    ");
            System.out.print(result_array[i]);
            System.out.print("\n");
        }
        int fixTestNum = 90;
        for(int interval = 2; interval <= 10; interval++){

            double minusSum = 0;
            for(int i = 0; i < fixTestNum; i++){
               double rightSum = addIntervalSum(i, interval, numberRight);
               double diff_pri_Sum = addIntervalSum(i, interval, result_array);
               minusSum += Math.abs(rightSum - diff_pri_Sum) / rightSum;
            }
            double r = minusSum / fixTestNum;
            System.out.print("区间大小为" +interval+ "  " + "误差率为" + r*100 +"%\n");

        }
    }

}

// 以下是用两个Stack来实现一个队列，只有基本的一些功能
class QueueUseStack {
    Stack<KTreeNode> first;
    Stack<KTreeNode> second;

    QueueUseStack() {
        first = new Stack<KTreeNode>();
        second = new Stack<KTreeNode>();
    }

    void push(KTreeNode tree) {
        first.push(tree);
    }

    KTreeNode pop() {
        if (second.isEmpty()) {
            if (first.isEmpty()) {
                return null;
            }

            while (!first.isEmpty()) {
                second.push(first.pop());
            }
            return second.pop();
        } else {
            return second.pop();
        }

    }

    boolean isEmpty() {
        if (first.isEmpty() && second.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}

