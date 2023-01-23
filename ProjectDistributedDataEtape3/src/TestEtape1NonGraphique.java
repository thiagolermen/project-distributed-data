import java.util.Random;

public class TestEtape1NonGraphique {

	SharedObject sentence;
	static String myName;
    static Random rand = new Random();

	public static void main(String argv[]) {
		
		if (argv.length != 1) {
			System.out.println("java Irc <name>");
			return;
		}
		myName = argv[0];
	
		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
        for (int i = 0; i < 5; i++) {
        	Sentence_itf so = (Sentence_itf) Client.lookup("IRC");
            if (so == null) {
            	so = (Sentence_itf) Client.create(new Sentence());
    			Client.register("IRC", so);
            }
        }

        int numBoucles = 1;
        while (true) {
            numBoucles++;
            // Random selection of the tested object
            int numObj = rand.nextInt(5);
            Sentence_itf so = (Sentence_itf) Client.lookup("IRC");
            
            //System.out.println("État de l'objet " + numObj + " pour le client "+TestEtape1NonGraphique.myName + " = "+so.getState());

            // Generates a random number -> 0 corresponds a read lock request and 1 a write lock request
            if (rand.nextInt(2) == 0) {
                // lock the object in read mode
                so.lock_read();
                // invoke the method
                String data = so.read();
                System.out.println(data + " at " + numObj);
                // unlock the object
                so.unlock();
            } else {
                // lock the object in write mode
                so.lock_write();
                // invoke the method
                so.write(TestEtape1NonGraphique.myName + " wrote daleson2" + numBoucles);
                // unlock the object
                so.unlock();
            }
        }
	}
}
