package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import piglet.PExp;
import piglet.PInteger;
import piglet.PLoadStmt;
import piglet.PMoveStmt;
import piglet.PStmt;
import piglet.PStmtExp;
import piglet.PStmtList;
import piglet.PStoreStmt;
import piglet.PTemp;

/**
 * <p>该类表示一个MiniJava中的方法。继承自{@link MIdentifier}。
 * <p>id表示其在dtable中的索引
 *
 * @author castor_v_pollux
 */
public class MMethod extends MIdentifier {

	/**
	 * 方法的局部变量列表
	 */
	private HashMap<String, MVar> vars = new HashMap<>();
	private ArrayList<MVar> varList = new ArrayList<>();

	/**
	 * 方法的参数列表
	 */
	private HashMap<String, MVar> arguments = new HashMap<>();

	/**
	 * 返回类型
	 */
	private MType returnType = null;

	/**
	 * 方法所在的类（对于main方法为null）
	 */
	private MClass scope = null;

	/**
	 * 方法序号
	 */
	private int id;

	public MMethod(String name) {
		super(name);
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

	/**
	 * 建符号表时为局部变量分配TEMP
	 * @param var 变量
	 */
	public void addVar(MVar var) {
		var.setId(PTemp.newTempId());
		vars.put(var.getName(), var);
		varList.add(var);
	}

	/**
	 * 建符号表时为参数分配TEMP，TEMP 0为{@code this}
	 * @param arg 参数
	 */
	public void addArgument(MVar arg) {
		// Start from 1 (0 for this)
		arguments.put(arg.getName(), arg);
		arg.setId(arguments.size());
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	/**
	 * piglet代码中的函数名
	 * @return 方法的全名（限定名）
	 */
	public String getFullName() {
		return String.format("%s_%s", scope.getName(), getName());
	}

	/**
	 * 获取参数个数
	 * @return 参数个数
	 */
	public int getArgumentCount() {
		return arguments.size();
	}

	/**
	 * 生成对方法局部变量的piglet初始化语句，对每个局部变量需要一个MOVE语句
	 * @param stmtList 方法体的语句组
	 */
	public void initPVar(PStmtList stmtList) {
		for (MVar var : varList)
			stmtList.add(new PMoveStmt(PTemp.valueOf(var.getId()), new PInteger(0)));
	}

	/**
	 * 获取一个变量对应的piglet表达式。
	 * @param name 变量在minijava中的名字
	 * @return 与minijava变量等价的piglet表达式
	 */
	public PExp getVarPExp(String name) {
		// 如果是方法局部变量，直接返回TEMP
		if (vars.containsKey(name)) {
			MVar var = vars.get(name);
			return PTemp.valueOf(var.getId()).setType(var.getType());
		}
		// 如果是参数，若不超过20个参数，返回TEMP，否则返回一个从内存中加载该参数的PStmtExp
		if (arguments.containsKey(name)) {
			MVar arg = arguments.get(name);
			int id = arg.getId();
			if (arguments.size() < 20 || id < 19)
				return PTemp.valueOf(id).setType(arg.getType());
			PStmtList stmtList = new PStmtList();
			PTemp tmp = PTemp.newTemp();
			stmtList.add(new PLoadStmt(tmp, PTemp.valueOf(19), (id - 19) * 4));
			return new PStmtExp(stmtList, tmp).setType(arg.getType());
		}
		// 如果是类的域，交给类来处理
		return scope.getFieldPExp(name);
	}

	/**
	 * 获取一个赋值语句(name=exp)对应的piglet表达式。
	 * @param name 变量在minijava中的名字
	 * @param exp piglet表达式
	 * @return 与minijava赋值语句等价的piglet语句
	 */
	public PStmt putVarPExp(String name, PExp exp) {
		// 如果是方法局部变量，直接返回MOVE语句
		if (vars.containsKey(name)) {
			MVar var = vars.get(name);
			return new PMoveStmt(PTemp.valueOf(var.getId()), exp);
		}
		// 如果是参数，若不超过20个参数，返回MOVE语句，否则返回一个往内存中写该参数的PStoreStmt
		if (arguments.containsKey(name)) {
			MVar arg = arguments.get(name);
			int id = arg.getId();
			if (arguments.size() < 20 || id < 19)
				return new PMoveStmt(PTemp.valueOf(arg.getId()), exp);
			return new PStoreStmt(PTemp.valueOf(19), (id - 19) * 4, exp);
		}
		// 如果是类的域，交给类来处理
		return scope.putFieldPExp(name, exp);
	}

}
