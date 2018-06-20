package visitor;

import java.util.ArrayList;
import java.util.Enumeration;

import minijava.ErrorManager;
import minijava.TypeCheckError;
import symbol.MClass;
import symbol.MIdentifier;
import symbol.MMethod;
import symbol.MType;
import symbol.MVar;
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
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.TypeDeclaration;
import syntaxtree.WhileStatement;

/**
 * <p>该类为一个{@code Visitor}，用来遍历语法分析树以检查语句中的类型匹配问题。
 * <p>该{@code Visitor}只对每个方法体的语句列表进行遍历，根据已建立的符号表进行语句和表达式的类型检查。
 * <p>使用带返回值带参数的{@link GJDepthFirst}作为父类{@code Visitor}原因为：
 * <ul>
 * <li>在访问语句时需要传递所在的方法
 * <li>对于标识符需要返回相应的类、方法或变量
 * <li>对于表达式需要返回其值的类型（使用MVar进行包装，将表达式看做一个右值变量）
 * </ul>
 * <p>在这一次遍历中可以查出的类型错误有：
 * <ul>
 * <li>使用未定义的类型、方法或变量
 * <li>各种语句和表达式中的类型不匹配
 * <li>方法调用的参数不匹配；方法的返回值与签名不匹配
 * </ul>
 *
 * @author castor_v_pollux
 */
public class TypeCheckVisitor extends GJDepthFirst<Object, MIdentifier> {

