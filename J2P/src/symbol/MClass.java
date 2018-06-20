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
 * <p>�����ʾһ��MiniJava�е��ࡣ�̳���{@link MIdentifier}����ʵ����{@link MType}�ӿڡ�
 * <p>�������ű�ʱΪ��ͷ�����˳�����id���ڴ�����̳�ʱ�븸��ͬ�������ƣ�ͬ�����������������{@code HashMap}���ڷ���ʱ�Ŀ��ٲ���
 *
 * @author castor_v_pollux
 */
public class MClass extends MIdentifier implements MType {

	/**
	 * ������б�
	 */
	private HashMap<String, MVar> fields = new HashMap<>();
	private ArrayList<MVar> fieldList = new ArrayList<>();

	/**
	 * ��ķ����б�
	 */
	private HashMap<String, MMethod> methods = new HashMap<>();
	private ArrayList<MMethod> methodList = new ArrayList<>();

	/**
	 * ���ܵĸ���
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
	 * �����ű�ʱ��������ArrayList�У���ı�Ŵ�1��ʼ
	 * @param field ��
	 */
	public void addField(MVar field) {
		fieldList.add(field);
		field.setId(fieldList.size());
	}

	public MVar findField(String name) {
		return fields.get(name);
	}

	/**
	 * �����ű�ʱ��������ArrayList�У������ı�Ŵ�0��ʼ
	 * @param method ����
	 */
	public void addMethod(MMethod method) {
		method.setId(methodList.size());
		methodList.add(method);
	}

	public MMethod findMethod(String name) {
		return methods.get(name);
	}

	/**
	 * <p>�̳и������ͷ�������������ҪΪÿ�������������id�����ڷ�������Ҫ�����������������������ԭ����id
	 * <p>ͬʱ����HashMap�����������ṩ���ٲ��ҹ���
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
	 * ��ȡ��new���ʽ�ȼ۵�piglet���ʽ����Ҫ���������飺
	 * <ul>
	 * <li>����һ��������
	 * <li>����һ�����Ա�ʹ��ѭ��������������Ϊ0
	 * <li>�����Ա��һλ����Ϊ�������ַ
	 * </ul>
	 * @return piglet���ʽ
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
	 * ��ȡһ��������Ӧ��piglet���ʽ��
	 * @param name ������minijava�е�����
	 * @return ��minijava�����ȼ۵�piglet���ʽ
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
	 * ��ȡһ����ֵ���(name=exp)��Ӧ��piglet���ʽ��
	 * @param name ������minijava�е�����
	 * @param exp piglet���ʽ
	 * @return ��minijava��ֵ���ȼ۵�piglet���
	 */
	public PStmt putFieldPExp(String name, PExp exp) {
		if (fields.containsKey(name)) {
			MVar field = fields.get(name);
			return new PStoreStmt(PTemp.valueOf(0), field.getId() * 4, exp);
		} else
			return null;// It's impossible!
	}

}