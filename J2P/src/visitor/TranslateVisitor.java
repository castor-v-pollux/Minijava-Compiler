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
 * <p>该类为一个{@code Visitor}，用来将Minijava语句翻译成piglet语句。
 * <p>这个{@code Visitor}只对每个方法的语句部分进行遍历，将翻译后的语句封装在类中传递到上层。
 * <p>使用带返回值带参数的{@link GJDepthFirst}作为父类{@code Visitor}原因为：
 * <ul>
 * <li>在访问语句时需要传递所在的方法以确定作用域
 * <li>对于语句的翻译需要返回包装piglet语句的{@link PCode}对象
 * </ul>
 *
 * @author castor_v_pollux
 */
public class TranslateVisitor extends GJDepthFirst<Object, MIdentifier> {

	/**
	 * 为了将(A*)的列表翻译得到的piglet代码对象全部传到上层，这里重载了对{@link NodelistOptional}的处理，返回所有节点结果的列表
	 */
	public Object visit(NodeListOptional n, MIdentifier argu) {
		ArrayList<Object> array = new ArrayList<>();
		if (n.present())
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
				array.add(e.nextElement().accept(this, argu));
		return array;
	}

	/**
	 * 对于{@link Goal}，使用main方法中的语句和所有类的方法构造出{@link PGoal}对象并返回
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
	 * 对于{@link MainClass}，将其语句构造成{@link PStmtList}并返回
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
	 * 对两种类一视同仁:)
	 */
	public Object visit(TypeDeclaration n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * 对于类，遍历其方法，返回得到的{@code ArrayList<PProcedure>}
	 */
	public Object visit(ClassDeclaration n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		return n.f4.accept(this, clazz);
	}

	/**
	 * 对于类，遍历其方法，返回得到的{@code ArrayList<PProcedure>}
	 */
	public Object visit(ClassExtendsDeclaration n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		return n.f6.accept(this, clazz);
	}

	/**
	 * <p>对于一个方法，将其语句构造成{@link PStmtList}，结合返回的表达式构造成{@link PProcedure}返回到上层。
	 * <p>方法参数个数应加上1（this作为参数传入），然后若大于20则减为20（多余的参数通过分配空间传递，见{@link TranslateVisitor#visit(MessageSend, MIdentifier)}）
	 */
	public Object visit(MethodDeclaration n, MIdentifier argu) {
		MMethod method = (MMethod) n.f2.accept(this, argu);
		PStmtList stmtList = new PStmtList();
		method.initPVar(stmtList);
		ArrayList<Object> stmts = (ArrayList<Object>) n.f8.accept(this, method);
		for (Object obj : stmts) {
			// Minijava的单个语句可能被翻译成piglet的单个或多个语句
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
	 * 返回值可能为{@code PStmt}，也可能为{@code ArrayList<PStmt>}
	 */
	public Object visit(Statement n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * 将Block中每个语句翻译的结果整合成{@code ArrayList<PStmt>}
	 */
	public Object visit(Block n, MIdentifier argu) {
		ArrayList<PStmt> pStmtList = new ArrayList<>();
		ArrayList<Object> stmts = (ArrayList<Object>) n.f1.accept(this, argu);
		for (Object obj : stmts) {
			// Minijava的单个语句可能被翻译成piglet的单个或多个语句
			if (obj instanceof PStmt)
				pStmtList.add((PStmt) obj);
			else
				pStmtList.addAll((ArrayList<PStmt>) obj);
		}
		return pStmtList;
	}

	/*----以下为具体语句和表达式的翻译例程----*/

	/**
	 * <p>对于赋值语句，需要根据左值类型选择不一样的语句翻译：
	 * <ul>
	 * <li>若左值为TEMP（如方法局部变量，前19个参数），直接使用MOVE语句翻译
	 * <li>若左值为内存位置（如类的域，超过19个参数），使用HSTORE语句翻译
	 * </ul>
	 * <p>故在得到右值翻译出的{@link PExp}后，到{@link MMethod}中根据不同的变量进行翻译。
	 */
	public Object visit(AssignmentStatement n, MIdentifier argu) {
		return ((MMethod) argu).putVarPExp(n.f0.f0.tokenImage, (PExp) n.f2.accept(this, argu));
	}

	/**
	 * <p>对于数组赋值语句，需要进行如下操作：
	 * <ul>
	 * <li>获取数组地址
	 * <li>检查数组非空，然后获取数组长度
	 * <li>检查下标是否越界
	 * <li>使用HStore语句完成赋值
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
	 * 使用CJump语句进行跳转即可
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
	 * 循环语句，使用CJump和Jump语句进行跳转
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
	 * 返回Print打印语句
	 */
	public Object visit(PrintStatement n, MIdentifier argu) {
		PExp exp = (PExp) n.f2.accept(this, argu);
		return new PPrintStmt(exp);
	}

	/**
	 * 语法树帮我们处理不同的表达式
	 */
	public Object visit(Expression n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * 对于&&表达式，注意若左操作数为真，则不执行右操作数，因此需要提前跳转
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
	 * 对于<表达式，直接使用对应的BinOp语句
	 */
	public Object visit(CompareExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.LT, exp1, exp2);
	}

	/**
	 * 对于+表达式，直接使用对应的BinOp语句
	 */
	public Object visit(PlusExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.PLUS, exp1, exp2);
	}

	/**
	 * 对于-表达式，直接使用对应的BinOp语句
	 */
	public Object visit(MinusExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.MINUS, exp1, exp2);
	}

	/**
	 * 对于*表达式，直接使用对应的BinOp语句
	 */
	public Object visit(TimesExpression n, MIdentifier argu) {
		PExp exp1 = (PExp) n.f0.accept(this, argu);
		PExp exp2 = (PExp) n.f2.accept(this, argu);
		return new PBinOp(PBinOpType.TIMES, exp1, exp2);
	}

	/**
	 * 对于数组取值语句，需要进行以下操作：
	 * <ul>
	 * <li>检查数组非空并获取数组长度
	 * <li>检查下标越界
	 * <li>通过HStore语句获取值
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
	 * 对于数组长度，检查数组非空并使用HSTORE语句获取长度
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
	 * 对于方法调用，需要做以下操作：
	 * <ul>
	 * <li>检查对象非空
	 * <li>从dTable中获取方法地址
	 * <li>将参数各个表达式传给CALL，第一个参数为对象自身
	 * <li>如果参数超过19个，分配一块临时空间，将多余的参数放入临时空间，然后将地址作为第20个参数
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
	 * 对参数列表的处理
	 */
	public Object visit(ExpressionList n, MIdentifier argu) {
		ArrayList<Object> array = (ArrayList<Object>) n.f1.accept(this, argu);
		array.add(0, n.f0.accept(this, argu));
		return array;
	}

	/**
	 * 对单个参数的处理
	 */
	public Object visit(ExpressionRest n, MIdentifier argu) {
		return n.f1.accept(this, argu);
	}

	/**
	 * 语法树帮我们处理不同的表达式
	 */
	public Object visit(PrimaryExpression n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * 返回整数常量
	 */
	public Object visit(IntegerLiteral n, MIdentifier argu) {
		return new PInteger(Integer.valueOf(n.f0.tokenImage));
	}

	/**
	 * 返回bool常量，true为1
	 */
	public Object visit(TrueLiteral n, MIdentifier argu) {
		return new PInteger(1);
	}

	/**
	 * 返回bool常量，false为0
	 */
	public Object visit(FalseLiteral n, MIdentifier argu) {
		return new PInteger(0);
	}

	/**
	 * <p>对于标识符，分为以下几种情况：
	 * <ul>
	 * <li>若该标识符表示一个类（传入参数{@code argu}为{@link MIdentifier#TAG_CLASS}），从符号表中找到相应类（{@link SymbolTable#findClass(String)}）并返回{@link MClass}对象
	 * <li>若该标识符表示一个方法（传入参数{@code argu}为{@link MClass}类型），从该类中找到相应方法（{@link MClass#findMethod(String)}）并返回{@link MMethod}对象
	 * <li>若该标识符表示一个变量（传入参数{@code argu}为{@link MMethod}类型），从该方法中得到该变量的piglet表达式对象（{@link MMethod#getVarPExp(String)}）
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
	 * 返回TEMP 0，需要设置一下自身类型
	 */
	public Object visit(ThisExpression n, MIdentifier argu) {
		return PTemp.valueOf(0).setType(((MMethod) argu).getScope());
	}

	/**
	 * <p>对于数组的new表达式，需要进行以下操作：
	 * <ul>
	 * <li>检查数组长度是否为正
	 * <li>分配4*len+4字节空间，前4字节用来存数组长度
	 * <li>循环将数组清零
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
	 * 对于对象的new表达式，交给类进行处理{@link MClass#getNewPExp()}
	 */
	public Object visit(AllocationExpression n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		return clazz.getNewPExp();
	}

	/**
	 * 对于取反表达式，返回1-exp
	 */
	public Object visit(NotExpression n, MIdentifier argu) {
		PExp exp = (PExp) n.f1.accept(this, argu);
		return new PBinOp(PBinOpType.MINUS, new PInteger(1), exp);
	}

	/**
	 * piglet为前缀表达式，因此括号没有必要
	 */
	public Object visit(BracketExpression n, MIdentifier argu) {
		return n.f1.accept(this, argu);
	}

}
