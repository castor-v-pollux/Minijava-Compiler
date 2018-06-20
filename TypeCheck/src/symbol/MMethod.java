package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import minijava.ErrorManager;
import minijava.TypeCheckError;

/**
 * 该类表示一个MiniJava中的方法。继承自{@link MIdentifier}。
 *
 * @author castor_v_pollux
 */
public class MMethod extends MIdentifier {

	/**
	 * 方法的局部变量列表
	 */
	private HashMap<String, MVar> vars = new HashMap<>();

	/**
	 * 方法的参数列表
	 */
	private ArrayList<MVar> arguments = new ArrayList<>();

	/**
	 * 返回类型
	 */
	private MType returnType = null;

	/**
	 * 方法所在的类（对于main方法为null）
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
	 * 检查调用函数时的参数列表是否匹配
	 * @param args 调用的参数列表
	 * @return 是否匹配
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
	 * 寻找一个指定的变量，先在方法的参数列表和局部变量列表中寻找，再到所在类中寻找
	 * @param name 变量名
	 * @return 变量，未找到返回{@code null}
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
	 * 两个方法相同当且仅当它们同名、每一个参数的类型相同且返回值类型也相同
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
