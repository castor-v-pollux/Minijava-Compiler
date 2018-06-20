package piglet;

/**
 * piglet代码包装类，继承自{@link PStmt}，表示CJUMP语句。
 *
 * @author castor_v_pollux
 */
public class PCJumpStmt extends PStmt {

	private PExp pExp;

	private PLabel pLabel;

	public PCJumpStmt(PExp pExp, PLabel pLabel) {
		this.pExp = pExp;
		this.pLabel = pLabel;
	}

	@Override
	public int print(int depth) {
		System.out.print("CJUMP ");
		int l = pExp.print(depth + 6);
		// 若表达式为多行，则Label需要换行并与表达式开头对齐
		if (l == MULTILINE) {
			System.out.println();
			indent(depth + 6);
			pLabel.print(depth + 6);
		} else {
			space();
			pLabel.print(depth + 6 + l);
		}
		System.out.println();
		return MULTILINE;
	}

}
