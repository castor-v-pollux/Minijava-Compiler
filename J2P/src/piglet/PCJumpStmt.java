package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PStmt}����ʾCJUMP��䡣
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
		// �����ʽΪ���У���Label��Ҫ���в�����ʽ��ͷ����
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
