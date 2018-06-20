package visitor;

import java.util.ArrayList;
import java.util.Enumeration;

import piglet.PAllocate;
import piglet.PBinOp;
import piglet.PBinOp.PBinOpType;
import piglet.PCJumpStmt;
import piglet.PCall;
import piglet.PCode;
import piglet.PErrorStmt;
import piglet.PExp;
import piglet.PGoal;
import piglet.PInteger;
import piglet.PJumpStmt;
import piglet.PLabel;
import piglet.PLoadStmt;
import piglet.PMoveStmt;
import piglet.PNoOpStmt;
import piglet.PPrintStmt;
import piglet.PProcedure;
import piglet.PStmt;
import piglet.PStmtExp;
import piglet.PStmtList;
import piglet.PStoreStmt;
import piglet.PTemp;
import symbol.MClass;
import symbol.MIdentifier;
import symbol.MMethod;
import symbol.SymbolTable;
import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionRest;
import syntaxtree.FalseLiteral;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.Node;
import syntaxtree.NodeListOptional;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.TypeDeclaration;
import syntaxtree.WhileStatement;

/**
 * <p>����Ϊһ��{@code Visitor}��������Minijava��䷭���piglet��䡣
 * <p>���{@code Visitor}ֻ��ÿ����������䲿�ֽ��б�����������������װ�����д��ݵ��ϲ㡣
 * <p>ʹ�ô�����ֵ��������{@link GJDepthFirst}��Ϊ����{@code Visitor}ԭ��Ϊ��
 * <ul>
 * <li>�ڷ������ʱ��Ҫ�������ڵķ�����ȷ��������
 * <li>�������ķ�����Ҫ���ذ�װpiglet����{@link PCode}����
 * </ul>
 *
 * @author castor_v_pollux
 */
public class TranslateVisitor extends GJDepthFirst<Object, MIdentifier> {

	/**
	 * Ϊ�˽�(A*)���б���õ���piglet�������ȫ�������ϲ㣬���������˶�{@link NodelistOptional}�Ĵ����������нڵ������б�
	 */
	public Object visit(NodeListOptional n, MIdentifier argu) {
		ArrayList<Object> array = new ArrayList<>();
		if (n.present())
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
				array.add(e.nextElement().accept(this, argu));
		return array;
	}

	/**
	 * ����{@link Goal}��ʹ��main�����е�����������ķ��������{@link PGoal}���󲢷���
	 */
	public Object visit(Goal n, MIdentifier argu) {
		PStmtList main = (PStmtList) n.f0.accept(this, argu);
		PGoal goal = new PGoal(main);
		ArrayList<Object> classList = (ArrayList<Object>) n.f1.accept(this, argu);
		for (Object obj : classList)
			goal.addAll((ArrayList<PProcedure>) obj);
		return goal;
	}

	/**
	 * ����{@link MainClass}��������乹���{@link PStmtList}������
	 */
	public Object visit(MainClass n, MIdentifier argu) {
		PStmtList stmtList = new PStmtList();
		ArrayList<Object> stmts = (ArrayList<Object>) n.f15.accept(this, SymbolTable.getMainMethod());
		for (Object obj : stmts) {
			if (obj instanceof PStmt)
				stmtList.add((PStmt) obj);
			else
				stmtList.addAll((ArrayList<PStmt>) obj);
		}
		return stmtList;
	}

