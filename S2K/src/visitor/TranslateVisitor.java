package visitor;

import java.util.Vector;

import symbol.Method;
import symbol.SymbolTable;
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
import syntaxtree.NodeOptional;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.SimpleExp;
import syntaxtree.StmtExp;
import syntaxtree.Temp;

/**
 * 该Visitor利用已经得到的寄存器分配结果，将Spiglet翻译成Kanga代码。
 *
 * @author castor_v_pollux
 */
public class TranslateVisitor extends GJDepthFirst<Object, Method> {

	/**
	 * (Label)?
	 */
	public Object visit(NodeOptional n, Method argu) {
		if (n.present())
			System.out.print("L" + n.node.accept(this, argu));
		return null;
	}

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public Object visit(Goal n, Method argu) {
		Method method = SymbolTable.get(n.f0.tokenImage);
		System.out.println(method.getHeader());
		n.f1.accept(this, method);
		System.out.println("END");
		n.f3.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public Object visit(Procedure n, Method argu) {
		String name = n.f0.f0.tokenImage;
		Method method = SymbolTable.get(name);
		System.out.println(method.getHeader());
		n.f4.accept(this, method);
		System.out.println("END");
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public Object visit(NoOpStmt n, Method argu) {
		System.out.println("\tNOOP");
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public Object visit(ErrorStmt n, Method argu) {
		System.out.println("\tERROR");
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Temp()
	 * f2 -> Label()
	 */
	public Object visit(CJumpStmt n, Method method) {
		String reg = method.getReg((Integer) n.f1.accept(this, method), "v0");
		System.out.println(String.format("\tCJUMP %s L%d", reg, n.f2.accept(this, method)));
		return null;
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public Object visit(JumpStmt n, Method method) {
		System.out.println(String.format("\tJUMP L%d", n.f1.accept(this, method)));
		return null;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Temp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Temp()
	 */
	public Object visit(HStoreStmt n, Method method) {
		String reg1 = method.getReg((Integer) n.f1.accept(this, method), "v0");
		String reg2 = method.getReg((Integer) n.f3.accept(this, method), "v1");
		System.out.println(String.format("\tHSTORE %s %s %s", reg1, n.f2.f0.tokenImage, reg2));
		return null;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Temp()
	 * f3 -> IntegerLiteral()
	 */
	public Object visit(HLoadStmt n, Method method) {
		int tempId = (Integer) n.f1.accept(this, method);
		String reg = method.getReg((Integer) n.f2.accept(this, method), "v1");
		if (method.isSpilled(tempId)) {
			System.out.println(String.format("\tHLOAD v1 %s %s", reg, n.f3.f0.tokenImage));
			method.putReg(tempId, "v1");
		} else
			System.out.println(String.format("\tHLOAD %s %s %s", method.getReg(tempId, null), reg, n.f3.f0.tokenImage));
		return null;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public Object visit(MoveStmt n, Method method) {
		method.putReg((Integer) n.f1.accept(this, method), (String) n.f2.accept(this, method));
		return null;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> SimpleExp()
	 */
	public Object visit(PrintStmt n, Method method) {
		System.out.println(String.format("\tPRINT %s", n.f1.accept(this, method)));
		return null;
	}

	/**
	 * f0 -> Call()
	 *       | HAllocate()
	 *       | BinOp()
	 *       | SimpleExp()
	 */
	public Object visit(Exp n, Method argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> SimpleExp()
	 * f4 -> "END"
	 */
	public Object visit(StmtExp n, Method method) {
		method.storeReg();
		method.loadParam();
		n.f1.accept(this, method);
		System.out.println("\tMOVE v0 " + n.f3.accept(this, method));
		method.restoreReg();
		return null;
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> SimpleExp()
	 * f2 -> "("
	 * f3 -> ( Temp() )*
	 * f4 -> ")"
	 */
	public Object visit(Call n, Method method) {
		Vector<Node> paramNode = n.f3.nodes;
		int l = paramNode.size();
		int i;
		for (i = 0; i < l && i < 4; i++)
			System.out.println(String.format("\tMOVE a%d %s", i,
					method.getReg((Integer) paramNode.get(i).accept(this, method), "v0")));
		for (; i < l; i++)
			System.out.println(String.format("\tPASSARG %d %s", i - 3,
					method.getReg((Integer) paramNode.get(i).accept(this, method), "v0")));
		System.out.println("\tCALL " + n.f1.accept(this, method));
		return "v0";
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> SimpleExp()
	 */
	public Object visit(HAllocate n, Method argu) {
		return "HALLOCATE " + n.f1.accept(this, argu);
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Temp()
	 * f2 -> SimpleExp()
	 */
	public Object visit(BinOp n, Method method) {
		String reg = method.getReg((Integer) n.f1.accept(this, method), "v0");
		return String.format("%s %s %s", n.f0.f0.choice.toString(), reg, n.f2.accept(this, method));
	}

	/**
	 * f0 -> Temp()
	 *       | IntegerLiteral()
	 *       | Label()
	 */
	public Object visit(SimpleExp n, Method method) {
		switch (n.f0.which) {
		case 0:
			int tempId = (Integer) n.f0.accept(this, method);
			return method.getReg(tempId, "v1");
		case 1:
			return n.f0.accept(this, method);
		case 2:
			return ((Label) n.f0.choice).f0.tokenImage;
		}
		return null;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public Object visit(Temp n, Method argu) {
		return Integer.parseInt(n.f1.f0.tokenImage);
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public Object visit(IntegerLiteral n, Method argu) {
		return n.f0.tokenImage;
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public Object visit(Label n, Method method) {
		return symbol.Label.getName(String.format("%s_%s", method.getName(), n.f0.tokenImage));
	}

}
