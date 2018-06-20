package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import minijava.ErrorManager;
import minijava.Graph;
import minijava.TypeCheckError;

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
	 * <p>该方法检查所有类的继承关系。步骤为：
	 * <ul>
	 * <li>1.对类的继承关系进行拓扑排序，若继承关系中存在环，报错；
	 * <li>2.使用步骤1中得到的拓扑顺序，自顶向下让子类继承父类的变量和方法。
	 * </ul>
	 * <p>拓扑排序用到的工具类为{@link Graph}
	 * <p>该方法中可以查出的类型错误有：
	 * <ul>
	 * <li>类的循环继承：继承关系中出现环
	 * <li>不合法的方法重写：子类定义了和父类同名但不同签名（包括参数列表和返回值）的方法
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