	/**
	 * ��������һ��ͬ��:)
	 */
	public Object visit(TypeDeclaration n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * �����࣬�����䷽�������صõ���{@code ArrayList<PProcedure>}
	 */
	public Object visit(ClassDeclaration n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		return n.f4.accept(this, clazz);
	}

	/**
	 * �����࣬�����䷽�������صõ���{@code ArrayList<PProcedure>}
	 */
	public Object visit(ClassExtendsDeclaration n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		return n.f6.accept(this, clazz);
	}

	/**
	 * <p>����һ��������������乹���{@link PStmtList}����Ϸ��صı��ʽ�����{@link PProcedure}���ص��ϲ㡣
	 * <p>������������Ӧ����1��this��Ϊ�������룩��Ȼ��������20���Ϊ20������Ĳ���ͨ������ռ䴫�ݣ���{@link TranslateVisitor#visit(MessageSend, MIdentifier)}��
	 */
	public Object visit(MethodDeclaration n, MIdentifier argu) {
		MMethod method = (MMethod) n.f2.accept(this, argu);
		PStmtList stmtList = new PStmtList();
		method.initPVar(stmtList);
		ArrayList<Object> stmts = (ArrayList<Object>) n.f8.accept(this, method);
		for (Object obj : stmts) {
			// Minijava�ĵ��������ܱ������piglet�ĵ����������
			if (obj instanceof PStmt)
				stmtList.add((PStmt) obj);
			else
				stmtList.addAll((ArrayList<PStmt>) obj);
		}
		int paramCnt = method.getArgumentCount() + 1;
		return new PProcedure(method.getFullName(), paramCnt > 20 ? 20 : paramCnt,
				new PStmtExp(stmtList, (PExp) n.f10.accept(this, method)));
	}

	/**
	 * ����ֵ����Ϊ{@code PStmt}��Ҳ����Ϊ{@code ArrayList<PStmt>}
	 */
	public Object visit(Statement n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * ��Block��ÿ����䷭��Ľ�����ϳ�{@code ArrayList<PStmt>}
	 */
	public Object visit(Block n, MIdentifier argu) {
		ArrayList<PStmt> pStmtList = new ArrayList<>();
		ArrayList<Object> stmts = (ArrayList<Object>) n.f1.accept(this, argu);
		for (Object obj : stmts) {
			// Minijava�ĵ��������ܱ������piglet�ĵ����������
			if (obj instanceof PStmt)
				pStmtList.add((PStmt) obj);
			else
				pStmtList.addAll((ArrayList<PStmt>) obj);
		}
		return pStmtList;
	}

	/*----����Ϊ�������ͱ��ʽ�ķ�������----*/

	/**
	 * <p>���ڸ�ֵ��䣬��Ҫ������ֵ����ѡ��һ������䷭�룺
	 * <ul>
	 * <li>����ֵΪTEMP���緽���ֲ�������ǰ19����������ֱ��ʹ��MOVE��䷭��
	 * <li>����ֵΪ�ڴ�λ�ã�������򣬳���19����������ʹ��HSTORE��䷭��
	 * </ul>
	 * <p>���ڵõ���ֵ�������{@link PExp}�󣬵�{@link MMethod}�и��ݲ�ͬ�ı������з��롣
	 */
	public Object visit(AssignmentStatement n, MIdentifier argu) {
		return ((MMethod) argu).putVarPExp(n.f0.f0.tokenImage, (PExp) n.f2.accept(this, argu));
	}

	/**
	 * <p>�������鸳ֵ��䣬��Ҫ�������²�����
	 * <ul>
	 * <li>��ȡ�����ַ
	 * <li>�������ǿգ�Ȼ���ȡ���鳤��
	 * <li>����±��Ƿ�Խ��
	 * <li>ʹ��HStore�����ɸ�ֵ
	 * </ul>
	 */
	public Object visit(ArrayAssignmentStatement n, MIdentifier argu) {
		ArrayList<PStmt> stmts = new ArrayList<>();
		PTemp array = PTemp.newTemp();
		PTemp len = PTemp.newTemp();
		PTemp index = PTemp.newTemp();
		stmts.add(new PMoveStmt(array, (PExp) n.f0.accept(this, argu)));
		// Check array nonnull and get array length
		PLabel ok1 = PLabel.newLabel();
		stmts.add(new PCJumpStmt(new PBinOp(PBinOpType.PLUS, array, new PInteger(1)), ok1));
		stmts.add(PErrorStmt.getInstance());
		stmts.add(ok1);
		stmts.add(new PLoadStmt(len, array, 0));
		// index = exp
		stmts.add(new PMoveStmt(index, (PExp) n.f2.accept(this, argu)));
		// Check 0<=index<length
		PLabel error = PLabel.newLabel();
		PLabel ok2 = PLabel.newLabel();
		PLabel ok3 = PLabel.newLabel();
		stmts.add(new PCJumpStmt(new PBinOp(PBinOpType.LT, index, new PInteger(0)), ok2));
		stmts.add(PErrorStmt.getInstance());
		stmts.add(ok2);
		stmts.add(new PCJumpStmt(new PBinOp(PBinOpType.LT, index, len), error));
		stmts.add(new PJumpStmt(ok3));
		stmts.add(error);
		stmts.add(PErrorStmt.getInstance());
		stmts.add(ok3);
		stmts.add(new PStoreStmt(new PBinOp(PBinOpType.PLUS, array, new PBinOp(PBinOpType.TIMES,
				index, new PInteger(4))), 4,
				(PExp) n.f5.accept(this, argu)));
		return stmts;
	}

	/**
	 * ʹ��CJump��������ת����
	 */
	public Object visit(IfStatement n, MIdentifier argu) {
		ArrayList<PStmt> stmts = new ArrayList<>();
		PLabel elze = PLabel.newLabel();
		PLabel end = PLabel.newLabel();
		stmts.add(new PCJumpStmt((PExp) n.f2.accept(this, argu), elze));
		Object obj = n.f4.accept(this, argu);
		if (obj instanceof PStmt)
			stmts.add((PStmt) obj);
		else
			stmts.addAll((ArrayList<PStmt>) obj);
		stmts.add(new PJumpStmt(end));
		stmts.add(elze);
		obj = n.f6.accept(this, argu);
		if (obj instanceof PStmt)
			stmts.add((PStmt) obj);
		else
			stmts.addAll((ArrayList<PStmt>) obj);
		stmts.add(end);
		stmts.add(PNoOpStmt.getInstance());
		return stmts;
	}

	/**
	 * ѭ����䣬ʹ��CJump��Jump��������ת
	 */
	public Object visit(WhileStatement n, MIdentifier argu) {
		ArrayList<PStmt> stmts = new ArrayList<>();
		PLabel start = PLabel.newLabel();
		PLabel end = PLabel.newLabel();
		stmts.add(start);
		stmts.add(new PCJumpStmt((PExp) n.f2.accept(this, argu), end));
		Object obj = n.f4.accept(this, argu);
		if (obj instanceof PStmt)
			stmts.add((PStmt) obj);
		else
			stmts.addAll((ArrayList<PStmt>) obj);
		stmts.add(new PJumpStmt(start));
		stmts.add(end);
		stmts.add(PNoOpStmt.getInstance());
		return stmts;
	}

	/**
	 * ����Print��ӡ���
	 */
	public Object visit(PrintStatement n, MIdentifier argu) {
		PExp exp = (PExp) n.f2.accept(this, argu);
		return new PPrintStmt(exp);
	}

	/**
	 * �﷨�������Ǵ���ͬ�ı��ʽ
	 */
	public Object visit(Expression n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * ����&&���ʽ��ע�����������Ϊ�棬��ִ���Ҳ������������Ҫ��ǰ��ת
	 */
	public Object visit(AndExpression n, MIdentifier argu) {
		PStmtList stmtList = new PStmtList();
		PTemp tmp = PTemp.newTemp();
		PLabel label = PLabel.newLabel();
		stmtList.add(new PMoveStmt(tmp, new PInteger(0)))
				.add(new PCJumpStmt((PExp) n.f0.accept(this, argu), label))
				.add(new PCJumpStmt((PExp) n.f2.accept(this, argu), label))
				.add(new PMoveStmt(tmp, new PInteger(1)))
				.add(label)
				.add(PNoOpStmt.getInstance());
		return new PStmtExp(stmtList, tmp);
	}

	/**
	 * ����<���ʽ��ֱ��ʹ�ö�Ӧ��BinOp���
	 */
	public Object visit(CompareExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.LT, exp1, exp2);
	}

	/**
	 * ����+���ʽ��ֱ��ʹ�ö�Ӧ��BinOp���
	 */
	public Object visit(PlusExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.PLUS, exp1, exp2);
	}

	/**
	 * ����-���ʽ��ֱ��ʹ�ö�Ӧ��BinOp���
	 */
	public Object visit(MinusExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.MINUS, exp1, exp2);
	}

