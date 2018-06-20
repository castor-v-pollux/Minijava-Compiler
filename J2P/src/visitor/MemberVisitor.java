package visitor;

import symbol.MClass;
import symbol.MIdentifier;
import symbol.MMethod;
import symbol.MType;
import symbol.MVar;
import symbol.SymbolTable;
import syntaxtree.ArrayType;
import syntaxtree.BooleanType;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterRest;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MethodDeclaration;
import syntaxtree.Type;
import syntaxtree.VarDeclaration;

/**
 * <p>该类为一个{@code Visitor}，用来遍历语法分析树以建立完整的符号表。
 * <p>这是建立符号表的第二次遍历所使用的{@code Visitor}，它只对每个类的成员变量和方法进行遍历，建立相应的符号表模型并添加进符号表。
 * <p>使用带返回值带参数的{@link GJDepthFirst}作为父类{@code Visitor}原因为：
 * <ul>
 * <li>在访问方法、变量时需要传递所在的类/方法以建立符号表
 * <li>对于标识符节点的遍历需要返回相应的{@link MIdentifier}对象用于变量和方法名
 * </ul>
 *
 * @author castor_v_pollux
 */
public class MemberVisitor extends GJDepthFirst<Object, MIdentifier> {

	/**
	 * f0 -> MainClass()
	 * f1 -> ( TypeDeclaration() )*
	 * f2 -> <EOF>
	 */
	@Override
	public Object visit(final Goal n, final MIdentifier argu) {
		n.f0.accept(this, null);
		n.f1.accept(this, null);
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
	@Override
	public Object visit(final MainClass n, final MIdentifier argu) {
		MMethod main = new MMethod(n.f6.tokenImage);
		n.f14.accept(this, main);
		SymbolTable.setMainMethod(main);
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
	@Override
	public Object visit(final ClassDeclaration n, final MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		n.f3.accept(this, clazz);
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
	@Override
	public Object visit(final ClassExtendsDeclaration n, final MIdentifier argu) {
		MClass clazz = (MClass) n.f1.accept(this, MIdentifier.TAG_CLASS);
		MClass superClass = (MClass) n.f3.accept(this, MIdentifier.TAG_CLASS);
		clazz.setSuper(superClass);
		n.f5.accept(this, clazz);
		n.f6.accept(this, clazz);
		return null;
	}

	/**
	 * f0 -> Type()
	 * f1 -> Identifier()
	 * f2 -> ";"
	 */
	@Override
	public Object visit(final VarDeclaration n, final MIdentifier argu) {
		MType type = (MType) n.f0.accept(this, null);
		MIdentifier name = (MIdentifier) n.f1.accept(this, null);
		MVar var = new MVar(name.getName(), type);
		/* 一个变量可能出现在类中，也可能出现在方法中，对作用域进行判断 */
		if (argu instanceof MClass)
			((MClass) argu).addField(var);
		else
			((MMethod) argu).addVar(var);
		return null;
	}

	/**
	 * f0 -> "public"
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
	@Override
	public Object visit(final MethodDeclaration n, final MIdentifier argu) {
		MType returnType = (MType) n.f1.accept(this, null);
		MIdentifier name = (MIdentifier) n.f2.accept(this, null);
		MMethod method = new MMethod(name.getName());
		method.setReturnType(returnType);
		method.setScope((MClass) argu);
		n.f4.accept(this, method);
		n.f7.accept(this, method);
		((MClass) argu).addMethod(method);
		return null;
	}

	/**
	 * f0 -> Type()
	 * f1 -> Identifier()
	 */
	@Override
	public Object visit(final FormalParameter n, final MIdentifier argu) {
		MType type = (MType) n.f0.accept(this, null);
		MIdentifier name = (MIdentifier) n.f1.accept(this, null);
		MVar var = new MVar(name.getName(), type);
		((MMethod) argu).addArgument(var);
		return null;
	}

	/**
	 * f0 -> ","
	 * f1 -> FormalParameter()
	 */
	@Override
	public Object visit(final FormalParameterRest n, final MIdentifier argu) {
		n.f1.accept(this, argu);
		return null;
	}

	/**
	 * <p>对于Type类，会返回指定的{@link MType}类型对象
	 * <p>f0 -> ArrayType()
	 *       | BooleanType()
	 *       | IntegerType()
	 *       | Identifier()
	 */
	@Override
	public Object visit(final Type n, final MIdentifier argu) {
		return n.f0.accept(this, MIdentifier.TAG_CLASS);
	}

	/**
	 * f0 -> ArrayType()
	 *       | BooleanType()
	 *       | IntegerType()
	 *       | Identifier()
	 */
	@Override
	public Object visit(final ArrayType n, final MIdentifier argu) {
		return MType.ArrayType;
	}

	/**
	 * f0 -> "boolean"
	 */
	@Override
	public Object visit(final BooleanType n, final MIdentifier argu) {
		return MType.BooleanType;
	}

	/**
	 * f0 -> "int"
	 */
	@Override
	public Object visit(final IntegerType n, final MIdentifier argu) {
		return MType.IntType;
	}

	/**
	 * <p>当传入参数{@code argu}为{@link MIdentifier#TAG_CLASS}时，表示该标识符为一个类，此时应从符号表中查找该类（使用{@link SymbolTable#findClass}）并返回{@link MClass}对象；
	 * 否则返回正常的{@link MIdentifier}对象。
	 * <p>f0 -> <IDENTIFIER>
	 */
	@Override
	public Object visit(final Identifier n, final MIdentifier argu) {
		if (argu == MIdentifier.TAG_CLASS)
			return SymbolTable.findClass(n.f0.tokenImage);
		else
			return new MIdentifier(n.f0.tokenImage);
	}

}
