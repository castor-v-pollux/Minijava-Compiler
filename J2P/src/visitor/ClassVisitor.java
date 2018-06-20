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
 * <p>该类为一个{@code Visitor}，用来遍历语法分析树以建立只包含各个类声明的符号表。
 * <p>这是建立符号表的第一次遍历所使用的{@code Visitor}，它只遍历与类声明相关的节点。
 * <p>使用带返回值但无参数的{@link GJNoArguDepthFirst}作为父类{@code Visitor}原因为：
 * <ul>
 * <li>只遍历类声明，不需要传递作用域等信息
 * <li>对于标识符节点的遍历需要返回生成的{@link MIdentifier}对象
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
	 * <p>使用语法分析树中的标识符节点建立{@link MIdentifier}返回到上层。
	 * <p>f0 -> <IDENTIFIER>
	 */
	@Override
	public MIdentifier visit(final Identifier n) {
		return new MIdentifier(n.f0.tokenImage);
	}

}
