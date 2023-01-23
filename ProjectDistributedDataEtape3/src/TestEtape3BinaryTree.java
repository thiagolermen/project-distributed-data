import java.util.Random;

public class TestEtape3BinaryTree {

    static String myName;
    static Random rand = new Random();

    public static void main(String argv[]) {

		if (argv.length != 1) {
			System.out.println("java TestEtape3BinaryTree <name>");
			return;
		}
		myName = argv[0];

        Client.init();

        int nbObj = 5;
        

        for (int i = 0; i < nbObj; i++) {
            BinaryTree_itf btf = (BinaryTree_itf) Client.lookup("Tree"+i);
            if (btf == null) {
                btf = (BinaryTree_itf) Client.create(new BinaryTree());
                Client.register("Tree"+i, btf);
            }
            BinaryTree_itf st1 = (BinaryTree_itf) Client.lookup("SubTree1"+i);
            if (st1 == null) {
                st1 = (BinaryTree_itf) Client.create(new BinaryTree());
                Client.register("SubTree1"+i, st1);
            }
            BinaryTree_itf st2 = (BinaryTree_itf) Client.lookup("SubTree2"+i);
            if (st2 == null) {
                st2 = (BinaryTree_itf) Client.create(new BinaryTree());
                Client.register("SubTree2"+i, st2);
            }
        }

        //for (int i = 1; i <= nbLoops; i++) {
        int currentLoop = 1;
        while (true) {
            System.out.println("Current loop: "+currentLoop);
            currentLoop++;
            int numObj = rand.nextInt(nbObj);
            BinaryTree_itf btf = (BinaryTree_itf) Client.lookup("Tree"+numObj);
            BinaryTree_itf st1 = (BinaryTree_itf) Client.lookup("SubTree1"+numObj);
            BinaryTree_itf st2 = (BinaryTree_itf) Client.lookup("SubTree2"+numObj);

            int indicator = rand.nextInt(3);
            if (rand.nextInt(2) == 0) {
                if (indicator == 0) {
                    btf.lock_read();
                    int data = btf.getNodeData();
                    btf.unlock();
                } else if (indicator == 1) {
                    btf.lock_read();
                    SharedObject so = btf.getSubTree1();
                    btf.unlock();
                } else {
                    btf.lock_read();
                    SharedObject so = btf.getSubTree2();
                    btf.unlock();
                }
            } else {
                if (indicator == 0) {
                    btf.lock_write();
                    btf.setNodeData(7);
                    btf.unlock();
                } else if (indicator == 1) {
                    btf.lock_write();
                    btf.setSubTree1((SharedObject)st2);
                    btf.unlock();
                } else {
                    btf.lock_write();
                    btf.setSubTree2((SharedObject)st1);
                    btf.unlock();
                }   
            }                  
        }
    }
}
