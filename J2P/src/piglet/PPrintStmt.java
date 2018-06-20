package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PStmt}����ʾPRINT��䡣
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
