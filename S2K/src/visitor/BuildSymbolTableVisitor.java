package visitor;

import symbol.Interval;
import symbol.Method;
import symbol.Statement;
import symbol.SymbolTable;
import syntaxtree.Call;
import syntaxtree.Goal;
import syntaxtree.Label;
import syntaxtree.NodeOptional;
import syntaxtree.Procedure;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.Temp;

/**
 * 该Visitor用来建立语句序号和label对应的序号，保存到符号表中。
 *
 * @author castor_v_pollux
 */
public class BuildSymbolTableVisitor extends GJDepthFirst<String, Method> {

	int id;

	/**
	 * (Label)?
	 */
	public String visit(NodeOptional n, Method argu) {
		if (n.present())
			symbol.Label.add(n.node.accept(this, argu), id);
		return null;
	}

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public String visit(Goal n, Method argu) {
		Method main = new Method(n.f0.tokenImage, 0);
		main.add(new Statement());// Entry
		id = 1;
		n.f1.accept(this, main);
		main.add(new Statement());// Exit
		SymbolTable.add(main);
		n.f3.accept(this, null);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public String visit(Procedure n, Method argu) {
		String name = n.f0.f0.tokenImage;
		int param = Integer.parseInt(n.f2.f0.tokenImage);
		Method method = new Method(name, param);
		n.f4.accept(this, method);
		SymbolTable.add(method);
		return null;
	}

	/**
	 * f0 -> NoOpStmt()
	 *       | ErrorStmt()
	 *       | CJumpStmt()
	 *       | JumpStmt()
	 *       | HStoreStmt()
	 *       | HLoadStmt()
	 *       | MoveStmt()
	 *       | PrintStmt()
	 */
	public String visit(Stmt n, Method method) {
		method.add(new Statement());
		n.f0.accept(this, method);
		id++;
		return null;
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> SimpleExp()
	 * f4 -> "END"
	 */
	public String visit(StmtExp n, Method method) {
		method.add(new Statement());// Entry
		id = 1;
		n.f1.accept(this, method);
		method.add(new Statement());// Return
		id++;
		n.f3.accept(this, method);
		method.add(new Statement());// Exit
		return null;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 * f2 -> "("
	 * f3 -> ( Temp() )*
	 * f4 -> ")"
	 */
	public String visit(Call n, Method method) {
		n.f1.accept(this, method);
		n.f3.accept(this, method);
		method.addCall(id);
		method.updateCallParam(n.f3.size());
		return null;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public String visit(Temp n, Method method) {
		int temp = Integer.parseInt(n.f1.f0.tokenImage);
		if (temp < 20)
			method.addInteval(temp, new Interval(temp, 0, id));
		else
			method.addInteval(temp, new Interval(temp, id, id));
		return null;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public String visit(Label n, Method method) {
		return String.format("%s_%s", method.getName(), n.f0.tokenImage);
	}

}
