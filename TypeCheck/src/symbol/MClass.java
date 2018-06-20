package symbol;

import java.util.HashMap;
import java.util.Map.Entry;

import minijava.ErrorManager;
import minijava.TypeCheckError;

/**
 * 该类表示一个MiniJava中的类。继承自{@link MIdentifier}并且实现了{@link MType}接口。
 *
 * @author castor_v_pollux
 */
public class MClass extends MIdentifier implements MType {

	/**
	 * 类的域列表
	 */
	private HashMap<String, MVar> fields = new HashMap<>();

	/**
	 * 类的方法列表
	 */
	private HashMap<String, MMethod> methods = new HashMap<>();

	/**
	 * 可能的父类
	 */
	private MClass superClass = null;

	public MClass(String name, int row, int column) {
		super(name, row, column);
	}

	public void setSuper(MClass clazz) {
		superClass = clazz;
	}

	public MClass getSuper() {
		return superClass;
	}

	public void addField(MVar var) {
		if (fields.containsKey(var.getName())) {
			ErrorManager.error(
					new TypeCheckError(String.format("Duplicate field '%s' in type '%s'", var.getName(), getName()),
							var.getRow(), var.getColumn()));
		} else {
			fields.put(var.getName(), var);
		}
	}

	public MVar findField(String name) {
		return fields.get(name);
	}

	public void addMethod(MMethod method) {
		if (methods.containsKey(method.getName()))
			ErrorManager.error(
					new TypeCheckError(String.format("Duplicate method '%s' in type '%s'", method.getName(), getName()),
							method.getRow(), method.getColumn()));
		else
			methods.put(method.getName(), method);
	}

	public MMethod findMethod(String name) {
		return methods.get(name);
	}

	/**
	 * 继承父类的域和方法
	 */
	public void copyFromSuper() {
		if (superClass == null)
			return;
		for (Entry<String, MMethod> entry : methods.entrySet()) {
			String name = entry.getKey();
			MMethod method = entry.getValue();
			/*
			 * Check: Is there a method in super class with the same name but different
			 * signature?
			 */
			MMethod m = superClass.findMethod(name);
			if (m != null && !m.equals(method))
				ErrorManager.error(
						new TypeCheckError(String.format("Method overriding fails: incompatible signature with '%s.%s'",
								m.getScope().getName(), name), method.getRow(), method.getColumn()));
		}
		HashMap<String, MVar> newFields = (HashMap<String, MVar>) superClass.fields.clone();
		newFields.putAll(fields);
		fields = newFields;
		HashMap<String, MMethod> newMethods = (HashMap<String, MMethod>) superClass.methods.clone();
		newMethods.putAll(methods);
		methods = newMethods;
	}

	/**
	 * 对于引用类型，能够类型转换的条件是必须为子类与父类的关系。
	 */
	@Override
	public boolean canConvertTo(MType type) {
		if (type instanceof MClass) {
			MClass clazz = (MClass) type;
			MClass t = this;
			while (t != null) {
				if (t == clazz)
					return true;
				t = t.getSuper();
			}
		}
		return false;
	}

}