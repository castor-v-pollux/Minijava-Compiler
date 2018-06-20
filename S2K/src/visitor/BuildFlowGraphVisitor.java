package visitor;

import symbol.Method;
import symbol.Statement;
import symbol.SymbolTable;
import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.Call;
import syntaxtree.ErrorStmt;
import syntaxtree.Goal;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.SimpleExp;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.Temp;

/**
 * 该Visitor用来建立程序流图。
 *
 * @author castor_v_pollux
 */
public class BuildFlowGraphVisitor extends GJDepthFirst<Integer, Method> {

	int id;
	boolean param = false;

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public Integer visit(Goal n, Method argu) {
		Method method = SymbolTable.get(n.f0.tokenImage);
		method.addFlow(0, 1);
		id = 1;
		n.f1.accept(this, method);
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
	public Integer visit(Procedure n, Method argu) {
		String name = n.f0.f0.tokenImage;
		Method method = SymbolTable.get(name);
		id = 0;
		n.f4.accept(this, method);
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
	public Integer visit(Stmt n, Method argu) {
		n.f0.accept(this, argu);
		id++;
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public Integer visit(NoOpStmt n, Method method) {
		method.addFlow(id, id + 1);
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public Integer visit(ErrorStmt n, Method method) {
		method.addFlow(id, id + 1);
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Temp()
	 * f2 -> Label()
	 */
	public Integer visit(CJumpStmt n, Method method) {
		Statement statement = method.get(id);
		statement.addUse(n.f1.accept(this, method));
		method.addFlow(id, id + 1);
		method.addFlow(id, n.f2.accept(this, method));
		return null;
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public Integer visit(JumpStmt n, Method method) {
		method.addFlow(id, n.f1.accept(this, method));
		return null;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Temp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Temp()
	 */
	public Integer visit(HStoreStmt n, Method method) {
		Statement statement = method.get(id);
		statement.addUse(n.f1.accept(this, method));
		statement.addUse(n.f3.accept(this, method));
		method.addFlow(id, id + 1);
		return null;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Temp()
	 * f3 -> IntegerLiteral()
	 */
	public Integer visit(HLoadStmt n, Method method) {
		Statement statement = method.get(id);
		statement.addDef(n.f1.accept(this, method));
		statement.addUse(n.f2.accept(this, method));
		method.addFlow(id, id + 1);
		return null;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public Integer visit(MoveStmt n, Method method) {
		Statement statement = method.get(id);
		statement.addDef(n.f1.accept(this, method));
		method.addFlow(id, id + 1);
		n.f2.accept(this, method);
		return null;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public Integer visit(PrintStmt n, Method method) {
		method.addFlow(id, id + 1);
		n.f1.accept(this, method);
		return null;
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> SimpleExp()
	 * f4 -> "END"
	 */
	public Integer visit(StmtExp n, Method method) {
		method.addFlow(0, 1);
		id++;
		n.f1.accept(this, method);
		n.f3.accept(this, method);
		method.addFlow(id, id + 1);
		return null;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 * f2 -> "("
	 * f3 -> ( Temp() )*
	 * f4 -> ")"
	 */
	public Integer visit(Call n, Method method) {
		n.f1.accept(this, method);
		param = true;
		n.f3.accept(this, method);
		param = false;
		return null;
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Temp()
	 * f2 -> SimpleExp()
	 */
	public Integer visit(BinOp n, Method method) {
		Statement statement = method.get(id);
		statement.addUse(n.f1.accept(this, method));
		n.f2.accept(this, method);
		return null;
	}

	/**
	 * f0 -> Temp()
	 *       | IntegerLiteral()
	 *       | Label()
	 */
	public Integer visit(SimpleExp n, Method method) {
		if (n.f0.which == 0) {
			Statement statement = method.get(id);
			statement.addUse(n.f0.choice.accept(this, method));
		}
		return null;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public Integer visit(Temp n, Method method) {
		Integer temp = Integer.parseInt(n.f1.f0.tokenImage);
		if (param) {
			Statement statement = method.get(id);
			statement.addUse(temp);
		}
		return temp;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public Integer visit(Label n, Method method) {
		return symbol.Label.getId(String.format("%s_%s", method.getName(), n.f0.tokenImage));
	}

}
