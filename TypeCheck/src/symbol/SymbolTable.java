package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import minijava.ErrorManager;
import minijava.Graph;
import minijava.TypeCheckError;

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
		if (mClasses.containsKey(clazz.getName())) {
			ErrorManager.error(new TypeCheckError(String.format("The type '%s' is already defined", clazz.getName()),
					clazz.getRow(), clazz.getColumn()));
		} else {
			mClasses.put(clazz.getName(), clazz);
		}
	}

	public static MClass findClass(String name) {
		return mClasses.get(name);
	}

	/**
	 * <p>�÷������������ļ̳й�ϵ������Ϊ��
	 * <ul>
	 * <li>1.����ļ̳й�ϵ���������������̳й�ϵ�д��ڻ�������
	 * <li>2.ʹ�ò���1�еõ�������˳���Զ�����������̳и���ı����ͷ�����
	 * </ul>
	 * <p>���������õ��Ĺ�����Ϊ{@link Graph}
	 * <p>�÷����п��Բ�������ʹ����У�
	 * <ul>
	 * <li>���ѭ���̳У��̳й�ϵ�г��ֻ�
	 * <li>���Ϸ��ķ�����д�����ඨ���˺͸���ͬ������ͬǩ�������������б�ͷ���ֵ���ķ���
	 * </ul>
	 */
	public static void checkClassExtension() {
		// First TopSort the classes to ensure there is no extending cycle.
		// Then pass fields and methods down in topological order.
		Graph<MClass> g = new Graph<>();
		for (MClass clazz : mClasses.values())
			if (clazz.getSuper() == null)
				g.addNode(clazz);
			else
				g.addEdge(clazz.getSuper(), clazz);
		ArrayList<MClass> order = null;
		try {
			order = g.topSort();
		} catch (TypeCheckError e) {
			ErrorManager.error(e);
			return;
		}
		for (MClass clazz : order)
			clazz.copyFromSuper();
	}

}
