package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PCode}����ʾһ��piglet������
 *
 * @author castor_v_pollux
 */
public class PProcedure extends PCode {

	/**
	 * ������
	 */
	private String name;

	/**
	 * �������������벻����20
	 */
	private int paramCnt;

	private PStmtExp pStmtExp;

	public PProcedure(String name, int paramCnt, PStmtExp pStmtExp) {
		this.name = name;
		this.paramCnt = paramCnt;
		this.pStmtExp = pStmtExp;
	}

	@Override
	public int print(int depth) {
		System.out.println(String.format("\n%s[%d]", name, paramCnt));
		pStmtExp.print(depth);
		System.out.println();
		return MULTILINE;
	}

}
