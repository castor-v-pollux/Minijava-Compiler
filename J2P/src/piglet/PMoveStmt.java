package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PStmt}����ʾMOVE��䡣
 *
 * @author castor_v_pollux
 */
public class PMoveStmt extends PStmt {

	private PTemp pTemp;

	private PExp pExp;

	public PMoveStmt(PTemp pTemp, PExp pExp) {
		this.pTemp = pTemp;
		this.pExp = pExp;
	}

	@Override
	public int print(int depth) {
		System.out.print("MOVE ");
		int l = pTemp.print(depth + 5);
		space();
		pExp.print(depth + 5 + l + 1);
		System.out.println();
		return MULTILINE;
	}

}
