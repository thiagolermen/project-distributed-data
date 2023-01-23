import java.io.Serializable;

public class BinaryTree implements Serializable {

    private SharedObject subTree1, subTree2;

    private int nodeData;

    public BinaryTree() {
        this.subTree1 = null;
        this.subTree2 = null;
        this.nodeData = 0;    
    }

    public int getNodeData() {
        return this.nodeData;    
    }

    public SharedObject getSubTree1() {
        return this.subTree1;    
    }

    public SharedObject getSubTree2() {
        return this.subTree2;    
    }

    public void setNodeData(int nodeData) {
        this.nodeData = nodeData;    
    }

    public void setSubTree1(SharedObject subTree1) {
        this.subTree1 = subTree1;    
    }

    public void setSubTree2(SharedObject subTree2) {
        this.subTree2 = subTree2;    
    }
}
