package visitor;

import java.util.Enumeration;

import syntaxtree.BinOp;
import syntaxtree.CJumpStmt;
import syntaxtree.Call;
import syntaxtree.ErrorStmt;
import syntaxtree.Exp;
import syntaxtree.Goal;
import syntaxtree.HAllocate;
import syntaxtree.HLoadStmt;
import syntaxtree.HStoreStmt;
import syntaxtree.IntegerLiteral;
import syntaxtree.JumpStmt;
import syntaxtree.Label;
import syntaxtree.MoveStmt;
import syntaxtree.NoOpStmt;
import syntaxtree.Node;
import syntaxtree.NodeList;
import syntaxtree.NodeListOptional;
import syntaxtree.NodeOptional;
import syntaxtree.NodeSequence;
import syntaxtree.NodeToken;
import syntaxtree.Operator;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.StmtList;
import syntaxtree.Temp;

/**
 *<p>该Visitor负责遍历语法树以获取当前的最大TEMP号。
 *<p>使用{@link GJNoArguDepthFirst}的原因是，不需要往下传递信息，但要返回当前最大寄存器号。
 *
 * @author castor_v_pollux
 */
public class MaxTempVisitor extends GJNoArguDepthFirst<Integer> {

	public Integer visit(NodeList n) {
		Integer ret = 0;
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
			ret = Math.max(ret, e.nextElement().accept(this));
		}
		return ret;
	}

	public Integer visit(NodeListOptional n) {
		if (n.present()) {
			Integer ret = 0;
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
				ret = Math.max(ret, e.nextElement().accept(this));
			}
			return ret;
		} else
			return 0;
	}

	public Integer visit(NodeOptional n) {
		if (n.present())
			return n.node.accept(this);
		else
			return 0;
	}

	public Integer visit(NodeSequence n) {
		Integer ret = 0;
		for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
			ret = Math.max(ret, e.nextElement().accept(this));
		}
		return ret;
	}

	public Integer visit(NodeToken n) {
		return 0;
	}

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public Integer visit(Goal n) {
		return Math.max(n.f1.accept(this), n.f3.accept(this));
	}

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public Integer visit(StmtList n) {
		return n.f0.accept(this);
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public Integer visit(Procedure n) {
		return n.f4.accept(this);
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
	public Integer visit(Stmt n) {
		return n.f0.accept(this);
	}

	/**
	 * f0 -> "NOOP"
	 */
	public Integer visit(NoOpStmt n) {
		return 0;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public Integer visit(ErrorStmt n) {
		return 0;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Exp()
	 * f2 -> Label()
	 */
	public Integer visit(CJumpStmt n) {
		return n.f1.accept(this);
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public Integer visit(JumpStmt n) {
		return 0;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Exp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Exp()
	 */
	public Integer visit(HStoreStmt n) {
		return Math.max(n.f1.accept(this), n.f3.accept(this));
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 * f3 -> IntegerLiteral()
	 */
	public Integer visit(HLoadStmt n) {
		return Math.max(n.f1.accept(this), n.f2.accept(this));
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public Integer visit(MoveStmt n) {
		return Math.max(n.f1.accept(this), n.f2.accept(this));
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> Exp()
	 */
	public Integer visit(PrintStmt n) {
		return n.f1.accept(this);
	}

	/**
	 * f0 -> StmtExp()
	 *       | Call()
	 *       | HAllocate()
	 *       | BinOp()
	 *       | Temp()
	 *       | IntegerLiteral()
	 *       | Label()
	 */
	public Integer visit(Exp n) {
		return n.f0.accept(this);
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> Exp()
	 * f4 -> "END"
	 */
	public Integer visit(StmtExp n) {
		return Math.max(n.f1.accept(this), n.f3.accept(this));
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> Exp()
	 * f2 -> "("
	 * f3 -> ( Exp() )*
	 * f4 -> ")"
	 */
	public Integer visit(Call n) {
		return Math.max(n.f1.accept(this), n.f3.accept(this));
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> Exp()
	 */
	public Integer visit(HAllocate n) {
		return n.f1.accept(this);
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Exp()
	 * f2 -> Exp()
	 */
	public Integer visit(BinOp n) {
		return Math.max(n.f1.accept(this), n.f2.accept(this));
	}

	/**
	 * f0 -> "LT"
	 *       | "PLUS"
	 *       | "MINUS"
	 *       | "TIMES"
	 */
	public Integer visit(Operator n) {
		return 0;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public Integer visit(Temp n) {
		return Integer.valueOf(n.f1.f0.tokenImage);
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public Integer visit(IntegerLiteral n) {
		return 0;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public Integer visit(Label n) {
		return 0;
	}

}
