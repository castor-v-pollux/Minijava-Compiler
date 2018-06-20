package visitor;

import symbol.MClass;
import symbol.MIdentifier;
import symbol.SymbolTable;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.MainClass;

/**
 * <p>����Ϊһ��{@code Visitor}�����������﷨�������Խ���ֻ���������������ķ��ű�
 * <p>���ǽ������ű�ĵ�һ�α�����ʹ�õ�{@code Visitor}����ֻ��������������صĽڵ㡣
 * <p>ʹ�ô�����ֵ���޲�����{@link GJNoArguDepthFirst}��Ϊ����{@code Visitor}ԭ��Ϊ��
 * <ul>
 * <li>ֻ����������������Ҫ�������������Ϣ
 * <li>���ڱ�ʶ���ڵ�ı�����Ҫ�������ɵ�{@link MIdentifier}����
 * </ul>
 *
 * @author castor_v_pollux
 */
public class ClassVisitor extends GJNoArguDepthFirst<MIdentifier> {

	/**
	 * f0 -> MainClass() 
	 * f1 -> ( TypeDeclaration() )* 
	 * f2 -> <EOF>
	 */
	@Override
	public MIdentifier visit(final Goal n) {
		n.f0.accept(this);
		n.f1.accept(this);
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
	public MIdentifier visit(final MainClass n) {
		MIdentifier name = n.f1.accept(this);
		MClass clazz = new MClass(name.getName());
		SymbolTable.addClass(clazz);
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
	public MIdentifier visit(final ClassDeclaration n) {
		MIdentifier name = n.f1.accept(this);
		MClass clazz = new MClass(name.getName());
		SymbolTable.addClass(clazz);
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
	public MIdentifier visit(final ClassExtendsDeclaration n) {
		MIdentifier name = n.f1.accept(this);
		MClass clazz = new MClass(name.getName());
		SymbolTable.addClass(clazz);
		return null;
	}

	/**
	 * <p>ʹ���﷨�������еı�ʶ���ڵ㽨��{@link MIdentifier}���ص��ϲ㡣
	 * <p>f0 -> <IDENTIFIER>
	 */
	@Override
	public MIdentifier visit(final Identifier n) {
		return new MIdentifier(n.f0.tokenImage);
	}

}
