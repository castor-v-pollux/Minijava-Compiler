package piglet;

/**
 * piglet代码包装类，继承自{@link PStmt}，表示PRINT语句。
 *
 * @author castor_v_pollux
 */
public class PPrintStmt extends PStmt {

	private PExp pExp;

	public PPrintStmt(PExp pExp) {
		this.pExp = pExp;
	}

	@Override
	public int print(int depth) {
		System.out.print("PRINT ");
		pExp.print(depth + 6);
		System.out.println();
		return MULTILINE;
	}

}