	/**
	 * ����*���ʽ��ֱ��ʹ�ö�Ӧ��BinOp���
	 */
	public Object visit(TimesExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.TIMES, exp1, exp2);
	}

	/**
	 * ��������ȡֵ��䣬��Ҫ�������²�����
	 * <ul>
	 * <li>�������ǿղ���ȡ���鳤��
	 * <li>����±�Խ��
	 * <li>ͨ��HStore����ȡֵ
	 * </ul>
	 */
	public Object visit(ArrayLookup n, MIdentifier argu) {
		PStmtList stmtList = new PStmtList();
		PTemp array = PTemp.newTemp();
		PTemp len = PTemp.newTemp();
		PLabel ok1 = PLabel.newLabel();
		// Check array nonnull and get array length
		stmtList.add(new PMoveStmt(array, (PExp) n.f0.accept(this, argu)))
				.add(new PCJumpStmt(new PBinOp(PBinOpType.PLUS, array, new PInteger(1)), ok1))
				.add(PErrorStmt.getInstance())
				.add(ok1)
				.add(new PLoadStmt(len, array, 0));
		// Check 0<=index<length and get value
		PTemp index = PTemp.newTemp();
		PTemp value = PTemp.newTemp();
		PLabel error = PLabel.newLabel();
		PLabel ok2 = PLabel.newLabel();
		PLabel ok3 = PLabel.newLabel();
		stmtList.add(new PMoveStmt(index, (PExp) n.f2.accept(this, argu)))
				.add(new PCJumpStmt(new PBinOp(PBinOpType.LT, index, new PInteger(0)), ok2))
				.add(PErrorStmt.getInstance())
				.add(ok2)
				.add(new PCJumpStmt(new PBinOp(PBinOpType.LT, index, len), error))
				.add(new PJumpStmt(ok3))
				.add(error)
				.add(PErrorStmt.getInstance())
				.add(ok3)
				.add(new PLoadStmt(value,
						new PBinOp(PBinOpType.PLUS, array, new PBinOp(PBinOpType.TIMES, index, new PInteger(4))), 4));
		return new PStmtExp(stmtList, value);
	}

	/**
	 * �������鳤�ȣ��������ǿղ�ʹ��HSTORE����ȡ����
	 */
	public Object visit(ArrayLength n, MIdentifier argu) {
		PStmtList stmtList = new PStmtList();
		PTemp array = PTemp.newTemp();
		PTemp len = PTemp.newTemp();
		PLabel ok = PLabel.newLabel();
		// Check array nonnull and get array length
		stmtList.add(new PMoveStmt(array, (PExp) n.f0.accept(this, argu)))
				.add(new PCJumpStmt(new PBinOp(PBinOpType.PLUS, array, new PInteger(1)), ok))
				.add(PErrorStmt.getInstance())
				.add(ok)
				.add(new PLoadStmt(len, array, 0));
		return new PStmtExp(stmtList, len);
	}

	/**
	 * ���ڷ������ã���Ҫ�����²�����
	 * <ul>
	 * <li>������ǿ�
	 * <li>��dTable�л�ȡ������ַ
	 * <li>�������������ʽ����CALL����һ������Ϊ��������
	 * <li>�����������19��������һ����ʱ�ռ䣬������Ĳ���������ʱ�ռ䣬Ȼ�󽫵�ַ��Ϊ��20������
	 * </ul>
	 */
	public Object visit(MessageSend n, MIdentifier argu) {
		PStmtList stmtList = new PStmtList();
		PTemp dTable = PTemp.newTemp();
		PTemp func = PTemp.newTemp();
		PTemp obj = PTemp.newTemp();
		PLabel ok = PLabel.newLabel();
		PExp exp = (PExp) n.f0.accept(this, argu);
		MMethod method = (MMethod) n.f2.accept(this, (MClass) exp.getType());
		int id = method.getId();
		// Check obj nonnull and get method address
		stmtList.add(new PMoveStmt(obj, exp))
				.add(new PCJumpStmt(new PBinOp(PBinOpType.PLUS, obj, new PInteger(1)), ok))
				.add(PErrorStmt.getInstance())
				.add(ok)
				.add(new PLoadStmt(dTable, obj, 0))
				.add(new PLoadStmt(func, dTable, id * 4));
		PCall call = new PCall(new PStmtExp(stmtList, func));
		// If param cnt exceeds 19, allocate space and pass pointer by the 20th param
		ArrayList<Object> paramList = (ArrayList<Object>) n.f4.accept(this, argu);
		if (paramList == null)
			paramList = new ArrayList<>();
		call.addParam(obj);
		int l = paramList.size();
		if (l < 20)
			for (Object param : paramList)
				call.addParam((PExp) param);
		else {
			for (int i = 0; i < 18; i++)
				call.addParam((PExp) paramList.get(i));
			PTemp extra = PTemp.newTemp();
			PStmtList stmts = new PStmtList();
			stmts.add(new PMoveStmt(extra, new PAllocate(new PInteger((l - 18) * 4))));
			for (int i = 18; i < l; i++)
				stmts.add(new PStoreStmt(extra, (i - 18) * 4, (PExp) paramList.get(i)));
			call.addParam(new PStmtExp(stmts, extra));
		}
		return call.setType(method.getReturnType());
	}

	/**
	 * �Բ����б�Ĵ���
	 */
	public Object visit(ExpressionList n, MIdentifier argu) {
		ArrayList<Object> array = (ArrayList<Object>) n.f1.accept(this, argu);
		array.add(0, n.f0.accept(this, argu));
		return array;
	}

	/**
	 * �Ե��������Ĵ���
	 */
	public Object visit(ExpressionRest n, MIdentifier argu) {
		return n.f1.accept(this, argu);
	}

	/**
	 * �﷨�������Ǵ���ͬ�ı��ʽ
	 */
	public Object visit(PrimaryExpression n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * ������������
	 */
	public Object visit(IntegerLiteral n, MIdentifier argu) {
		return new PInteger(Integer.valueOf(n.f0.tokenImage));
	}

	/**
	 * ����bool������trueΪ1
	 */
	public Object visit(TrueLiteral n, MIdentifier argu) {
		return new PInteger(1);
	}

	/**
	 * ����bool������falseΪ0
	 */
	public Object visit(FalseLiteral n, MIdentifier argu) {
		return new PInteger(0);
	}

	/**
	 * <p>���ڱ�ʶ������Ϊ���¼��������
	 * <ul>
	 * <li>���ñ�ʶ����ʾһ���ࣨ�������{@code argu}Ϊ{@link MIdentifier#TAG_CLASS}�����ӷ��ű����ҵ���Ӧ�ࣨ{@link SymbolTable#findClass(String)}��������{@link MClass}����
	 * <li>���ñ�ʶ����ʾһ���������������{@code argu}Ϊ{@link MClass}���ͣ����Ӹ������ҵ���Ӧ������{@link MClass#findMethod(String)}��������{@link MMethod}����
	 * <li>���ñ�ʶ����ʾһ���������������{@code argu}Ϊ{@link MMethod}���ͣ����Ӹ÷����еõ��ñ�����piglet���ʽ����{@link MMethod#getVarPExp(String)}��
	 * </ul>
	 */
	public Object visit(Identifier n, MIdentifier argu) {
		if (argu == MIdentifier.TAG_CLASS)
			return SymbolTable.findClass(n.f0.tokenImage);
		else if (argu instanceof MClass) {
			MClass clazz = (MClass) argu;
			return clazz.findMethod(n.f0.tokenImage);
		} else if (argu instanceof MMethod) {
			MMethod method = (MMethod) argu;
			return method.getVarPExp(n.f0.tokenImage);
		} else
			return null;// It's impossible!
	}

	/**
	 * ����TEMP 0����Ҫ����һ����������
	 */
	public Object visit(ThisExpression n, MIdentifier argu) {
		return PTemp.valueOf(0).setType(((MMethod) argu).getScope());
	}

	/**
	 * <p>���������new���ʽ����Ҫ�������²�����
	 * <ul>
	 * <li>������鳤���Ƿ�Ϊ��
	 * <li>����4*len+4�ֽڿռ䣬ǰ4�ֽ����������鳤��
	 * <li>ѭ������������
	 * </ul>
	 */
	public Object visit(ArrayAllocationExpression n, MIdentifier argu) {
		PStmtList stmtList = new PStmtList();
		PTemp len = PTemp.newTemp();
		PTemp array = PTemp.newTemp();
		// len = exp
		stmtList.add(new PMoveStmt(len, (PExp) n.f3.accept(this, argu)));
		// Check len >= 0 and allocate space, otherwise error
		PLabel error = PLabel.newLabel();
		stmtList.add(new PCJumpStmt(new PBinOp(PBinOpType.LT, len, new PInteger(0)), error))
				.add(PErrorStmt.getInstance())
				.add(error)
				.add(new PMoveStmt(array, new PAllocate(new PBinOp(PBinOpType.TIMES,
						new PBinOp(PBinOpType.PLUS, len, new PInteger(1)), new PInteger(4)))));
		// Loop to memset array to 0
		PTemp loop = PTemp.newTemp();
		PLabel start = PLabel.newLabel();
		PLabel end = PLabel.newLabel();
		stmtList.add(new PMoveStmt(loop, new PInteger(4)))
				.add(start)
				.add(new PCJumpStmt(new PBinOp(PBinOpType.LT, loop, new PBinOp(PBinOpType.TIMES,
						new PBinOp(PBinOpType.PLUS, len, new PInteger(1)), new PInteger(4))), end))
				.add(new PStoreStmt(new PBinOp(PBinOpType.PLUS, array, loop), 0, new PInteger(0)))
				.add(new PMoveStmt(loop, new PBinOp(PBinOpType.PLUS, loop, new PInteger(4))))
				.add(new PJumpStmt(start))
				.add(end)
				.add(new PStoreStmt(array, 0, len));
		return new PStmtExp(stmtList, array);
	}

	/**
	 * ���ڶ����new���ʽ����������д���{@link MClass#getNewPExp()}
	 */
	public Object visit(AllocationExpression n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		return clazz.getNewPExp();
	}

	/**
	 * ����ȡ�����ʽ������1-exp
	 */
	public Object visit(NotExpression n, MIdentifier argu) {
		PExp exp = (PExp) n.f1.accept(this, argu);
		return new PBinOp(PBinOpType.MINUS, new PInteger(1), exp);
	}

	/**
	 * pigletΪǰ׺���ʽ���������û�б�Ҫ
	 */
	public Object visit(BracketExpression n, MIdentifier argu) {
		return n.f1.accept(this, argu);
	}

}
