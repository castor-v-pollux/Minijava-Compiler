package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import j2p.Graph;

/**
 * ���ű��ά���ࡣ����Ϊ�����࣬�����ͷ�����Ϊstatic��
 *
 * @author castor_v_pollux
 */
public final class SymbolTable {

	/**
	 * MiniJava�����б�
	 */
	private static HashMap<String, MClass> mClasses = new HashMap<>();

	/**
	 * MiniJava��main����
	 */
	private static MMethod mainMethod;

	public static void setMainMethod(MMethod main) {
		mainMethod = main;
	}

	public static MMethod getMainMethod() {
		return mainMethod;
	}

	public static void addClass(MClass clazz) {
		mClasses.put(clazz.getName(), clazz);
	}

	public static MClass findClass(String name) {
		return mClasses.get(name);
	}

	/**
	 * <p>�÷������������ļ̳й�ϵ������Ϊ��
	 * <ul>
	 * <li>1.����ļ̳й�ϵ������������
	 * <li>2.ʹ�ò���1�еõ�������˳���Զ�����������̳и���ı����ͷ�����
	 * </ul>
	 * <p>���������õ��Ĺ�����Ϊ{@link Graph}
	 */
	public static void performClassExtension() {
		Graph<MClass> g = new Graph<>();
		for (MClass clazz : mClasses.values())
			if (clazz.getSuper() == null)
				g.addNode(clazz);
			else
				g.addEdge(clazz.getSuper(), clazz);
		ArrayList<MClass> order = g.topSort();
		for (MClass clazz : order)
			clazz.copyFromSuper();
	}

}
