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
 * <p>����Ϊһ��{@code Visitor}�����������﷨�������Լ������е�����ƥ�����⡣
 * <p>��{@code Visitor}ֻ��ÿ�������������б���б����������ѽ����ķ��ű�������ͱ��ʽ�����ͼ�顣
 * <p>ʹ�ô�����ֵ��������{@link GJDepthFirst}��Ϊ����{@code Visitor}ԭ��Ϊ��
 * <ul>
 * <li>�ڷ������ʱ��Ҫ�������ڵķ���
 * <li>���ڱ�ʶ����Ҫ������Ӧ���ࡢ���������
 * <li>���ڱ��ʽ��Ҫ������ֵ�����ͣ�ʹ��MVar���а�װ�������ʽ����һ����ֵ������
 * </ul>
 * <p>����һ�α����п��Բ�������ʹ����У�
 * <ul>
 * <li>ʹ��δ��������͡����������
 * <li>�������ͱ��ʽ�е����Ͳ�ƥ��
 * <li>�������õĲ�����ƥ�䣻�����ķ���ֵ��ǩ����ƥ��
 * </ul>
 *
 * @author castor_v_pollux
 */
public class TypeCheckVisitor extends GJDepthFirst<Object, MIdentifier> {

	/**
	 * Ϊ�˷���Ĵ�����÷���ʱ�Ĳ����б������NodeListOptional�ڵ��Ĭ�ϴ���ʽ�����˸��ģ�ʹ�䷵��ÿ���ڵ�ķ���ֵ���ɵ��б�
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
	 * <p>��Ҫ��鷽���ķ���ֵ�Ƿ��뷽��ǩ��һ��
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
	 * <p>���ڸ�ֵ��䣬�Ҳ���ʽ�����ͱ�����������������ƥ��
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
	 * <p>�������鸳ֵ��䣬��Ҫ����������͡��±�Ϊ{@code int}���ͣ��Լ��Ҳ���ʽΪ{@code int}����
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
	 * <p>����{@code if}��䣬��Ҫ�������Ϊ{@code boolean}����
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
	 * <p>����{@code while}��䣬��Ҫ�������Ϊ{@code boolean}����
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
	 * <p>���������䣬��Ҫ������Ϊ{@code int}����
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
	 * <p>�����и÷���Ĭ�Ϸ���null��������Ҫ��д���Է��ؾ�����ʽ��������"��ֵ"����
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
	 * <p>����&&���ʽ����Ҫ�������Ϊ{@code boolean}���ͣ�������{@code boolean}����
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
	 * <p>���ڱȽϱ��ʽ����Ҫ�������Ϊ{@code int}���ͣ�������{@code boolean}����
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
	 * <p>���ڼӷ����ʽ����Ҫ�������Ϊ{@code int}���ͣ�������{@code int}����
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
	 * <p>���ڼ������ʽ����Ҫ�������Ϊ{@code int}���ͣ�������{@code int}����
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
	 * <p>���ڳ˷����ʽ����Ҫ�������Ϊ{@code int}���ͣ�������{@code int}����
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
	 * <p>��������ô���ʽ����Ҫ����������͡��±�Ϊ{@code int}���ͣ�������{@code int}����
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
	 * <p>�������鳤�ȱ��ʽ����Ҫ����������ͣ�������{@code int}����
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
	 * <p>���ڷ������ñ��ʽ����Ҫ�����¼�飺
	 * <ul>
	 * <li>��������Ϊ��������
	 * <li>���������д��ڸ÷���
	 * <li>�����б��뷽��������ƥ��
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
	 * <p>�����������õĲ����б�
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
	 * <p>���صõ��ı��ʽ�����ڱ�ʶ�������ظñ�ʶ���ĵ�ǰλ�ã�������������λ�ã��Ծ�ȷ��λ����
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
	 * <p>��������Ϊ{@code int}����
	 * <p>f0 -> <INTEGER_LITERAL>
	 */
	public Object visit(IntegerLiteral n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, MType.IntType);
	}

	/**
	 * <p>{@code true}Ϊ{@code boolean}����
	 * <p>f0 -> "true"
	 */
	public Object visit(TrueLiteral n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, MType.BooleanType);
	}

	/**
	 * <p>{@code false}Ϊ{@code boolean}����
	 * <p>f0 -> "false"
	 */
	public Object visit(FalseLiteral n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, MType.BooleanType);
	}

	/**
	 * <p>���ڱ�ʶ������Ϊ���¼��������
	 * <ul>
	 * <li>���ñ�ʶ����ʾһ���ࣨ�������{@code argu}Ϊ{@link MIdentifier#TAG_CLASS}�����ӷ��ű����ҵ���Ӧ�ࣨ{@link SymbolTable#findClass(String)}��������{@link MClass}����
	 * <li>���ñ�ʶ����ʾһ���������������{@code argu}Ϊ{@link MClass}���ͣ����Ӹ������ҵ���Ӧ������{@link MClass#findMethod(String)}��������{@link MMethod}����
	 * <li>���ñ�ʶ����ʾһ���������������{@code argu}Ϊ{@link MMethod}���ͣ����Ӹ÷������ҵ���Ӧ������{@link MMethod#findVar(String)}��������{@link MVar}����
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
	 * <p>����{@code this}���ʽ�����ظ÷��������������
	 * <p>f0 -> "this"
	 */
	public Object visit(ThisExpression n, MIdentifier argu) {
		return new MVar(null, n.f0.beginLine, n.f0.beginColumn, ((MMethod) argu).getScope());
	}

	/**
	 * <p>�����½�������ʽ��������鳤��Ϊ{@code int}���ͣ���������������
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
	 * <p>������ʵ�������ʽ�����ظ��������
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
	 * <p>����ȡ�����ʽ����Ҫ�����ʽΪ{@code boolean}���ͣ�������{@code boolean}����
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
	 * ������{@code exp}�Ƿ�Ϊ����{@code type}
	 * @param exp ��ֵ���ʽ
	 * @param type ָ������
	 */
	private void typeCheck(MVar exp, MType type) {
		if (!exp.getType().canConvertTo(type))
			ErrorManager.error(new TypeCheckError(String.format("Type mismatch: cannot convert from '%s' to '%s'",
					exp.getType().getName(), type.getName()), exp.getRow(), exp.getColumn()));
	}

}
