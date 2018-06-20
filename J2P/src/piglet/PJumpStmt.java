package piglet;

/**
 * piglet代码包装类，继承自{@link PStmt}，表示JUMP语句。
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
