package symbol;

import java.util.ArrayList;
import java.util.HashMap;

import piglet.PAllocate;
import piglet.PBinOp;
import piglet.PBinOp.PBinOpType;
import piglet.PCJumpStmt;
import piglet.PExp;
import piglet.PIdentifier;
import piglet.PInteger;
import piglet.PJumpStmt;
import piglet.PLabel;
import piglet.PLoadStmt;
import piglet.PMoveStmt;
import piglet.PStmt;
import piglet.PStmtExp;
import piglet.PStmtList;
import piglet.PStoreStmt;
import piglet.PTemp;

/**
 * <p>该类表示一个MiniJava中的类。继承自{@link MIdentifier}并且实现了{@link MType}接口。
 * <p>建立符号表时为域和方法按顺序分配id，在处理类继承时与父类同名的域复制，同名方法替代，并建立{@code HashMap}便于翻译时的快速查找
 *
 * @author castor_v_pollux
 */
public class MClass extends MIdentifier implements MType {

	/**
	 * 类的域列表
	 */
	private HashMap<String, MVar> fields = new HashMap<>();
	private ArrayList<MVar> fieldList = new ArrayList<>();

	/**
	 * 类的方法列表
	 */
	private HashMap<String, MMethod> methods = new HashMap<>();
	private ArrayList<MMethod> methodList = new ArrayList<>();

	/**
	 * 可能的父类
	 */
	private MClass superClass = null;

	public MClass(String name) {
		super(name);
	}

	public void setSuper(MClass clazz) {
		superClass = clazz;
	}

	public MClass getSuper() {
		return superClass;
	}

	/**
	 * 建符号表时仅仅存入ArrayList中，域的标号从1开始
	 * @param field 域
	 */
	public void addField(MVar field) {
		fieldList.add(field);
		field.setId(fieldList.size());
	}

	public MVar findField(String name) {
		return fields.get(name);
	}

	/**
	 * 建符号表时仅仅存入ArrayList中，方法的编号从0开始
	 * @param method 方法
	 */
	public void addMethod(MMethod method) {
		method.setId(methodList.size());
		methodList.add(method);
	}

	public MMethod findMethod(String name) {
		return methods.get(name);
	}

	/**
	 * <p>继承父类的域和方法：对于域，需要为每个新增的域分配id，对于方法，需要将重名方法进行替代，保留原来的id
	 * <p>同时，将HashMap建立起来以提供快速查找功能
	 */
	public void copyFromSuper() {
		if (superClass == null) {
			for (MVar field : fieldList)
				fields.put(field.getName(), field);
			for (MMethod method : methodList)
				methods.put(method.getName(), method);
			return;
		}
		fields = (HashMap<String, MVar>) superClass.fields.clone();
		int fieldCnt = fields.size();
		for (MVar field : fieldList) {
			field.setId(++fieldCnt);
			fields.put(field.getName(), field);
		}
		methods = (HashMap<String, MMethod>) superClass.methods.clone();
		int methodCnt = methods.size();
		for (MMethod method : methodList) {
			if (methods.containsKey(method.getName())) {
				method.setId(methods.get(method.getName()).getId());
			} else {
				method.setId(methodCnt++);
			}
			methods.put(method.getName(), method);
		}
		methodList.clear();
		for (MMethod method : methods.values())
			methodList.add(method);
		methodList.sort((o1, o2) -> o1.getId() - o2.getId());
	}

	/**
	 * 获取与new表达式等价的piglet表达式，需要做以下事情：
	 * <ul>
	 * <li>分配一个方法表
	 * <li>分配一个属性表，使用循环将各个属性设为0
	 * <li>将属性表第一位设置为方法表地址
	 * </ul>
	 * @return piglet表达式
	 */
	public PStmtExp getNewPExp() {
		PStmtList stmtList = new PStmtList();
		PTemp dTable = PTemp.newTemp();
		PTemp vTable = PTemp.newTemp();
		int l;
		// Initialize dTable
		l = methodList.size();
		if (l > 0) {
			stmtList.add(new PMoveStmt(dTable, new PAllocate(new PInteger(l * 4))));
			for (MMethod method : methodList) {
				stmtList.add(new PStoreStmt(dTable, method.getId() * 4, new PIdentifier(method.getFullName())));
			}
		} else
			stmtList.add(new PMoveStmt(dTable, new PInteger(0)));
		// Initialize vTable
		l = fields.size();
		stmtList.add(new PMoveStmt(vTable, new PAllocate(new PInteger(4 + l * 4))));
		if (l > 0) {
			PTemp loop = PTemp.newTemp();
			PLabel start = PLabel.newLabel();
			PLabel end = PLabel.newLabel();
			// Loop to memset vTable to 0
			stmtList.add(new PMoveStmt(loop, new PInteger(4))).add(start)
					.add(new PCJumpStmt(new PBinOp(PBinOpType.LT, loop, new PInteger(4 + 4 * l)), end))
					.add(new PStoreStmt(new PBinOp(PBinOpType.PLUS, vTable, loop), 0, new PInteger(0)))
					.add(new PMoveStmt(loop, new PBinOp(PBinOpType.PLUS, loop, new PInteger(4))))
					.add(new PJumpStmt(start))
					.add(end);
		}
		stmtList.add(new PStoreStmt(vTable, 0, dTable));
		return (PStmtExp) new PStmtExp(stmtList, vTable).setType(this);
	}

	/**
	 * 获取一个变量对应的piglet表达式。
	 * @param name 变量在minijava中的名字
	 * @return 与minijava变量等价的piglet表达式
	 */
	public PExp getFieldPExp(String name) {
		if (fields.containsKey(name)) {
			MVar field = fields.get(name);
			PStmtList stmtList = new PStmtList();
			PTemp tmp = PTemp.newTemp();
			stmtList.add(new PLoadStmt(tmp, PTemp.valueOf(0), field.getId() * 4));
			return new PStmtExp(stmtList, tmp).setType(field.getType());
		} else
			return null;// It's impossible!
	}

	/**
	 * 获取一个赋值语句(name=exp)对应的piglet表达式。
	 * @param name 变量在minijava中的名字
	 * @param exp piglet表达式
	 * @return 与minijava赋值语句等价的piglet语句
	 */
	public PStmt putFieldPExp(String name, PExp exp) {
		if (fields.containsKey(name)) {
			MVar field = fields.get(name);
			return new PStoreStmt(PTemp.valueOf(0), field.getId() * 4, exp);
		} else
			return null;// It's impossible!
	}

}