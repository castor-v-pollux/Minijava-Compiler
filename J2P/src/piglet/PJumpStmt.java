package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PStmt}����ʾJUMP��䡣
 *
 * @author castor_v_pollux
 */
public class PJumpStmt extends PStmt {

	private PLabel pLabel;

	public PJumpStmt(PLabel pLabel) {
		this.pLabel = pLabel;
	}

	@Override
	public int print(int depth) {
		System.out.print("JUMP ");
		pLabel.print(depth + 5);
		System.out.println();
		return MULTILINE;
	}

}