	/**
	 * 为了方便的处理调用方法时的参数列表，这里对NodeListOptional节点的默认处理方式进行了更改，使其返回每个节点的返回值构成的列表
	 */
	public Object visit(NodeListOptional n, MIdentifier argu) {
		ArrayList<Object> array = new ArrayList<>();
		if (n.present())
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();)
				array.add(e.nextElement().accept(this, argu));
		return array;
	}

	/**
	* f0 -> MainClass()
	* f1 -> ( TypeDeclaration() )*
	* f2 -> <EOF>
	*/
	public Object visit(Goal n, MIdentifier argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> "public"
	 * f4 -> "static"
	 * f5 -> "void"
	 * f6 -> "main"
	 * f7 -> "("
	 * f8 -> "String"
	 * f9 -> "["
	 * f10 -> "]"
	 * f11 -> Identifier()
	 * f12 -> ")"
	 * f13 -> "{"
	 * f14 -> ( VarDeclaration() )*
	 * f15 -> ( Statement() )*
	 * f16 -> "}"
	 * f17 -> "}"
	 */
	public Object visit(MainClass n, MIdentifier argu) {
		n.f15.accept(this, SymbolTable.getMainMethod());
		return null;
	}

	/**
	 * f0 -> ClassDeclaration()
	 *       | ClassExtendsDeclaration()
	 */
	public Object visit(TypeDeclaration n, MIdentifier argu) {
		n.f0.accept(this, null);
		return null;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> ( VarDeclaration() )*
	 * f4 -> ( MethodDeclaration() )*
	 * f5 -> "}"
	 */
	public Object visit(ClassDeclaration n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		n.f4.accept(this, clazz);
		return null;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "extends"
	 * f3 -> Identifier()
	 * f4 -> "{"
	 * f5 -> ( VarDeclaration() )*
	 * f6 -> ( MethodDeclaration() )*
	 * f7 -> "}"
	 */
	public Object visit(ClassExtendsDeclaration n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		n.f6.accept(this, clazz);
		return null;
	}

	/**
	 * <p>需要检查方法的返回值是否与方法签名一致
	 * <p>f0 -> "public"
	 * f1 -> Type()
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( FormalParameterList() )?
	 * f5 -> ")"
	 * f6 -> "{"
	 * f7 -> ( VarDeclaration() )*
	 * f8 -> ( Statement() )*
	 * f9 -> "return"
	 * f10 -> Expression()
	 * f11 -> ";"
	 * f12 -> "}"
	 */
	public Object visit(MethodDeclaration n, MIdentifier argu) {
		MMethod method = (MMethod) n.f2.accept(this, argu);
		n.f8.accept(this, method);

		MVar exp = (MVar) n.f10.accept(this, method);
		typeCheck(exp, method.getReturnType());
		return null;
	}

	/**
	 * f0 -> "{"
	 * f1 -> ( Statement() )*
	 * f2 -> "}"
	 */
	public Object visit(Block n, MIdentifier argu) {
		n.f1.accept(this, argu);
		return null;
	}

	/**
	 * <p>对于赋值语句，右侧表达式的类型必须与左侧变量类型相匹配
	 * <p>f0 -> Identifier()
	 * f1 -> "="
	 * f2 -> Expression()
	 * f3 -> ";"
	 */
	public Object visit(AssignmentStatement n, MIdentifier argu) {
		MVar var = (MVar) n.f0.accept(this, argu);
		MVar exp = (MVar) n.f2.accept(this, argu);
		typeCheck(exp, var.getType());
		return null;
	}

	/**
	 * <p>对于数组赋值语句，需要检查数组类型、下标为{@code int}类型，以及右侧表达式为{@code int}类型
	 * <p>f0 -> Identifier()
	 * f1 -> "["
	 * f2 -> Expression()
	 * f3 -> "]"
	 * f4 -> "="
	 * f5 -> Expression()
	 * f6 -> ";"
	 */
	public Object visit(ArrayAssignmentStatement n, MIdentifier argu) {
		MVar var = (MVar) n.f0.accept(this, argu);
		typeCheck(var, MType.ArrayType);
		MVar index = (MVar) n.f2.accept(this, argu);
		typeCheck(index, MType.IntType);
		MVar exp = (MVar) n.f5.accept(this, argu);
		typeCheck(exp, MType.IntType);
		return null;
	}

	/**
	 * <p>对于{@code if}语句，需要检查条件为{@code boolean}类型
	 * <p>f0 -> "if"
	 * f1 -> "("
	 * f2 -> Expression()
	 * f3 -> ")"
	 * f4 -> Statement()
	 * f5 -> "else"
	 * f6 -> Statement()
	 */
	public Object visit(IfStatement n, MIdentifier argu) {
		MVar exp = (MVar) n.f2.accept(this, argu);
		typeCheck(exp, MType.BooleanType);
		n.f4.accept(this, argu);
		n.f6.accept(this, argu);
		return null;
	}

	/**
	 * <p>对于{@code while}语句，需要检查条件为{@code boolean}类型
	 * <p>f0 -> "while"
	 * f1 -> "("
	 * f2 -> Expression()
	 * f3 -> ")"
	 * f4 -> Statement()
	 */
	public Object visit(WhileStatement n, MIdentifier argu) {
		MVar exp = (MVar) n.f2.accept(this, argu);
		typeCheck(exp, MType.BooleanType);
		n.f4.accept(this, argu);
		return null;
	}

	/**
	 * <p>对于输出语句，需要检查参数为{@code int}类型
	 * <p>f0 -> "System.out.println"
	 * f1 -> "("
	 * f2 -> Expression()
	 * f3 -> ")"
	 * f4 -> ";"
	 */
	public Object visit(PrintStatement n, MIdentifier argu) {
		MVar exp = (MVar) n.f2.accept(this, argu);
		typeCheck(exp, MType.IntType);
		return null;
	}

	/**
	 * <p>父类中该方法默认返回null，这里需要重写，以返回具体表达式所产生的"右值"变量
	 * <p>f0 -> AndExpression()
	 *       | CompareExpression()
	 *       | PlusExpression()
	 *       | MinusExpression()
	 *       | TimesExpression()
	 *       | ArrayLookup()
	 *       | ArrayLength()
	 *       | MessageSend()
	 *       | PrimaryExpression()
	 */
	public Object visit(Expression n, MIdentifier argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * <p>对于&&表达式，需要检查两侧为{@code boolean}类型，并返回{@code boolean}类型
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "&&"
	 * f2 -> PrimaryExpression()
	 */
	public Object visit(AndExpression n, MIdentifier argu) {
		MVar op1 = (MVar) n.f0.accept(this, argu);
		typeCheck(op1, MType.BooleanType);
		MVar op2 = (MVar) n.f2.accept(this, argu);
		typeCheck(op2, MType.BooleanType);
		return op1;
	}

	/**
	 * <p>对于比较表达式，需要检查两侧为{@code int}类型，并返回{@code boolean}类型
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "<"
	 * f2 -> PrimaryExpression()
	 */
	public Object visit(CompareExpression n, MIdentifier argu) {
		MVar op1 = (MVar) n.f0.accept(this, argu);
		typeCheck(op1, MType.IntType);
		MVar op2 = (MVar) n.f2.accept(this, argu);
		typeCheck(op2, MType.IntType);
		return new MVar(null, op1.getRow(), op1.getColumn(), MType.BooleanType);
	}

	/**
	 * <p>对于加法表达式，需要检查两侧为{@code int}类型，并返回{@code int}类型
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "+"
	 * f2 -> PrimaryExpression()
	 */
	public Object visit(PlusExpression n, MIdentifier argu) {
		MVar op1 = (MVar) n.f0.accept(this, argu);
		typeCheck(op1, MType.IntType);
		MVar op2 = (MVar) n.f2.accept(this, argu);
		typeCheck(op2, MType.IntType);
		return op1;
	}

	/**
	 * <p>对于减法表达式，需要检查两侧为{@code int}类型，并返回{@code int}类型
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "-"
	 * f2 -> PrimaryExpression()
	 */
	public Object visit(MinusExpression n, MIdentifier argu) {
		MVar op1 = (MVar) n.f0.accept(this, argu);
		typeCheck(op1, MType.IntType);
		MVar op2 = (MVar) n.f2.accept(this, argu);
		typeCheck(op2, MType.IntType);
		return op1;
	}

	/**
	 * <p>对于乘法表达式，需要检查两侧为{@code int}类型，并返回{@code int}类型
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "*"
	 * f2 -> PrimaryExpression()
	 */
	public Object visit(TimesExpression n, MIdentifier argu) {
		MVar op1 = (MVar) n.f0.accept(this, argu);
		typeCheck(op1, MType.IntType);
		MVar op2 = (MVar) n.f2.accept(this, argu);
		typeCheck(op2, MType.IntType);
		return op1;
	}

	/**
	 * <p>对于数组访存表达式，需要检查数组类型、下标为{@code int}类型，并返回{@code int}类型
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "["
	 * f2 -> PrimaryExpression()
	 * f3 -> "]"
	 */
	public Object visit(ArrayLookup n, MIdentifier argu) {
		MVar array = (MVar) n.f0.accept(this, argu);
		typeCheck(array, MType.ArrayType);
		MVar index = (MVar) n.f2.accept(this, argu);
		typeCheck(index, MType.IntType);
		return new MVar(null, array.getRow(), array.getColumn(), MType.IntType);
	}

	/**
	 * <p>对于数组长度表达式，需要检查数组类型，并返回{@code int}类型
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "."
	 * f2 -> "length"
	 */
	public Object visit(ArrayLength n, MIdentifier argu) {
		MVar array = (MVar) n.f0.accept(this, argu);
		typeCheck(array, MType.ArrayType);
		return new MVar(null, array.getRow(), array.getColumn(), MType.IntType);
	}

	/**
	 * <p>对于方法调用表达式，需要做以下检查：
	 * <ul>
	 * <li>变量必须为引用类型
	 * <li>变量的类中存在该方法
	 * <li>参数列表与方法参数相匹配
	 * </ul>
	 * <p>f0 -> PrimaryExpression()
	 * f1 -> "."
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( ExpressionList() )?
	 * f5 -> ")"
	 */
	public Object visit(MessageSend n, MIdentifier argu) {
		MVar var = (MVar) n.f0.accept(this, argu);
		if (!(var.getType() instanceof MClass))
			ErrorManager.error(new TypeCheckError(String.format("Cannot invoke '%s' on the primitive type '%s'",
					n.f2.f0.tokenImage, var.getType().getName()), var.getRow(), var.getColumn()));
		MClass clazz = (MClass) var.getType();
		MMethod method = (MMethod) n.f2.accept(this, clazz);
		ArrayList<Object> params = (ArrayList<Object>) n.f4.accept(this, argu);
		if (params == null)
			params = new ArrayList<>();
		if (!method.checkArgument(params))
			ErrorManager.error(new TypeCheckError(
					String.format("The method '%s' in the type '%s' is not applicable for the arguments",
							method.getName(), clazz.getName()),
					n.f2.f0.beginLine, n.f2.f0.beginColumn));
		return new MVar(null, var.getRow(), var.getColumn(), method.getReturnType());
	}

	/**
	 * <p>构建方法调用的参数列表
	 * <p>f0 -> Expression()
	 * f1 -> ( ExpressionRest() )*
	 */
	public Object visit(ExpressionList n, MIdentifier argu) {
		ArrayList<Object> array = (ArrayList<Object>) n.f1.accept(this, argu);
		array.add(0, n.f0.accept(this, argu));
		return array;
	}

	/**
	 * f0 -> ","
	 * f1 -> Expression()
	 */
	public Object visit(ExpressionRest n, MIdentifier argu) {
		return n.f1.accept(this, argu);
	}

	/**
	 * <p>返回得到的表达式。对于标识符，返回该标识符的当前位置（而非其声明的位置）以精确定位错误。
	 * <p>f0 -> IntegerLiteral()
	 *       | TrueLiteral()
	 *       | FalseLiteral()
	 *       | Identifier()
	 *       | ThisExpression()
	 *       | ArrayAllocationExpression()
	 *       | AllocationExpression()
	 *       | NotExpression()
	 *       | BracketExpression()
	 */
	public Object visit(PrimaryExpression n, MIdentifier argu) {
		if (n.f0.choice instanceof Identifier) {
			Identifier id = (Identifier) n.f0.choice;
			MVar var = (MVar) id.accept(this, argu);
			return new MVar(null, id.f0.beginLine, id.f0.beginColumn, var.getType());
		}
		return n.f0.accept(this, argu);
	}

	/**
	 * <p>整数常量为{@code int}类型
	 * <p>f0 -> <INTEGER_LITERAL>
	 */
	public Object visit(IntegerLiteral n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, MType.IntType);
	}

	/**
	 * <p>{@code true}为{@code boolean}类型
	 * <p>f0 -> "true"
	 */
	public Object visit(TrueLiteral n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, MType.BooleanType);
	}

	/**
	 * <p>{@code false}为{@code boolean}类型
	 * <p>f0 -> "false"
	 */
	public Object visit(FalseLiteral n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, MType.BooleanType);
	}

	/**
	 * <p>对于标识符，分为以下几种情况：
	 * <ul>
	 * <li>若该标识符表示一个类（传入参数{@code argu}为{@link MIdentifier#TAG_CLASS}），从符号表中找到相应类（{@link SymbolTable#findClass(String)}）并返回{@link MClass}对象
	 * <li>若该标识符表示一个方法（传入参数{@code argu}为{@link MClass}类型），从该类中找到相应方法（{@link MClass#findMethod(String)}）并返回{@link MMethod}对象
	 * <li>若该标识符表示一个变量（传入参数{@code argu}为{@link MMethod}类型），从该方法中找到相应变量（{@link MMethod#findVar(String)}）并返回{@link MVar}对象
	 * </ul>
	 * <p>f0 -> <IDENTIFIER>
	 */
	public Object visit(Identifier n, MIdentifier argu) {
		if (argu == MIdentifier.TAG_CLASS) {
			MClass clazz = SymbolTable.findClass(n.f0.tokenImage);
			if (clazz == null)
				ErrorManager.error(new TypeCheckError(String.format("%s cannot be resolved to a type", n.f0.tokenImage),
						n.f0.beginLine, n.f0.beginColumn));
			return clazz;
		} else if (argu instanceof MClass) {
			MMethod method = ((MClass) argu).findMethod(n.f0.tokenImage);
			if (method == null)
				ErrorManager.error(new TypeCheckError(String.format("The method '%s' is undefined for the type '%s'",
						n.f0.tokenImage, argu.getName()), n.f0.beginLine, n.f0.beginColumn));
			return method;
		} else if (argu instanceof MMethod) {
			MVar var = ((MMethod) argu).findVar(n.f0.tokenImage);
			if (var == null)
				ErrorManager
						.error(new TypeCheckError(String.format("%s cannot be resolved to a variable", n.f0.tokenImage),
								n.f0.beginLine, n.f0.beginColumn));
			return var;
		} else
			return null;
	}

	/**
	 * <p>对于{@code this}表达式，返回该方法所在类的类型
	 * <p>f0 -> "this"
	 */
	public Object visit(ThisExpression n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, ((MMethod) argu).getScope());
	}

	/**
	 * <p>对于新建数组表达式，检查数组长度为{@code int}类型，并返回数组类型
	 * <p>f0 -> "new"
	 * f1 -> "int"
	 * f2 -> "["
	 * f3 -> Expression()
	 * f4 -> "]"
	 */
	public Object visit(ArrayAllocationExpression n, MIdentifier argu) {
		MVar exp = (MVar) n.f3.accept(this, argu);
		typeCheck(exp, MType.IntType);
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, MType.ArrayType);
	}

	/**
	 * <p>对于类实例化表达式，返回该类的类型
	 * <p>f0 -> "new"
	 * f1 -> Identifier()
	 * f2 -> "("
	 * f3 -> ")"
	 */
	public Object visit(AllocationExpression n, MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, clazz);
	}

	/**
	 * <p>对于取反表达式，需要检查表达式为{@code boolean}类型，并返回{@code boolean}类型
	 * <p>f0 -> "!"
	 * f1 -> Expression()
	 */
	public Object visit(NotExpression n, MIdentifier argu) {
		MVar exp = (MVar) n.f1.accept(this, argu);
		typeCheck(exp, MType.BooleanType);
		return exp;
	}

	/**
	 * f0 -> "("
	 * f1 -> Expression()
	 * f2 -> ")"
	 */
	public Object visit(BracketExpression n, MIdentifier argu) {
		return n.f1.accept(this, argu);
	}

	/**
	 * 检查变量{@code exp}是否为类型{@code type}
	 * @param exp 右值表达式
	 * @param type 指定类型
	 */
	private void typeCheck(MVar exp, MType type) {
		if (!exp.getType().canConvertTo(type))
			ErrorManager.error(new TypeCheckError(String.format("Type mismatch: cannot convert from '%s' to '%s'",
					exp.getType().getName(), type.getName()), exp.getRow(), exp.getColumn()));
	}

}
