package visitor;

import java.util.ArrayList;

import spiglet.SCode;
import spiglet.STemp;
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
import syntaxtree.NodeToken;
import syntaxtree.PrintStmt;
import syntaxtree.Procedure;
import syntaxtree.Stmt;
import syntaxtree.StmtExp;
import syntaxtree.StmtList;
import syntaxtree.Temp;

/**
 * <p>该Visitor负责将Piglet翻译为SPiglet。
 * <p>使用{@link GJDepthFirst}的原因是，在翻译表达式时，需要根据实际需要的表达式类型进行翻译，故需要传参；需要返回SPiglet表达式的包装。
 *
 * @author castor_v_pollux
 */
public class TranslateVisitor extends GJDepthFirst<SCode, TranslateVisitor.ExpType> {

	/**
	 * 表达式类型，分为寄存器，简单表达式和复杂表达式。
	 */
	public static enum ExpType {
		TEMP, SIMPLEEXP, EXP
	}

	/**
	 * f0 -> "MAIN"
	 * f1 -> StmtList()
	 * f2 -> "END"
	 * f3 -> ( Procedure() )*
	 * f4 -> <EOF>
	 */
	public SCode visit(Goal n, ExpType argu) {
		System.out.println("MAIN");
		n.f1.accept(this, null);
		System.out.println("END");
		n.f3.accept(this, null);
		return null;
	}

	/**
	 * f0 -> ( ( Label() )? Stmt() )*
	 */
	public SCode visit(StmtList n, ExpType argu) {
		n.f0.accept(this, null);
		return null;
	}

