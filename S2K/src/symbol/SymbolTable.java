package symbol;

import java.util.HashMap;

/**
 * 符号表的管理类，维护方法的列表。
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
	 * 使用线性扫描算法进行寄存器分配。对于每个方法，对其依次进行活性分析，计算活性区间并进行寄存器分配。
	 */
	public static void LinearScan() {
		for (Method method : methods.values()) {
			method.activityAnalyze();
			method.computeAllInteval();
			method.allocateRegisters();
		}
	}

}
