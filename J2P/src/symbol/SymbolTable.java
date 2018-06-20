package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import j2p.Graph;

/**
 * 符号表的维护类。此类为单例类，变量和方法均为static。
 *
 * @author castor_v_pollux
 */
public final class SymbolTable {

	/**
	 * MiniJava的类列表
	 */
	private static HashMap<String, MClass> mClasses = new HashMap<>();

	/**
	 * MiniJava的main函数
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
	 * <p>该方法检查所有类的继承关系。步骤为：
	 * <ul>
	 * <li>1.对类的继承关系进行拓扑排序
	 * <li>2.使用步骤1中得到的拓扑顺序，自顶向下让子类继承父类的变量和方法。
	 * </ul>
	 * <p>拓扑排序用到的工具类为{@link Graph}
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
