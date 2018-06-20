package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PStmt}����ʾLOAD��䡣
 *
 * @author castor_v_pollux
 */
public class PLoadStmt extends PStmt {

	private PTemp pTemp;

	private PExp pExp;

	private int offset;

	public PLoadStmt(PTemp pTemp, PExp pExp, int offset) {
		this.pTemp = pTemp;
		this.pExp = pExp;
		this.offset = offset;
	}

	@Override
	public int print(int depth) {
		System.out.print("HLOAD ");
		int lTemp = pTemp.print(depth + 6);
		space();
		int lExp = pExp.print(depth + 6 + lTemp + 1);
		// ������ʽ���У���offset�������������ʽ����
		if (lExp == MULTILINE) {
			System.out.println();
			indent(depth + 6 + lTemp + 1);
			System.out.println(offset);
		} else {
			space();
			System.out.println(offset);
		}
		return MULTILINE;
	}

}
