package symbol;

import java.util.HashMap;

/**
 * ���ű�Ĺ����࣬ά���������б�
 *
 * @author castor_v_pollux
 */
public class SymbolTable {

	private static HashMap<String, Method> methods = new HashMap<>();

	public static void add(Method method) {
		methods.put(method.getName(), method);
	}

	public static Method get(String name) {
		return methods.get(name);
	}

	/**
	 * ʹ������ɨ���㷨���мĴ������䡣����ÿ���������������ν��л��Է���������������䲢���мĴ������䡣
	 */
	public static void LinearScan() {
		for (Method method : methods.values()) {
			method.activityAnalyze();
			method.computeAllInteval();
			method.allocateRegisters();
		}
	}

}
