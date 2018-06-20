package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import minijava.ErrorManager;
import minijava.TypeCheckError;

/**
 * �����ʾһ��MiniJava�еķ������̳���{@link MIdentifier}��
 *
 * @author castor_v_pollux
 */
public class MMethod extends MIdentifier {

	/**
	 * �����ľֲ������б�
	 */
	private HashMap<String, MVar> vars = new HashMap<>();

	/**
	 * �����Ĳ����б�
	 */
	private ArrayList<MVar> arguments = new ArrayList<>();

	/**
	 * ��������
	 */
	private MType returnType = null;

	/**
	 * �������ڵ��ࣨ����main����Ϊnull��
	 */
	private MClass scope = null;

	public MMethod(String name, int row, int column) {
		super(name, row, column);
	}

	public MType getReturnType() {
		return returnType;
	}

	public void setReturnType(MType returnType) {
		this.returnType = returnType;
	}

	public MClass getScope() {
		return scope;
	}

	public void setScope(MClass scope) {
		this.scope = scope;
	}

	public void addVar(MVar var) {
		if (arguments.contains(var) || vars.containsKey(var.getName())) {
			ErrorManager.error(new TypeCheckError(String.format("Duplicate local variable '%s'", var.getName()),
					var.getRow(), var.getColumn()));
		} else {
			vars.put(var.getName(), var);
		}
	}

	public void addArgument(MVar arg) {
		if (arguments.contains(arg)) {
			ErrorManager.error(new TypeCheckError(String.format("Duplicate parameter '%s'", arg.getName()),
					arg.getRow(), arg.getColumn()));
		} else {
			arguments.add(arg);
		}
	}

	/**
	 * �����ú���ʱ�Ĳ����б��Ƿ�ƥ��
	 * @param args ���õĲ����б�
	 * @return �Ƿ�ƥ��
	 */
	public boolean checkArgument(ArrayList<Object> args) {
		if (args.size() != arguments.size())
			return false;
		for (int i = 0; i < arguments.size(); i++)
			if (!((MVar) args.get(i)).getType().canConvertTo(arguments.get(i).getType()))
				return false;
		return true;
	}

	/**
	 * Ѱ��һ��ָ���ı��������ڷ����Ĳ����б�;ֲ������б���Ѱ�ң��ٵ���������Ѱ��
	 * @param name ������
	 * @return ������δ�ҵ�����{@code null}
	 */
	public MVar findVar(String name) {
		for (MVar arg : arguments)
			if (arg.getName().equals(name))
				return arg;
		if (vars.containsKey(name))
			return vars.get(name);
		if (scope == null)
			return null;
		return scope.findField(name);
	}

	/**
	 * ����������ͬ���ҽ�������ͬ����ÿһ��������������ͬ�ҷ���ֵ����Ҳ��ͬ
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj != null && obj instanceof MMethod) {
			MMethod m = (MMethod) obj;
			if (!m.getName().equals(getName()))
				return false;
			if (m.getReturnType() != returnType)
				return false;
			if (m.arguments.size() != arguments.size())
				return false;
			for (int i = 0; i < arguments.size(); i++)
				if (m.arguments.get(i).getType() != arguments.get(i).getType())
					return false;
			return true;
		}
		return false;
	}

}
