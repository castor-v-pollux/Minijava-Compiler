package piglet;

/**
 * piglet代码包装类，继承自{@link PStmt}，表示STORE语句。
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
		// 如果表达式跨行，则offset换行输出并与表达式对齐
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
