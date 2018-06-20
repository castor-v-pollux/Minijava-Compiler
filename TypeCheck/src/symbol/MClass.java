package symbol;

import java.util.HashMap;
import java.util.Map.Entry;

import minijava.ErrorManager;
import minijava.TypeCheckError;

/**
 * �����ʾһ��MiniJava�е��ࡣ�̳���{@link MIdentifier}����ʵ����{@link MType}�ӿڡ�
 *
 * @author castor_v_pollux
 */
public class MClass extends MIdentifier implements MType {

	/**
	 * ������б�
	 */
	private HashMap<String, MVar> fields = new HashMap<>();

	/**
	 * ��ķ����б�
	 */
	private HashMap<String, MMethod> methods = new HashMap<>();

	/**
	 * ���ܵĸ���
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
	 * �̳и������ͷ���
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
	 * �����������ͣ��ܹ�����ת���������Ǳ���Ϊ�����븸��Ĺ�ϵ��
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