	/**
	 * f0 -> Label()
	 * f1 -> "["
	 * f2 -> IntegerLiteral()
	 * f3 -> "]"
	 * f4 -> StmtExp()
	 */
	public SCode visit(Procedure n, ExpType argu) {
		System.out.println(String.format("%s[%s]", n.f0.f0.tokenImage, n.f2.f0.tokenImage));
		System.out.println("BEGIN");
		SCode exp = n.f4.accept(this, ExpType.SIMPLEEXP);
		System.out.println(String.format("RETURN %s", exp));
		System.out.println("END");
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
	public SCode visit(Stmt n, ExpType argu) {
		n.f0.accept(this, null);
		return null;
	}

	/**
	 * f0 -> "NOOP"
	 */
	public SCode visit(NoOpStmt n, ExpType argu) {
		System.out.println("\tNOOP");
		return null;
	}

	/**
	 * f0 -> "ERROR"
	 */
	public SCode visit(ErrorStmt n, ExpType argu) {
		System.out.println("\tERROR");
		return null;
	}

	/**
	 * f0 -> "CJUMP"
	 * f1 -> Exp()
	 * f2 -> Label()
	 */
	public SCode visit(CJumpStmt n, ExpType argu) {
		SCode exp = n.f1.accept(this, ExpType.TEMP);
		System.out.println(String.format("\tCJUMP %s %s", exp, n.f2.f0.tokenImage));
		return null;
	}

	/**
	 * f0 -> "JUMP"
	 * f1 -> Label()
	 */
	public SCode visit(JumpStmt n, ExpType argu) {
		System.out.println(String.format("\tJUMP %s", n.f1.f0.tokenImage));
		return null;
	}

	/**
	 * f0 -> "HSTORE"
	 * f1 -> Exp()
	 * f2 -> IntegerLiteral()
	 * f3 -> Exp()
	 */
	public SCode visit(HStoreStmt n, ExpType argu) {
		SCode exp1 = n.f1.accept(this, ExpType.TEMP);
		SCode exp2 = n.f3.accept(this, ExpType.TEMP);
		System.out.println(String.format("\tHSTORE %s %s %s", exp1, n.f2.f0.tokenImage, exp2));
		return null;
	}

	/**
	 * f0 -> "HLOAD"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 * f3 -> IntegerLiteral()
	 */
	public SCode visit(HLoadStmt n, ExpType argu) {
		SCode exp1 = n.f1.accept(this, ExpType.TEMP);
		SCode exp2 = n.f2.accept(this, ExpType.TEMP);
		System.out.println(String.format("\tHLOAD %s %s %s", exp1, exp2, n.f3.f0.tokenImage));
		return null;
	}

	/**
	 * f0 -> "MOVE"
	 * f1 -> Temp()
	 * f2 -> Exp()
	 */
	public SCode visit(MoveStmt n, ExpType argu) {
		SCode exp1 = n.f1.accept(this, ExpType.TEMP);
		SCode exp2 = n.f2.accept(this, ExpType.EXP);
		System.out.println(String.format("\tMOVE %s %s", exp1, exp2));
		return null;
	}

	/**
	 * f0 -> "PRINT"
	 * f1 -> Exp()
	 */
	public SCode visit(PrintStmt n, ExpType argu) {
		SCode exp = n.f1.accept(this, ExpType.SIMPLEEXP);
		System.out.println(String.format("\tPRINT %s", exp));
		return null;
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
	public SCode visit(Exp n, ExpType argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * f0 -> "BEGIN"
	 * f1 -> StmtList()
	 * f2 -> "RETURN"
	 * f3 -> Exp()
	 * f4 -> "END"
	 */
	public SCode visit(StmtExp n, ExpType argu) {
		n.f1.accept(this, null);
		return n.f3.accept(this, ExpType.SIMPLEEXP);
	}

	/**
	 * f0 -> "CALL"
	 * f1 -> Exp()
	 * f2 -> "("
	 * f3 -> ( Exp() )*
	 * f4 -> ")"
	 */
	public SCode visit(Call n, ExpType argu) {
		SCode func = n.f1.accept(this, ExpType.SIMPLEEXP);
		ArrayList<SCode> params = new ArrayList<>();
		for (Node node : n.f3.nodes)
			params.add(node.accept(this, ExpType.TEMP));

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("CALL %s (", func));
		if (!params.isEmpty())
			sb.append(params.get(0));
		for (int i = 1; i < params.size(); i++)
			sb.append(' ').append(params.get(i));
		sb.append(')');

		if (argu == ExpType.EXP)
			return new SCode(sb.toString());

		SCode ret = STemp.newTemp();
		System.out.println(String.format("\tMOVE %s %s", ret, sb));
		return ret;
	}

	/**
	 * f0 -> "HALLOCATE"
	 * f1 -> Exp()
	 */
	public SCode visit(HAllocate n, ExpType argu) {
		SCode exp = n.f1.accept(this, ExpType.SIMPLEEXP);
		String s = String.format("HALLOCATE %s", exp);

		if (argu == ExpType.EXP)
			return new SCode(s);

		SCode ret = STemp.newTemp();
		System.out.println(String.format("\tMOVE %s %s", ret, exp));
		return ret;
	}

	/**
	 * f0 -> Operator()
	 * f1 -> Exp()
	 * f2 -> Exp()
	 */
	public SCode visit(BinOp n, ExpType argu) {
		SCode exp1 = n.f1.accept(this, ExpType.TEMP);
		SCode exp2 = n.f2.accept(this, ExpType.SIMPLEEXP);
		String s = String.format("%s %s %s", ((NodeToken) n.f0.f0.choice).tokenImage, exp1, exp2);

		if (argu == ExpType.EXP)
			return new SCode(s);

		SCode ret = STemp.newTemp();
		System.out.println(String.format("\tMOVE %s %s", ret, s));
		return ret;
	}

	/**
	 * f0 -> "TEMP"
	 * f1 -> IntegerLiteral()
	 */
	public SCode visit(Temp n, ExpType argu) {
		return new STemp(Integer.valueOf(n.f1.f0.tokenImage));
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public SCode visit(IntegerLiteral n, ExpType argu) {
		if (argu == ExpType.TEMP) {
			SCode ret = STemp.newTemp();
			System.out.println(String.format("\tMOVE %s %s", ret, n.f0.tokenImage));
			return ret;
		}
		return new SCode(n.f0.tokenImage);
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public SCode visit(Label n, ExpType argu) {
		if (argu == null) {
			// 说明该Label是语句标号，直接打印
			System.out.print(n.f0.tokenImage);
			return null;
		}
		if (argu == ExpType.TEMP) {
			SCode ret = STemp.newTemp();
			System.out.println(String.format("\tMOVE %s %s", ret, n.f0.tokenImage));
			return ret;
		}
		return new SCode(n.f0.tokenImage);
	}

}
