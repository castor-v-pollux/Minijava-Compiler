package piglet;

/**
 * piglet代码包装类，继承自{@link PExp}，表示一个匿名函数。
 *
 * @author castor_v_pollux
 */
public class PStmtExp extends PExp {

	private PStmtList pStmtList;

	private PExp pExp;

	public PStmtExp(PStmtList pStmtList, PExp pExp) {
		this.pStmtList = pStmtList;
		this.pExp = pExp;
	}

	@Override
	public int print(int depth) {
		System.out.println("BEGIN");
		pStmtList.print(depth + TAB_WIDTH);
		indent(depth);
		System.out.print("RETURN ");
		pExp.print(depth + 7);
		System.out.println();
		indent(depth);
		System.out.print("END");
		return MULTILINE;
	}

}
