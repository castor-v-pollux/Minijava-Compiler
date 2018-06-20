package symbol;

import java.util.HashMap;

/**
 * 全局标号的管理类。对于原Spiglet程序中每个方法的局部标号，将其映射并替换为全局的标号。
 *
 * @author castor_v_pollux
 */
public class Label {

	private static int labelCnt = 0;

	private static HashMap<String, Integer> labelToId = new HashMap<>();
	private static HashMap<String, Integer> labelToName = new HashMap<>();

	/**
	 * 将一个方法局部标号与语句序号相联系
	 * @param label 标号
	 * @param id 所在语句的序号
	 */
	public static void add(String label, int id) {
		labelToId.put(label, id);
		labelToName.put(label, labelCnt++);
	}

	/**
	 * 获取一个标号所在的语句序号
	 * @param label 标号
	 * @return 语句序号
	 */
	public static int getId(String label) {
		return labelToId.get(label);
	}

	/**
	 * 获取一个局部标号的全局名称
	 * @param label 标号
	 * @return 全局序号
	 */
	public static int getName(String label) {
		return labelToName.get(label);
	}

}
