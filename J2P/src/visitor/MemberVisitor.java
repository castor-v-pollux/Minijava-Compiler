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
 * <p>����Ϊһ��{@code Visitor}�����������﷨�������Խ��������ķ��ű�
 * <p>���ǽ������ű�ĵڶ��α�����ʹ�õ�{@code Visitor}����ֻ��ÿ����ĳ�Ա�����ͷ������б�����������Ӧ�ķ��ű�ģ�Ͳ���ӽ����ű�
 * <p>ʹ�ô�����ֵ��������{@link GJDepthFirst}��Ϊ����{@code Visitor}ԭ��Ϊ��
 * <ul>
 * <li>�ڷ��ʷ���������ʱ��Ҫ�������ڵ���/�����Խ������ű�
 * <li>���ڱ�ʶ���ڵ�ı�����Ҫ������Ӧ��{@link MIdentifier}�������ڱ����ͷ�����
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
		/* һ���������ܳ��������У�Ҳ���ܳ����ڷ����У�������������ж� */
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
	 * <p>����Type�࣬�᷵��ָ����{@link MType}���Ͷ���
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
	 * <p>���������{@code argu}Ϊ{@link MIdentifier#TAG_CLASS}ʱ����ʾ�ñ�ʶ��Ϊһ���࣬��ʱӦ�ӷ��ű��в��Ҹ��ࣨʹ��{@link SymbolTable#findClass}��������{@link MClass}����
	 * ���򷵻�������{@link MIdentifier}����
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
