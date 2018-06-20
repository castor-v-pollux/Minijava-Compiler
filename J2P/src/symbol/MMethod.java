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
 * <p>�����ʾһ��MiniJava�еķ������̳���{@link MIdentifier}��
 * <p>id��ʾ����dtable�е�����
 *
 * @author castor_v_pollux
 */
public class MMethod extends MIdentifier {

	/**
	 * �����ľֲ������б�
	 */
	private HashMap<String, MVar> vars = new HashMap<>();
	private ArrayList<MVar> varList = new ArrayList<>();

	/**
	 * �����Ĳ����б�
	 */
	private HashMap<String, MVar> arguments = new HashMap<>();

	/**
	 * ��������
	 */
	private MType returnType = null;

	/**
	 * �������ڵ��ࣨ����main����Ϊnull��
	 */
	private MClass scope = null;

	/**
	 * �������
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
	 * �����ű�ʱΪ�ֲ���������TEMP
	 * @param var ����
	 */
	public void addVar(MVar var) {
		var.setId(PTemp.newTempId());
		vars.put(var.getName(), var);
		varList.add(var);
	}

	/**
	 * �����ű�ʱΪ��������TEMP��TEMP 0Ϊ{@code this}
	 * @param arg ����
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
	 * piglet�����еĺ�����
	 * @return ������ȫ�����޶�����
	 */
	public String getFullName() {
		return String.format("%s_%s", scope.getName(), getName());
	}

	/**
	 * ��ȡ��������
	 * @return ��������
	 */
	public int getArgumentCount() {
		return arguments.size();
	}

	/**
	 * ���ɶԷ����ֲ�������piglet��ʼ����䣬��ÿ���ֲ�������Ҫһ��MOVE���
	 * @param stmtList ������������
	 */
	public void initPVar(PStmtList stmtList) {
		for (MVar var : varList)
			stmtList.add(new PMoveStmt(PTemp.valueOf(var.getId()), new PInteger(0)));
	}

	/**
	 * ��ȡһ��������Ӧ��piglet���ʽ��
	 * @param name ������minijava�е�����
	 * @return ��minijava�����ȼ۵�piglet���ʽ
	 */
	public PExp getVarPExp(String name) {
		// ����Ƿ����ֲ�������ֱ�ӷ���TEMP
		if (vars.containsKey(name)) {
			MVar var = vars.get(name);
			return PTemp.valueOf(var.getId()).setType(var.getType());
		}
		// ����ǲ�������������20������������TEMP�����򷵻�һ�����ڴ��м��ظò�����PStmtExp
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
		// ���������򣬽�����������
		return scope.getFieldPExp(name);
	}

	/**
	 * ��ȡһ����ֵ���(name=exp)��Ӧ��piglet���ʽ��
	 * @param name ������minijava�е�����
	 * @param exp piglet���ʽ
	 * @return ��minijava��ֵ���ȼ۵�piglet���
	 */
	public PStmt putVarPExp(String name, PExp exp) {
		// ����Ƿ����ֲ�������ֱ�ӷ���MOVE���
		if (vars.containsKey(name)) {
			MVar var = vars.get(name);
			return new PMoveStmt(PTemp.valueOf(var.getId()), exp);
		}
		// ����ǲ�������������20������������MOVE��䣬���򷵻�һ�����ڴ���д�ò�����PStoreStmt
		if (arguments.containsKey(name)) {
			MVar arg = arguments.get(name);
			int id = arg.getId();
			if (arguments.size() < 20 || id < 19)
				return new PMoveStmt(PTemp.valueOf(arg.getId()), exp);
			return new PStoreStmt(PTemp.valueOf(19), (id - 19) * 4, exp);
		}
		// ���������򣬽�����������
		return scope.putFieldPExp(name, exp);
	}

}
