import java.lang.reflect.*;

public class StubGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@SuppressWarnings("rawtypes") // Avoid annoying warnings (for example because of Class[] utilization)
	public static void createStub(Class c) {
		
		try {
			String simpleClassName = c.getSimpleName();
			Method[] methods = c.getMethods();
			
			// Class header declaration
			String textFile = "public class " + simpleClassName + "_stub extends SharedObjects implements " + simpleClassName + "_itf, java.io.Serializable {\n\n"; 
			
			// Class constructor
			textFile += "\tpublic " + simpleClassName + "_stub (int id, Object obj) {\n";
			textFile += "\t\tsuper(id, obj);\n";
			textFile += "\t}\n\n";
			
			for (Method m : methods) {
				
				String methodModifier = Modifier.toString(m.getModifiers());
				// We write only the public methods
				if (methodModifier.split("\\s+")[0].equals("public")) {
					
					// Method header declaration if the method is public
					textFile += "\t" + methodModifier + " " + m.getReturnType().getSimpleName() + " " +  m.getName() + " ";
					
					int numArgs = 0;
					Class[] listParameterTypes = m.getParameterTypes();
					for(Class p : listParameterTypes){
						textFile += "(" + p.getSimpleName() + " arg" + ++numArgs;
						
						if (numArgs < listParameterTypes.length) {
							textFile += ", ";
						}
						
					}
					textFile += ") {\n";
					
					
				}
				
				
				
				
				
			}
			
			textFile += "}\n";
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

}
