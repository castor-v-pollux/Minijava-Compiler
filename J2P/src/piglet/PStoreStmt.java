package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PStmt}����ʾSTORE��䡣
 *
 * @author castor_v_pollux
 */
public class PStoreStmt extends PStmt {

	private PExp pExpBase;

	private int offset;

	private PExp pExpValue;

	public PStoreStmt(PExp pExpBase, int offset, PExp pExpValue) {
		this.pExpBase = pExpBase;
		this.offset = offset;
		this.pExpValue = pExpValue;
	}

	@Override
	public int print(int depth) {
		System.out.print("HSTORE ");
		int lBase = pExpBase.print(depth + 7);
		// ������ʽ���У���offset�������������ʽ����
		if (lBase == MULTILINE) {
			System.out.println();
			indent(depth + 7);
			System.out.println(offset);
			indent(depth + 7);
			pExpValue.print(depth + 7);
		} else {
			String s = String.format(" %d ", offset);
			System.out.print(s);
			pExpValue.print(depth + 7 + lBase + s.length());
		}
		System.out.println();
		return MULTILINE;
	}

}
