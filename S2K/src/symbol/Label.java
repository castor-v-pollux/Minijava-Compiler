package symbol;

import java.util.HashMap;

/**
 * ȫ�ֱ�ŵĹ����ࡣ����ԭSpiglet������ÿ�������ľֲ���ţ�����ӳ�䲢�滻Ϊȫ�ֵı�š�
 *
 * @author castor_v_pollux
 */
public class Label {

	private static int labelCnt = 0;

	private static HashMap<String, Integer> labelToId = new HashMap<>();
	private static HashMap<String, Integer> labelToName = new HashMap<>();

	/**
	 * ��һ�������ֲ����������������ϵ
	 * @param label ���
	 * @param id �����������
	 */
	public static void add(String label, int id) {
		labelToId.put(label, id);
		labelToName.put(label, labelCnt++);
	}

	/**
	 * ��ȡһ��������ڵ�������
	 * @param label ���
	 * @return ������
	 */
	public static int getId(String label) {
		return labelToId.get(label);
	}

	/**
	 * ��ȡһ���ֲ���ŵ�ȫ������
	 * @param label ���
	 * @return ȫ�����
	 */
	public static int getName(String label) {
		return labelToName.get(label);
	}

}
