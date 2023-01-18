import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.*;

public class StubGenerator {

	public static void main(String[] args) {
		// Auto-generated method stub
		try {
			createStub(Class.forName(args[0]));
		} catch (Exception e) {
			System.err.println("Error during stub creation - use java StubGenerator <name_class>");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes") // Avoid annoying warnings (for example because of Class[] utilization)
	public static void createStub(Class c) {
		try {
			String simpleClassName = c.getSimpleName();
			Method[] methods = c.getDeclaredMethods();
			
			// Class header declaration
			String textFile = "\npublic class " + simpleClassName + "_stub extends SharedObject implements java.io.Serializable, " + simpleClassName + "_itf {\n\n"; 
			
			// Constructor
			textFile += "\tpublic " + simpleClassName + "_stub(int id, Object object) {\n";
			textFile += "\t\tsuper(id, object);\n";
			textFile += "\t}\n\n";
			
			for (Method m : methods) {
				
				String methodModifier = Modifier.toString(m.getModifiers());
				// We only write the public methods
				if (methodModifier.split("\\s+")[0].equals("public")) {
					
					// Method header declaration
					String returnType = m.getReturnType().getSimpleName();
					String methodName = m.getName();
					textFile += "\t" + methodModifier + " " + returnType + " " +  methodName + "(";
					Class[] listParameterTypes = m.getParameterTypes();
					int n = listParameterTypes.length;
					for (int numArg = 1; numArg <= n; numArg++) {
						textFile += listParameterTypes[numArg-1].getSimpleName() + " arg" + numArg;
						if (numArg < n) {
							textFile += ", ";
						}
					}
					textFile += ") {\n";
					
					// Method instructions
					textFile += "\t\t" + simpleClassName + " object = (" + simpleClassName + ") this.obj;\n\t\t";
					if (returnType != "void") {
						textFile += "return ";
					}
					textFile += "object." +  methodName + "(";
					for (int numArg = 1; numArg <= n; numArg++) {
						textFile += "arg" + numArg;
						if (numArg < n) {
							textFile += ", ";
						}
					}
					textFile += ");\n\t}\n\n";
				}
			}
            textFile += "}\n";

			// Class file creation and filling
			File file = new File(simpleClassName + "_stub.java");
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(textFile.getBytes());
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}